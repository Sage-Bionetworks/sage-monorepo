"""
Clean battle evaluations from the database.

This script deletes all battles and their associated evaluations from the database.
Useful for starting fresh with new simulation data.
"""

import argparse
import os
import sys

from rich.console import Console

from bixarena_tools.evaluation.db_helper import DatabaseConfig, get_db_connection

console = Console()


def clean_evaluations(conn) -> tuple[int, int]:
    """
    Delete all battle evaluations and battles from the database.

    Args:
        conn: Database connection

    Returns:
        Tuple of (evaluation_count, battle_count) deleted
    """
    with conn.cursor() as cur:
        # Delete evaluations first (due to foreign key constraint)
        cur.execute("DELETE FROM api.battle_evaluation")
        eval_count = cur.rowcount

        # Delete battles
        cur.execute("DELETE FROM api.battle")
        battle_count = cur.rowcount

        conn.commit()

        return eval_count, battle_count


def main():
    """Main entry point for clean script."""
    parser = argparse.ArgumentParser(
        description="Clean all battles and evaluations from the database"
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
        console.print("[bold red]⚠ Database Cleanup Tool[/bold red]")
        console.print("=" * 60)

        with get_db_connection(db_config) as conn:
            # Get current counts
            with conn.cursor() as cur:
                cur.execute("SELECT COUNT(*) FROM api.battle_evaluation")
                eval_count = cur.fetchone()["count"]

                cur.execute("SELECT COUNT(*) FROM api.battle")
                battle_count = cur.fetchone()["count"]

            console.print("\n[yellow]Current database state:[/yellow]")
            console.print(f"  • Battles: {battle_count}")
            console.print(f"  • Evaluations: {eval_count}")

            if battle_count == 0 and eval_count == 0:
                console.print("\n[green]Database is already clean![/green]")
                return

            # Confirmation prompt
            if not args.yes:
                console.print(
                    "\n[bold red]WARNING: This will delete ALL battles and "
                    "evaluations![/bold red]"
                )
                response = console.input("[yellow]Are you sure? (yes/no): [/yellow]")
                if response.lower() not in ["yes", "y"]:
                    console.print("[cyan]Operation cancelled.[/cyan]")
                    return

            # Perform cleanup
            console.print("\n[cyan]Cleaning database...[/cyan]")
            deleted_evals, deleted_battles = clean_evaluations(conn)

            console.print(f"\n[green]✓ Deleted {deleted_evals} evaluations[/green]")
            console.print(f"[green]✓ Deleted {deleted_battles} battles[/green]")
            console.print("\n[bold green]Database cleaned successfully![/bold green]")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)


if __name__ == "__main__":
    main()
