"""Typer CLI entrypoint."""

from __future__ import annotations

from collections.abc import Iterable
from typing import Any

import typer

from ..config.loader import DEFAULT_LIMIT, load_config
from ..core import errors as oc_errors
from ..core.client import OpenChallengesClient
from ..output.formatters import to_table
from ..output.registry import get_format, register_default_formatters

# Module-level option object to satisfy lint rule against function calls in defaults.
# Typer infers repeatable option from list[str] annotation (no 'multiple' kw in >=0.12).
STATUS_OPTION: list[str] | None = typer.Option(  # type: ignore[assignment]
    None,
    "--status",
    help="Filter by status (repeatable)",
)
ORG_SEARCH_OPTION = typer.Option(
    None, "--search", help="Search terms for organizations"
)

app = typer.Typer(help="OpenChallenges unified Python client & CLI")
register_default_formatters()


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
    output: str = typer.Option("table", help="Output format: table|json|yaml"),
):
    ctx.obj = {
        "client": _client(api_url, api_key, limit),
        "output": output,
    }


challenges_app = typer.Typer(help="Challenge-related operations")
orgs_app = typer.Typer(help="Organization-related operations")
config_app = typer.Typer(help="Configuration & diagnostics")

app.add_typer(challenges_app, name="challenges")
app.add_typer(orgs_app, name="orgs")
app.add_typer(config_app, name="config")


@challenges_app.command("list")
def list_challenges(
    ctx: typer.Context,
    limit: int | None = typer.Option(None, help="Override limit for this command"),
    status: list[str] | None = STATUS_OPTION,
    output: str | None = typer.Option(
        None, help="Override output format for this command"
    ),
):
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
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
        _emit(rows, output or base_output, title="Challenges")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover (CLI path)
        _handle_error(e)


@challenges_app.command("stream")
def stream_challenges(
    ctx: typer.Context,
    status: list[str] | None = STATUS_OPTION,
    limit: int = typer.Option(0, help="Optional cap on streamed items (0 = all)"),
    output: str = typer.Option(None, help="Override output format for this command"),
):
    """Stream all challenges (or until optional cap)."""
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
    status_list = [s.upper().strip() for s in status] if status else None
    try:
        rows = []
        for count, c in enumerate(
            client.iter_all_challenges(status=status_list), start=1
        ):
            rows.append(
                {
                    "id": c.id,
                    "slug": c.slug,
                    "name": c.name,
                    "status": c.status,
                    "platform_id": c.platform_id,
                }
            )
            if limit and count >= limit:
                break
        _emit(rows, output or base_output, title="Challenges (stream)")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@orgs_app.command("list")
def list_orgs(
    ctx: typer.Context,
    limit: int | None = typer.Option(None, help="Override limit for this command"),
    search: str | None = ORG_SEARCH_OPTION,
    output: str | None = typer.Option(
        None, help="Override output format for this command"
    ),
):
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
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
        fmt = output or base_output
        _emit(rows, fmt, title="Organizations")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@orgs_app.command("stream")
def stream_orgs(
    ctx: typer.Context,
    search: str | None = ORG_SEARCH_OPTION,
    limit: int = typer.Option(0, help="Optional cap on streamed items (0 = all)"),
    output: str = typer.Option(None, help="Override output format for this command"),
):
    """Stream all organizations (or until optional cap)."""
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
    try:
        rows = []
        for count, o in enumerate(
            client.iter_all_organizations(search=search), start=1
        ):
            rows.append(
                {
                    "id": o.id,
                    "name": o.name,
                    "acronym": o.acronym,
                    "website_url": o.website_url,
                }
            )
            if limit and count >= limit:
                break
        fmt = output or base_output
        _emit(rows, fmt, title="Organizations (stream)")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@config_app.command("show")
def show_config(
    ctx: typer.Context,
    output: str = typer.Option(None, help="Override output format for this command"),
):
    """Show the resolved configuration values and their precedence sources."""
    # Re-run load_config with source tracking using current global overrides.
    # client instance not required for diagnostics; retained context ensures
    # global options parsed.
    # The facade stores the config internally; to show sources we must reload.
    # We cannot introspect sources from existing client (kept simple), so we
    # reconstruct using the same override args captured in ctx.params earlier.
    # Retrieve potential overrides from the Typer context parameters if present.
    params = ctx.parent.params if ctx.parent else {}
    api_url = params.get("api_url")  # type: ignore[assignment]
    api_key = params.get("api_key")  # type: ignore[assignment]
    limit = params.get("limit", DEFAULT_LIMIT)  # type: ignore[assignment]
    resolved = load_config(
        override_api_key=api_key,
        override_api_url=api_url,
        default_limit=limit,
        with_sources=True,
    )
    src = resolved.sources or {}
    rows = [
        {
            "key": "api_url",
            "value": resolved.api_url,
            "source": src.get("api_url", "unknown"),
        },
        {
            "key": "api_key",
            "value": "***" if resolved.api_key else None,
            "source": src.get("api_key", "unknown"),
        },
        {
            "key": "default_limit",
            "value": resolved.default_limit,
            "source": src.get("default_limit", "unknown"),
        },
        {
            "key": "retries",
            "value": resolved.retries,
            "source": src.get("retries", "unknown"),
        },
    ]
    base_output = ctx.obj["output"]
    _emit(rows, output or base_output, title="Configuration")


# ---------------------------------------------------------------------------
# Helpers


def _emit(rows: Iterable[dict[str, Any]], fmt: str, *, title: str) -> None:
    seq = list(rows)
    # Table needs title; registry funcs ignore unknown extras, so handle here.
    if fmt == "table":
        to_table(seq, title=title)
        return
    formatter = get_format(fmt)
    if not formatter:
        typer.echo(
            f"Unknown output format '{fmt}'. Available: table,json,yaml",
            err=True,
        )
        raise typer.Exit(1)
    formatter(seq)


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
