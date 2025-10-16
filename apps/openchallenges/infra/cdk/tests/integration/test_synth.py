"""Integration tests for CDK stack synthesis."""

import os
import subprocess
from pathlib import Path
from unittest.mock import patch


class TestSynthesis:
    """Tests for CDK stack synthesis."""

    def test_dev_synth(self):
        """Test dev environment synthesis."""
        with patch.dict(
            os.environ, {"ENV": "dev", "DEVELOPER_NAME": "testuser"}, clear=True
        ):
            result = subprocess.run(
                ["cdk", "synth", "--quiet"],
                cwd=Path(__file__).parent.parent.parent,
                capture_output=True,
                text=True,
            )

            # Check that synthesis succeeded
            assert result.returncode == 0, f"CDK synth failed: {result.stderr}"

            # Check that output contains expected stack name
            assert "openchallenges-dev-testuser-buckets" in result.stdout

    def test_stage_synth(self):
        """Test stage environment synthesis."""
        with patch.dict(os.environ, {"ENV": "stage"}, clear=True):
            result = subprocess.run(
                ["cdk", "synth", "--quiet"],
                cwd=Path(__file__).parent.parent.parent,
                capture_output=True,
                text=True,
            )

            # Check that synthesis succeeded
            assert result.returncode == 0, f"CDK synth failed: {result.stderr}"

            # Check that output contains expected stack name
            assert "openchallenges-stage-buckets" in result.stdout

    def test_prod_synth(self):
        """Test prod environment synthesis."""
        with patch.dict(os.environ, {"ENV": "prod"}, clear=True):
            result = subprocess.run(
                ["cdk", "synth", "--quiet"],
                cwd=Path(__file__).parent.parent.parent,
                capture_output=True,
                text=True,
            )

            # Check that synthesis succeeded
            assert result.returncode == 0, f"CDK synth failed: {result.stderr}"

            # Check that output contains expected stack name
            assert "openchallenges-prod-buckets" in result.stdout
