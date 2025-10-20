# BixArena Tools

Tools for Bradley-Terry evaluation and ranking in the BixArena project.

## Features

- **Bradley-Terry Evaluation**: Compute BT scores using FastChat's proven implementation
- **Bootstrap Confidence Intervals**: Statistical confidence intervals for rankings
- **Statistical Ranking (final ranking)**: Determine model rankings based on confidence interval overlaps
- **Mock Data Generation**: Generate synthetic vote data for testing
- **CLI Interface**: Easy-to-use command-line interface

## How It Works

### Battle Vote Evaluation Pipeline

BixArena uses a sophisticated statistical approach to evaluate model performance from pairwise comparison votes:

#### 1. **Data Collection**

- Users compare two models side-by-side (Model A vs Model B)
- Each vote records: which models were compared and the user's preference (Model A, Model B, or Tie)
- Votes accumulate over time from many users making independent comparisons

#### 2. **Bradley-Terry Model Fitting**

The Bradley-Terry (BT) model is a probabilistic framework that:

- Assigns each model a strength rating based on observed win/loss/tie patterns
- Uses statistical optimization (L-BFGS-B) to find ratings that best explain the vote outcomes
- Accounts for all pairwise comparisons simultaneously, not just individual matchups
- Produces scores on a standardized scale (typically anchored at 1000 ± 400 points)

**Why Bradley-Terry?** Unlike simple win-rate calculations, BT handles:

- Different models being compared different numbers of times
- Transitive relationships (if A beats B, and B beats C, then A should rate higher than C)
- Tie outcomes with appropriate probability modeling

#### 3. **Bootstrap Confidence Intervals**

To measure uncertainty in the ratings:

- Resample the vote data 1000 times (with replacement) using multinomial sampling
- Compute BT scores for each resample
- Calculate the 2.5th and 97.5th percentiles to get 95% confidence intervals
- Wider intervals indicate more uncertainty (often due to fewer votes)

#### 4. **Statistical Ranking**

Rather than ranking purely by score, we use **statistical significance**:

- Model A ranks higher than Model B **only if** A's lower confidence bound exceeds B's upper confidence bound
- Models with overlapping confidence intervals are considered **statistically tied**
- Rank is computed as: `1 + count(models that are statistically significantly better)`

**Example:**

- Models with ranks 1, 1, 1, 1 (4 models tied at top)
- Next distinct model gets rank 5 (not rank 2), because 4 models are not statistically worse
- This is similar to Olympic medal standings

#### 5. **Leaderboard Generation**

The final leaderboard displays:

- **Rank**: Statistical ranking (ties preserved)
- **Model**: Model identifier
- **Score**: Bradley-Terry rating (higher = stronger)
- **95% CI**: Confidence interval showing uncertainty
- **Votes**: Number of comparisons involving this model

### Key Principles

1. **Statistical Rigor**: Rankings reflect measurable differences, not random noise
2. **Transparency**: Confidence intervals show where we're certain vs. uncertain
3. **Fairness**: All models are evaluated on the same statistical framework
4. **Reproducibility**: Bootstrap sampling ensures stable rankings with enough votes
