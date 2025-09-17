"""Typer CLI entrypoint."""

from __future__ import annotations

from collections.abc import Iterable
from typing import Any, Optional

import typer

from ..core.client import OpenChallengesClient
from ..core import errors as oc_errors
from ..output.formatters import to_table, to_json

# Module-level option objects to satisfy lint rule against function calls in defaults
# Typer typing quirk: declare the type for a multi option where None means not provided
STATUS_OPTION: list[str] | None = typer.Option(  # type: ignore[assignment]
    None, "--status", help="Filter by status (repeatable)", multiple=True
)
ORG_SEARCH_OPTION = typer.Option(
    None, "--search", help="Search terms for organizations"
)

app = typer.Typer(help="OpenChallenges unified Python client & CLI")


def _client(
    api_url: str | None, api_key: str | None, limit: int
) -> OpenChallengesClient:
    return OpenChallengesClient(api_key=api_key, api_url=api_url, default_limit=limit)


@app.callback()
def global_options(
    ctx: typer.Context,
    api_url: str = typer.Option(None, help="API base URL (env OC_API_URL)"),
    api_key: str = typer.Option(None, help="API key (env OC_API_KEY)"),
    limit: int = typer.Option(5, help="Default max items"),
    output: str = typer.Option("table", help="Output format: table|json"),
):
    ctx.obj = {
        "client": _client(api_url, api_key, limit),
        "output": output,
    }


challenges_app = typer.Typer(help="Challenge-related operations")
orgs_app = typer.Typer(help="Organization-related operations")
app.add_typer(challenges_app, name="challenges")
app.add_typer(orgs_app, name="orgs")


@challenges_app.command("list")
def list_challenges(
    ctx: typer.Context,
    limit: int | None = typer.Option(None, help="Override limit for this command"),
    status: list[str] | None = STATUS_OPTION,
):
    client: OpenChallengesClient = ctx.obj["client"]
    output = ctx.obj["output"]
    status_list = list(status) if status else None
    # Normalize statuses (case-insensitive) if provided
    if status_list:
        status_list = [s.upper().strip() for s in status_list if s.strip()]
    try:
        items = list(client.list_challenges(limit=limit, status=status_list))
        rows = [
            {
                "id": c.id,
                "slug": c.slug,
                "name": c.name,
                "status": c.status,
                "platform_id": c.platform_id,
            }
            for c in items
        ]
        _emit(rows, output, title="Challenges")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover (CLI path)
        _handle_error(e)


@orgs_app.command("list")
def list_orgs(
    ctx: typer.Context,
    limit: int | None = typer.Option(None, help="Override limit for this command"),
    search: str | None = ORG_SEARCH_OPTION,
):
    client: OpenChallengesClient = ctx.obj["client"]
    output = ctx.obj["output"]
    try:
        items = list(client.list_organizations(limit=limit, search=search))
        rows = [
            {
                "id": o.id,
                "name": o.name,
                "acronym": o.acronym,
                "website_url": o.website_url,
            }
            for o in items
        ]
        _emit(rows, output, title="Organizations")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


# ---------------------------------------------------------------------------
# Helpers


def _emit(rows: Iterable[dict[str, Any]], fmt: str, *, title: str) -> None:
    if fmt == "json":
        to_json(list(rows))
    else:
        to_table(list(rows), title=title)


_EXIT_CODES: dict[type[Exception], int] = {
    oc_errors.AuthError: 2,
    oc_errors.NotFoundError: 3,
    oc_errors.RateLimitError: 4,
    oc_errors.ClientError: 5,
    oc_errors.ServerError: 6,
    oc_errors.NetworkError: 7,
}


def _handle_error(err: oc_errors.OpenChallengesError) -> None:  # pragma: no cover
    code = 1
    for klass, exit_code in _EXIT_CODES.items():
        if isinstance(err, klass):
            code = exit_code
            break
    typer.echo(f"Error: {err}", err=True)
    raise typer.Exit(code)


if __name__ == "__main__":  # pragma: no cover
    app()
