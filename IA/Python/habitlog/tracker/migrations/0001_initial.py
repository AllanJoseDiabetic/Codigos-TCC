from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion
import django.utils.timezone


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(
            settings.AUTH_USER_MODEL
        ),
    ]

    operations = [
        migrations.CreateModel(
            name="Profile",
            fields=[
                (
                    "id",
                    models.AutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID"
                    )
                ),
                (
                    "timezone",
                    models.CharField(
                        default="UTC",
                        max_length=64
                    )
                ),
                (
                    "user",
                    models.OneToOneField(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="habit_profile",
                        to=settings.AUTH_USER_MODEL
                    )
                ),
            ],
        ),

        migrations.CreateModel(
            name="Category",
            fields=[
                (
                    "id",
                    models.AutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID"
                    )
                ),
                (
                    "name",
                    models.CharField(max_length=80)
                ),
                (
                    "owner",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="habit_categories",
                        to=settings.AUTH_USER_MODEL
                    )
                ),
            ],
            options={
                "ordering": ["name", "id"],
                "unique_together": {("owner", "name")},
            },
        ),

        migrations.CreateModel(
            name="Task",
            fields=[
                (
                    "id",
                    models.AutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID"
                    )
                ),
                (
                    "name",
                    models.CharField(max_length=120)
                ),
                (
                    "active",
                    models.BooleanField(default=True)
                ),
                (
                    "weekdays",
                    models.CharField(max_length=13)
                ),
                (
                    "created_on",
                    models.DateField()
                ),
                (
                    "category",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="tasks",
                        to="tracker.category"
                    )
                ),
                (
                    "owner",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="habit_tasks",
                        to=settings.AUTH_USER_MODEL
                    )
                ),
            ],
            options={
                "ordering": [
                    "category__name",
                    "name",
                    "id"
                ],
            },
        ),

        migrations.CreateModel(
            name="TaskPeriod",
            fields=[
                (
                    "id",
                    models.AutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID"
                    )
                ),
                (
                    "start_date",
                    models.DateField()
                ),
                (
                    "end_date",
                    models.DateField(
                        blank=True,
                        null=True
                    )
                ),
                (
                    "weekdays",
                    models.CharField(max_length=13)
                ),
                (
                    "task",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="periods",
                        to="tracker.task"
                    )
                ),
            ],
            options={
                "ordering": ["start_date", "id"],
            },
        ),

        migrations.CreateModel(
            name="Log",
            fields=[
                (
                    "id",
                    models.AutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID"
                    )
                ),
                (
                    "date",
                    models.DateField()
                ),
                (
                    "status",
                    models.CharField(
                        choices=[
                            ("Complete", "Complete"),
                            ("Incomplete", "Incomplete"),
                            ("Ignored", "Ignored"),
                            ("Pending", "Pending")
                        ],
                        default="Pending",
                        max_length=10
                    )
                ),
                (
                    "note",
                    models.TextField(
                        blank=True,
                        default=""
                    )
                ),
                (
                    "created_at",
                    models.DateTimeField(
                        default=django.utils.timezone.now
                    )
                ),
                (
                    "task",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="logs",
                        to="tracker.task"
                    )
                ),
            ],
            options={
                "ordering": [
                    "-date",
                    "task__name",
                    "id"
                ],
                "unique_together": {("task", "date")},
            },
        ),
    ]