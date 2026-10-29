import calendar

from datetime import date, timedelta

from django.contrib import messages
from django.contrib.auth import (
    get_user_model,
    login,
    logout
)
from django.contrib.auth.decorators import login_required
from django.db import transaction
from django.db.models import Min
from django.http import (
    Http404,
    HttpResponseNotAllowed
)
from django.shortcuts import (
    get_object_or_404,
    redirect,
    render
)
from django.urls import reverse
from django.utils.http import (
    url_has_allowed_host_and_scheme
)

from .forms import (
    RegistrationForm,
    UserLoginForm,
    ProfileForm,
    CategoryForm,
    TaskForm,
    LogEntryForm,
    TimezoneForm
)

from .models import (
    Profile,
    Category,
    Task,
    TaskPeriod,
    Log
)

from .services import (
    get_profile,
    get_user_today,
    can_edit_date,
    expire_old_pending,
    synchronize_logs,
    update_task_periods,
    month_before,
    month_after
)


User = get_user_model()


MONTH_NAMES = [
    "",
    "Janeiro",
    "Fevereiro",
    "Março",
    "Abril",
    "Maio",
    "Junho",
    "Julho",
    "Agosto",
    "Setembro",
    "Outubro",
    "Novembro",
    "Dezembro"
]


def landing(request):
    if request.user.is_authenticated:
        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/landing.html"
    )


def about(request):
    return render(
        request,
        "tracker/about.html"
    )


def register_view(request):
    if request.user.is_authenticated:
        return redirect(
            "tracker:overview"
        )

    form = RegistrationForm(
        request.POST or None
    )

    if request.method == "POST" and form.is_valid():
        data = form.cleaned_data

        with transaction.atomic():
            user = User.objects.create_user(
                username=data["username"],
                email=data["email"],
                password=data["password1"],
                first_name=data["first_name"],
                last_name=data["last_name"]
            )

            Profile.objects.create(
                user=user,
                timezone="UTC"
            )

        messages.success(
            request,
            "Conta criada com sucesso. Faça seu login."
        )

        return redirect(
            "tracker:login"
        )

    return render(
        request,
        "tracker/register.html",
        {
            "form": form
        }
    )


def login_view(request):
    if request.user.is_authenticated:
        return redirect(
            "tracker:overview"
        )

    form = UserLoginForm(
        request,
        data=request.POST or None
    )

    next_url = (
        request.POST.get("next")
        or request.GET.get("next", "")
    )

    if request.method == "POST" and form.is_valid():
        login(
            request,
            form.get_user()
        )

        messages.success(
            request,
            "Login realizado com sucesso."
        )

        if url_has_allowed_host_and_scheme(
            next_url,
            allowed_hosts={request.get_host()},
            require_https=request.is_secure()
        ):
            return redirect(
                next_url
            )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/login.html",
        {
            "form": form,
            "next": next_url
        }
    )


@login_required
def logout_view(request):
    if request.method != "POST":
        return HttpResponseNotAllowed(
            ["POST"]
        )

    logout(request)

    messages.info(
        request,
        "Você saiu da sua conta."
    )

    return redirect(
        "tracker:landing"
    )


@login_required
def overview(request):
    categories = Category.objects.filter(
        owner=request.user
    ).prefetch_related(
        "tasks"
    )

    return render(
        request,
        "tracker/overview.html",
        {
            "categories": categories
        }
    )


@login_required
def add_category(request):
    form = CategoryForm(
        request.POST or None,
        user=request.user
    )

    if request.method == "POST" and form.is_valid():
        Category.objects.create(
            owner=request.user,
            name=form.cleaned_data["name"]
        )

        messages.success(
            request,
            "Categoria criada com sucesso."
        )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/category_form.html",
        {
            "form": form,
            "heading": "Nova categoria"
        }
    )


@login_required
def edit_category(request, category_id):
    category = get_object_or_404(
        Category,
        pk=category_id,
        owner=request.user
    )

    form = CategoryForm(
        request.POST or None,
        user=request.user,
        category=category,
        initial={
            "name": category.name
        }
    )

    if request.method == "POST" and form.is_valid():
        category.name = (
            form.cleaned_data["name"]
        )

        category.save(
            update_fields=["name"]
        )

        messages.success(
            request,
            "Categoria renomeada com sucesso."
        )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/category_form.html",
        {
            "form": form,
            "heading": "Renomear categoria"
        }
    )


