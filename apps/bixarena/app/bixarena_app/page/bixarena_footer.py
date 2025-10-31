import os

import gradio as gr


def build_footer():
    """Build footer with branding and links."""
    # Get app version from environment variable or default to v1.0.0
    app_version = os.environ.get("APP_VERSION", "1.0.0")

    # Footer configuration
    tos_url = os.environ.get("APP_TOS_URL", "")
    contact_url = os.environ.get("APP_CONTACT_URL", "")
    issues_url = os.environ.get("APP_ISSUE_URL", "")

    # TODO: Replace with actual Sage Bionetworks logo image
    # Logo is available at: apps/bixarena/app/bixarena_app/static/sage-logo.png

    footer = gr.HTML(
        f"""
<style>
/* Remove default Gradio HTML container padding */
.footer-no-padding {{
    padding: 0 !important;
}}

.custom-footer {{
    width: 100%;
    border-top: 2px solid rgba(255, 255, 255, 0.2);
    padding: 32px 40px;
    margin-top: 60px;
    pointer-events: auto !important;
}}

.footer-container {{
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    max-width: 100%;
}}

.footer-left {{
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 14px;
}}

.footer-separator {{
    margin: 0 8px;
    color: #52525b !important;
}}

.footer-right {{
    display: flex;
    align-items: center;
    gap: 24px;
    font-size: 14px;
}}

.footer-link {{
    text-decoration: none !important;
    transition: color 0.2s ease;
    color: var(--body-text-color) !important;
}}

.footer-link:hover {{
    color: #f97316 !important;
}}

@media (max-width: 768px) {{
    .footer-container {{
        flex-direction: column;
        gap: 24px;
    }}

    .footer-left, .footer-right {{
        flex-direction: column;
        text-align: center;
    }}
}}
</style>

<div class="custom-footer">
    <div class="footer-container">
        <!-- Left section - Branding -->
        <div class="footer-left">
            <span>Powered by</span>
            <span style="font-weight: 600; color: white;">Sage Bionetworks</span>
            <span class="footer-separator">•</span>
            <span>v{app_version}</span>
        </div>

        <!-- Right section - Links -->
        <div class="footer-right">
            <a href="{tos_url}" class="footer-link" target="_blank" rel="noopener noreferrer">
                Terms of Service
            </a>
            <span class="footer-separator">•</span>
            <a href="{contact_url}" class="footer-link" target="_blank" rel="noopener noreferrer">
                Contact Us
            </a>
            <span class="footer-separator">•</span>
            <a href="{issues_url}" class="footer-link" target="_blank" rel="noopener noreferrer">
                Report Issue
            </a>
        </div>
    </div>
</div>
        """,
        elem_classes="footer-no-padding",
    )

    return footer
