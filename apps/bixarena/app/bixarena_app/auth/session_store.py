"""
Server-side session storage with cleanup
"""

import json
import os
import threading
import time
from abc import ABC, abstractmethod
from typing import Dict, Any, Optional


class SessionStore(ABC):
    """Abstract session store interface"""

    @abstractmethod
    def get(self, session_id: str) -> Optional[Dict[str, Any]]:
        """Get session data by ID"""
        pass

    @abstractmethod
    def set(self, session_id: str, data: Dict[str, Any]) -> None:
        """Set session data"""
        pass

    @abstractmethod
    def delete(self, session_id: str) -> None:
        """Delete session by ID"""
        pass

    @abstractmethod
    def cleanup_expired(self) -> None:
        """Clean up expired sessions"""
        pass


class MemorySessionStore(SessionStore):
    """In-memory session store with automatic cleanup"""

    def __init__(self):
        self._sessions: Dict[str, Dict[str, Any]] = {}
        self._lock = threading.RLock()
        self._start_cleanup_thread()

    def get(self, session_id: str) -> Optional[Dict[str, Any]]:
        """Get session data by ID"""
        with self._lock:
            return self._sessions.get(session_id)

    def set(self, session_id: str, data: Dict[str, Any]) -> None:
        """Set session data"""
        with self._lock:
            self._sessions[session_id] = data

    def delete(self, session_id: str) -> None:
        """Delete session by ID"""
        with self._lock:
            self._sessions.pop(session_id, None)

    def cleanup_expired(self) -> None:
        """Clean up expired sessions"""
        current_time = time.time()
        expired_sessions = []

        with self._lock:
            for session_id, data in self._sessions.items():
                # Remove sessions older than 2 hours (cookie expiry)
                if current_time - data.get("first_login_at", 0) > 2 * 3600:
                    expired_sessions.append(session_id)

            for session_id in expired_sessions:
                del self._sessions[session_id]

        if expired_sessions:
            print(f"🧹 Cleaned up {len(expired_sessions)} expired sessions")

    def _start_cleanup_thread(self) -> None:
        """Start background cleanup thread"""

        def cleanup_worker():
            while True:
                time.sleep(3600)  # Clean up every hour
                try:
                    self.cleanup_expired()
                except Exception as e:
                    print(f"Session cleanup error: {e}")

        cleanup_thread = threading.Thread(target=cleanup_worker, daemon=True)
        cleanup_thread.start()


def create_session_store() -> SessionStore:
    """Create session store"""
    return MemorySessionStore()
