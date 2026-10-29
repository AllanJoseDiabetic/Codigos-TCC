from django.urls import path

from . import views


app_name = "tracker"

urlpatterns = [
    path(
        "",
        views.bug_list,
        name="bug_list"
    ),

    path(
        "mine/",
        views.my_bug_list,
        name="my_bug_list"
    ),

    path(
        "signup/",
        views.signup,
        name="signup"
    ),

    path(
        "login/",
        views.signin,
        name="login"
    ),

    path(
        "logout/",
        views.signout,
        name="logout"
    ),

    path(
        "bug/new/",
        views.bug_create,
        name="bug_create"
    ),

    path(
        "bug/<int:bug_id>/edit/",
        views.bug_edit,
        name="bug_edit"
    ),

    path(
        "bug/<int:bug_id>/",
        views.bug_detail,
        name="bug_detail"
    ),

    path(
        "buglistmessages",
        views.bug_list_messages,
        name="bug_list_messages"
    ),

    path(
        "userbuglistmessages",
        views.user_bug_list_messages,
        name="user_bug_list_messages"
    ),

    path(
        "bugdetailcontent/<int:bug_id>",
        views.bug_detail_content,
        name="bug_detail_content"
    ),

    path(
        "usermessagecount",
        views.user_message_count,
        name="user_message_count"
    ),

    path(
        "completebug/<int:bug_id>",
        views.complete_bug,
        name="complete_bug"
    ),
]