@login_required
def delete_category(request, category_id):
    category = get_object_or_404(
        Category,
        pk=category_id,
        owner=request.user
    )

    if request.method == "POST":
        category.delete()

        messages.success(
            request,
            "Categoria, tarefas e registros excluídos."
        )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/confirm_delete.html",
        {
            "object_name": category.name,
            "object_type": "categoria",
            "description": (
                "Todas as tarefas e registros "
                "desta categoria serão excluídos."
            )
        }
    )


@login_required
def add_task(request):
    if not Category.objects.filter(
        owner=request.user
    ).exists():
        messages.info(
            request,
            "Crie uma categoria antes de adicionar uma tarefa."
        )

        return redirect(
            "tracker:add_category"
        )

    form = TaskForm(
        request.POST or None,
        user=request.user
    )

    if request.method == "POST" and form.is_valid():
        data = form.cleaned_data

        selected_days = ",".join(
            data["weekdays"]
        )

        today = get_user_today(
            request.user
        )

        with transaction.atomic():
            task = Task.objects.create(
                owner=request.user,
                name=data["name"],
                category=data["category"],
                active=data["active"],
                weekdays=selected_days,
                created_on=today
            )

            if task.active:
                TaskPeriod.objects.create(
                    task=task,
                    start_date=today,
                    weekdays=selected_days
                )

        messages.success(
            request,
            "Tarefa criada com sucesso."
        )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/task_form.html",
        {
            "form": form,
            "heading": "Nova tarefa"
        }
    )


@login_required
def edit_task(request, task_id):
    task = get_object_or_404(
        Task,
        pk=task_id,
        owner=request.user
    )

    initial = {
        "name": task.name,
        "category": task.category_id,
        "weekdays": (
            task.weekdays.split(",")
            if task.weekdays
            else []
        ),
        "active": task.active
    }

    form = TaskForm(
        request.POST or None,
        user=request.user,
        initial=initial
    )

    if request.method == "POST" and form.is_valid():
        data = form.cleaned_data

        previous_active = task.active
        previous_weekdays = task.weekdays

        today = get_user_today(
            request.user
        )

        with transaction.atomic():
            task.name = data["name"]
            task.category = data["category"]
            task.active = data["active"]

            task.weekdays = ",".join(
                data["weekdays"]
            )

            task.save()

            update_task_periods(
                task,
                previous_active,
                previous_weekdays,
                today
            )

        messages.success(
            request,
            "Tarefa atualizada com sucesso."
        )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/task_form.html",
        {
            "form": form,
            "heading": "Editar tarefa"
        }
    )


@login_required
def delete_task(request, task_id):
    task = get_object_or_404(
        Task,
        pk=task_id,
        owner=request.user
    )

    if request.method == "POST":
        task.delete()

        messages.success(
            request,
            "Tarefa e registros excluídos."
        )

        return redirect(
            "tracker:overview"
        )

    return render(
        request,
        "tracker/confirm_delete.html",
        {
            "object_name": task.name,
            "object_type": "tarefa",
            "description": (
                "Todos os registros desta tarefa "
                "serão excluídos permanentemente."
            )
        }
    )


def build_log_forms(logs, post_data=None):
    entries = []

    for log in logs:
        form = LogEntryForm(
            post_data,
            prefix="log_{}".format(log.id),
            initial={
                "status": log.status,
                "note": log.note
            }
        )

        entries.append({
            "log": log,
            "form": form
        })

    return entries


def save_log_forms(entries):
    validity = [
        entry["form"].is_valid()
        for entry in entries
    ]

    if not entries or not all(validity):
        return False

    with transaction.atomic():
        for entry in entries:
            log = entry["log"]
            data = entry["form"].cleaned_data

            log.status = data["status"]
            log.note = data["note"]

            log.save(
                update_fields=[
                    "status",
                    "note"
                ]
            )

    return True


