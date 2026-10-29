import decimal

import django.contrib.auth.models
import django.contrib.auth.validators
import django.core.validators
import django.db.models.deletion
import django.utils.timezone

from django.db import migrations, models


class Migration(migrations.Migration):
    initial = True

    dependencies = [
        (
            "auth",
            "0011_update_proxy_permissions"
        ),
    ]

    operations = [
        migrations.CreateModel(
            name="Organization",
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
                    models.CharField(
                        max_length=150,
                        unique=True
                    )
                ),
                (
                    "access_code_hash",
                    models.CharField(
                        max_length=128
                    )
                ),
                (
                    "created_at",
                    models.DateTimeField(
                        auto_now_add=True
                    )
                ),
            ],
            options={
                "ordering": ["name"]
            }
        ),

        migrations.CreateModel(
            name="User",
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
                    "password",
                    models.CharField(
                        max_length=128,
                        verbose_name="password"
                    )
                ),
                (
                    "last_login",
                    models.DateTimeField(
                        blank=True,
                        null=True,
                        verbose_name="last login"
                    )
                ),
                (
                    "is_superuser",
                    models.BooleanField(
                        default=False,
                        help_text=(
                            "Designates that this user "
                            "has all permissions without "
                            "explicitly assigning them."
                        ),
                        verbose_name="superuser status"
                    )
                ),
                (
                    "username",
                    models.CharField(
                        max_length=150,
                        unique=True,
                        validators=[
                            django.contrib.auth.validators.
                            UnicodeUsernameValidator()
                        ],
                        verbose_name="username"
                    )
                ),
                (
                    "first_name",
                    models.CharField(
                        blank=True,
                        max_length=150,
                        verbose_name="first name"
                    )
                ),
                (
                    "last_name",
                    models.CharField(
                        blank=True,
                        max_length=150,
                        verbose_name="last name"
                    )
                ),
                (
                    "email",
                    models.EmailField(
                        max_length=254,
                        unique=True
                    )
                ),
                (
                    "is_staff",
                    models.BooleanField(
                        default=False,
                        verbose_name="staff status"
                    )
                ),
                (
                    "is_active",
                    models.BooleanField(
                        default=True,
                        verbose_name="active"
                    )
                ),
                (
                    "date_joined",
                    models.DateTimeField(
                        default=django.utils.timezone.now,
                        verbose_name="date joined"
                    )
                ),
                (
                    "organization",
                    models.ForeignKey(
                        blank=True,
                        null=True,
                        on_delete=django.db.models.deletion.PROTECT,
                        related_name="members",
                        to="tracker.organization"
                    )
                ),
                (
                    "groups",
                    models.ManyToManyField(
                        blank=True,
                        related_name="user_set",
                        related_query_name="user",
                        to="auth.Group",
                        verbose_name="groups"
                    )
                ),
                (
                    "user_permissions",
                    models.ManyToManyField(
                        blank=True,
                        related_name="user_set",
                        related_query_name="user",
                        to="auth.Permission",
                        verbose_name="user permissions"
                    )
                ),
            ],
            options={
                "abstract": False
            },
            managers=[
                (
                    "objects",
                    django.contrib.auth.models.UserManager()
                )
            ]
        ),

        migrations.CreateModel(
            name="Bug",
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
                    "title",
                    models.CharField(
                        max_length=180
                    )
                ),
                (
                    "reproduction_steps",
                    models.TextField()
                ),
                (
                    "estimated_hours",
                    models.DecimalField(
                        decimal_places=2,
                        max_digits=7,
                        validators=[
                            django.core.validators.MinValueValidator(
                                decimal.Decimal("0.00")
                            )
                        ]
                    )
                ),
                (
                    "category",
                    models.CharField(
                        choices=[
                            ("functional", "Funcional"),
                            ("performance", "Desempenho"),
                            ("usability", "Usabilidade"),
                            ("compatibility", "Compatibilidade"),
                            ("security", "Segurança"),
                            ("other", "Outra"),
                        ],
                        max_length=20
                    )
                ),
                (
                    "severity",
                    models.CharField(
                        choices=[
                            ("low", "Baixa"),
                            ("medium", "Média"),
                            ("high", "Alta"),
                            ("critical", "Crítica"),
                        ],
                        max_length=20
                    )
                ),
                (
                    "active",
                    models.BooleanField(
                        default=True
                    )
                ),
                (
                    "created_at",
                    models.DateTimeField(
                        auto_now_add=True
                    )
                ),
                (
                    "updated_at",
                    models.DateTimeField(
                        auto_now=True
                    )
                ),
                (
                    "assignee",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.PROTECT,
                        related_name="assigned_bugs",
                        to="tracker.user"
                    )
                ),
                (
                    "created_by",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.PROTECT,
                        related_name="reported_bugs",
                        to="tracker.user"
                    )
                ),
                (
                    "organization",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="bugs",
                        to="tracker.organization"
                    )
                ),
            ],
            options={
                "ordering": ["-created_at"]
            }
        ),

        migrations.AddIndex(
            model_name="bug",
            index=models.Index(
                fields=["organization", "active"],
                name="bug_org_active_idx"
            )
        ),

        migrations.AddIndex(
            model_name="bug",
            index=models.Index(
                fields=["assignee", "active"],
                name="bug_user_active_idx"
            )
        ),
    ]