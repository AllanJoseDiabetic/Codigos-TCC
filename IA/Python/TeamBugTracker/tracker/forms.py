from django import forms

from django.contrib.auth import get_user_model
from django.contrib.auth.hashers import check_password
from django.contrib.auth.password_validation import (
    validate_password
)
from django.core.exceptions import ValidationError

from .models import Bug, Organization


User = get_user_model()


class SignUpForm(forms.ModelForm):
    organization = forms.ModelChoiceField(
        queryset=Organization.objects.none(),
        label="Organização",
        empty_label="Selecione sua organização"
    )

    access_code = forms.CharField(
        label="Código de acesso da organização",
        widget=forms.PasswordInput
    )

    password1 = forms.CharField(
        label="Senha",
        widget=forms.PasswordInput
    )

    password2 = forms.CharField(
        label="Confirmar senha",
        widget=forms.PasswordInput
    )

    class Meta:
        model = User

        fields = [
            "username",
            "email",
            "organization",
        ]

        labels = {
            "username": "Usuário",
            "email": "E-mail",
        }

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

        self.fields["organization"].queryset = (
            Organization.objects.order_by("name")
        )

        for field in self.fields.values():
            field.widget.attrs["class"] = "form-control"

    def clean_email(self):
        email = self.cleaned_data["email"].strip().lower()

        if User.objects.filter(
            email__iexact=email
        ).exists():
            raise ValidationError(
                "Este e-mail já está cadastrado."
            )

        return email

    def clean(self):
        cleaned_data = super().clean()

        password1 = cleaned_data.get("password1")
        password2 = cleaned_data.get("password2")

        organization = cleaned_data.get("organization")
        access_code = cleaned_data.get("access_code")

        if password1 and password2:
            if password1 != password2:
                self.add_error(
                    "password2",
                    "As senhas não coincidem."
                )
            else:
                user = User(
                    username=cleaned_data.get(
                        "username",
                        ""
                    ),
                    email=cleaned_data.get(
                        "email",
                        ""
                    )
                )

                try:
                    validate_password(
                        password1,
                        user=user
                    )
                except ValidationError as error:
                    self.add_error(
                        "password1",
                        error
                    )

        if organization and access_code:
            if not check_password(
                access_code,
                organization.access_code_hash
            ):
                self.add_error(
                    "access_code",
                    "Código de organização inválido."
                )

        return cleaned_data

    def save(self, commit=True):
        user = super().save(commit=False)

        user.organization = self.cleaned_data[
            "organization"
        ]

        user.set_password(
            self.cleaned_data["password1"]
        )

        if commit:
            user.save()

        return user


class BugForm(forms.ModelForm):
    class Meta:
        model = Bug

        fields = [
            "title",
            "reproduction_steps",
            "estimated_hours",
            "assignee",
            "category",
            "severity",
        ]

        labels = {
            "title": "Título",
            "reproduction_steps": (
                "Descrição e passos de reprodução"
            ),
            "estimated_hours": (
                "Estimativa de horas"
            ),
            "assignee": (
                "Responsável técnico"
            ),
            "category": "Categoria",
            "severity": "Severidade",
        }

        widgets = {
            "reproduction_steps": forms.Textarea(
                attrs={"rows": 7}
            ),
            "estimated_hours": forms.NumberInput(
                attrs={
                    "step": "0.01",
                    "min": "0"
                }
            ),
        }

    def __init__(
        self,
        *args,
        organization=None,
        **kwargs
    ):
        super().__init__(*args, **kwargs)

        self.organization = organization

        self.fields["assignee"].queryset = (
            User.objects.filter(
                organization=organization,
                is_active=True
            ).order_by("username")
        )

        for field in self.fields.values():
            field.widget.attrs["class"] = "form-control"

    def clean_assignee(self):
        assignee = self.cleaned_data["assignee"]

        if self.organization is None:
            raise ValidationError(
                "Organização não definida."
            )

        if assignee.organization_id != self.organization.id:
            raise ValidationError(
                "O responsável deve pertencer à organização."
            )

        return assignee

    def clean_reproduction_steps(self):
        steps = self.cleaned_data[
            "reproduction_steps"
        ].strip()

        if not steps:
            raise ValidationError(
                "Informe a descrição e os passos "
                "para reproduzir o defeito."
            )

        return steps