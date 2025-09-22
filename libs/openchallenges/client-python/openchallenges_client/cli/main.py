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
    available_platform_columns,
    filter_columns,
    print_challenge_columns,
    print_org_columns,
    print_platform_columns,
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


## Legacy backcompat helper removed (pre-release cleanup).


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
platforms_app = typer.Typer(help="Challenge platform-related operations")
config_app = typer.Typer(help="Configuration & diagnostics")

app.add_typer(challenges_app, name="challenges")
app.add_typer(orgs_app, name="orgs")
app.add_typer(platforms_app, name="platforms")
app.add_typer(config_app, name="config")


@challenges_app.command("list")
def list_challenges(
    ctx: typer.Context,
    limit: int | None = typer.Option(None, help="Override limit for this command"),
    status: list[str] | None = STATUS_OPTION,
    search: str | None = typer.Option(
        None, "--search", help="Search terms for challenges"
    ),
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
        invoke_kwargs = {
            "limit": limit,
            "status": status_list,
            "metrics": metrics,
        }
        if search is not None:
            invoke_kwargs["search"] = search
        items = list(client.list_challenges(**invoke_kwargs))
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
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@challenges_app.command("stream")
def stream_challenges(
    ctx: typer.Context,
    status: list[str] | None = STATUS_OPTION,
    limit: int = typer.Option(0, help="Optional cap on streamed items (0 = all)"),
    search: str | None = typer.Option(
        None, "--search", help="Search terms for challenges"
    ),
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
        invoke_kwargs = {
            "status": status_list,
            "metrics": metrics,
        }
        if search is not None:
            invoke_kwargs["search"] = search
        iterator = client.iter_all_challenges(**invoke_kwargs)
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
        iterator = client.iter_all_organizations(search=search, metrics=metrics)
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


@platforms_app.command("list")
def list_platforms(
    ctx: typer.Context,
    limit: int | None = typer.Option(None, help="Override limit for this command"),
    search: str | None = typer.Option(None, "--search", help="Search terms"),
    output: str | None = typer.Option(
        None, help="Override output format for this command"
    ),
    wide: bool = typer.Option(
        False,
        "--wide",
        help="Include slug and avatar_key columns",
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
            print_platform_columns(wide)
            raise typer.Exit(0)
        invoke_kwargs = {"limit": limit, "metrics": metrics}
        if search is not None:
            invoke_kwargs["search"] = search
        items = list(client.list_platforms(**invoke_kwargs))
        rows = [
            {
                "id": p.id,
                "slug": getattr(p, "slug", None),
                "name": p.name,
                "website_url": p.website_url,
                "avatar_key": p.avatar_key,
            }
            for p in items
        ]
        # Trim/reorder to default visible columns when user did not request an
        # explicit column subset. Default (non-wide) => id,name,website_url.
        # Wide => id,slug,name,website_url,avatar_key (ordering from helper).
        if columns is None:
            order = available_platform_columns(wide)
            rows = [{k: r.get(k) for k in order} for r in rows]
        rows = filter_columns(rows, columns, kind="platform", wide=wide)
        _emit(rows, output or base_output, title="Platforms")
        if verbose:
            typer.echo(
                (
                    "[stats] platforms "
                    f"emitted={metrics.emitted} "
                    f"skipped={metrics.skipped} retries={metrics.retries}"
                ),
                err=True,
            )
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@platforms_app.command("create")
def create_platform(
    ctx: typer.Context,
    slug: str | None = typer.Option(
        None,
        "--slug",
        help=(
            "Platform slug (lowercase, hyphen separated). If omitted and "
            "--interactive is set you'll be prompted."
        ),
    ),
    name: str | None = typer.Option(
        None,
        "--name",
        help="Display name. If omitted with --interactive you'll be prompted.",
    ),
    avatar_key: str | None = typer.Option(
        None,
        "--avatar-key",
        help=(
            "Avatar key (e.g., logo/codalab.jpg). If omitted with --interactive "
            "you'll be prompted."
        ),
    ),
    website_url: str | None = typer.Option(
        None,
        "--website-url",
        help="Website URL. If omitted with --interactive you'll be prompted.",
    ),
    interactive: bool = typer.Option(
        False,
        "--interactive",
        help="Enable interactive prompts for any missing fields.",
    ),
    output: str = typer.Option(
        "table",
        help="Output format for created resource (table|json|yaml|ndjson)",
    ),
):
    """Create a challenge platform.

    Non-interactive: supply all required options.
    Interactive: add --interactive; any missing values will be prompted.
    """
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"] if ctx.obj.get("output") else output

    def _prompt_if_missing(label: str, current: str | None) -> str | None:
        if current or not interactive:
            return current
        return typer.prompt(label)

    slug = _prompt_if_missing("Slug", slug)
    name = _prompt_if_missing("Name", name)
    avatar_key = _prompt_if_missing("Avatar key", avatar_key)
    website_url = _prompt_if_missing("Website URL", website_url)

    missing = [
        k
        for k, v in {
            "slug": slug,
            "name": name,
            "avatar_key": avatar_key,
            "website_url": website_url,
        }.items()
        if not v
    ]
    if missing:
        typer.echo(
            "Missing required fields: "
            + ", ".join(missing)
            + " (provide options or use --interactive)",
            err=True,
        )
        raise typer.Exit(1)
    try:
        created = client.create_platform(
            slug=str(slug),
            name=str(name),
            avatar_key=str(avatar_key),
            website_url=str(website_url),
        )
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)
        return

    row = {
        "id": getattr(created, "id", None),
        "slug": getattr(created, "slug", None),
        "name": getattr(created, "name", None),
        "avatar_key": getattr(created, "avatar_key", None),
        "website_url": getattr(created, "website_url", None),
    }
    # Reuse existing emit path
    if output == "ndjson":
        to_ndjson([row])
    elif output == "table":
        to_table([row], title="Created Platform")
    elif output in ("json", "yaml"):
        _emit([row], output, title="Created Platform")
    else:
        _emit([row], base_output, title="Created Platform")


@platforms_app.command("delete")
def delete_platform(
    ctx: typer.Context,
    platform_id: int = typer.Argument(..., help="Numeric platform id to delete"),
    yes: bool = typer.Option(
        False,
        "-y",
        "--yes",
        help="Skip confirmation prompt",
    ),
):
    """Delete a platform by id (irreversible)."""
    client: OpenChallengesClient = ctx.obj["client"]
    if not yes:
        confirm = typer.confirm(
            f"Delete platform id {platform_id}? This action cannot be undone.",
            abort=True,
        )
        if not confirm:  # pragma: no cover - typer.confirm abort handles
            raise typer.Exit(1)
    try:
        client.delete_platform(platform_id=platform_id)
        typer.echo(f"Deleted platform {platform_id}")
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)


@platforms_app.command("update")
def update_platform(
    ctx: typer.Context,
    platform_id: int = typer.Argument(..., help="Numeric platform id to update"),
    slug: str | None = typer.Option(
        None,
        "--slug",
        help=(
            "New slug (must match ^[a-z0-9]+(?:-[a-z0-9]+)*$). If omitted it is kept"
            " unless --interactive prompts it."
        ),
    ),
    name: str | None = typer.Option(
        None,
        "--name",
        help="New display name.",
    ),
    avatar_key: str | None = typer.Option(
        None,
        "--avatar-key",
        help="New avatar key (e.g., logo/codalab.jpg).",
    ),
    website_url: str | None = typer.Option(
        None,
        "--website-url",
        help="New website URL. Provide an empty string to clear.",
    ),
    interactive: bool = typer.Option(
        False,
        "--interactive",
        help="Prompt for any missing fields using current values as defaults.",
    ),
    yes: bool = typer.Option(
        False,
        "-y",
        "--yes",
        help="Skip diff confirmation before applying update.",
    ),
    output: str = typer.Option(
        "table",
        help="Output format for updated resource (table|json|yaml|ndjson)",
    ),
):
    """Update a challenge platform by id.

    Fetches current values, applies flag / interactive overrides, shows a diff,
    then submits a full PUT request if changes exist.
    """
    client: OpenChallengesClient = ctx.obj["client"]
    base_output = ctx.obj["output"] if ctx.obj.get("output") else output
    try:
        current = client.get_platform(platform_id=platform_id)
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)
        return

    def _extract(obj, *names):
        for n in names:
            if hasattr(obj, n):
                val = getattr(obj, n)
                if val is not None:
                    return val
        if isinstance(obj, dict):  # pragma: no cover - safety
            for n in names:
                if n in obj and obj[n] is not None:
                    return obj[n]
        return None

    # Extract current field values with fallbacks (some generated models or
    # API variants may omit slug in certain responses; attempt aliases).
    current_data = {
        "slug": _extract(current, "slug", "slug_", "slugValue"),
        "name": _extract(current, "name", "display_name"),
        "avatar_key": _extract(current, "avatar_key", "avatarKey"),
        "website_url": _extract(current, "website_url", "websiteUrl"),
    }

    # Fallback: some single-resource responses may omit slug; attempt to
    # derive from the platform summaries list (iter_all_platforms) if needed.
    if current_data["slug"] is None:
        try:  # pragma: no cover - network variability
            pid_str = str(platform_id)
            for p in client.iter_all_platforms():  # type: ignore[attr-defined]
                pid_candidate = getattr(p, "id", None)
                if pid_candidate is None:
                    continue
                if str(pid_candidate) == pid_str:
                    current_data["slug"] = getattr(p, "slug", None) or getattr(
                        p, "slug_", None
                    )
                    break
        except Exception:  # pragma: no cover - non-fatal
            pass
    # Note: if slug remains None it will prompt empty and user may supply new slug.

    def _prompt(label: str, key: str, existing: str | None) -> str | None:
        if not interactive:
            return None
        provided_map = {
            "slug": slug,
            "name": name,
            "avatar_key": avatar_key,
            "website_url": website_url,
        }
        if provided_map[key] is not None:
            return provided_map[key]

        existing_val = existing or ""

        # Prefer prompt_toolkit for true editable default buffer.
        try:  # pragma: no cover - library may not be installed in CI
            from prompt_toolkit import prompt as pt_prompt

            return pt_prompt(f"{label}: ", default=existing_val)
        except Exception:
            # Fallback: typer.prompt (user must edit default manually)
            return typer.prompt(label, default=existing_val)

    if interactive:
        slug = _prompt("Slug", "slug", current_data["slug"])  # type: ignore[assignment]
        name = _prompt("Name", "name", current_data["name"])  # type: ignore[assignment]
        avatar_key = _prompt("Avatar key", "avatar_key", current_data["avatar_key"])  # type: ignore[assignment]
        website_url = _prompt("Website URL", "website_url", current_data["website_url"])  # type: ignore[assignment]
        if website_url == "-":
            website_url = None  # type: ignore[assignment]
    # Blank responses are now considered explicit blanks (they will
    # overwrite existing values if supplied).

    # Merge logic
    new_data = dict(current_data)
    if slug is not None:
        new_data["slug"] = slug
    if name is not None:
        new_data["name"] = name
    if avatar_key is not None:
        new_data["avatar_key"] = avatar_key
    if website_url is not None:
        # Treat explicit empty string as a request to clear (set None)
        new_data["website_url"] = website_url or None

    # Validate slug format if changed
    import re as _re

    if new_data["slug"] != current_data["slug"]:
        pat = _re.compile(r"^[a-z0-9]+(?:-[a-z0-9]+)*$")
        if not pat.match(str(new_data["slug"])):
            typer.echo(
                "Invalid slug format. Must match ^[a-z0-9]+(?:-[a-z0-9]+)*$",
                err=True,
            )
            raise typer.Exit(1)

    # Determine changed fields
    changed = {
        k: (current_data[k], new_data[k])
        for k in ["slug", "name", "avatar_key", "website_url"]
        if current_data[k] != new_data[k]
    }
    if not changed:
        typer.echo("No changes to apply.")
        raise typer.Exit(0)

    # Show diff unless suppressed
    if not yes:
        # Local imports keep optional rich dependency impact minimal when
        # command run with non-table output and --yes.
        from rich.console import Console
        from rich.table import Table

        table = Table(title=f"Platform {platform_id} changes")
        table.add_column("Field")
        table.add_column("Current", style="dim")
        table.add_column("New", style="green")
        for field, (old, new) in changed.items():
            table.add_row(field, str(old), str(new))
        Console().print(table)
        if not typer.confirm("Apply these changes?", abort=True):  # pragma: no cover
            raise typer.Exit(1)

    try:
        updated = client.update_platform(
            platform_id=platform_id,
            slug=str(new_data["slug"]),
            name=str(new_data["name"]),
            avatar_key=str(new_data["avatar_key"]),
            website_url=new_data["website_url"],
        )
    except oc_errors.OpenChallengesError as e:  # pragma: no cover
        _handle_error(e)
        return

    row = {
        "id": getattr(updated, "id", None),
        "slug": getattr(updated, "slug", None),
        "name": getattr(updated, "name", None),
        "avatar_key": getattr(updated, "avatar_key", None),
        "website_url": getattr(updated, "website_url", None),
    }
    if output == "ndjson":
        to_ndjson([row])
    elif output == "table":
        to_table([row], title="Updated Platform")
    elif output in ("json", "yaml"):
        _emit([row], output, title="Updated Platform")
    else:
        _emit([row], base_output, title="Updated Platform")


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
