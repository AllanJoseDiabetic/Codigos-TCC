import os
import re
import secrets

from flask import (
    current_app,
    render_template,
    url_for
)
from flask_mail import Message
from PIL import (
    Image,
    ImageOps,
    UnidentifiedImageError
)

from .extensions import mail


def save_profile_picture(upload):
    try:
        upload.stream.seek(0)

        with Image.open(upload.stream) as image:
            image_format = image.format
            image.verify()

        if image_format not in ("JPEG", "PNG"):
            raise ValueError(
                "Formato de imagem não permitido."
            )

        upload.stream.seek(0)

        extension = (
            "jpg"
            if image_format == "JPEG"
            else "png"
        )

        filename = (
            f"{secrets.token_hex(16)}.{extension}"
        )

        destination = os.path.join(
            current_app.static_folder,
            "profile_pics",
            filename
        )

        with Image.open(upload.stream) as image:
            processed_image = (
                ImageOps.exif_transpose(image)
            )

            processed_image.thumbnail(
                (320, 320)
            )

            if image_format == "JPEG":
                processed_image = (
                    processed_image.convert("RGB")
                )
            else:
                processed_image = (
                    processed_image.convert("RGBA")
                )

            processed_image.save(
                destination,
                format=image_format
            )

        return filename

    except (
        OSError,
        ValueError,
        UnidentifiedImageError,
        Image.DecompressionBombError
    ) as error:
        raise ValueError(
            "A imagem enviada não pôde ser processada."
        ) from error


def remove_profile_picture(filename):
    if not filename:
        return

    valid_name = re.fullmatch(
        r"[0-9a-f]{32}\.(jpg|png)",
        filename
    )

    if not valid_name:
        return

    image_path = os.path.join(
        current_app.static_folder,
        "profile_pics",
        filename
    )

    try:
        os.remove(image_path)
    except FileNotFoundError:
        pass


def send_password_reset_email(user):
    config = current_app.config

    required_values = [
        config.get("MAIL_SERVER"),
        config.get("MAIL_DEFAULT_SENDER")
    ]

    if not all(required_values):
        raise RuntimeError(
            "O serviço SMTP não está configurado."
        )

    token = user.get_reset_token()

    reset_url = url_for(
        "site.reset_password",
        token=token,
        _external=True
    )

    message = Message(
        subject="Recuperação de senha",
        recipients=[user.email],
        body=render_template(
            "emails/reset_password.txt",
            user=user,
            reset_url=reset_url
        )
    )

    mail.send(message)