@login_required
def daily_log(request):
    today = get_user_today(
        request.user
    )

    synchronize_logs(
        request.user,
        today,
        today
    )

    logs = list(
        Log.objects.filter(
            task__owner=request.user,
            date=today
        ).select_related(
            "task",
            "task__category"
        ).order_by(
            "task__category__name",
            "task__name",
            "id"
        )
    )

    entries = build_log_forms(
        logs,
        request.POST if request.method == "POST" else None
    )

    if request.method == "POST":
        if save_log_forms(entries):
            messages.success(
                request,
                "Registros do dia atualizados."
            )

            return redirect(
                "tracker:daily"
            )

        if not entries:
            messages.info(
                request,
                "Não existem registros previstos para hoje."
            )

    return render(
        request,
        "tracker/daily.html",
        {
            "today": today,
            "entries": entries
        }
    )


def parse_requested_date(value):
    try:
        return date.fromisoformat(value)

    except (TypeError, ValueError):
        raise Http404(
            "Data inválida."
        )


@login_required
def weekly_log(request):
    today = get_user_today(
        request.user
    )

    requested_start = request.GET.get(
        "start"
    )

    if requested_start:
        selected_date = parse_requested_date(
            requested_start
        )

    else:
        selected_date = today

    week_start = (
        selected_date
        - timedelta(days=selected_date.weekday())
    )

    week_end = (
        week_start + timedelta(days=6)
    )

    synchronize_logs(
        request.user,
        week_start,
        week_end
    )

    logs = Log.objects.filter(
        task__owner=request.user,
        date__range=(
            week_start,
            week_end
        )
    ).select_related(
        "task"
    ).order_by(
        "date",
        "task__name"
    )

    grouped_logs = {}

    for log in logs:
        grouped_logs.setdefault(
            log.date,
            []
        ).append(log)

    days = []

    for offset in range(7):
        selected_day = (
            week_start + timedelta(days=offset)
        )

        days.append({
            "date": selected_day,
            "logs": grouped_logs.get(
                selected_day,
                []
            ),
            "editable": can_edit_date(
                request.user,
                selected_day
            )
        })

    return render(
        request,
        "tracker/weekly.html",
        {
            "days": days,
            "week_start": week_start,
            "week_end": week_end,
            "previous_start": (
                week_start - timedelta(days=7)
            ),
            "next_start": (
                week_start + timedelta(days=7)
            )
        }
    )


@login_required
def edit_weekly_day(request, day):
    selected_date = parse_requested_date(
        day
    )

    if not can_edit_date(
        request.user,
        selected_date
    ):
        return render(
            request,
            "tracker/confirm_delete.html",
            {
                "object_name": "Registro indisponível",
                "object_type": "acesso",
                "description": (
                    "Registros com mais de uma semana "
                    "ou de datas futuras não podem ser editados."
                ),
                "access_denied": True
            },
            status=403
        )

    synchronize_logs(
        request.user,
        selected_date,
        selected_date
    )

    logs = list(
        Log.objects.filter(
            task__owner=request.user,
            date=selected_date
        ).select_related(
            "task",
            "task__category"
        ).order_by(
            "task__category__name",
            "task__name"
        )
    )

    entries = build_log_forms(
        logs,
        request.POST if request.method == "POST" else None
    )

    if request.method == "POST":
        if save_log_forms(entries):
            messages.success(
                request,
                "Registros atualizados com sucesso."
            )

            monday = (
                selected_date
                - timedelta(
                    days=selected_date.weekday()
                )
            )

            return redirect(
                "{}?start={}".format(
                    reverse("tracker:weekly"),
                    monday.isoformat()
                )
            )

        if not entries:
            messages.info(
                request,
                "Não existem registros para esta data."
            )

    return render(
        request,
        "tracker/edit_day.html",
        {
            "selected_date": selected_date,
            "entries": entries
        }
    )


