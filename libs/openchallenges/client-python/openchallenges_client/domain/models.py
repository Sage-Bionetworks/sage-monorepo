"""Domain models exposed by the simplified client."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import date


@dataclass(frozen=True)
class ChallengeSummary:
    id: int
    slug: str
    name: str
    status: str | None
    platform_id: int | None
    platform_name: str | None
    start_date: date | None
    end_date: date | None
    # Derived / enrichment fields (computed post-construction in service)
    duration_days: int | None = None
    is_active: bool | None = None


@dataclass(frozen=True)
class OrganizationSummary:
    id: int
    name: str
    acronym: str | None
    website_url: str | None
