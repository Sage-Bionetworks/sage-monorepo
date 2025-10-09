# BixArena Tools

Tools for Bradley-Terry evaluation and ranking in the BixArena project.

## Features

- **Bradley-Terry Evaluation**: Compute BT scores using FastChat's proven implementation
- **Bootstrap Confidence Intervals**: Statistical confidence intervals for rankings
- **Mock Data Generation**: Generate synthetic vote data for testing
- **CLI Interface**: Easy-to-use command-line interface

## Usage

### Run BT Evaluation with Mock Data

```bash
# Run with default parameters (10 models, 500 votes, 1000 bootstrap samples)
uv run python -m bixarena_tools.cli_run_bt_eval

# Or use the console script entry point
uv run bixarena-bt-eval

# Customize the evaluation
uv run bixarena-bt-eval --num-models 8 --num-votes 1000 --num-bootstrap 500

# Save results to file
uv run bixarena-bt-eval --output-file bt_results.json --verbose

# Quick test with fewer models and votes
uv run bixarena-bt-eval --num-models 5 --num-votes 100 --num-bootstrap 100
```

### Parameters

- `--num-models`: Number of models to simulate (default: 10)
- `--num-votes`: Number of votes to generate (default: 500)
- `--num-bootstrap`: Bootstrap samples for confidence intervals (default: 1000)
- `--tie-probability`: Probability of tie outcomes (default: 0.05)
- `--random-seed`: Random seed for reproducibility (default: 42)
- `--output-file`: Optional file to save results (JSON format)
- `--verbose`: Enable verbose logging

## Implementation

This tool uses:

1. **FastChat's BT Implementation**: Leverages the proven `compute_bt` and `compute_bootstrap_bt` functions from FastChat
2. **BixArena Data Model**: Adapts FastChat's battle format to BixArena's vote preferences (model_a, model_b, tie)
3. **Bootstrap Statistics**: Provides confidence intervals for statistical significance

## Output Format

The evaluation produces a leaderboard DataFrame with:

- `rank`: Model ranking (1-based)
- `model_name`: Name of the model
- `bt_score`: Bradley-Terry score (higher is better)
- `vote_count`: Number of votes involving this model
- `bootstrap_q025`: 2.5th percentile (lower confidence bound)
- `bootstrap_q975`: 97.5th percentile (upper confidence bound)
- `ci_95`: Formatted 95% confidence interval string

## Example Output

```
🏆 Bradley-Terry Leaderboard Results:
--------------------------------------------------------------------------------
Rank   Model        BT Score   95% CI               Votes
--------------------------------------------------------------------------------
1      model_05     1073.925   [966.5, 1181.3]      41
2      model_01     1058.142   [952.3, 1164.0]      39
3      model_02     975.134    [877.6, 1072.6]      44
4      model_03     970.718    [873.6, 1067.8]      38
5      model_04     922.081    [829.9, 1014.3]      38
```

## Dependencies

- pandas >= 2.1
- numpy >= 1.26
- scipy >= 1.11

## Development

This package is part of the Sage Bionetworks monorepo and integrates with the BixArena evaluation platform.

### Running Tests

```bash
# Run the mock data generator directly
uv run python -m bixarena_tools.mock_bt_data

# Test with different configurations
uv run bixarena-bt-eval --num-models 3 --num-votes 50 --verbose
```

### Integration with BixArena

The evaluation functions can be imported and used programmatically:

```python
from bixarena_tools import (
    compute_bt_scores_and_bootstrap,
    format_leaderboard_output,
    SimConfig,
    simulate_battles
)

# Generate mock data
config = SimConfig(num_models=5, num_votes=100)
votes = simulate_battles(config)

# Compute BT scores
bt_results, confidence_intervals = compute_bt_scores_and_bootstrap(votes)

# Format for display
leaderboard = format_leaderboard_output(bt_results, confidence_intervals)
```
