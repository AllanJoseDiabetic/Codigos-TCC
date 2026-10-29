import re

from flask_login import current_user
from flask_wtf import FlaskForm
from flask_wtf.file import FileAllowed
from wtforms import (
    StringField,
    PasswordField,
    BooleanField,
    SubmitField,
    TextAreaField,
    FileField
)
from wtforms.validators import (
    DataRequired,
    Length,
    EqualTo,
    ValidationError
)

from .extensions import db
from .models import User


def clean_text(value):
    if isinstance(value, str):
        return value.strip()

    return value


def clean_email(value):
    if isinstance(value, str):
        return value.strip().lower()

    return value


def validate_email_format(form, field):
    email = field.data or ""

    pattern = (
        r"^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+"
        r"@"
        r"(?:[A-Za-z0-9]"
        r"(?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
        r"\.)+"
        r"[A-Za-z]{2,63}$"
    )

    if not re.fullmatch(pattern, email):
        raise ValidationError(
            "Informe um endereço de e-mail válido."
        )


class RegistrationForm(FlaskForm):
    username = StringField(
        "Nome de usuário",
        filters=[clean_text],
        validators=[
            DataRequired(),
            Length(min=2, max=20)
        ]
    )

    email = StringField(
        "E-mail",
        filters=[clean_email],
        validators=[
            DataRequired(),
            Length(max=254),
            validate_email_format
        ]
    )

    password = PasswordField(
        "Senha",
        validators=[
            DataRequired(),
            Length(min=8)
        ]
    )

    confirm_password = PasswordField(
        "Confirmar senha",
        validators=[
            DataRequired(),
            EqualTo(
                "password",
                message="As senhas devem ser iguais."
            )
        ]
    )

    submit = SubmitField("Criar conta")

    def validate_username(self, field):
        existing_user = db.session.scalar(
            db.select(User).where(
                User.username == field.data
            )
        )

        if existing_user:
            raise ValidationError(
                "Este nome de usuário já está em uso."
            )

    def validate_email(self, field):
        existing_user = db.session.scalar(
            db.select(User).where(
                User.email == field.data
            )
        )

        if existing_user:
            raise ValidationError(
                "Este e-mail já está cadastrado."
            )


class LoginForm(FlaskForm):
    email = StringField(
        "E-mail",
        filters=[clean_email],
        validators=[
            DataRequired(),
            validate_email_format
        ]
    )

    password = PasswordField(
        "Senha",
        validators=[DataRequired()]
    )

    remember = BooleanField(
        "Manter conectado"
    )

    submit = SubmitField("Entrar")


class UpdateAccountForm(FlaskForm):
    username = StringField(
        "Nome de usuário",
        filters=[clean_text],
        validators=[
            DataRequired(),
            Length(min=2, max=20)
        ]
    )

    email = StringField(
        "E-mail",
        filters=[clean_email],
        validators=[
            DataRequired(),
            Length(max=254),
            validate_email_format
        ]
    )

    picture = FileField(
        "Imagem de perfil",
        validators=[
            FileAllowed(
                ["jpg", "jpeg", "png"],
                "Envie uma imagem JPG ou PNG."
            )
        ]
    )

    submit = SubmitField(
        "Salvar alterações"
    )

    def validate_username(self, field):
        if field.data == current_user.username:
            return

        existing_user = db.session.scalar(
            db.select(User).where(
                User.username == field.data
            )
        )

        if existing_user:
            raise ValidationError(
                "Este nome de usuário já está em uso."
            )

    def validate_email(self, field):
        if field.data == current_user.email:
            return

        existing_user = db.session.scalar(
            db.select(User).where(
                User.email == field.data
            )
        )

        if existing_user:
            raise ValidationError(
                "Este e-mail já está cadastrado."
            )


class PostForm(FlaskForm):
    title = StringField(
        "Título",
        filters=[clean_text],
        validators=[
            DataRequired(),
            Length(min=1, max=120)
        ]
    )

    content = TextAreaField(
        "Conteúdo",
        validators=[
            DataRequired()
        ]
    )

    submit = SubmitField(
        "Salvar publicação"
    )


class RequestResetForm(FlaskForm):
    email = StringField(
        "E-mail",
        filters=[clean_email],
        validators=[
            DataRequired(),
            validate_email_format
        ]
    )

    submit = SubmitField(
        "Solicitar recuperação"
    )


class ResetPasswordForm(FlaskForm):
    password = PasswordField(
        "Nova senha",
        validators=[
            DataRequired(),
            Length(min=8)
        ]
    )

    confirm_password = PasswordField(
        "Confirmar nova senha",
        validators=[
            DataRequired(),
            EqualTo(
                "password",
                message="As senhas devem ser iguais."
            )
        ]
    )

    submit = SubmitField(
        "Alterar senha"
    )