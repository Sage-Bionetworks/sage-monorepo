"""
Server-side session storage with cleanup and Redis support
"""

import time
import threading
import os
from typing import Dict, Any, Optional
from abc import ABC, abstractmethod


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
                # Remove sessions older than 24 hours (cookie expiry)
                if current_time - data.get("created_at", 0) > 24 * 3600:
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


class RedisSessionStore(SessionStore):
    """Redis-based session store for production"""

    def __init__(self, redis_url: str = None):
        try:
            import redis

            self.redis = redis.from_url(redis_url or "redis://localhost:6379")
            self.redis.ping()  # Test connection
            print("✅ Connected to Redis for session storage")
        except ImportError:
            raise ImportError("Redis package not installed. Run: pip install redis")
        except Exception as e:
            raise ConnectionError(f"Failed to connect to Redis: {e}")

    def get(self, session_id: str) -> Optional[Dict[str, Any]]:
        """Get session data by ID"""
        try:
            import json

            data = self.redis.get(f"session:{session_id}")
            return json.loads(data) if data else None
        except Exception as e:
            print(f"Redis get error: {e}")
            return None

    def set(self, session_id: str, data: Dict[str, Any]) -> None:
        """Set session data with TTL"""
        try:
            import json

            self.redis.setex(
                f"session:{session_id}",
                24 * 3600,  # 24 hours TTL (matches cookie expiry)
                json.dumps(data, default=str),
            )
        except Exception as e:
            print(f"Redis set error: {e}")

    def delete(self, session_id: str) -> None:
        """Delete session by ID"""
        try:
            self.redis.delete(f"session:{session_id}")
        except Exception as e:
            print(f"Redis delete error: {e}")

    def cleanup_expired(self) -> None:
        """Redis handles TTL automatically, no manual cleanup needed"""
        pass


def create_session_store() -> SessionStore:
    """Create appropriate session store based on environment"""
    redis_url = os.environ.get("REDIS_URL")

    if redis_url and os.environ.get("ENVIRONMENT") == "production":
        try:
            return RedisSessionStore(redis_url)
        except Exception as e:
            print(f"⚠️  Failed to create Redis store, falling back to memory: {e}")

    return MemorySessionStore()
