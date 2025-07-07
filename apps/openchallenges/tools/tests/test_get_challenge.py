"""Unit tests for get_challenge function."""

from unittest.mock import MagicMock, Mock, patch

import pytest
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.rest import ApiException

from openchallenges_tools.get_challenge import get_challenge


class TestGetChallenge:
    """Test class for get_challenge function."""

    @patch(
        "openchallenges_tools.get_challenge.openchallenges_api_client_python.ApiClient"
    )
    def test_get_challenge_success(self, mock_api_client_class):
        """Test successful challenge retrieval."""
        # Arrange
        challenge_id = 516
        mock_challenge = Mock(spec=Challenge)
        mock_challenge.id = challenge_id
        mock_challenge.name = "Test Challenge"

        mock_api_client = MagicMock()
        mock_api_client_class.return_value.__enter__.return_value = mock_api_client

        mock_challenge_api = Mock()
        mock_challenge_api.get_challenge.return_value = mock_challenge

        with patch(
            "openchallenges_tools.get_challenge.openchallenges_api_client_python.ChallengeApi",
            return_value=mock_challenge_api,
        ):
            # Act
            result = get_challenge(challenge_id)

            # Assert
            assert result == mock_challenge
            mock_challenge_api.get_challenge.assert_called_once_with(challenge_id)
            mock_api_client_class.assert_called_once()

    @patch(
        "openchallenges_tools.get_challenge.openchallenges_api_client_python.ApiClient"
    )
    def test_get_challenge_api_exception(self, mock_api_client_class):
        """Test that ApiException is properly re-raised."""
        # Arrange
        challenge_id = 516
        api_exception = ApiException(status=404, reason="API Error")

        mock_api_client = MagicMock()
        mock_api_client_class.return_value.__enter__.return_value = mock_api_client

        mock_challenge_api = Mock()
        mock_challenge_api.get_challenge.side_effect = api_exception

        with patch(
            "openchallenges_tools.get_challenge.openchallenges_api_client_python.ChallengeApi",
            return_value=mock_challenge_api,
        ):
            # Act & Assert
            with pytest.raises(ApiException) as exc_info:
                get_challenge(challenge_id)

            assert exc_info.value == api_exception
            mock_challenge_api.get_challenge.assert_called_once_with(challenge_id)

    @patch(
        "openchallenges_tools.get_challenge.openchallenges_api_client_python.ApiClient"
    )
    @patch("builtins.print")
    def test_get_challenge_prints_error_message(
        self, mock_print, mock_api_client_class
    ):
        """Test that error message is printed when ApiException occurs."""
        # Arrange
        challenge_id = 516
        api_exception = ApiException(status=404, reason="API Error")

        mock_api_client = MagicMock()
        mock_api_client_class.return_value.__enter__.return_value = mock_api_client

        mock_challenge_api = Mock()
        mock_challenge_api.get_challenge.side_effect = api_exception

        with patch(
            "openchallenges_tools.get_challenge.openchallenges_api_client_python.ChallengeApi",
            return_value=mock_challenge_api,
        ):
            # Act & Assert
            with pytest.raises(ApiException):
                get_challenge(challenge_id)

            # Assert print was called with error message
            expected_message = (
                f"Exception when calling ChallengeApi->get_challenge: {api_exception}\n"
            )
            mock_print.assert_called_with(expected_message)

    @patch(
        "openchallenges_tools.get_challenge.openchallenges_api_client_python.ApiClient"
    )
    def test_get_challenge_with_different_challenge_ids(self, mock_api_client_class):
        """Test get_challenge works with different challenge IDs."""
        # Arrange
        test_ids = [1, 100, 516, 999]

        mock_api_client = MagicMock()
        mock_api_client_class.return_value.__enter__.return_value = mock_api_client

        mock_challenge_api = Mock()

        with patch(
            "openchallenges_tools.get_challenge.openchallenges_api_client_python.ChallengeApi",
            return_value=mock_challenge_api,
        ):
            for challenge_id in test_ids:
                mock_challenge = Mock(spec=Challenge)
                mock_challenge.id = challenge_id
                mock_challenge_api.get_challenge.return_value = mock_challenge

                # Act
                result = get_challenge(challenge_id)

                # Assert
                assert result.id == challenge_id
                mock_challenge_api.get_challenge.assert_called_with(challenge_id)

    @patch("openchallenges_tools.get_challenge.configuration")
    @patch(
        "openchallenges_tools.get_challenge.openchallenges_api_client_python.ApiClient"
    )
    def test_get_challenge_uses_correct_configuration(
        self, mock_api_client_class, mock_configuration
    ):
        """Test that the function uses the correct configuration."""
        # Arrange
        challenge_id = 516
        mock_challenge = Mock(spec=Challenge)

        mock_api_client = MagicMock()
        mock_api_client_class.return_value.__enter__.return_value = mock_api_client

        mock_challenge_api = Mock()
        mock_challenge_api.get_challenge.return_value = mock_challenge

        with patch(
            "openchallenges_tools.get_challenge.openchallenges_api_client_python.ChallengeApi",
            return_value=mock_challenge_api,
        ):
            # Act
            get_challenge(challenge_id)

            # Assert
            mock_api_client_class.assert_called_once_with(mock_configuration)
