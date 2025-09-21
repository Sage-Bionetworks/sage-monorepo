"""Typer CLI entrypoint."""

from __future__ import annotations

from collections.abc import Iterable
from typing import Any

import typer

from ..config.loader import DEFAULT_LIMIT, load_config
from ..core import errors as oc_errors
from ..core.client import OpenChallengesClient
from ..core.metrics import MetricsCollector
from ..output.formatters import to_ndjson, to_table
from ..output.registry import get_format, list_formats, register_default_formatters
from ._shared_columns import (
    available_challenge_columns,
    available_org_columns,
    filter_columns,
    print_challenge_columns,
    print_org_columns,
)

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
    verbose: bool = typer.Option(
        False,
        "--verbose",
        help="Emit summary metrics (emitted / skipped / retries) to stderr",
    ),
):
    ctx.obj = {
        "client": _client(api_url, api_key, limit),
        "output": output,
        "verbose": verbose,
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
    wide: bool = typer.Option(
        False,
        "--wide",
        help="Include start/end dates and duration_days",
    ),
    columns: str | None = typer.Option(
        None,
        "--columns",
        help=(
            "Comma-separated list of columns to include (order preserved). "
            "Use 'help' to list available columns. Example: --columns id,name,status"
        ),
    ),
):
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
    verbose: bool = ctx.obj.get("verbose", False)
    metrics = MetricsCollector()
    status_list = list(status) if status else None
    # Normalize statuses (case-insensitive) if provided
    if status_list:
        status_list = [s.upper().strip() for s in status_list if s.strip()]
    try:
        if columns == "help":
            print_challenge_columns(wide=wide)
            raise typer.Exit(0)
        # Collect items; wrap iteration to count emitted.
        try:
            items = list(
                client.list_challenges(limit=limit, status=status_list, metrics=metrics)
            )
        except TypeError as e:
            if "metrics" not in str(e):
                raise
            # Fallback for legacy/stub clients without metrics support
            items = list(
                client.list_challenges(limit=limit, status=status_list)  # type: ignore[arg-type]
            )
        rows = [_challenge_row(c, wide=wide) for c in items]
        rows = filter_columns(rows, columns, kind="challenge", wide=wide)
        _emit(rows, output or base_output, title="Challenges")
        if verbose:
            typer.echo(
                (
                    "[stats] challenges "
                    f"emitted={metrics.emitted} "
                    f"skipped={metrics.skipped} retries={metrics.retries}"
                ),
                err=True,
            )
    except oc_errors.OpenChallengesError as e:  # pragma: no cover (CLI path)
        _handle_error(e)


