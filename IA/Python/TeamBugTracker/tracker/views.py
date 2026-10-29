from django.contrib import messages
from django.contrib.auth import login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth import authenticate
from django.core.exceptions import PermissionDenied
from django.db.models import Q
from django.http import JsonResponse
from django.shortcuts import (
    get_object_or_404,
    redirect,
    render
)
from django.urls import reverse
from django.utils import timezone
from django.views.decorators.http import (
    require_GET,
    require_POST,
    require_http_methods
)

from .forms import BugForm, SignUpForm
from .models import Bug


def require_organization(request):
    if not request.user.organization_id:
        raise PermissionDenied(
            "Sua conta não está associada a uma organização."
        )

    return request.user.organization


def organization_bugs(request):
    require_organization(request)

    return Bug.objects.filter(
        organization_id=request.user.organization_id
    ).select_related(
        "organization",
        "assignee",
        "created_by"
    )


def get_organization_bug(request, bug_id):
    return get_object_or_404(
        organization_bugs(request),
        pk=bug_id
    )


def serialize_bug(bug):
    return {
        "id": bug.pk,
        "title": bug.title,
        "reproduction_steps": bug.reproduction_steps,
        "estimated_hours": str(bug.estimated_hours),
        "assignee": {
            "id": bug.assignee_id,
            "username": bug.assignee.username,
        },
        "created_by": bug.created_by.username,
        "organization": bug.organization.name,
        "category": bug.category,
        "category_label": bug.get_category_display(),
        "severity": bug.severity,
        "severity_label": bug.get_severity_display(),
        "active": bug.active,
        "status": (
            "Ativo" if bug.active else "Concluído"
        ),
        "created_at": bug.created_at.isoformat(),
        "updated_at": bug.updated_at.isoformat(),
        "detail_url": reverse(
            "tracker:bug_detail",
            args=[bug.pk]
        ),
        "edit_url": reverse(
            "tracker:bug_edit",
            args=[bug.pk]
        ),
    }


def form_errors(form):
    errors = form.errors.get_json_data()

    return {
        key: [
            error["message"]
            for error in entries
        ]
        for key, entries in errors.items()
    }


def wants_json(request):
    return (
        request.headers.get("X-Requested-With")
        == "XMLHttpRequest"
    )


@require_http_methods(["GET", "POST"])
def signup(request):
    if request.user.is_authenticated:
        return redirect("tracker:bug_list")

    if request.method == "POST":
        form = SignUpForm(request.POST)

        if form.is_valid():
            user = form.save()

            login(request, user)

            messages.success(
                request,
                "Conta criada com sucesso."
            )

            return redirect("tracker:bug_list")
    else:
        form = SignUpForm()

    return render(
        request,
        "tracker/signup.html",
        {"form": form}
    )


@require_http_methods(["GET", "POST"])
def signin(request):
    if request.user.is_authenticated:
        return redirect("tracker:bug_list")

    if request.method == "POST":
        form = AuthenticationForm(
            request=request,
            data=request.POST
        )

        if form.is_valid():
            username = form.cleaned_data["username"]
            password = form.cleaned_data["password"]

            user = authenticate(
                request,
                username=username,
                password=password
            )

            if user is not None:
                login(request, user)

                return redirect("tracker:bug_list")
    else:
        form = AuthenticationForm(request=request)

    for field in form.fields.values():
        field.widget.attrs["class"] = "form-control"

    return render(
        request,
        "tracker/login.html",
        {"form": form}
    )


@login_required
@require_POST
def signout(request):
    logout(request)

    return redirect("tracker:login")


@login_required
@require_GET
def bug_list(request):
    organization = require_organization(request)

    return render(
        request,
        "tracker/bug_list.html",
        {
            "page_title": "Defeitos da organização",
            "organization": organization,
            "list_mode": "organization",
            "data_url": reverse(
                "tracker:bug_list_messages"
            ),
        }
    )


@login_required
@require_GET
def my_bug_list(request):
    organization = require_organization(request)

    return render(
        request,
        "tracker/bug_list.html",
        {
            "page_title": "Meus defeitos",
            "organization": organization,
            "list_mode": "personal",
            "data_url": reverse(
                "tracker:user_bug_list_messages"
            ),
        }
    )


@login_required
@require_GET
def bug_list_messages(request):
    bugs = organization_bugs(request)

    return JsonResponse({
        "bugs": [
            serialize_bug(bug)
            for bug in bugs
        ]
    })


