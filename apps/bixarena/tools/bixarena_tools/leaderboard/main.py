"""BixArena CLI main entry point."""

import typer
from dotenv import load_dotenv
from rich.console import Console

from .evaluation import evaluation_app
from .leaderboard import leaderboard_app

# Load environment variables from .env file
load_dotenv()

app = typer.Typer(
    help="BixArena CLI - Tools for managing leaderboards and evaluations",
    no_args_is_help=True,
)
console = Console()


@app.callback()
def main():
    """BixArena command-line interface."""
    pass


app.add_typer(leaderboard_app, name="leaderboard")
app.add_typer(evaluation_app, name="evaluation")


if __name__ == "__main__":
    app()
