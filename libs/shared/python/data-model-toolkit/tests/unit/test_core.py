"""
Unit tests for the core module.
"""

from data_model_toolkit.core import get_version, hello_world


class TestHelloWorld:
    """Test cases for the hello_world function."""

    def test_hello_world_default(self):
        """Test hello_world with default parameters."""
        result = hello_world()
        assert result == "Hello, World! Welcome to DataModelToolkit!"

    def test_hello_world_with_name(self):
        """Test hello_world with a specific name."""
        result = hello_world("Alice")
        assert result == "Hello, Alice! Welcome to DataModelToolkit!"

    def test_hello_world_with_empty_string(self):
        """Test hello_world with an empty string."""
        result = hello_world("")
        assert result == "Hello, ! Welcome to DataModelToolkit!"

    def test_hello_world_with_none(self):
        """Test hello_world with None explicitly passed."""
        result = hello_world(None)
        assert result == "Hello, World! Welcome to DataModelToolkit!"

    def test_hello_world_return_type(self):
        """Test that hello_world returns a string."""
        result = hello_world()
        assert isinstance(result, str)


class TestGetVersion:
    """Test cases for the get_version function."""

    def test_get_version_returns_string(self):
        """Test that get_version returns a string."""
        version = get_version()
        assert isinstance(version, str)

    def test_get_version_is_not_empty(self):
        """Test that get_version returns a non-empty string."""
        version = get_version()
        assert len(version) > 0

    def test_get_version_matches_package_version(self):
        """Test that get_version returns the expected version."""
        version = get_version()
        assert version == "0.1.0"


class TestModuleIntegrity:
    """Test cases for module-level integrity."""

    def test_module_imports(self):
        """Test that all expected functions can be imported."""
        from data_model_toolkit import hello_world

        assert callable(hello_world)

    def test_module_attributes(self):
        """Test that module has expected attributes."""
        import data_model_toolkit

        assert hasattr(data_model_toolkit, "__version__")
        assert hasattr(data_model_toolkit, "__author__")
