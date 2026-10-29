from django.contrib import admin
from django.http import HttpResponseRedirect
from django.urls import include, path, reverse


def home(request):
    return HttpResponseRedirect(
        reverse("tracker:bug_list")
    )


urlpatterns = [
    path("", home, name="home"),
    path("admin/", admin.site.urls),
    path(
        "bugtracker/",
        include("tracker.urls")
    ),
]