@challenges_app.command("stream")
def stream_challenges(
    ctx: typer.Context,
    status: list[str] | None = STATUS_OPTION,
    limit: int = typer.Option(0, help="Optional cap on streamed items (0 = all)"),
    output: str = typer.Option(None, help="Override output format for this command"),
    wide: bool = typer.Option(
        False,
        "--wide",
        help="Include start/end dates and duration_days",
    ),
    columns: str | None = typer.Option(
        None,
        "--columns",
        help=(
            "Comma-separated list of columns to include (order preserved). "
            "Use 'help' to list available columns. Example: --columns id,name,status"
        ),
    ),
):
    """Stream all challenges (or until optional cap)."""
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
    verbose: bool = ctx.obj.get("verbose", False)
    metrics = MetricsCollector()
    status_list = [s.upper().strip() for s in status] if status else None
    try:
        if columns == "help":
            print_challenge_columns(wide=wide)
            raise typer.Exit(0)
        rows = []
        try:
            iterator = client.iter_all_challenges(status=status_list, metrics=metrics)
        except TypeError as e:
            if "metrics" not in str(e):
                raise
            iterator = client.iter_all_challenges(  # type: ignore[arg-type]
                status=status_list
            )
        for count, c in enumerate(iterator, start=1):
            rows.append(_challenge_row(c, wide=wide))
            if limit and count >= limit:
                break
        rows = filter_columns(rows, columns, kind="challenge", wide=wide)
        _emit(rows, output or base_output, title="Challenges (stream)")
        if verbose:
            typer.echo(
                (
                    "[stats] challenges "
                    f"emitted={metrics.emitted} "
                    f"skipped={metrics.skipped} retries={metrics.retries}"
                ),
                err=True,
            )
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
    columns: str | None = typer.Option(
        None,
        "--columns",
        help=(
            "Comma-separated list of columns to include (order preserved). "
            "Use 'help' to list available columns. Example: --columns id,name"
        ),
    ),
):
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
    verbose: bool = ctx.obj.get("verbose", False)
    metrics = MetricsCollector()
    try:
        if columns == "help":
            print_org_columns()
            raise typer.Exit(0)
        try:
            items = list(
                client.list_organizations(limit=limit, search=search, metrics=metrics)
            )
        except TypeError as e:
            if "metrics" not in str(e):
                raise
            items = list(
                client.list_organizations(limit=limit, search=search)  # type: ignore[arg-type]
            )
        rows = [_org_row(o) for o in items]
        rows = filter_columns(rows, columns, kind="org", wide=False)
        fmt = output or base_output
        _emit(rows, fmt, title="Organizations")
        if verbose:
            typer.echo(
                (
                    f"[stats] orgs emitted={metrics.emitted} "
                    f"skipped={metrics.skipped} retries={metrics.retries}"
                ),
                err=True,
            )
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@orgs_app.command("stream")
def stream_orgs(
    ctx: typer.Context,
    search: str | None = ORG_SEARCH_OPTION,
    limit: int = typer.Option(0, help="Optional cap on streamed items (0 = all)"),
    output: str = typer.Option(None, help="Override output format for this command"),
    columns: str | None = typer.Option(
        None,
        "--columns",
        help=(
            "Comma-separated list of columns to include (order preserved). "
            "Use 'help' to list available columns. Example: --columns id,name"
        ),
    ),
):
    """Stream all organizations (or until optional cap)."""
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"]
    verbose: bool = ctx.obj.get("verbose", False)
    metrics = MetricsCollector()
    try:
        if columns == "help":
            print_org_columns()
            raise typer.Exit(0)
        rows = []
        try:
            iterator = client.iter_all_organizations(search=search, metrics=metrics)
        except TypeError as e:
            if "metrics" not in str(e):
                raise
            iterator = client.iter_all_organizations(  # type: ignore[arg-type]
                search=search
            )
        for count, o in enumerate(iterator, start=1):
            rows.append(_org_row(o))
            if limit and count >= limit:
                break
        rows = filter_columns(rows, columns, kind="org", wide=False)
        fmt = output or base_output
        _emit(rows, fmt, title="Organizations (stream)")
        if verbose:
            typer.echo(
                (
                    f"[stats] orgs emitted={metrics.emitted} "
                    f"skipped={metrics.skipped} retries={metrics.retries}"
                ),
                err=True,
            )
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


def _challenge_row(c, *, wide: bool) -> dict[str, Any]:
    row: dict[str, Any] = {
        "id": c.id,
        "name": c.name,
        "status": c.status,
        "platform": c.platform_name or "",
    }
    if wide:
        row.update(
            {
                "start_date": c.start_date,
                "end_date": c.end_date,
                "duration_days": c.duration_days,
            }
        )
    return row


def _org_row(o) -> dict[str, Any]:
    return {
        "id": o.id,
        "name": o.name,
        "short_name": o.short_name or "",
        "website_url": o.website_url,
    }


_CHALLENGE_BASE_COLS = available_challenge_columns(False)
_CHALLENGE_WIDE_EXTRA = [
    c for c in available_challenge_columns(True) if c not in _CHALLENGE_BASE_COLS
]
_ORG_COLS = available_org_columns()


def _emit(rows: Iterable[dict[str, Any]], fmt: str, *, title: str) -> None:
    if fmt == "ndjson":
        # Streaming-friendly; do not materialize unnecessarily
        to_ndjson(rows)
        return
    seq = list(rows)
    if fmt == "table":
        to_table(seq, title=title)
        return
    formatter = get_format(fmt)
    if not formatter:
        available = ",".join(list_formats() + ["ndjson"])
        typer.echo(
            f"Unknown output format '{fmt}'. Available: {available}",
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
