"""Battle session state for the Gradio UI."""

from typing import NamedTuple
from uuid import UUID

from bixarena_app.config.constants import PROMPT_USE_LIMIT
from bixarena_app.config.system_message import (
    CONTINUATION_PROMPT,
    create_system_message_html,
)


class ChatMessage(NamedTuple):
    """A single chat message for Gradio display (not the backend API Message entity)."""

    role: str
    content: str | None


class State:
    """Per-model state within a battle (messages, streaming flags)."""

    def __init__(self, model_name: str, model_id: UUID):
        self.messages: list[ChatMessage] = []
        self.model_name = model_name
        self.model_id = model_id
        self.skip_next = False
        self.has_error = False
        self.is_truncated = False

    def append_message(self, role: str, content: str | None):
        self.messages.append(ChatMessage(role, content))

    def update_last_message(self, content: str):
        self.messages[-1] = ChatMessage(self.messages[-1].role, content)

    def to_gradio_chatbot(self):
        ret = []
        for msg in self.messages:
            if msg.content is not None:
                content = msg.content
                if msg.content == CONTINUATION_PROMPT:
                    content = create_system_message_html(msg.content)
                ret.append({"role": msg.role, "content": content})
        return ret


class BattleSession:
    """Track the active battle and round identifiers for the Gradio session."""

    def __init__(self):
        self.battle_id: UUID | None = None
        self.round_id: UUID | None = None
        self.round_count: int = 0
        self.last_prompt: str | None = None
        self.prompt_use_remaining: int = PROMPT_USE_LIMIT

    def reset(self):
        """Reset battle/round IDs while preserving prompt history for reuse."""
        self.battle_id = None
        self.round_id = None
        self.round_count = 0
