"""
Bradley-Terry evaluation using FastChat's existing implementation.
Adapted for BixArena data model and vote structure.
"""

import logging

import pandas as pd

# Import our standalone Bradley-Terry implementation (based on FastChat)
from .bradley_terry import compute_bootstrap_bt, compute_bt

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def convert_votes_to_battles(votes: list[dict]) -> pd.DataFrame:
    """
    Convert BixArena vote format to FastChat battle format.

    Args:
        votes: List of vote dictionaries with keys:
               - 'model_a': str - name of first model
               - 'model_b': str - name of second model
               - 'preference': str - one of 'model_a', 'model_b', 'tie'

    Returns:
        DataFrame with columns ['model_a', 'model_b', 'winner'] for FastChat
    """
    logger.info(f"Converting {len(votes)} votes to battles format")

    battles_data = []
    for vote in votes:
        # Map BixArena vote preferences to FastChat winner format
        if vote["preference"] == "model_a":
            winner = "model_a"
        elif vote["preference"] == "model_b":
            winner = "model_b"
        elif vote["preference"] == "tie":
            winner = "tie"
        else:
            logger.warning(f"Unknown preference: {vote['preference']}, skipping vote")
            continue

        battles_data.append(
            {"model_a": vote["model_a"], "model_b": vote["model_b"], "winner": winner}
        )

    if not battles_data:
        logger.error("No valid battles found after processing votes")
        return pd.DataFrame()

    battles_df = pd.DataFrame(battles_data)
    logger.info(f"Created battles DataFrame with {len(battles_df)} battles")
    return battles_df


def compute_bt_scores_and_bootstrap(
    votes: list[dict], num_bootstrap: int = 1000
) -> tuple[pd.DataFrame, dict[str, tuple[float, float]]]:
    """
    Compute Bradley-Terry scores with bootstrap confidence intervals.

    Args:
        votes: List of vote dictionaries (BixArena format)
        num_bootstrap: Number of bootstrap samples

    Returns:
        Tuple of (bt_results_df, confidence_intervals)
        - bt_results_df: DataFrame with columns ['model', 'bt_score', 'rank',
          'bootstrap_q025', 'bootstrap_q975']
        - confidence_intervals: Dict mapping model -> (lower_bound, upper_bound)
    """
    logger.info(
        f"Computing BT scores for {len(votes)} votes with {num_bootstrap} samples"
    )

    # Convert votes to battles format
    battles_df = convert_votes_to_battles(votes)
    if battles_df.empty:
        return pd.DataFrame(), {}

    # Compute base BT scores using our standalone implementation
    try:
        logger.info("Computing BT scores using standalone implementation")
        bt_scores = compute_bt(battles_df)

        if bt_scores.empty:
            logger.error("compute_bt returned empty scores")
            return pd.DataFrame(), {}

        logger.info(f"Computed BT scores for {len(bt_scores)} models")

    except Exception as e:
        logger.error(f"Error computing BT scores: {e}")
        return pd.DataFrame(), {}

    # Compute bootstrap confidence intervals
    confidence_intervals = {}
    try:
        logger.info("Computing bootstrap confidence intervals")

        # Our compute_bootstrap_bt expects DataFrame and returns DataFrame
        bootstrap_results = compute_bootstrap_bt(
            battles=battles_df, num_round=num_bootstrap, num_cpu=1
        )

        # Extract confidence intervals from bootstrap results
        # bootstrap_results is DataFrame with models as columns
        if not bootstrap_results.empty:
            for model in bt_scores.index:
                if model in bootstrap_results.columns:
                    # Calculate 2.5% and 97.5% percentiles for 95% CI
                    model_scores = bootstrap_results[model]
                    ci_lower = model_scores.quantile(0.025)
                    ci_upper = model_scores.quantile(0.975)
                    confidence_intervals[model] = (ci_lower, ci_upper)
                else:
                    # Fallback for missing models
                    score = bt_scores[model]
                    margin = score * 0.1
                    confidence_intervals[model] = (score - margin, score + margin)

        logger.info(
            f"Computed bootstrap intervals for {len(confidence_intervals)} models"
        )

    except Exception as e:
        logger.warning(f"Error computing bootstrap confidence intervals: {e}")
        logger.info("Using fallback confidence intervals")

        # Fallback: compute simple confidence intervals based on BT scores
        for model, score in bt_scores.items():
            margin = score * 0.1  # 10% margin as simple fallback
            confidence_intervals[model] = (score - margin, score + margin)

    # Create results DataFrame
    results_data = []
    for rank, (model, score) in enumerate(bt_scores.items(), 1):
        ci_lower, ci_upper = confidence_intervals.get(model, (score, score))
        results_data.append(
            {
                "model": model,
                "bt_score": float(score),
                "rank": rank,
                "bootstrap_q025": float(ci_lower),
                "bootstrap_q975": float(ci_upper),
            }
        )

    results_df = pd.DataFrame(results_data)
    logger.info(f"Created results DataFrame with {len(results_df)} models")

    return results_df, confidence_intervals


