"""
Bradley-Terry ranking implementation optimized for BixArena evaluation data,
while preserving the original Bradley-Terry algorithm.
"""

import math
import multiprocessing as mp
import os
import uuid
from datetime import UTC, datetime
from functools import partial

import numpy as np
from scipy.optimize import minimize
from scipy.special import expit
from tqdm import tqdm


def preprocess_evaluations_for_bt(evaluations: list[dict]):
    """
    Convert evaluations directly to Bradley-Terry format.

    Processes BixArena evaluation to the numeric outcomes used by BT algorithm.

    Args:
        evaluations: List of evaluation dicts with model1_name, model2_name, outcome

    Returns:
        matchups: array of shape (n_unique_matchups, 2) with model indices
        outcomes: array of shape (n_unique_matchups,) with outcomes as floats
        models: list of model names
        weights: array of shape (n_unique_matchups,) with occurrence counts
    """
    if not evaluations:
        return np.array([]), np.array([]), [], np.array([])

    # Extract all model names and create mapping
    all_models = set()
    for evaluation in evaluations:
        all_models.add(evaluation["model1_name"])
        all_models.add(evaluation["model2_name"])

    models = sorted(all_models)  # Sort for consistent ordering
    model_to_idx = {model: idx for idx, model in enumerate(models)}

    # Build schedule directly with outcomes: model1_idx, model2_idx, outcome
    # Outcome mapping: model1 win → 2 (1.0), tie → 1 (0.5), model2 win → 0 (0.0)
    outcome_map = {"model1": 2, "model2": 0, "tie": 1}

    schedule = []
    for evaluation in evaluations:
        outcome_value = evaluation["outcome"]
        outcome = outcome_map.get(outcome_value)
        if outcome is None:
            continue  # Skip invalid outcomes

        schedule.append(
            [
                model_to_idx[evaluation["model1_name"]],
                model_to_idx[evaluation["model2_name"]],
                outcome,
            ]
        )

    if not schedule:
        return np.array([]), np.array([]), models, np.array([])

    schedule = np.array(schedule, dtype=np.int32)

    # Count occurrences of each unique (model_a, model_b, outcome) combination
    matchups_outcomes, weights = np.unique(schedule, return_counts=True, axis=0)
    matchups = matchups_outcomes[:, [0, 1]]
    # Convert outcomes to labels:
    # 2→1.0 (model_a win), 1→0.5 (tie), 0→0.0 (model_b win)
    outcomes = matchups_outcomes[:, 2].astype(np.float64) / 2.0
    weights = weights.astype(np.float64)

    return matchups, outcomes, models, weights


def bt_loss_and_grad(ratings, matchups, outcomes, weights, alpha=1.0):
    """
    Compute Bradley-Terry loss and gradient.

    Args:
        ratings: array of current model ratings
        matchups: array of shape (n_matchups, 2) with model indices
        outcomes: array of shape (n_matchups,) with outcomes
        weights: array of shape (n_matchups,) with occurrence weights
        alpha: scaling parameter

    Returns:
        loss: scalar loss value
        model_grad: gradient with respect to model ratings
    """
    matchup_ratings = ratings[matchups]
    logits = alpha * (matchup_ratings[:, 0] - matchup_ratings[:, 1])
    probs = expit(logits)
    # this form naturally counts a draw as half a win and half a loss
    loss = -(
        (np.log(probs) * outcomes + np.log(1.0 - probs) * (1.0 - outcomes)) * weights
    ).sum()
    matchups_grads = -alpha * (outcomes - probs) * weights
    model_grad = np.zeros_like(ratings)
    # aggregate gradients at the model level using the indices in matchups
    np.add.at(
        model_grad,
        matchups[:, [0, 1]],
        matchups_grads[:, None] * np.array([1.0, -1.0], dtype=np.float64),
    )
    return loss, model_grad


def fit_bt(matchups, outcomes, weights, n_models, alpha, tol=1e-6):
    """
    Fit Bradley-Terry model using L-BFGS-B optimization.

    Args:
        matchups: array of shape (n_matchups, 2) with model indices
        outcomes: array of shape (n_matchups,) with outcomes
        weights: array of shape (n_matchups,) with occurrence weights
        n_models: number of models
        alpha: scaling parameter (typically log(base))
        tol: optimization tolerance

    Returns:
        ratings: array of fitted model ratings
    """
    initial_ratings = np.zeros(n_models, dtype=np.float64)
    result = minimize(
        fun=bt_loss_and_grad,
        x0=initial_ratings,
        args=(matchups, outcomes, weights, alpha),
        jac=True,
        method="L-BFGS-B",
        options={"disp": False, "maxiter": 100, "gtol": tol},
    )
    return result["x"]


def scale_and_offset(
    ratings,
    models,
    scale=400.0,
    init_rating=1000.0,
):
    """
    Convert ratings from natural scale to Elo scale.

    Args:
        ratings: array of model ratings on natural scale
        models: list of model names
        scale: scaling factor (default 400 for Elo scale)
        init_rating: initial rating offset (scores centered around this value)

    Returns:
        scaled_ratings: ratings on Elo scale, centered around init_rating
    """
    scaled_ratings = (ratings * scale) + init_rating
    return scaled_ratings


