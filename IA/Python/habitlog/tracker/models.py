from django.conf import settings
from django.db import models
from django.utils import timezone


class Profile(models.Model):
    user = models.OneToOneField(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="habit_profile"
    )

    timezone = models.CharField(
        max_length=64,
        default="UTC"
    )

    def __str__(self):
        return self.user.username


class Category(models.Model):
    owner = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="habit_categories"
    )

    name = models.CharField(
        max_length=80
    )

    class Meta:
        ordering = ["name", "id"]
        unique_together = [
            ("owner", "name")
        ]

    def __str__(self):
        return self.name


class Task(models.Model):
    owner = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="habit_tasks"
    )

    category = models.ForeignKey(
        Category,
        on_delete=models.CASCADE,
        related_name="tasks"
    )

    name = models.CharField(
        max_length=120
    )

    active = models.BooleanField(
        default=True
    )

    weekdays = models.CharField(
        max_length=13
    )

    created_on = models.DateField()

    class Meta:
        ordering = ["category__name", "name", "id"]

    def weekday_numbers(self):
        if not self.weekdays:
            return set()

        return {
            int(value)
            for value in self.weekdays.split(",")
        }

    def __str__(self):
        return self.name


class TaskPeriod(models.Model):
    task = models.ForeignKey(
        Task,
        on_delete=models.CASCADE,
        related_name="periods"
    )

    start_date = models.DateField()

    end_date = models.DateField(
        null=True,
        blank=True
    )

    weekdays = models.CharField(
        max_length=13
    )

    class Meta:
        ordering = ["start_date", "id"]

    def weekday_numbers(self):
        if not self.weekdays:
            return set()

        return {
            int(value)
            for value in self.weekdays.split(",")
        }

    def __str__(self):
        return "{} - {}".format(
            self.task.name,
            self.start_date
        )


class Log(models.Model):
    COMPLETE = "Complete"
    INCOMPLETE = "Incomplete"
    IGNORED = "Ignored"
    PENDING = "Pending"

    STATUS_CHOICES = [
        (COMPLETE, "Complete"),
        (INCOMPLETE, "Incomplete"),
        (IGNORED, "Ignored"),
        (PENDING, "Pending"),
    ]

    task = models.ForeignKey(
        Task,
        on_delete=models.CASCADE,
        related_name="logs"
    )

    date = models.DateField()

    status = models.CharField(
        max_length=10,
        choices=STATUS_CHOICES,
        default=PENDING
    )

    note = models.TextField(
        blank=True,
        default=""
    )

    created_at = models.DateTimeField(
        default=timezone.now
    )

    class Meta:
        ordering = ["-date", "task__name", "id"]
        unique_together = [
            ("task", "date")
        ]

    def __str__(self):
        return "{} - {} - {}".format(
            self.task.name,
            self.date,
            self.status
        )