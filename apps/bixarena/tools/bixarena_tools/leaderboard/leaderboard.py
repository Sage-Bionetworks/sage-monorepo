"""Leaderboard management commands."""

from datetime import UTC, datetime

import typer
from rich.console import Console
from rich.table import Table

from bixarena_tools.leaderboard.db_helper import (
    fetch_active_models,
    fetch_battle_evaluations,
    fetch_leaderboard_ids,
    fetch_leaderboards,
    get_db_connection,
    insert_leaderboard_entries,
    insert_leaderboard_snapshot,
)
from bixarena_tools.leaderboard.rank_battle import compute_leaderboard_bt

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


def display_leaderboard_summary(
    leaderboard_name: str,
    entries: list[dict],
    evaluation_count: int,
    limit: int = 20,
) -> None:
    """Display leaderboard results in a formatted table."""
    console.print(f"\n[bold cyan]═══ {leaderboard_name} ═══[/bold cyan]")
    console.print(f"Total evaluations: {evaluation_count}")
    console.print(f"Models ranked: {len(entries)}\n")

    if not entries:
        console.print("[yellow]No entries to display[/yellow]")
        return

    # Sort by rank first, then by BT score descending
    sorted_entries = sorted(entries, key=lambda x: (x["rank"], -x["btScore"]))

    table = Table(title=f"{leaderboard_name} Rankings")
    table.add_column("Rank", justify="center", style="bold")
    table.add_column("Model", style="cyan")
    table.add_column("BT Score", justify="right", style="green")
    table.add_column("95% CI", justify="center", style="yellow")
    table.add_column("Evals", justify="center", style="blue")
    table.add_column("License", style="magenta")

    for entry in sorted_entries[:limit]:
        ci_str = f"[{entry['bootstrapQ025']:.1f}, {entry['bootstrapQ975']:.1f}]"
        table.add_row(
            str(entry["rank"]),
            entry["modelName"],
            f"{entry['btScore']:.2f}",
            ci_str,
            str(entry["voteCount"]),
            entry["license"],
        )

    if len(sorted_entries) > limit:
        console.print(f"\n[dim]... and {len(sorted_entries) - limit} more[/dim]")

    console.print(table)


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
        with get_db_connection() as conn:
            # Fetch data
            console.print("\n[cyan]Fetching data from database...[/cyan]")

            leaderboards = fetch_leaderboards(conn)
            console.print(f"  ✓ Found {len(leaderboards)} leaderboards")

            # Validate leaderboard ID
            available_ids = fetch_leaderboard_ids(conn)
            if id not in available_ids:
                console.print(
                    f"[bold red]Error: Leaderboard '{id}' not found.[/bold red]"
                )
                console.print(f"Available leaderboards: {', '.join(available_ids)}")
                raise typer.Exit(1)

            all_models = fetch_active_models(conn)
            console.print(f"  ✓ Found {len(all_models)} active models")

            all_evaluations = fetch_battle_evaluations(conn)
            console.print(f"  ✓ Found {len(all_evaluations)} battle evaluations")

            if len(all_evaluations) == 0:
                console.print(
                    "[bold red]Error: No evaluations found in database.[/bold red]"
                )
                raise typer.Exit(1)

            # Create model lookup by name
            models_by_name = {model["name"]: model for model in all_models}

            # Find target leaderboard
            target_leaderboard = None
            for leaderboard in leaderboards:
                if leaderboard["slug"] == id:
                    target_leaderboard = leaderboard
                    break

            if not target_leaderboard:
                console.print(
                    f"[bold red]Error: Leaderboard '{id}' not found.[/bold red]"
                )
                raise typer.Exit(1)

            console.print(
                f"\n[bold]Processing leaderboard: {target_leaderboard['name']}[/bold]"
            )
            console.print(f"  • Using {len(all_evaluations)} total evaluations")

            # Compute rankings
            console.print(
                f"  • Computing rankings with {num_bootstrap} bootstrap iterations..."
            )
            leaderboard_entries = compute_leaderboard_bt(
                evaluations=all_evaluations,
                models=models_by_name,
                num_bootstrap=num_bootstrap,
            )

            # Filter by minimum evaluations if threshold is set
            if min_evals > 0:
                skipped_models = []
                filtered_entries = []

                for entry in leaderboard_entries:
                    if entry["voteCount"] >= min_evals:
                        filtered_entries.append(entry)
                    else:
                        skipped_models.append((entry["modelName"], entry["voteCount"]))

                leaderboard_entries = filtered_entries

                if skipped_models:
                    console.print(
                        f"\n[yellow]⚠ Warning: Skipped {len(skipped_models)} "
                        f"model(s) with < {min_evals} evaluations:[/yellow]"
                    )
                    for model_name, count in skipped_models[:5]:
                        console.print(f"  • {model_name}: {count} evaluations")
                    if len(skipped_models) > 5:
                        console.print(f"  • ... and {len(skipped_models) - 5} more")

            # Display results
            display_leaderboard_summary(
                target_leaderboard["name"],
                leaderboard_entries,
                len(all_evaluations),
            )

            console.print(
                "\n[bold green]✓ Leaderboard computation completed![/bold green]"
            )

            # Insert into database if not dry run
            if not dry_run:
                timestamp = datetime.now(UTC).strftime("%Y%m%d_%H%M%S")
                snapshot_identifier = f"snapshot_{timestamp}"
                description = (
                    f"Leaderboard snapshot with {len(all_evaluations)} "
                    f"evaluations and {len(leaderboard_entries)} ranked models"
                )

                # Insert snapshot
                snapshot_id = insert_leaderboard_snapshot(
                    conn,
                    target_leaderboard["id"],
                    snapshot_identifier,
                    description,
                )

                # Insert entries
                entry_count = insert_leaderboard_entries(
                    conn,
                    target_leaderboard["id"],
                    snapshot_id,
                    leaderboard_entries,
                )

                console.print(
                    "\n[bold green]✓ Database updated successfully![/bold green]"
                )
                console.print(f"  • Snapshot ID: {snapshot_id}")
                console.print(f"  • Snapshot identifier: {snapshot_identifier}")
                console.print(f"  • Entries inserted: {entry_count}")
            else:
                console.print(
                    "\n[yellow]DRY RUN: Results displayed only. "
                    "Database not updated.[/yellow]"
                )

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
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
                            s.created_at,
                            l.name as leaderboard_name,
                            l.slug as leaderboard_slug,
                            COUNT(e.id) as entry_count
                        FROM api.leaderboard_snapshot s
                        JOIN api.leaderboard l ON s.leaderboard_id = l.id
                        LEFT JOIN api.leaderboard_entry e ON s.id = e.snapshot_id
                        WHERE l.slug = %(slug)s
                        GROUP BY s.id, s.snapshot_identifier, s.description,
                                 s.created_at, l.name, l.slug
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
                            s.created_at,
                            l.name as leaderboard_name,
                            l.slug as leaderboard_slug,
                            COUNT(e.id) as entry_count
                        FROM api.leaderboard_snapshot s
                        JOIN api.leaderboard l ON s.leaderboard_id = l.id
                        LEFT JOIN api.leaderboard_entry e ON s.id = e.snapshot_id
                        GROUP BY s.id, s.snapshot_identifier, s.description,
                                 s.created_at, l.name, l.slug
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
            table.add_column("Entries", justify="right", style="green")
            table.add_column("Created", style="blue")
            table.add_column("Description", style="dim")

            for snapshot in snapshots:
                table.add_row(
                    snapshot["snapshot_identifier"],
                    snapshot["leaderboard_slug"],
                    str(snapshot["entry_count"]),
                    snapshot["created_at"].strftime("%Y-%m-%d %H:%M:%S"),
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
                        s.created_at,
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
            console.print(f"  Created: {snapshot['created_at']}")
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
