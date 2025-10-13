# BixArena Bradley-Terry (BT) Ranking Metric

This document explains the mathematical foundations, implementation details, and data flow for the Bradley-Terry based ranking metric used in BixArena.

## 1. Overview

The Bradley-Terry (BT) model produces a _relative skill/rating_ for each model based on **pairwise comparison votes**. Each vote compares two models (`model_a`, `model_b`) with an outcome: win for A, win for B, or tie. The BT model assigns a _latent strength_ to every model and fits these strengths so that the probability of observed outcomes is maximized.

We implement a vanilla BT formulation with simple tie handling (tie = half win + half loss). Ratings are then mapped to an _Elo-like scale_ (centered at `init_rating`, scaled by `scale`, with optional baseline anchoring) for interpretability.

## 2. Mathematical Foundations

### 2.1 Core Probability Model

For two models\( i \) and \( j \) with latent strengths (real-valued parameters) \( s_i, s_j \):

$$ P(i \text{ beats } j) = \frac{e^{s_i}}{e^{s_i} + e^{s_j}} = \sigma(s_i - s_j) $$

where \( \sigma(x) = \frac{1}{1 + e^{-x}} \) is the logistic (sigmoid) function.

Equivalently:

$$ P(i \text{ beats } j) = \text{expit}(s_i - s_j). $$

### 2.2 Tie Handling

We treat a tie as contributing half credit to each side. If an outcome variable \( y\_{ij} \in \{1, 0.5, 0\} \) represents win (1), tie (0.5), or loss (0) for the first listed model, the (negative) log-likelihood contribution for a single aggregated matchup with weight (count) \( w \) is:

$$ \ell*{ij} = -w \Big( y*{ij} \log p*{ij} + (1 - y*{ij}) \log (1 - p\_{ij}) \Big), $$

where \( p\_{ij} = P(i \text{ beats } j) = \sigma(s_i - s_j) \).

This treats a tie exactly as half a win plus half a loss; no explicit Davidson tie parameter is modeled (keeps implementation minimal). If richer tie modeling is needed later, a Davidson or Rao-Kupper extension can be introduced.

### 2.3 Full Objective

Let the set of _unique_ outcome groups (after aggregation) be indexed by \( k \), each with:

- Competitors \( (i_k, j_k) \)
- Outcome label \( y_k \in \{1, 0.5, 0\} \)
- Weight (count) \( w_k \)

The total **negative log-likelihood** is:

$$ \mathcal{L}(\mathbf{s}) = - \sum*k w_k \left( y_k \log p_k + (1 - y_k) \log (1 - p_k) \right), \quad p_k = \sigma(s*{i*k} - s*{j_k}). $$

### 2.4 Gradient

For each aggregated observation \( k \):

$$ \frac{\partial \mathcal{L}}{\partial s*{i_k}} = -w_k (y_k - p_k), \qquad \frac{\partial \mathcal{L}}{\partial s*{j_k}} = +w_k (y_k - p_k). $$

Collectively we scale this by an optional constant \( \alpha = \log(\text{base}) \) (mirroring Elo scaling path):

Implementation forms the gradient vector by accumulating contributions using `np.add.at` for efficiency.

### 2.5 Optimization

We solve an unconstrained convex optimization in \( \mathbb{R}^M \) (M = number of models) using **L-BFGS-B** with:

- Initial ratings: all zeros (centered solution—only relative differences matter)
- Tolerance: `gtol = 1e-6`
- Max iterations: `100`

Because the likelihood is shift-invariant (adding a constant to all \( s_i \) does not change probabilities), the solution is implicitly centered around the starting point (zero mean shift).

### 2.6 Scaling to Elo-like Ratings

Raw strengths \( s_i \) are mapped to display ratings:

$$ R_i = (s_i \cdot \text{scale}) + \text{init_rating}. $$

If an anchoring baseline model \( b \) is present:

$$ R_i \leftarrow R_i + (R_b^\* - R_b), $$

forcing the baseline model to take rating \( R_b^\* \) (e.g. 1114) and shifting all others accordingly.

### 2.7 Bootstrap Confidence Intervals

We derive empirical uncertainty via the following:

