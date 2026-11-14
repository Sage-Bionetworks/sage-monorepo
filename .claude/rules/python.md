---
paths:
  - '**/*.py'
---

# Python Guidelines for Sage Monorepo

## General Guidelines

- Use type hints for all function signatures and variable declarations.
- Prefer logging over print statements. Configure logging with appropriate levels (INFO for general, DEBUG for troubleshooting).
- Use environment variables for configuration. Document required variables in README or script docstrings.
- Structure code for modularity: separate concerns into functions and keep main logic in a `main()` function.

## Code Quality

- Run `uv run ruff check` before committing code.

## Testing

- Use mocking for external API calls in tests.

## Environment Variables

- Use `os.getenv()` for fetching environment variables, with error handling for missing values.

## Example Logging Configuration

```python
import logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)
```

## Example Exception Handling

```python
def fetch_data():
    try:
        # ... code ...
    except Exception as e:
        logger.error(f"Error fetching data: {e}")
        return None
```

## Example Environment Variable Usage

```python
host = os.getenv("HOST")
if not host:
    raise ValueError("HOST environment variable is required")
```

## References

- [PEP 8 – Style Guide for Python Code](https://peps.python.org/pep-0008/)
- [Python Logging HOWTO](https://docs.python.org/3/howto/logging.html)
- [Python Type Hints](https://docs.python.org/3/library/typing.html)
