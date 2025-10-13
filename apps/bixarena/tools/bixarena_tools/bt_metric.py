"""
Standalone Bradley-Terry implementation based on FastChat's rating_systems.py.

This is a minimal adaptation of FastChat's Bradley-Terry code to decouple from FastChat
while preserving the original algorithm and structure for easy comparison.

Original source: https://github.com/lm-sys/FastChat/blob/main/fastchat/serve/monitor/rating_systems.py
"""

import math
import multiprocessing as mp
import os
from functools import partial

import numpy as np
import pandas as pd
from scipy.optimize import minimize
from scipy.special import expit
from tqdm import tqdm


def get_matchups_models(df):
    """Extract matchups and model names from battles DataFrame."""
    n_rows = len(df)
    model_indices, models = pd.factorize(pd.concat([df["model_a"], df["model_b"]]))
    matchups = np.column_stack([model_indices[:n_rows], model_indices[n_rows:]])
    return matchups, models.to_list()


def preprocess_for_bt(df):
    """
    In BT we only need the unique (matchup,outcome) sets along with the weights
    of how often they occur.

    Args:
        df: DataFrame with columns ['model_a', 'model_b', 'winner']

    Returns:
        matchups: array of shape (n_unique_matchups, 2) with model indices
        outcomes: array of shape (n_unique_matchups,) with outcomes as floats
        models: list of model names
        weights: array of shape (n_unique_matchups,) with occurrence counts
    """
    n_rows = len(df)
    # the 3 columns of schedule represent: model_a id, model_b id, outcome_id
    schedule = np.full((n_rows, 3), fill_value=1, dtype=np.int32)
    # set the two model cols by mapping the model names to their int ids
    schedule[:, [0, 1]], models = get_matchups_models(df)
    # map outcomes to integers (same dtype as model ids for array compatibility)
    # model_a win -> 2, tie -> 1 (prefilled by default), model_b win -> 0
    schedule[df["winner"] == "model_a", 2] = 2
    schedule[df["winner"] == "model_b", 2] = 0
    # count the number of occurances of each observed result
    matchups_outcomes, weights = np.unique(schedule, return_counts=True, axis=0)
    matchups = matchups_outcomes[:, [0, 1]]
    # map 2 -> 1.0, 1 -> 0.5, 0 -> 0.0 which will be used as labels during optimization
    outcomes = matchups_outcomes[:, 2].astype(np.float64) / 2.0
    weights = weights.astype(np.float64)
    # each result is weighted by number of times it occurred in the dataset
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
    baseline_model="mixtral-8x7b-instruct-v0.1",
    baseline_rating=1114,
):
    """
    Convert ratings from natural scale to Elo scale with anchored baseline.

    Args:
        ratings: array of model ratings on natural scale
        models: list of model names
        scale: scaling factor (default 400 for Elo scale)
        init_rating: initial rating offset
        baseline_model: model to use as baseline anchor
        baseline_rating: target rating for baseline model

    Returns:
        scaled_ratings: ratings on Elo scale
    """
    scaled_ratings = (ratings * scale) + init_rating
    if baseline_model in models:
        baseline_idx = models.index(baseline_model)
        scaled_ratings += baseline_rating - scaled_ratings[..., [baseline_idx]]
    return scaled_ratings


def compute_bt(df, base=10.0, scale=400.0, init_rating=1000, tol=1e-6):
    """
    Compute Bradley-Terry ratings for models.

    Args:
        df: DataFrame with columns ['model_a', 'model_b', 'winner']
        base: base for logarithm (default 10.0)
        scale: scaling factor for final ratings
        init_rating: initial rating offset
        tol: optimization tolerance

    Returns:
        pd.Series with model ratings, sorted in descending order
    """
    matchups, outcomes, models, weights = preprocess_for_bt(df)
    ratings = fit_bt(matchups, outcomes, weights, len(models), math.log(base), tol)
    scaled_ratings = scale_and_offset(ratings, models, scale, init_rating=init_rating)
    return pd.Series(scaled_ratings, index=models).sort_values(ascending=False)


def compute_bootstrap_bt(
    battles,
    num_round,
    base=10.0,
    scale=400.0,
    init_rating=1000.0,
    tol=1e-6,
    num_cpu=None,
):
    """
    Compute bootstrap confidence intervals for Bradley-Terry ratings.

    Args:
        battles: DataFrame with columns ['model_a', 'model_b', 'winner']
        num_round: number of bootstrap rounds
        base: base for logarithm (default 10.0)
        scale: scaling factor for final ratings
        init_rating: initial rating offset
        tol: optimization tolerance
        num_cpu: number of CPU cores to use (None for all available)

    Returns:
        pd.DataFrame with bootstrap results, columns are models, sorted by median
    """
    matchups, outcomes, models, weights = preprocess_for_bt(battles)
    # bootstrap sample unique outcomes and counts using multinomial distribution
    rng = np.random.default_rng(seed=0)
    idxs = rng.multinomial(
        n=len(battles), pvals=weights / weights.sum(), size=(num_round)
    )
    # only occurrence count distribution changes between samples (can be 0)
    boot_weights = idxs.astype(np.float64) / len(battles)

    # the only thing different across samples is the distribution of weights
    bt_fn = partial(
        fit_bt, matchups, outcomes, n_models=len(models), alpha=np.log(base), tol=tol
    )
    with mp.Pool(num_cpu if num_cpu else os.cpu_count()) as pool:
        results = list(tqdm(pool.imap_unordered(bt_fn, boot_weights), total=num_round))

    ratings = np.array(results)
    scaled_ratings = scale_and_offset(ratings, models, scale, init_rating)
    df = pd.DataFrame(scaled_ratings, columns=models)
    return df[df.median().sort_values(ascending=False).index]
