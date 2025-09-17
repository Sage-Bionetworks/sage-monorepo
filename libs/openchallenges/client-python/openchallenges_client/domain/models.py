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
    start_date: date | None
    end_date: date | None


@dataclass(frozen=True)
class OrganizationSummary:
    id: int
    name: str
    acronym: str | None
    website_url: str | None
