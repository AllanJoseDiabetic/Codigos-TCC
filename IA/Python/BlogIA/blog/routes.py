from urllib.parse import urljoin, urlparse

from flask import (
    Blueprint,
    render_template,
    redirect,
    url_for,
    flash,
    request,
    abort,
    current_app
)

from flask_login import (
    login_user,
    logout_user,
    current_user,
    login_required
)

from .extensions import db
from .models import User, Post

from .forms import (
    RegistrationForm,
    LoginForm,
    UpdateAccountForm,
    PostForm,
    RequestResetForm,
    ResetPasswordForm
)

from .services import (
    save_profile_picture,
    remove_profile_picture,
    send_password_reset_email
)


site = Blueprint("site", __name__)


def is_safe_redirect(target):
    if not target:
        return False

    base_url = urlparse(request.host_url)

    destination = urlparse(
        urljoin(request.host_url, target)
    )

    return (
        destination.scheme in ("http", "https")
        and destination.netloc == base_url.netloc
    )


@site.route("/")
@site.route("/home")
def home():
    page = request.args.get(
        "page",
        1,
        type=int
    )

    statement = (
        db.select(Post)
        .order_by(
            Post.created_at.desc(),
            Post.id.desc()
        )
    )

    pagination = db.paginate(
        statement,
        page=page,
        per_page=5,
        error_out=False
    )

    return render_template(
        "home.html",
        posts=pagination.items,
        pagination=pagination
    )


@site.route("/about")
def about():
    return render_template(
        "about.html"
    )


@site.route(
    "/register",
    methods=["GET", "POST"]
)
def register():
    if current_user.is_authenticated:
        return redirect(
            url_for("site.home")
        )

    form = RegistrationForm()

    if form.validate_on_submit():
        user = User(
            username=form.username.data,
            email=form.email.data
        )

        user.set_password(
            form.password.data
        )

        db.session.add(user)
        db.session.commit()

        flash(
            "Conta criada com sucesso. Faça seu login.",
            "success"
        )

        return redirect(
            url_for("site.login")
        )

    return render_template(
        "register.html",
        form=form
    )


@site.route(
    "/login",
    methods=["GET", "POST"]
)
def login():
    if current_user.is_authenticated:
        return redirect(
            url_for("site.home")
        )

    form = LoginForm()

    if form.validate_on_submit():
        user = db.session.scalar(
            db.select(User).where(
                User.email == form.email.data
            )
        )

        if user and user.check_password(
            form.password.data
        ):
            login_user(
                user,
                remember=form.remember.data
            )

            flash(
                "Login realizado com sucesso.",
                "success"
            )

            next_page = request.args.get(
                "next"
            )

            if is_safe_redirect(next_page):
                return redirect(
                    next_page
                )

            return redirect(
                url_for("site.home")
            )

        flash(
            "E-mail ou senha incorretos.",
            "danger"
        )

    return render_template(
        "login.html",
        form=form
    )


@site.route(
    "/logout",
    methods=["POST"]
)
@login_required
def logout():
    logout_user()

    flash(
        "Você saiu da sua conta.",
        "info"
    )

    return redirect(
        url_for("site.home")
    )


@site.route(
    "/account",
    methods=["GET", "POST"]
)
@login_required
def account():
    form = UpdateAccountForm()

    if form.validate_on_submit():
        new_picture = None

        if form.picture.data:
            try:
                new_picture = save_profile_picture(
                    form.picture.data
                )

            except ValueError as error:
                form.picture.errors.append(
                    str(error)
                )

                return render_template(
                    "account.html",
                    form=form
                )

        old_picture = current_user.image_file

        current_user.username = (
            form.username.data
        )

        current_user.email = (
            form.email.data
        )

        if new_picture:
            current_user.image_file = (
                new_picture
            )

        db.session.commit()

        if new_picture:
            remove_profile_picture(
                old_picture
            )

        flash(
            "Perfil atualizado com sucesso.",
            "success"
        )

        return redirect(
            url_for("site.account")
        )

    if request.method == "GET":
        form.username.data = (
            current_user.username
        )

        form.email.data = (
            current_user.email
        )

    return render_template(
        "account.html",
        form=form
    )


