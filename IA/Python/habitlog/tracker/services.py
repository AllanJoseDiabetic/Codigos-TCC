from datetime import timedelta

import pytz

from django.db import transaction
from django.db.models import Q
from django.utils import timezone

from .models import (
    Profile,
    TaskPeriod,
    Log
)


def get_profile(user):
    profile, created = Profile.objects.get_or_create(
        user=user,
        defaults={
            "timezone": "UTC"
        }
    )

    return profile


def get_user_today(user):
    profile = get_profile(user)

    try:
        selected_timezone = pytz.timezone(
            profile.timezone
        )

    except pytz.UnknownTimeZoneError:
        selected_timezone = pytz.UTC

    current_time = timezone.now().astimezone(
        selected_timezone
    )

    return current_time.date()


def can_edit_date(user, selected_date):
    today = get_user_today(user)

    oldest_allowed = today - timedelta(
        days=7
    )

    return (
        oldest_allowed <= selected_date <= today
    )


def expire_old_pending(user, today=None):
    if today is None:
        today = get_user_today(user)

    cutoff = today - timedelta(days=7)

    return Log.objects.filter(
        task__owner=user,
        date__lt=cutoff,
        status=Log.PENDING
    ).update(
        status=Log.INCOMPLETE
    )


def synchronize_logs(user, start_date, end_date):
    today = get_user_today(user)

    if start_date > end_date:
        return

    final_date = min(
        end_date,
        today
    )

    if start_date > final_date:
        expire_old_pending(
            user,
            today
        )
        return

    periods = TaskPeriod.objects.filter(
        task__owner=user,
        start_date__lte=final_date
    ).filter(
        Q(end_date__isnull=True)
        | Q(end_date__gte=start_date)
    ).select_related(
        "task"
    )

    pending_creation = []
    scheduled_dates = set()

    for period in periods:
        period_start = max(
            start_date,
            period.start_date
        )

        period_end = final_date

        if period.end_date is not None:
            period_end = min(
                period_end,
                period.end_date
            )

        if period_start > period_end:
            continue

        allowed_weekdays = (
            period.weekday_numbers()
        )

        current_date = period_start

        while current_date <= period_end:
            if current_date.weekday() in allowed_weekdays:
                key = (
                    period.task_id,
                    current_date
                )

                if key not in scheduled_dates:
                    scheduled_dates.add(key)

                    pending_creation.append(
                        Log(
                            task_id=period.task_id,
                            date=current_date,
                            status=Log.PENDING,
                            note=""
                        )
                    )

            current_date += timedelta(days=1)

    with transaction.atomic():
        if pending_creation:
            Log.objects.bulk_create(
                pending_creation,
                ignore_conflicts=True
            )

        expire_old_pending(
            user,
            today
        )


def update_task_periods(
    task,
    previous_active,
    previous_weekdays,
    today
):
    schedule_changed = (
        previous_weekdays != task.weekdays
    )

    active_changed = (
        previous_active != task.active
    )

    if not schedule_changed and not active_changed:
        return

    if previous_active:
        current_period = TaskPeriod.objects.filter(
            task=task,
            end_date__isnull=True
        ).order_by(
            "-start_date",
            "-id"
        ).first()

        if current_period is not None:
            if current_period.start_date >= today:
                current_period.delete()

            else:
                current_period.end_date = (
                    today - timedelta(days=1)
                )

                current_period.save(
                    update_fields=["end_date"]
                )

    if task.active:
        TaskPeriod.objects.create(
            task=task,
            start_date=today,
            weekdays=task.weekdays
        )


def month_before(year, month):
    if month == 1:
        return year - 1, 12

    return year, month - 1


def month_after(year, month):
    if month == 12:
        return year + 1, 1

    return year, month + 1