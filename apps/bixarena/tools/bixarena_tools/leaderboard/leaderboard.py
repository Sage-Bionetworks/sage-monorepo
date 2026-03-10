"""Leaderboard management commands."""

import typer
from bixarena_leaderboard.db_helper import (
    get_db_connection,
    update_leaderboard_snapshot,
)
from bixarena_leaderboard.snapshot_generator import generate_snapshot
from rich.console import Console
from rich.table import Table

leaderboard_app = typer.Typer(
    help="Leaderboard operations",
    no_args_is_help=True,
)
console = Console()

# Snapshot subcommand group
snapshot_app = typer.Typer(
    help="Snapshot operations (add, list, get, delete)",
    no_args_is_help=True,
)
leaderboard_app.add_typer(snapshot_app, name="snapshot")


@snapshot_app.command("add")
def snapshot_add(
    id: str = typer.Option(
        "overall",
        "--id",
        help="Leaderboard ID to create snapshot for",
    ),
    num_bootstrap: int = typer.Option(
        100,
        "--num-bootstrap",
        help="Number of bootstrap iterations for confidence intervals",
    ),
    min_evals: int = typer.Option(
        0,
        "--min",
        help="Minimum evaluations per model to include in the leaderboard",
    ),
    significant: bool = typer.Option(
        False,
        "--significant",
        help=(
            "Rank by statistical significance (CI overlap). "
            "If False, rank by BT score only."
        ),
    ),
    dry_run: bool = typer.Option(
        False,
        "--dry-run",
        help="Compute and display results without writing to database",
    ),
):
    """
    Create a new leaderboard snapshot.

    This command computes Bradley-Terry rankings from battle evaluations
    and creates a new snapshot in the database.

    Example:
        uv run bixarena leaderboard snapshot add --id overall --num-bootstrap 1000
    """
    console.print("[bold blue]BixArena Leaderboard Snapshot Creation[/bold blue]")
    console.print("=" * 60)

    try:
        result = generate_snapshot(
            leaderboard_slug=id,
            num_bootstrap=num_bootstrap,
            min_evals=min_evals,
            significant=significant,
            dry_run=dry_run,
        )

        if dry_run:
            console.print(
                "\n[yellow]DRY RUN: Results computed only. "
                "Database not updated.[/yellow]"
            )
        else:
            console.print("\n[bold green]✓ Database updated successfully![/bold green]")
            console.print(f"  • Snapshot ID: {result['snapshot_id']}")
            console.print(f"  • Snapshot identifier: {result['snapshot_identifier']}")

        console.print(f"  • Entries: {result['entry_count']}")
        console.print(f"  • Evaluations used: {result['evaluation_count']}")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        raise typer.Exit(1) from e


@snapshot_app.command("list")
def snapshot_list(
    id: str | None = typer.Option(
        None,
        "--id",
        help="Filter by leaderboard ID (shows all if not specified)",
    ),
):
    """
    List all snapshots or snapshots for a specific leaderboard.

    Example:
        uv run bixarena leaderboard snapshot list
        uv run bixarena leaderboard snapshot list --id overall
    """
    console.print("[bold blue]BixArena Leaderboard Snapshots[/bold blue]")
    console.print("=" * 60)

    try:
        with get_db_connection() as conn, conn.cursor() as cur:
            if id:
                # Filter by specific leaderboard
                cur.execute(
                    """
                        SELECT
                            s.id,
                            s.snapshot_identifier,
                            s.description,
                            s.visibility,
                            s.created_at,
                            s.updated_at,
                            l.name as leaderboard_name,
                            l.slug as leaderboard_slug,
                            COUNT(e.id) as entry_count
                        FROM api.leaderboard_snapshot s
                        JOIN api.leaderboard l ON s.leaderboard_id = l.id
                        LEFT JOIN api.leaderboard_entry e ON s.id = e.snapshot_id
                        WHERE l.slug = %(slug)s
                        GROUP BY s.id, s.snapshot_identifier, s.description,
                                 s.visibility, s.created_at, s.updated_at,
                                 l.name, l.slug
                        ORDER BY s.created_at DESC
                        """,
                    {"slug": id},
                )
            else:
                # Get all snapshots
                cur.execute(
                    """
                        SELECT
                            s.id,
                            s.snapshot_identifier,
                            s.description,
                            s.visibility,
                            s.created_at,
                            s.updated_at,
                            l.name as leaderboard_name,
                            l.slug as leaderboard_slug,
                            COUNT(e.id) as entry_count
                        FROM api.leaderboard_snapshot s
                        JOIN api.leaderboard l ON s.leaderboard_id = l.id
                        LEFT JOIN api.leaderboard_entry e ON s.id = e.snapshot_id
                        GROUP BY s.id, s.snapshot_identifier, s.description,
                                 s.visibility, s.created_at, s.updated_at,
                                 l.name, l.slug
                        ORDER BY l.slug, s.created_at DESC
                        """
                )

            snapshots = cur.fetchall()

            if not snapshots:
                filter_msg = f" for leaderboard '{id}'" if id else ""
                console.print(f"\n[yellow]No snapshots found{filter_msg}.[/yellow]")
                return

            table = Table(title="Leaderboard Snapshots")
            table.add_column("Identifier", style="cyan")
            table.add_column("Leaderboard", style="magenta")
            table.add_column("Visibility", style="yellow")
            table.add_column("Entries", justify="right", style="green")
            table.add_column("Created", style="blue")
            table.add_column("Updated", style="blue")
            table.add_column("Description", style="dim")

            for snapshot in snapshots:
                table.add_row(
                    snapshot["snapshot_identifier"],
                    snapshot["leaderboard_slug"],
                    snapshot["visibility"],
                    str(snapshot["entry_count"]),
                    snapshot["created_at"].strftime("%Y-%m-%d %H:%M:%S"),
                    snapshot["updated_at"].strftime("%Y-%m-%d %H:%M:%S"),
                    snapshot["description"][:50] + "..."
                    if len(snapshot["description"]) > 50
                    else snapshot["description"],
                )

            console.print(f"\n[green]Found {len(snapshots)} snapshot(s)[/green]\n")
            console.print(table)

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        raise typer.Exit(1) from e


