"""Community Quest section component for BioArena home page."""

from datetime import datetime

import gradio as gr

# Quest configuration - hardcoded for Season 1
QUEST_CONFIG = {
    "title": "Building BioArena",
    "description": "We are constructing a medieval arena in Minecraft to symbolize our collective effort. Every battle counts towards the build.",
    "goal": 2850,
    "start_date": "2026-02-01",
    "duration_days": 90,
    "conversion_text": "1 Battle = 1 Block",
    "conversion_description": "Every time you evaluate a model, you earn a block for the community build (up to 10/day).",
    "daily_limit": 10,
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
) -> tuple[gr.Column, gr.Button]:
    """Build the Community Quest section for home page.

    Args:
        progress_data: Optional dict with quest progress info (current_blocks, goal_blocks, percentage, days_remaining)

    Returns:
        Tuple of (quest_container, contribute_button)
    """
    # Use provided progress data or defaults
    if progress_data is None:
        progress_data = {
            "current_blocks": 0,
            "goal_blocks": QUEST_CONFIG["goal"],
            "percentage": 0.0,
            "days_remaining": QUEST_CONFIG["duration_days"],
        }

    current_blocks = progress_data["current_blocks"]
    goal_blocks = progress_data["goal_blocks"]
    percentage = progress_data["percentage"]
    days_remaining = progress_data["days_remaining"]

    # Generate carousel images HTML
    images_html = ""
    indicators_html = ""
    for i, image_url in enumerate(QUEST_CONFIG["current_update"]["images"]):
        active_class = "active" if i == 0 else ""
        images_html += f'<img src="{image_url}" class="carousel-image {active_class}" alt="Minecraft arena progress" />\n'
        indicators_html += f'<span class="indicator {active_class}" onclick="jumpToImage({i})" role="button" tabindex="0" aria-label="View image {i + 1}"></span>\n'

    # Only show indicators if more than one image
    indicators_display = (
        "" if len(QUEST_CONFIG["current_update"]["images"]) > 1 else "display: none;"
    )

    # Build the carousel HTML (left column)
    carousel_html = f"""
    <div style="position: relative; height: 100%;">
        <div class="quest-carousel" onmouseenter="pauseCarousel()" onmouseleave="resumeCarousel()">
            <div class="carousel-container">
                {images_html}
            </div>
            <div class="carousel-indicators" style="{indicators_display}">
                {indicators_html}
            </div>
        </div>

        <!-- Update overlay -->
        <div style="position: absolute; bottom: 20px; left: 20px; right: 20px; background: rgba(0, 0, 0, 0.75); backdrop-filter: blur(8px); padding: 1rem; border-radius: 8px; border: 1px solid rgba(255, 255, 255, 0.1);">
            <h4 style="color: white; font-weight: 600; margin: 0 0 0.25rem 0; display: flex; align-items: center; gap: 0.5rem;">
                <span style="width: 8px; height: 8px; border-radius: 50%; background-color: #22c55e;"></span>
                {QUEST_CONFIG["current_update"]["title"]}
            </h4>
            <p style="color: rgba(255, 255, 255, 0.9); font-size: 0.875rem; margin: 0;">
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
        }}

        .carousel-container {{
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
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
        }}

        .carousel-image.active {{
            opacity: 1;
            z-index: 1;
        }}

        .carousel-indicators {{
            position: absolute;
            bottom: 80px;
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            gap: 8px;
            z-index: 10;
            padding: 8px 12px;
            background: rgba(0, 0, 0, 0.4);
            border-radius: 20px;
            backdrop-filter: blur(4px);
        }}

        .indicator {{
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.5);
            cursor: pointer;
            transition: background 0.3s, transform 0.3s;
        }}

        .indicator:hover {{
            background: rgba(255, 255, 255, 0.9);
            transform: scale(1.3);
        }}

        .indicator.active {{
            background: var(--accent-teal, #14b8a6);
            transform: scale(1.15);
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

    <script>
    (function() {{
        const carouselId = 'quest-carousel-home-{datetime.now().timestamp()}';
        let currentIndex = 0;
        const images = document.querySelectorAll('.quest-carousel .carousel-image');
        const indicators = document.querySelectorAll('.quest-carousel .indicator');
        let autoRotateInterval;
        const ROTATION_INTERVAL = 6000; // 6 seconds

        function showImage(index) {{
            // Hide all images, show selected
            images.forEach((img, i) => {{
                img.classList.toggle('active', i === index);
            }});
            // Update indicators
            indicators.forEach((ind, i) => {{
                ind.classList.toggle('active', i === index);
            }});
            currentIndex = index;
        }}

        function nextImage() {{
            showImage((currentIndex + 1) % images.length);
        }}

        window.jumpToImage = function(index) {{
            stopAutoRotate();
            showImage(index);
            startAutoRotate();
        }};

        function startAutoRotate() {{
            if (images.length > 1) {{
                autoRotateInterval = setInterval(nextImage, ROTATION_INTERVAL);
            }}
        }}

        function stopAutoRotate() {{
            if (autoRotateInterval) {{
                clearInterval(autoRotateInterval);
            }}
        }}

        window.pauseCarousel = stopAutoRotate;
        window.resumeCarousel = startAutoRotate;

        // Start auto-rotation if more than one image
        startAutoRotate();
    }})();
    </script>
    """

    # Build progress and info HTML (right column - without button)
    progress_html = f"""
    <div style="display: flex; flex-direction: column; gap: 1.5rem; height: 100%;">
        <!-- Progress Bar Section -->
        <div>
            <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.5rem;">
                <span style="color: var(--body-text-color); font-weight: 500;">Arena Progress</span>
                <span style="color: var(--body-text-color); font-weight: 600;">{percentage:.1f}% Complete</span>
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
                        We have {days_remaining} days to complete the arena structure before the season ends.
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
                    gr.HTML(progress_html)
                    # CTA button positioned at bottom of right column
                    contribute_button = gr.Button(
                        "Contribute a Block Now",
                        variant="primary",
                        size="lg",
                        elem_id="quest-cta-button",
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
        #quest-cta-button {
            margin-top: 1rem !important;
        }
        </style>
        """)

    return quest_container, contribute_button
