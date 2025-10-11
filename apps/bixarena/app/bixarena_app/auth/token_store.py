import time


class _TokenStore:
    def __init__(self):
        self._token: str | None = None
        self._exp: float = 0.0

    def set(self, token: str, expires_in: int):
        self._token = token
        self._exp = time.time() + expires_in - 5  # 5s skew

    def get(self) -> str | None:
        if not self._token:
            return None
        if time.time() >= self._exp:
            # expired
            self._token = None
            return None
        return self._token


TOKEN_STORE = _TokenStore()
