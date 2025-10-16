"""BixArena Tools - Bradley-Terry evaluation and ranking tools."""

__version__ = "1.0.0"

from .bt_eval import (
    compute_bt_scores_and_bootstrap,
    compute_vote_counts,
    convert_votes_to_battles,
    format_leaderboard_output,
    update_vote_counts_in_leaderboard,
)
from .mock_bt_data import (
    SimConfig,
    print_simulation_summary,
    simulate_battles,
)

__all__ = [
    "compute_bt_scores_and_bootstrap",
    "format_leaderboard_output",
    "convert_votes_to_battles",
    "compute_vote_counts",
    "update_vote_counts_in_leaderboard",
    "SimConfig",
    "simulate_battles",
    "print_simulation_summary",
]