@login_required
def monthly_log(request, year, month):
    if year < 1 or year > 9999:
        raise Http404(
            "Ano inválido."
        )

    if month < 1 or month > 12:
        raise Http404(
            "Mês inválido."
        )

    today = get_user_today(
        request.user
    )

    first_day = date(
        year,
        month,
        1
    )

    if first_day > today:
        raise Http404(
            "Mês futuro indisponível."
        )

    last_day_number = calendar.monthrange(
        year,
        month
    )[1]

    last_day = date(
        year,
        month,
        last_day_number
    )

    synchronize_logs(
        request.user,
        first_day,
        last_day
    )

    logs = list(
        Log.objects.filter(
            task__owner=request.user,
            date__range=(
                first_day,
                last_day
            )
        ).select_related(
            "task"
        ).order_by(
            "date",
            "task__name"
        )
    )

    counts = {
        "Complete": 0,
        "Incomplete": 0,
        "Ignored": 0,
        "Pending": 0
    }

    for log in logs:
        counts[log.status] += 1

    previous_year, previous_month = month_before(
        year,
        month
    )

    next_year, next_month = month_after(
        year,
        month
    )

    previous_month_data = None

    if previous_year >= 1:
        previous_month_data = {
            "year": previous_year,
            "month": previous_month
        }

    next_month_data = None

    if date(next_year, next_month, 1) <= today:
        next_month_data = {
            "year": next_year,
            "month": next_month
        }

    return render(
        request,
        "tracker/monthly.html",
        {
            "year": year,
            "month": month,
            "month_name": MONTH_NAMES[month],
            "logs": logs,
            "counts": counts,
            "previous_month": previous_month_data,
            "next_month": next_month_data
        }
    )


@login_required
def past_months(request):
    today = get_user_today(
        request.user
    )

    earliest_period = TaskPeriod.objects.filter(
        task__owner=request.user
    ).aggregate(
        earliest=Min("start_date")
    )["earliest"]

    earliest_log = Log.objects.filter(
        task__owner=request.user
    ).aggregate(
        earliest=Min("date")
    )["earliest"]

    available_dates = [
        value
        for value in [
            earliest_period,
            earliest_log
        ]
        if value is not None
    ]

    if available_dates:
        first_date = min(
            available_dates
        )
    else:
        first_date = today

    months = []

    current_year = first_date.year
    current_month = first_date.month

    while (
        current_year,
        current_month
    ) <= (
        today.year,
        today.month
    ):
        months.append({
            "year": current_year,
            "month": current_month,
            "name": MONTH_NAMES[current_month]
        })

        current_year, current_month = month_after(
            current_year,
            current_month
        )

    months.reverse()

    return render(
        request,
        "tracker/past.html",
        {
            "months": months
        }
    )


@login_required
def progress_view(request):
    today = get_user_today(
        request.user
    )

    expire_old_pending(
        request.user,
        today
    )

    tasks = Task.objects.filter(
        owner=request.user
    ).select_related(
        "category"
    )

    summaries = []

    chart_rows = [
        [
            "Tarefa",
            "Complete",
            "Incomplete",
            "Ignored",
            "Pending"
        ]
    ]

    for task in tasks:
        counts = {
            "Complete": 0,
            "Incomplete": 0,
            "Ignored": 0,
            "Pending": 0
        }

        statuses = task.logs.values_list(
            "status",
            flat=True
        )

        for status in statuses:
            counts[status] += 1

        evaluated = (
            counts["Complete"]
            + counts["Incomplete"]
        )

        completion_rate = (
            round(
                counts["Complete"] * 100 / evaluated,
                1
            )
            if evaluated
            else 0
        )

        summaries.append({
            "task": task,
            "counts": counts,
            "completion_rate": completion_rate
        })

        chart_rows.append([
            "{} (#{} )".format(
                task.name,
                task.id
            ),
            counts["Complete"],
            counts["Incomplete"],
            counts["Ignored"],
            counts["Pending"]
        ])

    return render(
        request,
        "tracker/progress.html",
        {
            "summaries": summaries,
            "chart_rows": chart_rows
        }
    )


@login_required
def profile_settings(request):
    form = ProfileForm(
        request.POST or None,
        instance=request.user
    )

    if request.method == "POST" and form.is_valid():
        form.save()

        messages.success(
            request,
            "Perfil atualizado com sucesso."
        )

        return redirect(
            "tracker:profile"
        )

    return render(
        request,
        "tracker/profile.html",
        {
            "form": form
        }
    )


@login_required
def timezone_settings(request):
    profile = get_profile(
        request.user
    )

    form = TimezoneForm(
        request.POST or None,
        initial={
            "timezone": profile.timezone
        }
    )

    if request.method == "POST" and form.is_valid():
        profile.timezone = (
            form.cleaned_data["timezone"]
        )

        profile.save(
            update_fields=["timezone"]
        )

        messages.success(
            request,
            "Fuso horário atualizado com sucesso."
        )

        return redirect(
            "tracker:timezone"
        )

    return render(
        request,
        "tracker/timezone.html",
        {
            "form": form,
            "current_timezone": profile.timezone
        }
    )