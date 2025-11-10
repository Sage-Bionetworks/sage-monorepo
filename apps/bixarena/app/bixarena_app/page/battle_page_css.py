"""
CSS styles for the BixArena battle page.
"""

# CSS for example prompt cards and navigation
EXAMPLE_PROMPTS_CSS = """
/* Example prompt UI section */
#prompt-card-section > .row {
    display: flex;
    align-items: center;
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
}

#prompt-card-section .prompt-card-wrapper > div {
    display: flex;
    flex: 1;
}

/* Example prompt card button */
#prompt-card-section button.prompt-card {
    background: transparent;
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    padding: 12px 16px;
    transition: all 0.2s ease;
    width: 100%;
    height: auto;
    min-height: 87px;
    display: flex;
    align-items: flex-start;
    cursor: pointer;
}

#prompt-card-section button.prompt-card:hover {
    background: rgba(255, 255, 255, 0.08);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

#prompt-card-section button.prompt-card .prompt-text {
    text-align: left;
    font-size: 14px;
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
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.2s ease;
}

.nav-button {
    user-select: none;
}

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


/* Responsive layout */
@media (min-width: 1024px) and (max-width: 1280px) {
    #prompt-card-section button.prompt-card .prompt-text {
        -webkit-line-clamp: unset;
    }
}

@media (max-width: 1024px) {
    /* Hide example prompts entirely on mobile */
    #prompt-card-section {
        display: none !important;
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
"""

# CSS for disclaimer
DISCLAIMER_CSS = """
#disclaimer {
    padding: 16px 24px;
}

#disclaimer-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}

#disclaimer-text {
    display: flex;
    align-items: center;
    gap: 8px;
}

.pulse-dot {
    width: 6px;
    height: 6px;
    background-color: rgba(245, 158, 11, 1);
    border-radius: 50%;
}
"""
