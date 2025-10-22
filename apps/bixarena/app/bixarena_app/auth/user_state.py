import time
from typing import Any


class UserState:
    """Minimal in-memory user state"""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return

        self._current_user = None
        self._session_timestamp = None
        self._error_message = None
        self._initialized = True

    def get_current_user(self) -> dict[str, Any] | None:
        return self._current_user

    def set_current_user(self, user_data: dict[str, Any]) -> None:
        self._current_user = user_data
        self._session_timestamp = time.time()
        self._error_message = None

    def clear_session(self) -> None:
        self._current_user = None
        self._session_timestamp = None
        self._error_message = None

    def is_authenticated(self) -> bool:
        return self.get_current_user() is not None

    def get_display_name(self) -> str:
        user = self.get_current_user()
        if not user:
            return "Guest"
        return user.get("firstName", user.get("userName", "User"))

    def set_error(self, message: str) -> None:
        self._error_message = message

    def get_error(self) -> str | None:
        return self._error_message


def get_user_state() -> UserState:
    return UserState()
