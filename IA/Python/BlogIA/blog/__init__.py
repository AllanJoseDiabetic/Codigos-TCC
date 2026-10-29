import os

from flask import Flask, render_template

from .extensions import (
    db,
    bcrypt,
    login_manager,
    mail,
    csrf
)


def create_app():
    app = Flask(__name__)

    secret_key = os.environ.get("BLOG_SECRET_KEY")

    if not secret_key:
        raise RuntimeError(
            "A variavel BLOG_SECRET_KEY deve ser configurada."
        )

    app.config.update(
        SECRET_KEY=secret_key,

        SQLALCHEMY_DATABASE_URI=os.environ.get(
            "BLOG_DATABASE_URI",
            "sqlite:///blog.db"
        ),

        SQLALCHEMY_TRACK_MODIFICATIONS=False,

        MAIL_SERVER=os.environ.get(
            "BLOG_MAIL_SERVER",
            ""
        ),

        MAIL_PORT=int(
            os.environ.get("BLOG_MAIL_PORT", "587")
        ),

        MAIL_USE_TLS=True,
        MAIL_USE_SSL=False,

        MAIL_USERNAME=os.environ.get(
            "BLOG_MAIL_USERNAME"
        ),

        MAIL_PASSWORD=os.environ.get(
            "BLOG_MAIL_PASSWORD"
        ),

        MAIL_DEFAULT_SENDER=os.environ.get(
            "BLOG_MAIL_DEFAULT_SENDER"
        ),

        MAX_CONTENT_LENGTH=2 * 1024 * 1024
    )

    os.makedirs(
        app.instance_path,
        exist_ok=True
    )

    os.makedirs(
        os.path.join(
            app.static_folder,
            "profile_pics"
        ),
        exist_ok=True
    )

    db.init_app(app)
    bcrypt.init_app(app)
    login_manager.init_app(app)
    mail.init_app(app)
    csrf.init_app(app)

    login_manager.login_view = "site.login"

    login_manager.login_message = (
        "Entre na sua conta para acessar esta página."
    )

    login_manager.login_message_category = "info"

    from .models import User

    @login_manager.user_loader
    def load_user(user_id):
        try:
            return db.session.get(
                User,
                int(user_id)
            )
        except (TypeError, ValueError):
            return None

    from .routes import site

    app.register_blueprint(site)

    @app.errorhandler(403)
    def forbidden(error):
        return render_template(
            "errors/403.html"
        ), 403

    @app.errorhandler(404)
    def not_found(error):
        return render_template(
            "errors/404.html"
        ), 404

    @app.errorhandler(413)
    def request_too_large(error):
        return render_template(
            "errors/413.html"
        ), 413

    @app.errorhandler(500)
    def internal_error(error):
        db.session.rollback()

        return render_template(
            "errors/500.html"
        ), 500

    with app.app_context():
        db.create_all()

    return app