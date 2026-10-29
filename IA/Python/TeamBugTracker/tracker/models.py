from decimal import Decimal

from django.conf import settings
from django.contrib.auth.models import AbstractUser
from django.core.exceptions import ValidationError
from django.core.validators import MinValueValidator
from django.db import models


class Organization(models.Model):
    name = models.CharField(
        max_length=150,
        unique=True
    )

    access_code_hash = models.CharField(
        max_length=128
    )

    created_at = models.DateTimeField(
        auto_now_add=True
    )

    class Meta:
        ordering = ["name"]

    def __str__(self):
        return self.name


class User(AbstractUser):
    email = models.EmailField(
        unique=True
    )

    organization = models.ForeignKey(
        Organization,
        on_delete=models.PROTECT,
        related_name="members",
        null=True,
        blank=True
    )

    def __str__(self):
        return self.username


class Bug(models.Model):
    FUNCTIONAL = "functional"
    PERFORMANCE = "performance"
    USABILITY = "usability"
    COMPATIBILITY = "compatibility"
    SECURITY = "security"
    OTHER = "other"

    CATEGORY_CHOICES = [
        (FUNCTIONAL, "Funcional"),
        (PERFORMANCE, "Desempenho"),
        (USABILITY, "Usabilidade"),
        (COMPATIBILITY, "Compatibilidade"),
        (SECURITY, "Segurança"),
        (OTHER, "Outra"),
    ]

    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    CRITICAL = "critical"

    SEVERITY_CHOICES = [
        (LOW, "Baixa"),
        (MEDIUM, "Média"),
        (HIGH, "Alta"),
        (CRITICAL, "Crítica"),
    ]

    title = models.CharField(
        max_length=180
    )

    reproduction_steps = models.TextField()

    estimated_hours = models.DecimalField(
        max_digits=7,
        decimal_places=2,
        validators=[
            MinValueValidator(Decimal("0.00"))
        ]
    )

    assignee = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.PROTECT,
        related_name="assigned_bugs"
    )

    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.PROTECT,
        related_name="reported_bugs"
    )

    organization = models.ForeignKey(
        Organization,
        on_delete=models.CASCADE,
        related_name="bugs"
    )

    category = models.CharField(
        max_length=20,
        choices=CATEGORY_CHOICES
    )

    severity = models.CharField(
        max_length=20,
        choices=SEVERITY_CHOICES
    )

    active = models.BooleanField(
        default=True
    )

    created_at = models.DateTimeField(
        auto_now_add=True
    )

    updated_at = models.DateTimeField(
        auto_now=True
    )

    class Meta:
        ordering = ["-created_at"]

        indexes = [
            models.Index(
                fields=["organization", "active"],
                name="bug_org_active_idx"
            ),
            models.Index(
                fields=["assignee", "active"],
                name="bug_user_active_idx"
            ),
        ]

    def clean(self):
        super().clean()

        if (
            self.organization_id is not None
            and self.assignee_id is not None
        ):
            if (
                self.assignee.organization_id
                != self.organization_id
            ):
                raise ValidationError({
                    "assignee": (
                        "O responsável deve pertencer "
                        "à mesma organização do defeito."
                    )
                })

    def __str__(self):
        return self.title