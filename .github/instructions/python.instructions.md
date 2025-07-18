---
applyTo: '**/*.py'
---

# Python Instructions for Sage Monorepo

## General Guidelines

- Use type hints for all function signatures and variable declarations.
- Prefer logging over print statements. Configure logging with appropriate levels (INFO for general, DEBUG for troubleshooting).
- Always handle exceptions gracefully and log errors with context.
- Use environment variables for configuration. Document required variables in README or script docstrings.
- Structure code for modularity: separate concerns into functions and keep main logic in a `main()` function.
- Use concurrent processing (e.g., ThreadPoolExecutor) for I/O-bound tasks when appropriate.
- Avoid hardcoding sensitive information; use environment variables or configuration files.
- Remove unused imports and code to maintain clarity.
- Use list comprehensions and dictionary comprehensions for concise data transformations.
- Prefer explicit over implicit: check for `None`, empty strings, and handle edge cases.

## Code Quality

- Run linting tools (e.g., flake8, pylint) before committing code.
- Write self-documenting code with meaningful variable and function names.
- Avoid unnecessary comments; use docstrings for functions and modules.
- Remove debug logging before production deployment, or set logging level appropriately.

## Testing

- Write unit tests for all functions, especially those with complex logic.
- Use mocking for external API calls in tests.
- Validate edge cases and error handling in tests.

## File and Folder Structure

- For file and folder structure, refer to `copilot-instructions.md` in this repository.

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
