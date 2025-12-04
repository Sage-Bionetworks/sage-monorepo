"""
CSS styles for the BixArena battle page.
"""

# CSS for chatbot battle interface
CHATBOT_BATTLE_CSS = """
/* System message styling */
.message.bot.panel-full-width.thought:has(.system-message) {
    border: none;
    padding: 0;
    margin-top: 24px;
    background: transparent;
}

.system-message {
    border: 1px solid var(--accent-teal) !important;
    border-radius: 18px;
    padding: 6px 16px;
}

/* Remove outer border for system message */
#chatbot-container .message.bot.panel-full-width:has(.system-message) {
    border: none !important;
}

.system-message-content {
    display: flex;
    gap: 12px;
}

.system-message .system-icon {
    color: var(--accent-teal);
    font-size: var(--text-xl);
}

/* Separate the two chat windows */
#chatbot-container {
    background: transparent !important;
    border: none !important;
}

#chatbot-container .styler {
    background: transparent !important;
}

#chatbot-container .row {
    gap: 24px;
}

#chatbot-container > .styler > .row:first-child .column {
    border: 1px solid var(--border-color-primary);
    border-radius: 8px;
    overflow: hidden;
}

/* Make chatbot labels bigger */
#chatbot-container .block label {
    font-size: var(--text-md);
}

/* Responsive chatbot height based on viewport */
/* Add bottom padding to chatbot block to prevent message cropping */
#chatbot-container #chatbot {
    height: max(50svh, 350px) !important;
    padding-bottom: 16px;
}

/* Set border radius and remove borders for all message bubbles */
#chatbot-container .message.panel-full-width {
    border-radius: 18px !important;
    padding: 6px 16px !important;
    border: none !important;
}
/* Make all bot message bubbles transparent for light/dark theme compatibility */
#chatbot-container .message.bot.panel-full-width {
    background: transparent !important;
}

/* Make label icon match text size */
#chatbot-container .block label span {
    width: 1em;
    height: 1em;
}

/* Chatbot footer to reveal model names */
#chatbot-container .html-container {
    padding: 0 !important;
}

#chatbot-container .column > .block:has(.html-container) {
    margin-top: -24px;
}

.model-name-footer {
    padding: 12px 16px;
    background: var(--color-accent-soft);
    text-align: center;
    color: var(--body-text-color);
    font-weight: 500;
}

/* Responsive layout */
@media (max-width: 768px) {
    #chatbot-container #chatbot {
        height: max(50svh, 280px) !important;
    }

    #chatbot-container .row {
        gap: 16px;
    }
}
"""

# CSS for example prompt cards and navigation
EXAMPLE_PROMPTS_CSS = """
/* Example prompt UI section */
#prompt-card-section > .row {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-top: 16px;
}

#prompt-card-section > .row > .row {
    display: flex;
    align-items: stretch;
    gap: 12px;
}

/* Example prompt card wrapper */
#prompt-card-section .prompt-card-wrapper {
    flex: 1 1 0;
    display: flex;
    padding-top: 2px !important;
}

#prompt-card-section .prompt-card-wrapper > div {
    display: flex;
    flex: 1;
}

/* Example prompt card button */
#prompt-card-section button.prompt-card {
    background: transparent;
    border: 1px solid var(--border-color-primary);
    border-radius: 8px;
    padding: 12px 16px;
    transition: all 0.2s ease;
    width: 100%;
    height: auto;
    min-height: 89px;
    display: flex;
    align-items: flex-start;
    cursor: pointer;
}

#prompt-card-section button.prompt-card:hover {
    background: var(--panel-background-fill);
    transform: translateY(-1px);
}

#prompt-card-section button.prompt-card .prompt-text {
    text-align: left;
    font-size: var(--text-md);
    line-height: 1.5;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 4;
    overflow: hidden;
}

/* Nav arrow buttons */
#prompt-card-section > .row > button.nav-button {
    flex: 0 0 36px;
    width: 36px;
    min-width: 36px;
    max-width: 36px;
    height: 36px;
    padding: 0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: transparent;
    border: 1px solid var(--border-color-primary);
    transition: all 0.2s ease;
}

.nav-button {
    user-select: none;
}

#prompt-card-section > .row > .nav-button:not(:disabled):hover {
    background: var(--panel-background-fill);
    transform: translateY(-1px);
}

/* Hide the left nav button while disabled */
.nav-button.left[disabled] {
    visibility: hidden;
    opacity: 0;
    pointer-events: none;
}


/* Responsive layout */
@media (max-width: 1280px) {
    #prompt-card-section button.prompt-card .prompt-text {
        -webkit-line-clamp: unset;
    }
}

@media (max-width: 1024px) {
    #prompt-card-section > .row {
        align-items: center;
        flex-wrap: nowrap;
    }

    #prompt-card-section > .row > .row {
        flex-direction: column;
        gap: 8px;
    }

    #prompt-card-section .prompt-card-wrapper {
        width: 100%;
        flex: 0 0 auto;
    }

    #prompt-card-section button.prompt-card {
        align-items: center;
    }
}
"""

# CSS for input textbox styling
INPUT_PROMPT_CSS = """
/* Container for the input textbox - limit width and center */
#input_box.prompt_input {
    background: var(--background-fill-primary);
    max-width: 900px;
    margin: 0 auto;
    width: 100%;
}

#input_box.prompt_input textarea {
    border-radius: 12px;
    overflow-y: auto !important;
    padding: 16px 20px;
    line-height: 1.5;
}

.form:has(.prompt_input) {
    border: none;
    box-shadow: none;
}

/* Also limit the parent row container */
.row:has(#input_box.prompt_input) {
    max-width: 900px;
    margin: 0 auto;
}

/* Responsive layout */
@media (max-width: 768px) {
    .row:has(#input_box.prompt_input) {
        padding: 0 8px;
    }

    #input_box.prompt_input textarea {
        padding: 12px 16px;
        font-size: var(--text-md);
    }
}
"""

# CSS for disclaimer
DISCLAIMER_CSS = """
#disclaimer {
    padding: 16px 24px;
    max-width: 850px;
    margin: 0 auto;
}

#disclaimer-content {
    text-align: center;
}

#disclaimer-title {
    color: var(--accent-teal);
    font-weight: 600;
    margin-bottom: 8px;
    margin-top: 0;
}

#disclaimer-text {
    color: var(--body-text-color-subdued);
    line-height: 1.6;
    margin: 0;
}

#disclaimer-text strong {
    font-weight: 700;
}
"""

# CSS for Next Battle button
NEXT_BATTLE_BUTTON_CSS = """
#next-battle-row {
    justify-content: center;
}

#next-battle-btn {
    max-width: 240px;
}
"""