@snapshot_app.command("get")
def snapshot_get(
    identifier: str = typer.Argument(..., help="Snapshot identifier to retrieve"),
    limit: int = typer.Option(
        20,
        "--limit",
        help="Number of top entries to display",
    ),
):
    """
    Get details of a specific snapshot by identifier.

    Example:
        uv run bixarena leaderboard snapshot get snapshot_20251107_120000
    """
    console.print("[bold blue]BixArena Snapshot Details[/bold blue]")
    console.print("=" * 60)

    try:
        with get_db_connection() as conn, conn.cursor() as cur:
            # Get snapshot info
            cur.execute(
                """
                    SELECT
                        s.id,
                        s.snapshot_identifier,
                        s.description,
                        s.visibility,
                        s.created_at,
                        s.updated_at,
                        l.name as leaderboard_name,
                        l.slug as leaderboard_slug
                    FROM api.leaderboard_snapshot s
                    JOIN api.leaderboard l ON s.leaderboard_id = l.id
                    WHERE s.snapshot_identifier = %(identifier)s
                    """,
                {"identifier": identifier},
            )
            snapshot = cur.fetchone()

            if not snapshot:
                console.print(
                    f"\n[bold red]Snapshot '{identifier}' not found.[/bold red]"
                )
                raise typer.Exit(1)

            # Display snapshot info
            console.print("\n[bold]Snapshot Information:[/bold]")
            console.print(f"  Identifier: {snapshot['snapshot_identifier']}")
            console.print(
                f"  Leaderboard: {snapshot['leaderboard_name']} "
                f"({snapshot['leaderboard_slug']})"
            )
            console.print(f"  Visibility: {snapshot['visibility']}")
            console.print(f"  Created: {snapshot['created_at']}")
            console.print(f"  Updated: {snapshot['updated_at']}")
            console.print(f"  Description: {snapshot['description']}")

            # Get entries
            cur.execute(
                """
                    SELECT
                        m.name as model_name,
                        m.license,
                        e.rank,
                        e.bt_score,
                        e.vote_count,
                        e.bootstrap_q025,
                        e.bootstrap_q975
                    FROM api.leaderboard_entry e
                    JOIN api.model m ON e.model_id = m.id
                    WHERE e.snapshot_id = %(snapshot_id)s
                    ORDER BY e.rank, e.bt_score DESC
                    """,
                {"snapshot_id": snapshot["id"]},
            )
            entries = cur.fetchall()

            if not entries:
                console.print("\n[yellow]No entries found for this snapshot.[/yellow]")
                return

            # Display entries table
            table = Table(title=f"\nTop {min(limit, len(entries))} Models")
            table.add_column("Rank", justify="center", style="bold")
            table.add_column("Model", style="cyan")
            table.add_column("BT Score", justify="right", style="green")
            table.add_column("95% CI", justify="center", style="yellow")
            table.add_column("Evals", justify="center", style="blue")
            table.add_column("License", style="magenta")

            for entry in entries[:limit]:
                ci_str = (
                    f"[{entry['bootstrap_q025']:.1f}, {entry['bootstrap_q975']:.1f}]"
                )
                table.add_row(
                    str(entry["rank"]),
                    entry["model_name"],
                    f"{entry['bt_score']:.2f}",
                    ci_str,
                    str(entry["vote_count"]),
                    entry["license"],
                )

            if len(entries) > limit:
                console.print(f"\n[dim]... and {len(entries) - limit} more[/dim]")

            console.print(f"\n[green]Total entries: {len(entries)}[/green]")
            console.print(table)

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        raise typer.Exit(1) from e


