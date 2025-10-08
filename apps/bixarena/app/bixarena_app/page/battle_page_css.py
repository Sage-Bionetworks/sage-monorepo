"""
CSS styles for the BixArena battle page.
"""

# CSS for example prompt cards and navigation
EXAMPLE_PROMPTS_CSS = """

/* Example prompt UI section */
#prompt-card-section > .row {
    display: flex;           
    flex-direction: row;      
    flex-wrap: nowrap;      
    align-items: center;     
    gap: 16px; 
    width: 100%;
}

#prompt-card-section > .row > .row {
    flex: 1 1 auto;
    min-width: 0;
    display: flex;
    flex-direction: row;
    gap: 16px;
    align-items: center;
}

/* Example prompt card */
.prompt-card-container {
    padding: 12px 16px;
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.2s ease;
    flex: 1 1 0;
    min-width: 0;
    width: auto;
    margin: 0;
}

.gradio-container .prompt-card-container:hover {
    background: rgba(255, 255, 255, 0.08);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.gradio-container button.prompt-card {
    background: transparent !important;
    padding: 0px;
    text-align: left;
    font-size: 14px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
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
    align-self: center;
    background: transparent;
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.2s ease;
}

.nav-button {
    aspect-ratio: 1 / 1;
    box-sizing: border-box;
    user-select: none;
}

.nav-button.left { margin-right: 8px; }
.nav-button.right { margin-left: 8px; }

#prompt-card-section > .row > .nav-button:not(:disabled):hover {
    background: rgba(255, 255, 255, 0.08);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Hide the left nav button while disabled */
.nav-button.left[disabled] {
    visibility: hidden;
    opacity: 0;
    pointer-events: none;
}
"""

# CSS for input textbox styling
INPUT_PROMPT_CSS = """
#input_box.prompt_input {
    background: var(--background-fill-primary);
}

#input_box.prompt_input textarea {
    border-radius: 12px;
    overflow-y: auto !important;
}

.form:has(.prompt_input) {
    border: none;
    box-shadow: none;
}
"""