@site.route("/user/<string:username>")
def user_posts(username):
    user = db.session.scalar(
        db.select(User).where(
            User.username == username
        )
    )

    if user is None:
        abort(404)

    page = request.args.get(
        "page",
        1,
        type=int
    )

    statement = (
        db.select(Post)
        .where(
            Post.author_id == user.id
        )
        .order_by(
            Post.created_at.desc(),
            Post.id.desc()
        )
    )

    pagination = db.paginate(
        statement,
        page=page,
        per_page=5,
        error_out=False
    )

    return render_template(
        "user_posts.html",
        user=user,
        posts=pagination.items,
        pagination=pagination
    )


@site.route(
    "/post/new",
    methods=["GET", "POST"]
)
@login_required
def new_post():
    form = PostForm()

    if form.validate_on_submit():
        post = Post(
            title=form.title.data,
            content=form.content.data,
            author_id=current_user.id
        )

        db.session.add(post)
        db.session.commit()

        flash(
            "Publicação criada com sucesso.",
            "success"
        )

        return redirect(
            url_for(
                "site.post_detail",
                post_id=post.id
            )
        )

    return render_template(
        "post_form.html",
        form=form,
        heading="Nova publicação"
    )


@site.route("/post/<int:post_id>")
def post_detail(post_id):
    post = db.get_or_404(
        Post,
        post_id
    )

    return render_template(
        "post_detail.html",
        post=post
    )


@site.route(
    "/post/<int:post_id>/update",
    methods=["GET", "POST"]
)
@login_required
def update_post(post_id):
    post = db.get_or_404(
        Post,
        post_id
    )

    if post.author_id != current_user.id:
        abort(403)

    form = PostForm()

    if form.validate_on_submit():
        post.title = form.title.data
        post.content = form.content.data

        db.session.commit()

        flash(
            "Publicação atualizada com sucesso.",
            "success"
        )

        return redirect(
            url_for(
                "site.post_detail",
                post_id=post.id
            )
        )

    if request.method == "GET":
        form.title.data = post.title
        form.content.data = post.content

    return render_template(
        "post_form.html",
        form=form,
        heading="Editar publicação"
    )


@site.route(
    "/post/<int:post_id>/delete",
    methods=["POST"]
)
@login_required
def delete_post(post_id):
    post = db.get_or_404(
        Post,
        post_id
    )

    if post.author_id != current_user.id:
        abort(403)

    db.session.delete(post)
    db.session.commit()

    flash(
        "Publicação excluída com sucesso.",
        "success"
    )

    return redirect(
        url_for("site.home")
    )


@site.route(
    "/reset_password",
    methods=["GET", "POST"]
)
def request_password_reset():
    if current_user.is_authenticated:
        return redirect(
            url_for("site.home")
        )

    form = RequestResetForm()

    if form.validate_on_submit():
        if not (
            current_app.config.get("MAIL_SERVER")
            and current_app.config.get(
                "MAIL_DEFAULT_SENDER"
            )
        ):
            flash(
                "O serviço de recuperação por e-mail não está configurado.",
                "danger"
            )

            return render_template(
                "request_reset.html",
                form=form
            )

        user = db.session.scalar(
            db.select(User).where(
                User.email == form.email.data
            )
        )

        if user:
            try:
                send_password_reset_email(
                    user
                )

            except Exception:
                current_app.logger.exception(
                    "Falha no envio de recuperação de senha."
                )

                flash(
                    "Não foi possível processar o envio do e-mail.",
                    "danger"
                )

                return render_template(
                    "request_reset.html",
                    form=form
                )

        flash(
            "Se o e-mail estiver cadastrado, você receberá instruções de recuperação.",
            "info"
        )

        return redirect(
            url_for("site.login")
        )

    return render_template(
        "request_reset.html",
        form=form
    )


@site.route(
    "/reset_password/<token>",
    methods=["GET", "POST"]
)
def reset_password(token):
    if current_user.is_authenticated:
        return redirect(
            url_for("site.home")
        )

    user = User.verify_reset_token(
        token
    )

    if user is None:
        flash(
            "O link de recuperação é inválido ou expirou.",
            "danger"
        )

        return redirect(
            url_for(
                "site.request_password_reset"
            )
        )

    form = ResetPasswordForm()

    if form.validate_on_submit():
        user.set_password(
            form.password.data
        )

        db.session.commit()

        flash(
            "Senha alterada com sucesso. Entre com sua nova senha.",
            "success"
        )

        return redirect(
            url_for("site.login")
        )

    return render_template(
        "reset_password.html",
        form=form
    )