def compute_bt(
    evaluations: list[dict], base=10.0, scale=400.0, init_rating=1000.0, tol=1e-6
):
    """
    Compute Bradley-Terry ratings directly from evaluations.

    Args:
        evaluations: List of evaluation dictionaries with model_a, model_b, preference
        base: base for logarithm (default 10.0)
        scale: scaling factor for final ratings
        init_rating: initial rating offset
        tol: optimization tolerance

    Returns:
        dict with model ratings, sorted in descending order by rating
    """
    matchups, outcomes, models, weights = preprocess_evaluations_for_bt(evaluations)

    if len(models) == 0:
        return {}

    ratings = fit_bt(matchups, outcomes, weights, len(models), math.log(base), tol)
    scaled_ratings = scale_and_offset(ratings, models, scale, init_rating=init_rating)
    # Return as dict sorted by rating (descending)
    return dict(
        sorted(
            zip(models, scaled_ratings, strict=True), key=lambda x: x[1], reverse=True
        )
    )


def compute_bootstrap_bt(
    evaluations: list[dict],
    num_round: int,
    base=10.0,
    scale=400.0,
    init_rating=1000.0,
    tol=1e-6,
    num_cpu=None,
):
    """
    Compute bootstrap confidence intervals for Bradley-Terry ratings from evaluations.

    Args:
        evaluations: List of evaluation dictionaries with model_a, model_b, preference
        num_round: number of bootstrap rounds
        base: base for logarithm (default 10.0)
        scale: scaling factor for final ratings
        init_rating: initial rating offset
        tol: optimization tolerance
        num_cpu: number of CPU cores to use (None for all available)

    Returns:
        dict with bootstrap results, keys are models, values are arrays of ratings
        sorted by median rating (descending)
    """
    matchups, outcomes, models, weights = preprocess_evaluations_for_bt(evaluations)

    if len(models) == 0:
        return {}

    # bootstrap sample unique outcomes and counts using multinomial distribution
    rng = np.random.default_rng(seed=0)
    idxs = rng.multinomial(
        n=len(evaluations), pvals=weights / weights.sum(), size=(num_round)
    )
    # only occurrence count distribution changes between samples (can be 0)
    boot_weights = idxs.astype(np.float64) / len(evaluations)

    # the only thing different across samples is the distribution of weights
    bt_fn = partial(
        fit_bt, matchups, outcomes, n_models=len(models), alpha=np.log(base), tol=tol
    )
    with mp.Pool(num_cpu if num_cpu else os.cpu_count()) as pool:
        results = list(tqdm(pool.imap_unordered(bt_fn, boot_weights), total=num_round))

    ratings = np.array(results)
    scaled_ratings = scale_and_offset(ratings, models, scale, init_rating)

    # Convert to dict with model names as keys and rating arrays as values
    results_dict = {model: scaled_ratings[:, i] for i, model in enumerate(models)}

    # Sort by median rating (descending)
    sorted_models = sorted(
        results_dict.keys(), key=lambda m: np.median(results_dict[m]), reverse=True
    )
    return {model: results_dict[model] for model in sorted_models}


def compute_leaderboard_bt(
    evaluations: list[dict],
    models: dict[str, dict],
    num_bootstrap: int = 5000,
    base: float = 10.0,
    scale: float = 400.0,
    init_rating: float = 1000.0,
    tol: float = 1e-6,
) -> list[dict]:
    """
    Compute Bradley-Terry leaderboard directly from evaluations and model data.

    Args:
        evaluations: List of evaluation dictionaries with model_a, model_b, preference
        models: Dict mapping model_name -> model_info with id, name, etc.
        num_bootstrap: Number of bootstrap iterations for confidence intervals
        base: Base for logarithm (default 10.0)
        scale: Scaling factor for final ratings
        init_rating: Initial rating offset
        tol: Optimization tolerance

    Returns:
        List of leaderboard entry dictionaries ready for API
    """
    # Compute base scores directly from evaluations (optimized path)
    scores = compute_bt(evaluations, base, scale, init_rating, tol)
    if not scores:
        return []

    # Compute bootstrap confidence intervals directly from evaluations
    bootstrap_results = compute_bootstrap_bt(
        evaluations, num_bootstrap, base, scale, init_rating, tol, num_cpu=1
    )

    # Count evaluations per model
    evaluation_counts = {}
    for evaluation in evaluations:
        for model in [evaluation["model1_name"], evaluation["model2_name"]]:
            evaluation_counts[model] = evaluation_counts.get(model, 0) + 1

    # Compute confidence intervals for all models
    model_ci = {}
    for model_name in scores:
        model_bootstrap = bootstrap_results[model_name]
        model_ci[model_name] = {
            "lower": float(np.quantile(model_bootstrap, 0.025)),
            "upper": float(np.quantile(model_bootstrap, 0.975)),
        }

    # Compute statistical ranking based on CI overlap
    # A model's rank = 1 + number of models statistically significantly better than it
    # Model B is significantly better than A if: CI_lower(B) > CI_upper(A)
    model_order = list(scores.keys())
    final_rank = {}
    for model_a in model_order:
        final_rank[model_a] = 1
        for model_b in model_order:
            if model_a == model_b:
                continue
            # Check if model_b is statistically significantly better than model_a
            if model_ci[model_b]["lower"] > model_ci[model_a]["upper"]:
                final_rank[model_a] += 1

    # Format leaderboard entries
    leaderboard_entries = []
    current_time = datetime.now(UTC).isoformat()

    for model_name, bt_score in scores.items():
        model_info = models.get(str(model_name), {})
        ci = model_ci[model_name]

        entry = {
            "id": str(uuid.uuid4()),
            "modelId": model_info.get("id", str(uuid.uuid4())),
            "modelName": model_name,
            "license": model_info.get("license", "Unknown"),
            "btScore": float(bt_score),
            "voteCount": evaluation_counts.get(model_name, 0),
            "rank": final_rank[model_name],
            "createdAt": current_time,
            "bootstrapQ025": ci["lower"],
            "bootstrapQ975": ci["upper"],
        }
        leaderboard_entries.append(entry)

    return leaderboard_entries