@snapshot_app.command("delete")
def snapshot_delete(
    identifier: str = typer.Argument(..., help="Snapshot identifier to delete"),
    yes: bool = typer.Option(
        False,
        "--yes",
        "-y",
        help="Skip confirmation prompt",
    ),
):
    """
    Delete a snapshot by identifier.

    Example:
        uv run bixarena leaderboard snapshot delete snapshot_20251107_120000
    """
    console.print("[bold red]⚠ Snapshot Deletion Tool[/bold red]")
    console.print("=" * 60)

    try:
        with get_db_connection() as conn, conn.cursor() as cur:
            # Get snapshot info
            cur.execute(
                """
                    SELECT
                        s.id,
                        s.snapshot_identifier,
                        s.description,
                        s.created_at,
                        l.name as leaderboard_name,
                        COUNT(e.id) as entry_count
                    FROM api.leaderboard_snapshot s
                    JOIN api.leaderboard l ON s.leaderboard_id = l.id
                    LEFT JOIN api.leaderboard_entry e ON s.id = e.snapshot_id
                    WHERE s.snapshot_identifier = %(identifier)s
                    GROUP BY s.id, s.snapshot_identifier, s.description,
                             s.created_at, l.name
                    """,
                {"identifier": identifier},
            )
            snapshot = cur.fetchone()

            if not snapshot:
                console.print(
                    f"\n[bold red]Snapshot '{identifier}' not found.[/bold red]"
                )
                raise typer.Exit(1)

            # Display snapshot info
            console.print("\n[yellow]Snapshot to delete:[/yellow]")
            console.print(f"  • Identifier: {snapshot['snapshot_identifier']}")
            console.print(f"  • UUID: {snapshot['id']}")
            console.print(f"  • Leaderboard: {snapshot['leaderboard_name']}")
            console.print(f"  • Created: {snapshot['created_at']}")
            console.print(f"  • Description: {snapshot['description']}")
            console.print(f"  • Entries: {snapshot['entry_count']}")

            # Confirmation
            if not yes:
                console.print(
                    f"\n[bold red]WARNING: This will delete the snapshot and all "
                    f"{snapshot['entry_count']} associated entries![/bold red]"
                )
                confirm = typer.confirm("Are you sure?")
                if not confirm:
                    console.print("[cyan]Operation cancelled.[/cyan]")
                    return

            # Delete snapshot (CASCADE will delete entries)
            cur.execute(
                "DELETE FROM api.leaderboard_snapshot WHERE id = %(snapshot_id)s",
                {"snapshot_id": snapshot["id"]},
            )
            conn.commit()

            console.print("\n[green]✓ Deleted snapshot[/green]")
            console.print(f"[green]✓ Deleted {snapshot['entry_count']} entries[/green]")
            console.print("\n[bold green]Snapshot deleted successfully![/bold green]")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        raise typer.Exit(1) from e


@snapshot_app.command("update")
def snapshot_update(
    identifier: str = typer.Argument(..., help="Snapshot identifier to update"),
    visibility: str = typer.Option(
        None,
        "--visibility",
        help="New visibility status: 'public' or 'private'",
    ),
    description: str = typer.Option(
        None,
        "--description",
        help="New description for the snapshot",
    ),
):
    """Update snapshot properties (visibility, description)."""
    console.print("[bold blue]BixArena Snapshot Update[/bold blue]")
    console.print("=" * 60)

    # Build updates dict from provided options
    updates = {}
    if visibility is not None:
        if visibility not in ("public", "private"):
            console.print(
                f"[bold red]Error: Invalid visibility '{visibility}'. "
                "Must be 'public' or 'private'.[/bold red]"
            )
            raise typer.Exit(1)
        updates["visibility"] = visibility

    if description is not None:
        updates["description"] = description

    # Ensure at least one field is being updated
    if not updates:
        console.print(
            "[bold red]Error: No fields provided to update. Please specify "
            "at least one option (--visibility or --description).[/bold red]"
        )
        raise typer.Exit(1)

    try:
        with get_db_connection() as conn:
            # Update the snapshot
            updated_snapshot = update_leaderboard_snapshot(conn, identifier, **updates)

            if not updated_snapshot:
                console.print(
                    f"\n[bold red]Snapshot '{identifier}' not found.[/bold red]"
                )
                raise typer.Exit(1)

            # Show what was updated
            updated_fields = ", ".join(updates.keys())
            console.print("\n[bold green]✓ Snapshot updated successfully![/bold green]")
            console.print(f"[dim]Updated fields: {updated_fields}[/dim]\n")

            # Display updated snapshot in same format as list command
            table = Table(title="Updated Snapshot")
            table.add_column("Identifier", style="cyan")
            table.add_column("Leaderboard", style="magenta")
            table.add_column("Visibility", style="yellow")
            table.add_column("Entries", justify="right", style="green")
            table.add_column("Created", style="blue")
            table.add_column("Updated", style="blue")
            table.add_column("Description", style="dim")

            description = updated_snapshot["description"] or ""
            if len(description) > 50:
                description = description[:50] + "..."

            table.add_row(
                updated_snapshot["snapshot_identifier"],
                updated_snapshot["leaderboard_slug"],
                updated_snapshot["visibility"],
                str(updated_snapshot["entry_count"]),
                updated_snapshot["created_at"].strftime("%Y-%m-%d %H:%M:%S"),
                updated_snapshot["updated_at"].strftime("%Y-%m-%d %H:%M:%S"),
                description,
            )

            console.print(table)

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        raise typer.Exit(1) from e