def format_leaderboard_output(
    bt_results: pd.DataFrame,
    confidence_intervals: dict[str, tuple[float, float]] | None = None,
    include_ci: bool = True,
) -> pd.DataFrame:
    """
    Format BT results into leaderboard format matching BixArena data model.

    Args:
        bt_results: DataFrame with BT results
        confidence_intervals: Bootstrap confidence intervals (optional, can be None)
        include_ci: Whether to include confidence interval columns

    Returns:
        DataFrame formatted for leaderboard display
    """
    if bt_results.empty:
        logger.warning("Empty BT results for formatting")
        return pd.DataFrame()

    # Create leaderboard format matching BixArena API structure
    leaderboard_data = []

    for _, row in bt_results.iterrows():
        model = row["model"]
        bt_score = row["bt_score"]
        rank = row["rank"]

        entry = {
            "rank": int(rank),
            "model_name": model,
            "bt_score": float(bt_score),
            "vote_count": 0,  # Placeholder - would need actual vote counts from data
        }

        if include_ci:
            if "bootstrap_q025" in row and "bootstrap_q975" in row:
                # Use bootstrap confidence intervals from results
                ci_lower = row["bootstrap_q025"]
                ci_upper = row["bootstrap_q975"]
                entry["bootstrap_q025"] = float(ci_lower)
                entry["bootstrap_q975"] = float(ci_upper)
                entry["ci_95"] = f"[{ci_lower:.1f}, {ci_upper:.1f}]"
            elif confidence_intervals and model in confidence_intervals:
                # Use confidence intervals from parameter
                ci_lower, ci_upper = confidence_intervals[model]
                entry["bootstrap_q025"] = float(ci_lower)
                entry["bootstrap_q975"] = float(ci_upper)
                entry["ci_95"] = f"[{ci_lower:.1f}, {ci_upper:.1f}]"
            else:
                # No confidence intervals available
                entry["bootstrap_q025"] = float(bt_score)
                entry["bootstrap_q975"] = float(bt_score)
                entry["ci_95"] = f"[{bt_score:.1f}, {bt_score:.1f}]"

        leaderboard_data.append(entry)

    leaderboard_df = pd.DataFrame(leaderboard_data)

    logger.info(f"Formatted leaderboard with {len(leaderboard_df)} entries")
    return leaderboard_df


def compute_vote_counts(votes: list[dict]) -> dict[str, int]:
    """
    Compute vote counts for each model from the votes data.

    Args:
        votes: List of vote dictionaries

    Returns:
        Dict mapping model name to vote count
    """
    vote_counts = {}

    for vote in votes:
        model_a = vote["model_a"]
        model_b = vote["model_b"]

        # Count votes for each model
        vote_counts[model_a] = vote_counts.get(model_a, 0) + 1
        vote_counts[model_b] = vote_counts.get(model_b, 0) + 1

    return vote_counts


def update_vote_counts_in_leaderboard(
    leaderboard_df: pd.DataFrame, votes: list[dict]
) -> pd.DataFrame:
    """
    Update the vote_count column in leaderboard DataFrame with actual counts.

    Args:
        leaderboard_df: Leaderboard DataFrame
        votes: Original votes data

    Returns:
        Updated leaderboard DataFrame
    """
    vote_counts = compute_vote_counts(votes)

    # Update vote counts in leaderboard
    for idx in leaderboard_df.index:
        model_name = str(leaderboard_df.loc[idx, "model_name"])
        leaderboard_df.loc[idx, "vote_count"] = vote_counts.get(model_name, 0)

    return leaderboard_df