1. After preprocessing, we have unique rows \( (i_k, j_k, y_k, w_k) \).
2. We treat the vector of counts \( \mathbf{w} \) as multinomial over the observed outcomes.
3. For each bootstrap round \( r \):
   - Sample new counts \( \mathbf{w}^{(r)} \sim \text{Multinomial}(n, \mathbf{w}/\sum w) \), where \( n = \text{original number of votes} \).
   - Fit BT using the sampled weights (normalized by \( n \)).
4. Collect rating samples \( R_i^{(r)} \) across rounds.
5. Form \( (2.5\%, 97.5\%) \) empirical quantiles as the 95% interval.

This approximates uncertainty induced by finite sample variability of battle outcomes.

## 3. Data Flow in the BixArena Tooling

### 3.1 High-Level Pipeline

```
Raw Votes (model_a, model_b, preference) --> convert_votes_to_battles() --> battles DataFrame
    --> preprocess_for_bt() --> (matchups, outcomes, models, weights)
        --> fit_bt()/compute_bt() --> base ratings
            --> scale_and_offset() --> Elo-like ratings
                --> compute_bootstrap_bt() (optional) --> sampled rating matrix
                    --> quantiles --> confidence intervals
                        --> format_leaderboard_output() --> Leaderboard JSON/DataFrame
```

### 3.2 Components

| Stage                | File / Function                               | Responsibility                                                              |
| -------------------- | --------------------------------------------- | --------------------------------------------------------------------------- |
| Vote ingestion       | `bt_eval.py::convert_votes_to_battles`        | Map domain vote schema to internal battle schema (`winner` encoding).       |
| Aggregation          | `bradley_terry.py::preprocess_for_bt`         | Collapse duplicate matchups/outcomes; produce weighted unique observations. |
| Core model           | `bradley_terry.py::fit_bt`                    | Optimize strengths via L-BFGS-B.                                            |
| Rating compute       | `bradley_terry.py::compute_bt`                | Orchestrate preprocessing + fitting + scaling.                              |
| Bootstrap            | `bradley_terry.py::compute_bootstrap_bt`      | Sample outcome weight distributions; refit multiple times.                  |
| Confidence intervals | `bt_eval.py::compute_bt_scores_and_bootstrap` | Convert bootstrap samples into quantile bounds.                             |
| Leaderboard shaping  | `bt_eval.py::format_leaderboard_output`       | Produce rank-ordered, display-friendly structure.                           |
| Vote counts          | `bt_eval.py::compute_vote_counts`             | Aggregate raw vote appearances per model.                                   |

### 3.3 Tie Encoding

| Preference | Stored `winner` | Numeric label (optimization) |
| ---------- | --------------- | ---------------------------- |
| model_a    | `model_a`       | 1.0                          |
| model_b    | `model_b`       | 0.0                          |
| tie        | `tie`           | 0.5                          |

Internally the label transformation happens in `preprocess_for_bt` after collapsing results.

### 3.4 Weighting Strategy

Instead of repeating identical (A,B,outcome) rows, we count them and store a single row with weight \( w \). This reduces the optimization size from O(N votes) to O(U unique outcomes). The gradient and loss incorporate weights multiplicatively.

### 3.5 Bootstrap Efficiency

Rather than resampling raw vote rows, we resample outcome _counts_ using a multinomial. This is equivalent (in expectation) and dramatically more efficient when many duplicate matchups exist.

## 4. Edge Cases & Safeguards

| Scenario                              | Handling                                                                                                                                                                             |
| ------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| No valid votes                        | Return empty DataFrames early.                                                                                                                                                       |
| Single model                          | Trivial rating (no comparisons) — currently yields empty or zero strength; can add guard to skip.                                                                                    |
| Completely disconnected model subsets | Optimization still runs; relative scales between disconnected components are not statistically identified (shift ambiguity). Future enhancement: detect disconnected graph and warn. |
| All ties                              | Loss is symmetric; strengths remain near initialization. Resulting Elo ratings collapse toward `init_rating`.                                                                        |
| Extreme imbalance                     | Heavily weighted pairs can dominate; consider capping weights in future if needed.                                                                                                   |

## 5. Computational Complexity

