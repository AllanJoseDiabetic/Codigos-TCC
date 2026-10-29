from django.urls import path

from . import views


app_name = "tracker"


urlpatterns = [
    path(
        "",
        views.landing,
        name="landing"
    ),

    path(
        "about",
        views.about,
        name="about"
    ),

    path(
        "register",
        views.register_view,
        name="register"
    ),

    path(
        "login",
        views.login_view,
        name="login"
    ),

    path(
        "logout",
        views.logout_view,
        name="logout"
    ),

    path(
        "tasks/overview",
        views.overview,
        name="overview"
    ),

    path(
        "tasks/add",
        views.add_task,
        name="add_task"
    ),

    path(
        "tasks/edit/<int:task_id>",
        views.edit_task,
        name="edit_task"
    ),

    path(
        "tasks/categories/add",
        views.add_category,
        name="add_category"
    ),

    path(
        "tasks/categories/edit/<int:category_id>",
        views.edit_category,
        name="edit_category"
    ),

    path(
        "tasks/delete/task/<int:task_id>",
        views.delete_task,
        name="delete_task"
    ),

    path(
        "tasks/delete/category/<int:category_id>",
        views.delete_category,
        name="delete_category"
    ),

    path(
        "log/daily",
        views.daily_log,
        name="daily"
    ),

    path(
        "log/weekly",
        views.weekly_log,
        name="weekly"
    ),

    path(
        "log/weekly/edit/<str:day>",
        views.edit_weekly_day,
        name="edit_day"
    ),

    path(
        "log/monthly/<int:year>/<int:month>",
        views.monthly_log,
        name="monthly"
    ),

    path(
        "log/past",
        views.past_months,
        name="past"
    ),

    path(
        "progress",
        views.progress_view,
        name="progress"
    ),

    path(
        "settings/profile",
        views.profile_settings,
        name="profile"
    ),

    path(
        "settings/timezone",
        views.timezone_settings,
        name="timezone"
    ),
]