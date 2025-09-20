"""OpenChallenges organization CRUD demo.

Run:
    uv run python openchallenges_tools/manage_organization.py

Auth: Set OC_API_KEY (API key). Falls back to a demo key.
Config vars:
    OC_API_URL  (default http://localhost:8082/api/v1)
    OC_API_KEY

Demonstrates:
  - Create organization
  - Get organization (by login)
  - Update organization (diff summary before update)
  - Delete organization

Set OC_SKIP_UPDATE_MODS=1 to send an update with only required name
for testing validation of optional fields.
"""

from __future__ import annotations

import os
import time
from typing import Any

import openchallenges_api_client
from openchallenges_api_client.api.organization_api import OrganizationApi
from openchallenges_api_client.models.organization import Organization
from openchallenges_api_client.models.organization_create_request import (
    OrganizationCreateRequest,
)
from openchallenges_api_client.models.organization_update_request import (
    OrganizationUpdateRequest,
)
from openchallenges_api_client.rest import ApiException

OC_API_URL = os.getenv("OC_API_URL", "http://localhost:8082/api/v1")
OC_API_KEY = os.getenv("OC_API_KEY", "oc_dev_admin1.admin_secret_abcd1234efgh5678")


def build_configuration() -> openchallenges_api_client.Configuration:
    cfg = openchallenges_api_client.Configuration(host=OC_API_URL)
    if OC_API_KEY:
        cfg.api_key["apiKey"] = OC_API_KEY
    return cfg


def pretty(title: str, org: Organization | str) -> None:
    print(f"\n=== {title} ===")
    if isinstance(org, Organization):
        print(f"Login: {org.login}")
        print(f"Name: {org.name}")
        print(f"Description: {org.description}")
        print(f"Website URL: {org.website_url}")
        print(f"Avatar Key: {org.avatar_key}")
        print(f"Acronym: {org.acronym}")
        print(f"Created: {org.created_at}")
        print(f"Updated: {org.updated_at}")
    else:
        print(org)


def _fail(msg: str):
    raise SystemExit(msg)


def create_org(api_client) -> Organization:
    api = OrganizationApi(api_client)
    suffix = int(time.time())
    # login must match regex ^[a-z0-9]+(?:-[a-z0-9]+)*$
    login = f"demo-org-{suffix}"
    req = OrganizationCreateRequest(
        login=login,
        name=f"Demo Organization {suffix}",
        description="Demo organization created by manage_organization.py.",
        websiteUrl="https://example.org/org",
        acronym="DEMO",
    )
    try:
        org = api.create_organization(req)
    except ApiException as e:
        _fail(f"Failed to create organization: {e}")
    pretty("Created organization", org)
    return org


def get_org(api_client, login: str) -> Organization:
    api = OrganizationApi(api_client)
    try:
        org = api.get_organization(login)
    except ApiException as e:
        _fail(f"Failed to get organization {login}: {e}")
    pretty("Fetched organization", org)
    return org


def update_org(api_client, org: Organization) -> Organization:
    api = OrganizationApi(api_client)
    skip_mods = bool(os.getenv("OC_SKIP_UPDATE_MODS", "").strip())

    new_name = org.name  # keeping name stable like challenge script
    if skip_mods:
        new_description = org.description
        new_website = org.website_url
        new_acronym = org.acronym
        new_avatar = org.avatar_key
    else:
        base_desc = org.description or "Demo organization description"
        new_description = base_desc + " (Updated)"
        # force a website change even if one existed
        new_website = (
            (org.website_url.rstrip("/")) + "-updated"
            if org.website_url
            else "https://example.org/org-updated"
        )
        # update acronym (ensure <=10 chars)
        new_acronym = (
            (org.acronym + "U") if (org.acronym and len(org.acronym) < 9) else "DMOUPD"
        )
        new_avatar = org.avatar_key or "demo-avatar-key"

    diffs: list[tuple[str, Any, Any]] = []

    def record(field: str, old, new):
        if old != new:
            diffs.append((field, old, new))

    record("description", org.description, new_description)
    record("websiteUrl", org.website_url, new_website)
    record("acronym", org.acronym, new_acronym)
    record("avatarKey", org.avatar_key, new_avatar)

    if diffs:
        print("\nPlanned update changes:")
        for field, old, new in diffs:
            print(f"  - {field}: {old!r} -> {new!r}")
    else:
        print("\nNo org field changes (still sending update request)")

    req = OrganizationUpdateRequest(
        name=new_name,
        description=new_description,
        websiteUrl=new_website,
        acronym=new_acronym,
        avatarKey=new_avatar,
    )

    try:
        updated = api.update_organization(org.login, req)
    except ApiException as e:
        print("\n--- Update Error Details ---")
        print(f"Status: {getattr(e, 'status', None)}")
        print(f"Reason: {getattr(e, 'reason', None)}")
        if getattr(e, "headers", None):
            print(f"Headers: {e.headers}")
        if getattr(e, "body", None):
            print(f"Body: {e.body}")
        if getattr(e, "data", None):
            print(f"Data: {e.data}")
        print("Update payload:")
        try:
            print(req.to_json())
        except Exception:  # noqa: BLE001
            print(repr(req))
        print("--- End Update Error Details ---\n")
        _fail(f"Failed to update organization {org.login}: {e}")
    pretty("Updated organization", updated)
    return updated


def delete_org(api_client, login: str):
    api = OrganizationApi(api_client)
    try:
        api.delete_organization(login)
    except ApiException as e:
        _fail(f"\nFailed to delete organization {login}: {e}")
    pretty("Deleted organization", f"Organization {login} deleted")


def demo_crud_flow():
    cfg = build_configuration()
    with openchallenges_api_client.ApiClient(cfg) as api_client:
        org = create_org(api_client)
        fetched = get_org(api_client, org.login)
        updated = update_org(api_client, fetched)
        # final fetch confirm
        get_org(api_client, updated.login)
        delete_org(api_client, updated.login)
        print("\nOrganization CRUD demo complete.")


if __name__ == "__main__":
    demo_crud_flow()