| Component  | Complexity (M=models, U=unique outcomes, B=bootstrap rounds)                               |
| ---------- | ------------------------------------------------------------------------------------------ |
| Preprocess | O(N) to build + O(U log U) via `np.unique` (internally).                                   |
| Single fit | Each L-BFGS iteration: O(U) for loss+grad; iterations constant (≈100 max).                 |
| Bootstrap  | O(B _ U _ I) where I = iterations per fit (practically much smaller with caching effects). |

Parallelism: Bootstrap fits are parallelized with `multiprocessing.Pool`, distributing independent weight samples.

## 6. Potential Extensions

1. **Tie parameter (Davidson model)**: Introduce parameter \( \nu \) so tie probability is explicit.
2. **Regularization**: Add \( \lambda \| s \|^2 \) to stabilize in sparse regimes.
3. **Contextual BT**: Incorporate prompt/task features (see FastChat's contextual/style BT patterns).
4. **Graph Diagnostics**: Detect disconnected comparison graphs and partition rankings.
5. **Uncertainty Alternatives**: Use Fisher Information or Bayesian (Laplace / MCMC) posterior intervals.
6. **Drift Tracking**: Temporal weighting (exponential decay) for evolving model performance.

## 7. Implementation Integrity

The extracted implementation in `bradley_terry.py` preserves FastChat's original logic:

- Same likelihood & gradient
- Same optimizer configuration
- Same bootstrap sampling strategy
- Added documentation and minor formatting only

## 8. Example (Simplified)

Suppose 3 votes:

```
model_a=model_1, model_b=model_2, winner=model_a
model_a=model_2, model_b=model_3, tie
model_a=model_1, model_b=model_3, winner=model_3
```

Aggregation builds unique (matchup,outcome) rows like:

```
(model_1, model_2, outcome=1.0, weight=1)
(model_2, model_3, outcome=0.5, weight=1)
(model_1, model_3, outcome=0.0, weight=1)
```

Optimization adjusts \( s_1, s_2, s_3 \) so that:

- \( P(1>2) \) is high
- \( P(2>3) \) ≈ 0.5
- \( P(1>3) \) is low

Resulting order might be: `model_3 > model_1 > model_2` depending on balance (since model_3 beat model_1 outright and tied model_2).

## 9. API Surface Summary

| Function                          | Input            | Output                              | Notes                                 |
| --------------------------------- | ---------------- | ----------------------------------- | ------------------------------------- |
| `convert_votes_to_battles`        | List[dict] votes | DataFrame                           | Normalizes vote schema.               |
| `preprocess_for_bt`               | Battles DF       | matchups, outcomes, models, weights | Aggregation stage.                    |
| `compute_bt`                      | Battles DF       | pd.Series ratings                   | Deterministic rating point estimates. |
| `compute_bootstrap_bt`            | Battles DF, B    | DataFrame (B x M)                   | Sampled rating draws per round.       |
| `compute_bt_scores_and_bootstrap` | Votes list       | Results DF, CI dict                 | Orchestrator combining steps.         |
| `format_leaderboard_output`       | Results DF       | Leaderboard DF                      | Adds rank & CI formatting.            |

## 10. Reproducibility & Determinism

- Bootstrap uses a fixed RNG seed (0) for reproducibility.
- Non-bootstrap `compute_bt` is deterministic given input battles.
- Parallel order of bootstrap samples does not affect final quantiles.

## 11. Practical Guidance

| Goal                 | Recommendation                                                  |
| -------------------- | --------------------------------------------------------------- |
| Faster dev iteration | Reduce `num_bootstrap` (e.g. 100).                              |
| Production stability | Use 1000+ rounds for smoother intervals.                        |
| Comparing runs       | Keep seed constant and dataset fixed.                           |
| Adding new models    | Recompute from full history (BT is global).                     |
| Streaming updates    | Consider incremental approximations or periodic full recompute. |

## 12. Glossary

| Term             | Definition                                      |
| ---------------- | ----------------------------------------------- |
| Battle           | A single head-to-head comparison instance.      |
| Matchup          | Unique ordered pair (model_a, model_b).         |
| Outcome label    | Encoded numeric result (1.0, 0.5, 0.0).         |
| Weight           | Count of identical matchup/outcome occurrences. |
| Strength / Score | Latent parameter before scaling.                |
| Elo-like rating  | Scaled display value shown to users.            |

---

**Maintainers:** Update this document when modifying any of: outcome encoding, bootstrap method, scaling rules, or tie handling.
