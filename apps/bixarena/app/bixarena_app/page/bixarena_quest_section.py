"""Community Quest section component for BioArena home page."""

from datetime import datetime

import gradio as gr

# Quest configuration - hardcoded for Season 1
QUEST_CONFIG = {
    "title": "Building BioArena",
    "description": "We are constructing a medieval arena in Minecraft to symbolize our collective effort. Every battle counts towards the build.",
    "goal": 2850,
    "start_date": "2026-01-20",
    "end_date": "2026-04-20",
    "conversion_text": "1 Battle = 1 Block",
    "conversion_description": "Every time you evaluate a model, you earn a block that will be placed by the BioArena team in Minecraft.",
    "carousel_rotation_interval": 6000,  # Duration in milliseconds for each image
    "current_update": {
        "title": "Week 1 Update",
        "description": "We are building an inner wall of three blocks high with cobblestone blocks. (115 Blocks placed so far).",
        "images": [
            "https://placehold.co/1920x1080/1a1a1a/14b8a6?text=View+1",
            "https://placehold.co/1920x1080/1a1a1a/14b8a6?text=View+2",
            "https://placehold.co/1920x1080/1a1a1a/14b8a6?text=View+3",
        ],
    },
}


def build_quest_section(
    progress_data: dict | None = None,
) -> tuple[gr.Column, gr.HTML, gr.Button, gr.Button, gr.Button, str, int]:
    """Build the Community Quest section for home page.

    Args:
        progress_data: Optional dict with quest progress info (current_blocks, goal_blocks, percentage, days_remaining)

    Returns:
        Tuple of (quest_container, progress_html_container, contribute_button_authenticated,
                  contribute_button_login, carousel_init_trigger, carousel_id, rotation_interval)
    """
    # Use provided progress data or defaults
    if progress_data is None:
        from datetime import datetime

        end_date = datetime.strptime(QUEST_CONFIG["end_date"], "%Y-%m-%d")
        days_remaining = max(0, (end_date - datetime.now()).days)
        progress_data = {
            "current_blocks": 0,
            "goal_blocks": QUEST_CONFIG["goal"],
            "percentage": 0.0,
            "days_remaining": days_remaining,
        }

    current_blocks = progress_data["current_blocks"]
    goal_blocks = progress_data["goal_blocks"]
    percentage = progress_data["percentage"]
    days_remaining = progress_data["days_remaining"]

    # Generate unique ID for this carousel instance
    carousel_id = f"quest-carousel-{int(datetime.now().timestamp() * 1000)}"

    # Generate carousel images HTML
    images_html = ""
    indicators_html = ""
    for i, image_url in enumerate(QUEST_CONFIG["current_update"]["images"]):
        active_class = "active" if i == 0 else ""
        images_html += f'<img src="{image_url}" class="carousel-image {active_class}" alt="Minecraft arena progress" />\n'
        indicators_html += f'<span class="indicator {active_class}" data-index="{i}" role="button" tabindex="0" aria-label="View image {i + 1}"></span>\n'

    # Only show indicators if more than one image
    indicators_display = (
        "" if len(QUEST_CONFIG["current_update"]["images"]) > 1 else "display: none;"
    )

    # Build the carousel HTML (left column) - vertical stack
    carousel_html = f"""
    <div style="display: flex; flex-direction: column; gap: 1rem;">
        <!-- Image carousel -->
        <div id="{carousel_id}" class="quest-carousel">
            <div class="carousel-container">
                {images_html}
            </div>
        </div>

        <!-- Carousel indicators below image -->
        <div class="carousel-indicators-wrapper" style="{indicators_display}">
            <div class="carousel-indicators">
                {indicators_html}
            </div>
        </div>

        <!-- Update box below indicators -->
        <div class="carousel-update-box">
            <h4 style="color: var(--body-text-color); font-weight: 600; margin: 0 0 0.25rem 0; display: flex; align-items: center; gap: 0.5rem;">
                <span style="width: 8px; height: 8px; border-radius: 50%; background-color: #22c55e;"></span>
                {QUEST_CONFIG["current_update"]["title"]}
            </h4>
            <p style="color: var(--body-text-color-subdued); font-size: 0.875rem; margin: 0; line-height: 1.5;">
                {QUEST_CONFIG["current_update"]["description"]}
            </p>
        </div>
    </div>

    <style>
        .quest-carousel {{
            position: relative;
            width: 100%;
            padding-bottom: 56.25%; /* 16:9 aspect ratio */
            overflow: hidden;
            border-radius: 12px;
            background: #000;
            height: 0; /* Ensure padding-bottom creates the height */
        }}

        .carousel-container {{
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 1;
        }}

        .carousel-image {{
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            opacity: 0;
            transition: opacity 0.6s ease-in-out;
            will-change: opacity;
            z-index: 1;
        }}

        .carousel-image.active {{
            opacity: 1;
            z-index: 2;
        }}

        .carousel-indicators-wrapper {{
            display: flex;
            justify-content: center;
            margin: 0;
        }}

        .carousel-indicators {{
            display: flex;
            gap: 8px;
            padding: 8px 12px;
            background: var(--background-fill-secondary);
            border-radius: 20px;
            border: 1px solid var(--border-color-primary);
        }}

        .indicator {{
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: var(--body-text-color-subdued);
            opacity: 0.5;
            cursor: pointer;
            transition: background 0.3s, transform 0.3s, opacity 0.3s;
        }}

        .indicator:hover {{
            opacity: 0.9;
            transform: scale(1.3);
        }}

        .indicator.active {{
            background: var(--accent-teal, #14b8a6);
            opacity: 1;
            transform: scale(1.15);
        }}

        .carousel-update-box {{
            background: var(--panel-background-fill);
            border: 1px solid var(--border-color-primary);
            border-radius: 8px;
            padding: 1rem;
        }}

        /* Mobile responsive - trigger when columns would be too small */
        /* Left section < 500px OR right section < 400px */
        @media (max-width: 1000px) {{
            #quest-section-grid {{
                grid-template-columns: 1fr !important;
            }}
            .quest-carousel {{
                margin-bottom: 1.5rem;
            }}
        }}
    </style>
    """

    # Build progress and info HTML (right column - without button)
    progress_html = f"""
    <div style="display: flex; flex-direction: column; gap: 1.5rem; height: 100%;">
        <!-- Progress Bar Section -->
        <div>
            <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.5rem;">
                <span style="color: var(--body-text-color); font-weight: 500;">Arena Progress</span>
                <span style="color: var(--body-text-color); font-weight: 600;">{int(percentage)}% Complete</span>
            </div>

            <div style="width: 100%; height: 16px; background-color: var(--background-fill-secondary); border-radius: 8px; overflow: hidden; border: 1px solid var(--border-color-primary); position: relative;">
                <div style="height: 100%; background: linear-gradient(90deg, var(--accent-teal) 0%, #2dd4bf 100%); border-radius: 8px; width: {min(percentage, 100)}%;"></div>
            </div>

            <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 0.75rem;">
                <div>
                    <p style="font-size: 2rem; font-weight: 700; color: var(--body-text-color); margin: 0; line-height: 1;">{current_blocks:,}</p>
                    <p style="font-size: 0.75rem; color: var(--body-text-color-subdued); text-transform: uppercase; letter-spacing: 0.05em; margin: 0.25rem 0 0 0;">Blocks Placed</p>
                </div>
                <div style="text-align: right;">
                    <p style="font-size: 2rem; font-weight: 700; color: var(--body-text-color); margin: 0; line-height: 1;">{goal_blocks:,}</p>
                    <p style="font-size: 0.75rem; color: var(--body-text-color-subdued); text-transform: uppercase; letter-spacing: 0.05em; margin: 0.25rem 0 0 0;">Goal</p>
                </div>
            </div>
        </div>

        <!-- Divider -->
        <div style="height: 1px; background: var(--border-color-primary);"></div>

        <!-- Info Cards -->
        <div style="display: flex; flex-direction: column; gap: 1rem;">
            <!-- Card 1: Conversion -->
            <div style="display: flex; align-items: start; gap: 1rem;">
                <div style="flex-shrink: 0; width: 40px; height: 40px; border-radius: 8px; background: color-mix(in srgb, var(--color-accent) 10%, transparent); border: 1px solid color-mix(in srgb, var(--color-accent) 30%, transparent); display: flex; align-items: center; justify-content: center;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--color-accent)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                    </svg>
                </div>
                <div style="flex: 1;">
                    <h4 style="color: var(--body-text-color); font-weight: 600; margin: 0 0 0.25rem 0; font-size: 1rem;">
                        {QUEST_CONFIG["conversion_text"]}
                    </h4>
                    <p style="color: var(--body-text-color-subdued); font-size: 0.875rem; margin: 0; line-height: 1.5;">
                        {QUEST_CONFIG["conversion_description"]}
                    </p>
                </div>
            </div>

            <!-- Card 2: Time Remaining -->
            <div style="display: flex; align-items: start; gap: 1rem;">
                <div style="flex-shrink: 0; width: 40px; height: 40px; border-radius: 8px; background: color-mix(in srgb, #3b82f6 10%, transparent); border: 1px solid color-mix(in srgb, #3b82f6 30%, transparent); display: flex; align-items: center; justify-content: center;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                </div>
                <div style="flex: 1;">
                    <h4 style="color: var(--body-text-color); font-weight: 600; margin: 0 0 0.25rem 0; font-size: 1rem;">
                        {days_remaining} Days Left
                    </h4>
                    <p style="color: var(--body-text-color-subdued); font-size: 0.875rem; margin: 0; line-height: 1.5;">
                        We have {days_remaining} days to complete the arena in Minecraft before the season ends.
                    </p>
                </div>
            </div>
        </div>
    </div>
    """

    # Build the complete quest section using Gradio layout components
    with gr.Column(elem_id="quest-section-wrapper") as quest_container:
        # Header section (outside bordered box, matching Arena Rules style)
        gr.HTML(f"""
        <div style="padding: 2.5rem 1.5rem;">
            <!-- Section Header -->
            <div style="text-align: center; margin-bottom: 3rem;">
                <!-- Community Quest Badge -->
                <div style="margin-bottom: 1rem;">
                    <span style="display: inline-block; padding: 0.25rem 0.75rem; border-radius: 12px; background: rgba(34, 197, 94, 0.1); border: 1px solid rgba(34, 197, 94, 0.3); color: #22c55e; font-size: 0.875rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em;">
                        Community Quest
                    </span>
                </div>

                <h1 style="font-size: var(--text-section-title); color: var(--body-text-color); margin-bottom: 0.75rem; font-weight: 600;">
                    {QUEST_CONFIG["title"]}
                </h1>

                <p style="color: var(--body-text-color-subdued); font-size: var(--text-xl); max-width: 48rem; margin: 0 auto;">
                    {QUEST_CONFIG["description"]}
                </p>
            </div>
        </div>
        """)

        # Content box (bordered container with carousel and progress)
        with gr.Column(elem_id="quest-content-box"):
            with gr.Row(elem_id="quest-section-grid"):
                with gr.Column(scale=1):
                    gr.HTML(carousel_html)
                with gr.Column(scale=1):
                    # Progress HTML container that can be updated dynamically
                    progress_html_container = gr.HTML(
                        progress_html, elem_id="quest-progress-container"
                    )
                    # Two CTA buttons - visibility controlled by authentication state
                    # Button for authenticated users - navigates to battle page
                    contribute_button_authenticated = gr.Button(
                        "Contribute a Block Now",
                        variant="primary",
                        size="lg",
                        visible=False,
                        elem_id="quest-cta-btn-authenticated",
                        elem_classes=["quest-cta-btn"],
                    )
                    # Button for unauthenticated users - redirects to login
                    contribute_button_login = gr.Button(
                        "Contribute a Block Now",
                        variant="primary",
                        size="lg",
                        visible=False,
                        elem_id="quest-cta-btn-login",
                        elem_classes=["quest-cta-btn"],
                    )

        # Styling
        gr.HTML("""
        <style>
        /* Wrapper for entire quest section */
        #quest-section-wrapper {
            padding: 0 !important;
            gap: 0 !important;
        }
        #quest-section-wrapper > .block {
            padding: 0 !important;
            margin: 0 !important;
            border: none !important;
            box-shadow: none !important;
            gap: 0 !important;
        }

        /* Content box with border */
        #quest-content-box {
            background-color: var(--panel-background-fill);
            border-radius: 12px;
            border: 1px solid var(--border-color-primary);
            margin: 0 1.5rem !important;
            padding: 2.5rem 1.5rem !important;
            gap: 0 !important;
            min-width: 0 !important;
            width: auto !important;
        }
        #quest-content-box > .block {
            padding: 0 !important;
            margin: 0 !important;
            border: none !important;
            box-shadow: none !important;
            gap: 0 !important;
            min-width: 0 !important;
        }

        /* Grid layout for content */
        #quest-section-grid {
            display: grid;
            grid-template-columns: minmax(0, 3fr) minmax(0, 2fr);
            gap: 2rem;
            align-items: start;
            min-width: 0 !important;
        }
        #quest-section-grid > .block {
            padding: 0 !important;
            margin: 0 !important;
            border: none !important;
            box-shadow: none !important;
            gap: 0 !important;
            min-width: 0 !important;
        }

        /* CTA button styling */
        .quest-cta-btn {
            margin-top: 1rem !important;
            width: 100%;
        }
        #quest-cta-btn-authenticated {
            margin-top: 1rem !important;
        }
        #quest-cta-btn-login {
            margin-top: 1rem !important;
        }
        </style>
        """)

        # Hidden button to trigger carousel initialization via JavaScript
        carousel_init_trigger = gr.Button(
            "Init Carousel", visible=False, elem_id="carousel-init-trigger"
        )

    # Get rotation interval from config
    rotation_interval = QUEST_CONFIG.get("carousel_rotation_interval", 6000)

    return (
        quest_container,
        progress_html_container,
        contribute_button_authenticated,
        contribute_button_login,
        carousel_init_trigger,
        carousel_id,
        rotation_interval,
    )
