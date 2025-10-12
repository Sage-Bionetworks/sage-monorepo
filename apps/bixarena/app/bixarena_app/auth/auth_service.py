from bixarena_app.auth.user_state import get_user_state


class AuthService:
    """Authentication service (transient in-memory user only).

    Thin façade over the singleton UserState to keep existing call sites stable.
    """

    def __init__(self):
        self._state = get_user_state()

    def logout(self) -> None:
        """Logout current user and clear state."""
        username = self._state.get_display_name()
        self._state.clear_session()
        print(f"👋 User logged out: {username}")

    def is_authenticated(self) -> bool:
        return self._state.is_authenticated()

    def get_current_user(self) -> dict | None:
        return self._state.get_current_user()

    def get_display_name(self) -> str:
        return self._state.get_display_name()

    def set_current_user(self, user: dict) -> None:
        self._state.set_current_user(user)


_auth_service: AuthService | None = None


def get_auth_service() -> AuthService:
    global _auth_service
    if _auth_service is None:
        _auth_service = AuthService()
    return _auth_service
