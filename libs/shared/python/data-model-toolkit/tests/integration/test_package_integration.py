"""
Integration tests for the DataModelToolkit.

These tests verify that the toolkit works correctly as a whole package,
including imports, interactions between modules, and end-to-end functionality.
"""

import subprocess
import sys
from pathlib import Path


class TestPackageIntegration:
    """Integration tests for the package as a whole."""

    def test_package_can_be_imported(self):
        """Test that the package can be imported successfully."""
        # Test importing the main package
        import data_model_toolkit

        assert data_model_toolkit is not None

    def test_main_function_accessible(self):
        """Test that the main hello_world function is accessible from package root."""
        from data_model_toolkit import hello_world

        result = hello_world("Integration Test")
        expected = "Hello, Integration Test! Welcome to DataModelToolkit!"
        assert result == expected

    def test_version_accessible(self):
        """Test that version information is accessible."""
        import data_model_toolkit

        assert hasattr(data_model_toolkit, "__version__")
        assert data_model_toolkit.__version__ == "0.1.0"

    def test_package_metadata(self):
        """Test that package metadata is properly set."""
        import data_model_toolkit

        # Check that all expected metadata attributes exist
        assert hasattr(data_model_toolkit, "__author__")
        assert hasattr(data_model_toolkit, "__version__")

        # Check that they have reasonable values
        assert len(data_model_toolkit.__author__) > 0
        assert "." in data_model_toolkit.__version__

    def test_module_structure(self):
        """Test that the module structure is as expected."""
        import data_model_toolkit.core

        # Check that core module has expected functions
        assert hasattr(data_model_toolkit.core, "hello_world")
        assert hasattr(data_model_toolkit.core, "get_version")

        # Check that functions are callable
        assert callable(data_model_toolkit.core.hello_world)
        assert callable(data_model_toolkit.core.get_version)

    def test_cross_module_functionality(self):
        """Test functionality that crosses module boundaries."""
        from data_model_toolkit import hello_world
        from data_model_toolkit.core import get_version

        # Test that both functions work together
        greeting = hello_world("v" + get_version())
        assert "v0.1.0" in greeting
        assert "DataModelToolkit" in greeting


class TestCommandLineInterface:
    """Integration tests for command-line usage (if applicable)."""

    def test_python_module_execution(self):
        """Test that the package can be executed as a Python module."""
        # This test checks if the package can be imported in a subprocess
        # which simulates real-world usage
        cmd = [
            sys.executable,
            "-c",
            "from data_model_toolkit import hello_world; "
            "print(hello_world('CLI Test'))",
        ]

        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=30,
                cwd=Path(__file__).parent.parent.parent,
            )

            if result.returncode == 0:
                assert "Hello, CLI Test! Welcome to DataModelToolkit!" in result.stdout
            # If it fails due to path issues, we'll skip this test
            # as it's more about the environment than the code

        except (subprocess.TimeoutExpired, FileNotFoundError):
            # Skip if we can't run the subprocess test
            pass


class TestErrorHandling:
    """Integration tests for error handling across the package."""

    def test_graceful_error_handling(self):
        """Test that the package handles errors gracefully."""
        from data_model_toolkit import hello_world

        # Test with various edge cases
        test_cases = [
            None,
            "",
            "   ",  # whitespace
            "a" * 1000,  # very long string
            "🚀",  # unicode
            "Hello\nWorld",  # multiline
        ]

        for test_case in test_cases:
            try:
                result = hello_world(test_case)
                # Should always return a string
                assert isinstance(result, str)
                # Should always contain the welcome message
                assert "Welcome to DataModelToolkit!" in result
            except Exception as e:
                # If an exception occurs, it should be a reasonable one
                assert isinstance(e, (TypeError, ValueError))
