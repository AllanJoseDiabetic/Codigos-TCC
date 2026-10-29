from datetime import datetime, timezone

from flask import current_app
from flask_login import UserMixin
from itsdangerous import (
    URLSafeTimedSerializer,
    BadSignature,
    SignatureExpired
)

from .extensions import db, bcrypt


class User(db.Model, UserMixin):
    __tablename__ = "user"

    id = db.Column(
        db.Integer,
        primary_key=True
    )

    username = db.Column(
        db.String(20),
        unique=True,
        nullable=False,
        index=True
    )

    email = db.Column(
        db.String(254),
        unique=True,
        nullable=False,
        index=True
    )

    password_hash = db.Column(
        db.String(128),
        nullable=False
    )

    image_file = db.Column(
        db.String(64),
        nullable=False,
        default="default.svg"
    )

    posts = db.relationship(
        "Post",
        back_populates="author",
        lazy=True
    )

    def set_password(self, password):
        self.password_hash = (
            bcrypt.generate_password_hash(
                password
            ).decode("utf-8")
        )

    def check_password(self, password):
        return bcrypt.check_password_hash(
            self.password_hash,
            password
        )

    def get_reset_token(self):
        serializer = URLSafeTimedSerializer(
            current_app.config["SECRET_KEY"]
        )

        return serializer.dumps(
            {"user_id": self.id},
            salt="password-reset"
        )

    @staticmethod
    def verify_reset_token(token, max_age=1800):
        serializer = URLSafeTimedSerializer(
            current_app.config["SECRET_KEY"]
        )

        try:
            data = serializer.loads(
                token,
                salt="password-reset",
                max_age=max_age
            )

            user_id = int(data["user_id"])

        except (
            BadSignature,
            SignatureExpired,
            KeyError,
            TypeError,
            ValueError
        ):
            return None

        return db.session.get(
            User,
            user_id
        )

    def __repr__(self):
        return f"<User {self.username}>"


class Post(db.Model):
    __tablename__ = "post"

    id = db.Column(
        db.Integer,
        primary_key=True
    )

    title = db.Column(
        db.String(120),
        nullable=False
    )

    content = db.Column(
        db.Text,
        nullable=False
    )

    created_at = db.Column(
        db.DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc)
    )

    author_id = db.Column(
        db.Integer,
        db.ForeignKey("user.id"),
        nullable=False,
        index=True
    )

    author = db.relationship(
        "User",
        back_populates="posts"
    )

    def __repr__(self):
        return f"<Post {self.id}: {self.title}>"