@login_required
@require_GET
def user_bug_list_messages(request):
    bugs = organization_bugs(request).filter(
        assignee=request.user
    )

    return JsonResponse({
        "bugs": [
            serialize_bug(bug)
            for bug in bugs
        ]
    })


@login_required
@require_GET
def user_message_count(request):
    require_organization(request)

    bugs = Bug.objects.filter(
        organization_id=request.user.organization_id
    )

    personal = bugs.filter(
        assignee=request.user
    )

    return JsonResponse({
        "organization_count": bugs.count(),
        "organization_active_count": bugs.filter(
            active=True
        ).count(),
        "personal_count": personal.count(),
        "personal_active_count": personal.filter(
            active=True
        ).count(),
    })


@login_required
@require_GET
def bug_detail(request, bug_id):
    bug = get_organization_bug(
        request,
        bug_id
    )

    return render(
        request,
        "tracker/bug_detail.html",
        {
            "bug": bug,
        }
    )


@login_required
@require_GET
def bug_detail_content(request, bug_id):
    bug = get_organization_bug(
        request,
        bug_id
    )

    return JsonResponse({
        "bug": serialize_bug(bug)
    })


@login_required
@require_http_methods(["GET", "POST"])
def bug_create(request):
    organization = require_organization(request)

    if request.method == "POST":
        form = BugForm(
            request.POST,
            organization=organization
        )

        if form.is_valid():
            bug = form.save(commit=False)

            bug.organization = organization
            bug.created_by = request.user
            bug.active = True

            bug.full_clean()
            bug.save()

            destination = reverse(
                "tracker:bug_detail",
                args=[bug.pk]
            )

            if wants_json(request):
                return JsonResponse(
                    {
                        "ok": True,
                        "id": bug.pk,
                        "redirect": destination,
                    },
                    status=201
                )

            messages.success(
                request,
                "Defeito cadastrado com sucesso."
            )

            return redirect(destination)

        if wants_json(request):
            return JsonResponse(
                {
                    "ok": False,
                    "errors": form_errors(form),
                },
                status=400
            )

    else:
        form = BugForm(
            organization=organization
        )

    return render(
        request,
        "tracker/bug_form.html",
        {
            "form": form,
            "page_title": "Cadastrar defeito",
            "button_label": "Cadastrar defeito",
            "editing": False,
        }
    )


@login_required
@require_http_methods(["GET", "POST"])
def bug_edit(request, bug_id):
    organization = require_organization(request)

    bug = get_organization_bug(
        request,
        bug_id
    )

    if request.method == "POST":
        form = BugForm(
            request.POST,
            instance=bug,
            organization=organization
        )

        if form.is_valid():
            updated_bug = form.save()

            destination = reverse(
                "tracker:bug_detail",
                args=[updated_bug.pk]
            )

            if wants_json(request):
                return JsonResponse({
                    "ok": True,
                    "id": updated_bug.pk,
                    "redirect": destination,
                })

            messages.success(
                request,
                "Defeito atualizado com sucesso."
            )

            return redirect(destination)

        if wants_json(request):
            return JsonResponse(
                {
                    "ok": False,
                    "errors": form_errors(form),
                },
                status=400
            )

    else:
        form = BugForm(
            instance=bug,
            organization=organization
        )

    return render(
        request,
        "tracker/bug_form.html",
        {
            "form": form,
            "page_title": "Editar defeito",
            "button_label": "Salvar alterações",
            "editing": True,
            "bug": bug,
        }
    )


@login_required
@require_POST
def complete_bug(request, bug_id):
    bug = get_organization_bug(
        request,
        bug_id
    )

    if not bug.active:
        return JsonResponse(
            {
                "ok": False,
                "error": (
                    "Este defeito já foi marcado "
                    "como concluído."
                ),
            },
            status=400
        )

    updated = Bug.objects.filter(
        pk=bug.pk,
        organization_id=request.user.organization_id,
        active=True
    ).update(
        active=False,
        updated_at=timezone.now()
    )

    if updated == 0:
        return JsonResponse(
            {
                "ok": False,
                "error": (
                    "O defeito já foi concluído "
                    "por outra operação."
                ),
            },
            status=400
        )

    bug.refresh_from_db()

    return JsonResponse({
        "ok": True,
        "bug": serialize_bug(bug),
    })