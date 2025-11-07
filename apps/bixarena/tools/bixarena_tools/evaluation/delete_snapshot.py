"""
Delete leaderboard snapshots from the database.

This script deletes a specific leaderboard snapshot and its associated entries.
Useful for removing incorrect or test snapshots.
"""

import argparse
import os
import sys

from rich.console import Console

from bixarena_tools.evaluation.db_helper import DatabaseConfig, get_db_connection

console = Console()


def get_snapshot_info(conn, snapshot_identifier: str) -> dict | None:
    """
    Get information about a snapshot.

    Args:
        conn: Database connection
        snapshot_identifier: Identifier of the snapshot
            (e.g., 'snapshot_20251107_014723')

    Returns:
        Dict with snapshot info or None if not found
    """
    with conn.cursor() as cur:
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
            WHERE s.snapshot_identifier = %(snapshot_identifier)s
            GROUP BY s.id, s.snapshot_identifier, s.description, s.created_at, l.name
            """,
            {"snapshot_identifier": snapshot_identifier},
        )
        result = cur.fetchone()
        return dict(result) if result else None


def delete_snapshot(conn, snapshot_uuid: str) -> int:
    """
    Delete a leaderboard snapshot and its entries.

    Args:
        conn: Database connection
        snapshot_uuid: UUID of the snapshot to delete

    Returns:
        Number of entries deleted
    """
    with conn.cursor() as cur:
        # Get entry count before deletion
        cur.execute(
            """
            SELECT COUNT(*)
            FROM api.leaderboard_entry
            WHERE snapshot_id = %(snapshot_uuid)s
            """,
            {"snapshot_uuid": snapshot_uuid},
        )
        entry_count = cur.fetchone()["count"]

        # Delete snapshot (CASCADE will delete entries)
        cur.execute(
            "DELETE FROM api.leaderboard_snapshot WHERE id = %(snapshot_uuid)s",
            {"snapshot_uuid": snapshot_uuid},
        )

        conn.commit()

        return entry_count


def main():
    """Main entry point for delete snapshot script."""
    parser = argparse.ArgumentParser(
        description="Delete a leaderboard snapshot from the database"
    )
    parser.add_argument(
        "--id",
        type=str,
        required=True,
        help="Snapshot identifier to delete (e.g., 'snapshot_20251107_014723')",
    )
    parser.add_argument(
        "--yes",
        "-y",
        action="store_true",
        help="Skip confirmation prompt",
    )

    args = parser.parse_args()

    # Create database config from env vars
    db_config = DatabaseConfig(
        host=os.getenv("POSTGRES_HOST"),
        port=int(os.getenv("POSTGRES_PORT", "5432")),
        database=os.getenv("POSTGRES_DB"),
        user=os.getenv("POSTGRES_USER"),
        password=os.getenv("POSTGRES_PASSWORD"),
    )

    try:
        console.print("[bold red]⚠ Snapshot Deletion Tool[/bold red]")
        console.print("=" * 60)

        with get_db_connection(db_config) as conn:
            # Get snapshot info by identifier
            snapshot_info = get_snapshot_info(conn, args.id)

            if not snapshot_info:
                console.print(
                    f"\n[bold red]Error: Snapshot '{args.id}' not found.[/bold red]"
                )
                sys.exit(1)

            # Display snapshot information
            console.print("\n[yellow]Snapshot to delete:[/yellow]")
            console.print(f"  • Identifier: {snapshot_info['snapshot_identifier']}")
            console.print(f"  • UUID: {snapshot_info['id']}")
            console.print(f"  • Leaderboard: {snapshot_info['leaderboard_name']}")
            console.print(f"  • Created: {snapshot_info['created_at']}")
            console.print(f"  • Description: {snapshot_info['description']}")
            console.print(f"  • Entries: {snapshot_info['entry_count']}")

            # Confirmation prompt
            if not args.yes:
                console.print(
                    "\n[bold red]WARNING: This will delete the snapshot and all "
                    f"{snapshot_info['entry_count']} associated entries![/bold red]"
                )
                response = console.input("[yellow]Are you sure? (yes/no): [/yellow]")
                if response.lower() not in ["yes", "y"]:
                    console.print("[cyan]Operation cancelled.[/cyan]")
                    return

            # Perform deletion using the UUID
            entry_count = delete_snapshot(conn, snapshot_info["id"])

            console.print("\n[green]✓ Deleted snapshot[/green]")
            console.print(f"[green]✓ Deleted {entry_count} entries[/green]")
            console.print("\n[bold green]Snapshot deleted successfully![/bold green]")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)


if __name__ == "__main__":
    main()
