"""Shared base gateway providing API client construction.

Centralizes creation of the generated SDK `ApiClient` with host + API key
injection so individual resource gateways stay DRY. If we later add common
headers (e.g., custom User-Agent) or timeouts, we do it in one place.
"""

from __future__ import annotations

import openchallenges_api_client

from ..config.loader import ClientConfig


class BaseGateway:
    def __init__(self, config: ClientConfig) -> None:  # pragma: no cover - simple
        self._cfg = config

    def _api_client(self) -> openchallenges_api_client.ApiClient:
        """Create an ApiClient configured with host and optional API key.

        The generated client maps `apiKey` logical name to the header
        `X-API-Key`. We only set it if present so anonymous-friendly endpoints
        continue to work without credentials.
        """
        cfg = openchallenges_api_client.Configuration(host=self._cfg.api_url)
        if self._cfg.api_key:
            cfg.api_key["apiKey"] = self._cfg.api_key
        return openchallenges_api_client.ApiClient(cfg)
