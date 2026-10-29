import pytz

from django import forms
from django.contrib.auth import get_user_model
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth.password_validation import (
    validate_password
)
from django.core.exceptions import ValidationError

from .models import Category, Log


User = get_user_model()


WEEKDAY_CHOICES = [
    ("0", "Segunda-feira"),
    ("1", "Terça-feira"),
    ("2", "Quarta-feira"),
    ("3", "Quinta-feira"),
    ("4", "Sexta-feira"),
    ("5", "Sábado"),
    ("6", "Domingo"),
]


class RegistrationForm(forms.Form):
    username = forms.CharField(
        label="Nome de usuário",
        min_length=2,
        max_length=150
    )

    email = forms.EmailField(
        label="E-mail",
        max_length=254
    )

    first_name = forms.CharField(
        label="Nome",
        max_length=150,
        required=False
    )

    last_name = forms.CharField(
        label="Sobrenome",
        max_length=150,
        required=False
    )

    password1 = forms.CharField(
        label="Senha",
        widget=forms.PasswordInput
    )

    password2 = forms.CharField(
        label="Confirmar senha",
        widget=forms.PasswordInput
    )

    def clean_username(self):
        username = self.cleaned_data["username"].strip()

        if User.objects.filter(
            username__iexact=username
        ).exists():
            raise ValidationError(
                "Este nome de usuário já está cadastrado."
            )

        field = User._meta.get_field("username")

        field.run_validators(username)

        return username

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

        if password1 and password2:
            if password1 != password2:
                self.add_error(
                    "password2",
                    "As senhas devem ser iguais."
                )

            else:
                temporary_user = User(
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
                        user=temporary_user
                    )

                except ValidationError as error:
                    self.add_error(
                        "password1",
                        error
                    )

        return cleaned_data


class UserLoginForm(AuthenticationForm):
    username = forms.CharField(
        label="Nome de usuário"
    )

    password = forms.CharField(
        label="Senha",
        widget=forms.PasswordInput
    )


class ProfileForm(forms.ModelForm):
    class Meta:
        model = User

        fields = [
            "username",
            "email",
            "first_name",
            "last_name"
        ]

        labels = {
            "username": "Nome de usuário",
            "email": "E-mail",
            "first_name": "Nome",
            "last_name": "Sobrenome",
        }

    def clean_username(self):
        username = self.cleaned_data["username"].strip()

        duplicate = User.objects.filter(
            username__iexact=username
        ).exclude(
            pk=self.instance.pk
        ).exists()

        if duplicate:
            raise ValidationError(
                "Este nome de usuário já está em uso."
            )

        return username

    def clean_email(self):
        email = self.cleaned_data["email"].strip().lower()

        duplicate = User.objects.filter(
            email__iexact=email
        ).exclude(
            pk=self.instance.pk
        ).exists()

        if duplicate:
            raise ValidationError(
                "Este e-mail já está em uso."
            )

        return email


class CategoryForm(forms.Form):
    name = forms.CharField(
        label="Nome da categoria",
        max_length=80
    )

    def __init__(
        self,
        *args,
        user=None,
        category=None,
        **kwargs
    ):
        super().__init__(*args, **kwargs)

        self.user = user
        self.category = category

    def clean_name(self):
        name = self.cleaned_data["name"].strip()

        queryset = Category.objects.filter(
            owner=self.user,
            name__iexact=name
        )

        if self.category is not None:
            queryset = queryset.exclude(
                pk=self.category.pk
            )

        if queryset.exists():
            raise ValidationError(
                "Você já possui uma categoria com este nome."
            )

        return name


class TaskForm(forms.Form):
    name = forms.CharField(
        label="Nome da tarefa",
        max_length=120
    )

    category = forms.ModelChoiceField(
        label="Categoria",
        queryset=Category.objects.none(),
        empty_label="Selecione uma categoria"
    )

    weekdays = forms.MultipleChoiceField(
        label="Dias previstos",
        choices=WEEKDAY_CHOICES,
        widget=forms.CheckboxSelectMultiple
    )

    active = forms.BooleanField(
        label="Tarefa ativa",
        required=False,
        initial=True
    )

    def __init__(
        self,
        *args,
        user=None,
        **kwargs
    ):
        super().__init__(*args, **kwargs)

        self.fields["category"].queryset = (
            Category.objects.filter(
                owner=user
            ).order_by("name")
        )

    def clean_name(self):
        return self.cleaned_data["name"].strip()

    def clean_weekdays(self):
        weekdays = self.cleaned_data["weekdays"]

        if not weekdays:
            raise ValidationError(
                "Selecione pelo menos um dia da semana."
            )

        return sorted(
            weekdays,
            key=int
        )


class LogEntryForm(forms.Form):
    status = forms.ChoiceField(
        label="Situação",
        choices=Log.STATUS_CHOICES
    )

    note = forms.CharField(
        label="Anotação",
        max_length=2000,
        required=False,
        widget=forms.Textarea(
            attrs={
                "rows": 2
            }
        )
    )


class TimezoneForm(forms.Form):
    timezone = forms.ChoiceField(
        label="Fuso horário",
        choices=[
            (value, value)
            for value in pytz.common_timezones
        ]
    )