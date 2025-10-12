from bixarena_app.auth.session_manager import get_session


class AuthService:
    """Authentication service (transient in-memory user only)"""

    def __init__(self):
        self.session = get_session()

    # The Java backend performs OIDC; this service only stores transient user state
    # populated by backend sync.

    def logout(self) -> None:
        """Logout current user"""
        username = self.session.get_display_name()
        self.session.clear_session()
        print(f"👋 User logged out: {username}")

    def is_authenticated(self) -> bool:
        """Check if user is authenticated"""
        return self.session.is_authenticated()

    def get_current_user(self) -> dict | None:
        """Get current user data"""
        return self.session.get_current_user()

    def get_display_name(self) -> str:
        """Get current user display name"""
        return self.session.get_display_name()


_auth_service = None


def get_auth_service() -> AuthService:
    """Get the global authentication service instance"""
    global _auth_service
    if _auth_service is None:
        _auth_service = AuthService()
    return _auth_service
