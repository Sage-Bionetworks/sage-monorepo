"""System messages displayed to users in the chat UI (not LLM chat content)."""

CONTINUATION_PROMPT = (
    "The response reached the maximum token limit and was truncated.<br>"
    "Would you like the model to resume streaming its response?"
)


def create_system_message_html(text: str) -> str:
    """Wrap a system message in styled HTML for Gradio chatbot display."""
    return (
        '<div class="system-message">'
        '<div class="system-message-content">'
        '<span class="system-icon">ⓘ</span>'
        f'<div class="system-text">{text}</div>'
        "</div></div>"
    )
