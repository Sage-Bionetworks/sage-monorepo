"""Community Quest section component for BioArena home page."""

import json
from datetime import datetime

import gradio as gr

# Tier configuration for quest contributors
TIER_CONFIG = {
    "champion": {
        "emoji": "🏆",
        "color": "#fbbf24",  # Gold
        "threshold": "10+",
        "description": "10+ battles/week on average",
    },
    "knight": {
        "emoji": "⚔️",
        "color": "#c0c0c0",  # Silver
        "threshold": "5+",
        "description": "5+",
    },
    "apprentice": {
        "emoji": "🌟",
        "color": "#cd7f32",  # Bronze
        "threshold": "<5",
        "description": "<5",
    },
}


def _get_default_progress_data() -> dict:
    """Get default progress data structure for quest.

    Returns:
        Dictionary with current_blocks, goal_blocks, percentage, days_remaining
    """
    return {
        "current_blocks": 0,
        "goal_blocks": 0,
        "percentage": 0.0,
        "days_remaining": 0,
    }


# Local UI configuration — fields not managed by the API
QUEST_UI_CONFIG = {
    "quest_id": "build-bioarena-together",
    "conversion_text": "1 Battle = 1 Block",
    "conversion_description": "Every time you evaluate a model, you earn a block that will be placed by the BioArena team in Minecraft.",
    "carousel_rotation_interval": 6000,  # Duration in milliseconds for each image
    "minecraft_arena_designer": {
        "name": "NeatCraft",
        "url": "https://www.youtube.com/@Neatcraft",
    },
    "quest_architect": {
        "name": "Thomas Schaffter",
        "url": "https://www.linkedin.com/in/tschaffter",
    },
}


def build_quest_not_found_section() -> tuple[
    gr.Column, gr.HTML, gr.HTML, gr.HTML, gr.Button, gr.Button, gr.Button, str, int
]:
    """Build a quest not found error section.

    Returns:
        Tuple of (quest_container, empty_html, empty_contributors_html,
                  empty_carousel_html, hidden buttons, carousel_id, rotation_interval)
    """
    with gr.Column(
        elem_id="quest-section-wrapper",
        elem_classes=["quest-section-wrapper"],
    ) as quest_container:
        gr.HTML(f"""
        <div style="padding: 2.5rem 1.5rem 2rem 1.5rem; text-align: center;">
            <div style="max-width: 600px; margin: 0 auto;">
                <div style="width: 64px; height: 64px; border-radius: 50%;
                            background: color-mix(in srgb, #f59e0b 10%, transparent);
                            border: 1px solid color-mix(in srgb, #f59e0b 30%, transparent);
                            display: flex; align-items: center; justify-content: center;
                            margin: 0 auto 1.5rem;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24"
                         fill="none" stroke="#f59e0b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                </div>
                <h2 style="color: var(--body-text-color); font-weight: 600;
                           margin: 0 0 0.75rem 0; font-size: 1.5rem;">
                    Quest Not Found
                </h2>
                <p style="color: var(--body-text-color-subdued); font-size: 1rem;
                         margin: 0; line-height: 1.6;">
                    The configured quest "<strong>{QUEST_UI_CONFIG["quest_id"]}</strong>" doesn't exist in the database.
                </p>
            </div>
        </div>
        """)

        # Add CSS to ensure hidden components stay hidden
        gr.HTML("""
        <style>
        #quest-error-empty-html,
        #quest-error-btn-auth,
        #quest-error-btn-login,
        #quest-error-carousel-trigger {
            display: none !important;
            visibility: hidden !important;
        }
        </style>
        """)

        # Hidden components (required for return signature compatibility)
        # These are inside the container but hidden with visible=False and CSS
        empty_html = gr.HTML("", visible=False)
        empty_contributors_html = gr.HTML("", visible=False)
        empty_carousel_html = gr.HTML("", visible=False)
        quest_btn_authenticated = gr.Button(
            visible=False, elem_id="quest-error-btn-auth"
        )
        quest_btn_login = gr.Button(visible=False, elem_id="quest-error-btn-login")
        carousel_init_trigger = gr.Button(
            visible=False, elem_id="quest-error-carousel-trigger"
        )

    return (
        quest_container,
        empty_html,
        empty_contributors_html,
        empty_carousel_html,
        quest_btn_authenticated,
        quest_btn_login,
        carousel_init_trigger,
        "",  # carousel_id
        0,  # rotation_interval
    )


def _build_user_progress_card_html(
    username: str | None, tier: str, battles_per_week: float
) -> str:
    """Build the tier progress card HTML.

    Shown to all users. For authenticated contributors, displays personalized
    progress. For anonymous visitors or non-contributors, shows the default
    apprentice state with 0 battles/week as motivation to start.

    Args:
        username: The user's display name, or None for anonymous/non-contributors
        tier: Current tier ('champion', 'knight', or 'apprentice')
        battles_per_week: The user's average battles per week

    Returns:
        HTML string for the progress card
    """
    tier_info = TIER_CONFIG.get(tier, TIER_CONFIG["apprentice"])
    emoji = tier_info["emoji"]
    tier_name = tier.capitalize()
    tier_color = tier_info["color"]

    # Determine next tier and progress
    if tier == "champion":
        # Already at max tier
        return f"""
        <div style="margin-bottom: 0.5rem;">
            <h4 style="color: var(--body-text-color); font-weight: 600;
                       margin: 0 0 0.75rem 0; font-size: 1rem;">
                {emoji} Welcome back, Champion {username}
            </h4>
            <div style="color: var(--body-text-color-subdued); font-size: 0.875rem; line-height: 1.4;">
                {battles_per_week:.1f} battles/week &mdash; Keep battling to hold your title!
            </div>
        </div>
        """

    # For apprentice -> knight or knight -> champion
    if tier == "apprentice":
        prev_threshold = 0.0
        next_threshold = 5.0
        next_tier = "knight"
    else:  # knight
        prev_threshold = 5.0
        next_threshold = 10.0
        next_tier = "champion"

    next_emoji = TIER_CONFIG[next_tier]["emoji"]
    next_name = next_tier.capitalize()

    band = next_threshold - prev_threshold
    progress_pct = min((battles_per_week - prev_threshold) / band * 100, 100)

    # Header varies based on whether the user is known
    if username:
        header = f"{emoji} Welcome back, {tier_name} {username}"
    else:
        header = f"{next_emoji} Become a {next_name}!"

    return f"""
    <div style="margin-bottom: 0.5rem;">
        <h4 style="color: var(--body-text-color); font-weight: 600;
                   margin: 0 0 0.75rem 0; font-size: 1rem;">
            {header}
        </h4>
        <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.5rem;">
            <span style="color: var(--body-text-color); font-weight: 500; font-size: 0.875rem;">Tier Progress</span>
            <span style="color: var(--body-text-color); font-weight: 600; font-size: 0.875rem;">{progress_pct:.0f}% Complete</span>
        </div>
        <div style="width: 100%; height: 10px; background-color: var(--background-fill-secondary);
                    border-radius: 5px; overflow: hidden;
                    border: 1px solid var(--border-color-primary);">
            <div style="height: 100%; width: {progress_pct:.0f}%;
                        background: var(--color-accent, #f97316);
                        border-radius: 5px;"></div>
        </div>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 0.75rem;">
            <div>
                <p style="font-size: 1.25rem; font-weight: 700; color: var(--body-text-color); margin: 0; line-height: 1;">{battles_per_week:.1f}</p>
                <p style="font-size: 0.75rem; color: var(--body-text-color-subdued); text-transform: uppercase; letter-spacing: 0.05em; margin: 0.25rem 0 0 0;">Battles/Week</p>
            </div>
            <div style="text-align: right;">
                <p style="font-size: 1.25rem; font-weight: 700; color: var(--body-text-color); margin: 0; line-height: 1;">{next_emoji} {next_name}</p>
                <p style="font-size: 0.75rem; color: var(--body-text-color-subdued); text-transform: uppercase; letter-spacing: 0.05em; margin: 0.25rem 0 0 0;">Goal</p>
            </div>
        </div>
    </div>
    """


def _build_tier_legend_html() -> str:
    """Build the tier legend HTML with descriptions and accuracy note.

    Returns:
        HTML string for the tier legend section
    """
    return f"""
            <!-- Contributor Tiers Legend -->
            <div style="margin-top: 1.5rem;">
                <h4 style="color: var(--body-text-color); font-weight: 600;
                           margin: 0 0 0.75rem 0; font-size: 1rem;">
                    Contributor Tiers
                </h4>
                <div style="display: flex; flex-direction: column; gap: 0.375rem;
                            color: var(--body-text-color-subdued); font-size: 0.875rem;">
                    <div>{TIER_CONFIG["champion"]["emoji"]} Champion ({TIER_CONFIG["champion"]["description"]})</div>
                    <div>{TIER_CONFIG["knight"]["emoji"]} Knight ({TIER_CONFIG["knight"]["description"]})</div>
                    <div>{TIER_CONFIG["apprentice"]["emoji"]} Apprentice ({TIER_CONFIG["apprentice"]["description"]})</div>
                </div>
                <div style="margin-top: 0.75rem; color: var(--body-text-color-subdued); font-size: 0.8125rem; font-style: italic; line-height: 1.5;">
                    Note: Tiers are calculated based on your average battle completion rate. As more days of the quest pass, tier assignments become increasingly accurate.
                </div>
            </div>
"""


def _build_progress_html(
    current_blocks: int, goal_blocks: int, percentage: float, days_remaining: int
) -> str:
    """Build the progress HTML for the quest section.

    Args:
        current_blocks: Number of blocks currently placed
        goal_blocks: Total goal blocks to be placed
        percentage: Completion percentage (0-100+)
        days_remaining: Days remaining in the quest

    Returns:
        HTML string for the progress section
    """
    return f"""
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
                        {QUEST_UI_CONFIG["conversion_text"]}
                    </h4>
                    <p style="color: var(--body-text-color-subdued); font-size: 0.875rem; margin: 0; line-height: 1.5;">
                        {QUEST_UI_CONFIG["conversion_description"]}
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


def _build_builders_credits_html(
    contributors_data: dict | None = None,
    current_user_data: dict | None = None,
) -> str:
    """Build the Builders and Credits HTML sections.

    Args:
        contributors_data: Optional dict with contributors_by_tier and total_contributors
        current_user_data: Optional dict with username, tier, battles_per_week for the
            logged-in user. When provided, a personal progress card is shown above the list.

    Returns:
        HTML string for the builders and credits sections
    """
    # Show "no contributors yet" if quest exists but no contributors
    if contributors_data is None or contributors_data.get("total_contributors", 0) == 0:
        return f"""
    <div style="display: flex; flex-direction: column; gap: 1.5rem;
                height: 100%; margin-top: 0.75rem;">
        <!-- Divider -->
        <div style="height: 1px; background: var(--border-color-primary);"></div>

        <!-- No Contributors Message -->
        <div style="flex: 1; display: flex; flex-direction: column; min-height: 0;">
            <h4 style="color: var(--body-text-color); font-weight: 600;
                       margin: 0 0 0.75rem 0; font-size: 1rem;">
                Builders (0)
            </h4>
            <div style="padding: 2rem; text-align: center;
                        color: var(--body-text-color-subdued); font-size: 0.875rem;">
                No builders yet. Be the first to contribute!
            </div>

{_build_tier_legend_html()}        </div>

        <!-- Divider -->
        <div style="height: 1px; background: var(--border-color-primary);"></div>

        <!-- Credits Section -->
        <div>
            <h4 style="color: var(--body-text-color); font-weight: 600; margin: 0 0 0.75rem 0; font-size: 1rem;">
                Credits
            </h4>
            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem;">
                    <span style="color: var(--body-text-color-subdued);">Minecraft Arena Designer:</span>
                    <a href="{QUEST_UI_CONFIG["minecraft_arena_designer"]["url"]}"
                       style="color: #3b82f6; text-decoration: none;">
                       {QUEST_UI_CONFIG["minecraft_arena_designer"]["name"]}
                    </a>
                </div>
                <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem;">
                    <span style="color: var(--body-text-color-subdued);">Quest Architect:</span>
                    <a href="{QUEST_UI_CONFIG["quest_architect"]["url"]}"
                       style="color: #3b82f6; text-decoration: none;">
                       {QUEST_UI_CONFIG["quest_architect"]["name"]}
                    </a>
                </div>
            </div>

            <!-- Copyright -->
            <div style="font-size: 0.75rem; color: var(--body-text-color-subdued);
                        margin-top: 0.75rem;">
                &copy; 2026 Thomas Schaffter. Quest content and illustrations.
            </div>
        </div>
    </div>
    """
    else:
        # Build real contributors list grouped by rank
        total_count = contributors_data["total_contributors"]
        contributors_by_tier = contributors_data["contributors_by_tier"]

        builders_parts = []
        for rank in ["champion", "knight", "apprentice"]:
            rank_contributors = contributors_by_tier.get(rank, [])
            for contributor in rank_contributors:
                username = contributor["username"]
                emoji = TIER_CONFIG[rank]["emoji"]
                builders_parts.append(
                    f'<span style="color: var(--body-text-color); '
                    f'font-size: 0.875rem;">'
                    f'<span style="margin-right: 0.25rem;">{emoji}</span>{username}'
                    f"</span>"
                )

        # Join with separators
        builders_html = '<span style="color: var(--body-text-color-subdued); font-size: 0.75rem;">•</span>'.join(
            builders_parts
        )

    # Build progress card (personalized for contributors, default for others)
    if current_user_data is not None:
        progress_card_html = _build_user_progress_card_html(
            username=current_user_data["username"],
            tier=current_user_data["tier"],
            battles_per_week=current_user_data["battles_per_week"],
        )
    else:
        progress_card_html = _build_user_progress_card_html(
            username=None, tier="apprentice", battles_per_week=0.0
        )

    return f"""
    <div style="display: flex; flex-direction: column; gap: 1.5rem;
                height: 100%; margin-top: 0.75rem;">
        <!-- Divider -->
        <div style="height: 1px; background: var(--border-color-primary);"></div>

        {progress_card_html}

        <!-- Builders Section -->
        <div style="flex: 1; display: flex; flex-direction: column;
                    min-height: 0;">
            <h4 style="color: var(--body-text-color); font-weight: 600;
                       margin: 0 0 0.75rem 0; font-size: 1rem;">
                Builders ({total_count})
            </h4>

            <div style="flex: 1; overflow-y: auto; min-height: 0;
                        padding-right: 0.25rem;">
                <div style="display: flex; flex-wrap: wrap;
                            align-items: center; gap: 0.5rem;
                            line-height: 1.5;">
                    {builders_html}
                </div>
            </div>

{_build_tier_legend_html()}        </div>

        <!-- Divider -->
        <div style="height: 1px; background: var(--border-color-primary);"></div>

        <!-- Credits Section (no title) -->
        <div style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div style="font-size: 0.875rem;
                        color: var(--body-text-color-subdued);">
                <span>Minecraft Arena Designer: </span>
                <a href="{QUEST_UI_CONFIG["minecraft_arena_designer"]["url"]}"
                   target="_blank"
                   rel="noopener noreferrer"
                   class="credit-link"
                   style="color: var(--color-accent);
                          text-decoration: none; font-weight: 500;">
                    {QUEST_UI_CONFIG["minecraft_arena_designer"]["name"]}
                </a>
            </div>
            <div style="font-size: 0.875rem;
                        color: var(--body-text-color-subdued);">
                <span>Quest Architect: </span>
                <a href="{QUEST_UI_CONFIG["quest_architect"]["url"]}"
                   target="_blank"
                   rel="noopener noreferrer"
                   class="credit-link"
                   style="color: var(--color-accent);
                          text-decoration: none; font-weight: 500;">
                    {QUEST_UI_CONFIG["quest_architect"]["name"]}
                </a>
            </div>
            <!-- Copyright -->
            <div style="font-size: 0.75rem; color: var(--body-text-color-subdued);
                        margin-top: 0.25rem;">
                &copy; 2026 Thomas Schaffter. Quest content and illustrations.
            </div>
        </div>
    </div>
    """


def _build_carousel_html(
    carousel_id: str,
    posts: list[dict] | None = None,
    active_post_index: int | None = None,
) -> str:
    """Build the carousel HTML for quest images.

    Args:
        carousel_id: Unique ID for this carousel instance
        posts: List of post dicts from the API (each with title, date, description, images, locked)
        active_post_index: Index of the currently active post (expanded by default)

    Returns:
        HTML string for the carousel with images and controls
    """
    # Use all posts in backend order; fallback if empty
    all_posts = posts or []
    if not all_posts:
        all_posts = [
            {
                "date": "",
                "title": "No updates yet",
                "description": "Check back soon for progress updates!",
                "images": [],
            }
        ]

    # Find the unlocked post to show in the image carousel
    unlocked_posts = [p for p in all_posts if not p.get("locked", False)]

    # Determine which unlocked post should be active/expanded
    active_post = None
    if unlocked_posts:
        active_post = unlocked_posts[-1]  # default to last unlocked post
        if active_post_index is not None:
            for post in unlocked_posts:
                if post.get("post_index") == active_post_index:
                    active_post = post
                    break

    # Generate carousel images HTML from active post (or empty if all locked)
    active_images = active_post["images"] if active_post else []
    images_html = "".join(
        f'<img src="{image_url}" class="carousel-image {"active" if i == 0 else ""}" alt="Minecraft arena progress" />\n'
        for i, image_url in enumerate(active_images)
    )
    indicators_html = "".join(
        f'<span class="indicator {"active" if i == 0 else ""}" data-index="{i}" role="button" tabindex="0" aria-label="View image {i + 1}"></span>\n'
        for i, _ in enumerate(active_images)
    )

    # Only show indicators if more than one image
    indicators_display = "" if len(active_images) > 1 else "display: none;"

    # Generate update cards HTML (accordion style)
    def _is_unlocked_reward(post: dict) -> bool:
        """Check if a post is an unlocked reward (has gates but is not locked)."""
        return not post.get("locked", False) and (
            post.get("required_progress") is not None
            or post.get("required_tier") is not None
        )

    def format_update_card(update: dict) -> str:
        """Format a single update accordion item HTML."""
        is_expanded = active_post is not None and update is active_post
        active_class = "active" if is_expanded else ""
        expanded_class = "expanded" if is_expanded else ""
        reward_class = "reward" if _is_unlocked_reward(update) else ""
        images_json = json.dumps(update["images"]).replace('"', "&quot;")

        # Format date if available
        date_display = ""
        if update.get("date"):
            try:
                date_obj = datetime.strptime(update["date"], "%Y-%m-%d")
                date_display = date_obj.strftime("%B %d, %Y")
            except (ValueError, TypeError):
                date_display = update["date"]

        # Convert description to paragraphs (split on \n\n)
        description: str = update["description"]
        paragraphs: list[str] = [
            p.strip() for p in description.split("\n\n") if p.strip()
        ]
        description_html: str = "".join(
            f'<p class="update-description">{p}</p>' for p in paragraphs
        )

        return f'''
        <div class="quest-update-accordion {active_class} {expanded_class} {
            reward_class
        }"
             data-images="{images_json}">
            <div class="accordion-header" role="button" tabindex="0">
                <div class="accordion-title-wrapper">
                    <h4>{update["title"]}</h4>
                    {
            f'<span class="update-date">{date_display}</span>' if date_display else ""
        }
                </div>
                <svg class="accordion-chevron" xmlns="http://www.w3.org/2000/svg"
                     width="20" height="20" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"></polyline>
                </svg>
            </div>
            <div class="accordion-content">
                {description_html}
            </div>
        </div>
        '''

    def format_locked_card(post: dict) -> str:
        """Format a locked post as a collapsible accordion card with requirement badges."""
        req_badges = []
        hint_parts = []
        req_progress = post.get("required_progress")
        req_tier = post.get("required_tier")
        if req_progress is not None:
            block_icon = (
                '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12"'
                ' viewBox="0 0 24 24" fill="none" stroke="currentColor"'
                ' stroke-width="2" stroke-linecap="round" stroke-linejoin="round">'
                '<path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4'
                "A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4"
                'A2 2 0 0 0 21 16z"></path></svg>'
            )
            req_badges.append(
                f'<span class="locked-req-badge">'
                f'<span class="locked-req-icon">{block_icon}</span>'
                f"{req_progress:,} blocks</span>"
            )
            hint_parts.append(
                f"The community must reach {req_progress:,} blocks to unlock this post."
            )
        if req_tier:
            tier_label = req_tier.capitalize()
            tier_emoji = TIER_CONFIG.get(req_tier, {}).get("emoji", "")
            req_badges.append(
                f'<span class="locked-req-badge">'
                f'<span class="locked-req-icon">{tier_emoji}</span>'
                f"{tier_label}</span>"
            )
            hint_parts.append(f"Become a {tier_label} to access this post.")
        badges_html = " ".join(req_badges)
        hint_text = "<br>".join(hint_parts)
        return f"""
        <div class="quest-update-accordion locked">
            <div class="accordion-header" role="button" tabindex="0">
                <div class="accordion-title-wrapper">
                    <h4>{post["title"]}</h4>
                    <div class="locked-badges">{badges_html}</div>
                </div>
                <svg class="locked-lock-icon" xmlns="http://www.w3.org/2000/svg"
                     width="18" height="18" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
            </div>
            <div class="accordion-content">
                <p class="update-description" style="font-style: italic;">{hint_text}</p>
            </div>
        </div>
        """

    def format_card(post: dict) -> str:
        """Format a post card — locked or unlocked — in backend order."""
        if post.get("locked", False):
            return format_locked_card(post)
        return format_update_card(post)

    all_cards_html = "".join(format_card(post) for post in reversed(all_posts))

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
            <div class="carousel-indicators" id="{carousel_id}-indicators">
                {indicators_html}
            </div>
        </div>

        <!-- Update cards below indicators -->
        <div class="quest-updates-container">
            {all_cards_html}
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
            flex-wrap: wrap;
            justify-content: center;
        }}

        .indicator {{
            width: 24px;
            min-width: 12px;
            height: 8px;
            flex-shrink: 1;
            border-radius: 4px;
            background: var(--body-text-color-subdued);
            opacity: 0.5;
            cursor: pointer;
            transition: background 0.3s, opacity 0.3s;
        }}

        .indicator:hover {{
            opacity: 0.9;
        }}

        .indicator.active {{
            background: var(--color-accent, #f97316);
            opacity: 1;
        }}

        .quest-updates-container {{
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
            margin-top: 1.5rem;
        }}

        /* Accordion item */
        .quest-update-accordion {{
            background: var(--panel-background-fill);
            border: 2px solid var(--border-color-primary);
            border-radius: 8px;
            transition: all 0.2s ease;
        }}

        .quest-update-accordion.active {{
            border-color: var(--color-accent);
        }}

        .quest-update-accordion.reward {{
            position: relative;
            border-color: #d4a853;
        }}

        .quest-update-accordion.reward.active {{
            border-color: transparent;
        }}

        .quest-update-accordion.reward.active::before {{
            content: '';
            position: absolute;
            inset: -2px;
            border-radius: 10px;
            background: conic-gradient(
                from var(--reward-angle, 0deg),
                #d4a853 0deg,
                #f5d78e 40deg,
                #d4a853 80deg,
                #b8943e 180deg,
                #d4a853 280deg,
                #f5d78e 320deg,
                #d4a853 360deg
            );
            -webkit-mask:
                linear-gradient(#fff 0 0) content-box,
                linear-gradient(#fff 0 0);
            mask:
                linear-gradient(#fff 0 0) content-box,
                linear-gradient(#fff 0 0);
            -webkit-mask-composite: xor;
            mask-composite: exclude;
            padding: 2px;
            animation: reward-trace 3s linear infinite;
        }}

        @keyframes reward-trace {{
            to {{
                --reward-angle: 360deg;
            }}
        }}

        @property --reward-angle {{
            syntax: '<angle>';
            initial-value: 0deg;
            inherits: false;
        }}

        /* Accordion header (always visible) */
        .accordion-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.8rem 1rem;
            cursor: pointer;
            gap: 1rem;
            transition: background 0.2s ease;
        }}

        .quest-update-accordion:not(.active):hover .accordion-header {{
            background: color-mix(
                in srgb,
                var(--color-accent) 3%,
                var(--panel-background-fill)
            );
        }}

        .accordion-title-wrapper {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex: 1;
            gap: 1rem;
        }}

        .accordion-title-wrapper h4 {{
            color: var(--body-text-color);
            font-weight: 600;
            margin: 0 !important;
            font-size: 1rem;
            flex: 1;
        }}

        .update-date {{
            color: var(--body-text-color-subdued);
            font-size: 0.75rem;
            white-space: nowrap;
        }}

        /* Chevron icon */
        .accordion-chevron {{
            color: var(--body-text-color-subdued);
            transition: transform 0.3s ease;
            flex-shrink: 0;
        }}

        .quest-update-accordion.expanded .accordion-chevron {{
            transform: rotate(180deg);
        }}

        /* Accordion content (collapsible) */
        .accordion-content {{
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.3s ease, padding 0.3s ease;
            padding: 0 2rem;
        }}

        .quest-update-accordion.expanded .accordion-content {{
            max-height: 400px;
            overflow-y: auto;
            padding: 0.5rem 2rem 1.5rem 2rem;
            scrollbar-color: var(--border-color-primary) transparent;
        }}

        .update-description {{
            color: var(--body-text-color-subdued);
            font-size: 0.875rem;
            margin: 0 0 0.75rem 0;
            line-height: 1.5;
        }}

        .update-description:last-child {{
            margin-bottom: 0;
        }}

        /* Locked post styling */
        .locked-lock-icon {{
            color: var(--color-accent);
            flex-shrink: 0;
        }}

        .locked-badges {{
            display: flex;
            gap: 0.375rem;
            flex-shrink: 0;
        }}

        .locked-req-badge {{
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            padding: 0.125rem 0.5rem;
            border-radius: 10px;
            font-size: 0.75rem;
            font-weight: 600;
            white-space: nowrap;
            background: color-mix(in srgb, var(--color-accent) 15%, transparent);
            color: var(--color-accent);
            border: 1px solid color-mix(in srgb, var(--color-accent) 30%, transparent);
        }}

        .locked-req-icon {{
            font-size: 0.75rem;
            line-height: 1;
        }}

        /* Credit links */
        a.credit-link {{
            transition: text-decoration 0.2s ease;
            text-underline-offset: 3px;
        }}

        a.credit-link:hover {{
            text-decoration: underline !important;
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

    return carousel_html


def build_quest_section(
    quest_data: dict,
    progress_data: dict | None = None,
    contributors_data: dict | None = None,
) -> tuple[
    gr.Column, gr.HTML, gr.HTML, gr.HTML, gr.Button, gr.Button, gr.Button, str, int
]:
    """Build the Community Quest section for home page.

    Args:
        quest_data: Dict from fetch_quest() with quest metadata (title, description, posts, active_post_index)
        progress_data: Optional dict with quest progress info (current_blocks, goal_blocks, percentage, days_remaining)
        contributors_data: Optional dict with contributors info (contributors_by_tier, total_contributors, error)

    Returns:
        Tuple of (quest_container, progress_html_container, contributors_html_container,
                  carousel_html_container, contribute_button_authenticated,
                  contribute_button_login, carousel_init_trigger, carousel_id, rotation_interval)
    """
    # Use provided progress data or defaults
    if progress_data is None:
        progress_data = _get_default_progress_data()

    current_blocks = progress_data["current_blocks"]
    goal_blocks = progress_data["goal_blocks"]
    percentage = progress_data["percentage"]
    days_remaining = progress_data["days_remaining"]

    # Generate unique ID for this carousel instance
    carousel_id = "quest-carousel"

    # Build carousel HTML using posts from API
    carousel_html = _build_carousel_html(
        carousel_id,
        posts=quest_data.get("posts", []),
        active_post_index=quest_data.get("active_post_index"),
    )

    # Build progress and info HTML (right column - without button)
    progress_html = _build_progress_html(
        current_blocks, goal_blocks, percentage, days_remaining
    )

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
                    {quest_data["title"]}
                </h1>

                <p style="color: var(--body-text-color-subdued); font-size: var(--text-xl); max-width: 48rem; margin: 0 auto;">
                    {quest_data["description"]}
                </p>
            </div>
        </div>
        """)

        # Content box (bordered container with carousel and progress)
        with gr.Column(elem_id="quest-content-box"):
            with gr.Row(elem_id="quest-section-grid"):
                with gr.Column(scale=1):
                    # Carousel HTML container that can be updated dynamically
                    carousel_html_container = gr.HTML(
                        carousel_html, elem_id="quest-carousel-container"
                    )
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
                    # Builders and Credits sections below CTA button
                    contributors_html_container = gr.HTML(
                        _build_builders_credits_html(contributors_data),
                        elem_id="quest-contributors-container",
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
            border-radius: 8px;
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
        .quest-cta-btn,
        #quest-cta-btn-authenticated,
        #quest-cta-btn-login {
            margin-top: 1rem !important;
        }
        .quest-cta-btn {
            width: 100%;
        }
        </style>
        """)

        # Hidden button to trigger carousel initialization via JavaScript
        carousel_init_trigger = gr.Button(
            "Init Carousel", visible=False, elem_id="carousel-init-trigger"
        )

    # Get rotation interval from config
    rotation_interval = QUEST_UI_CONFIG.get("carousel_rotation_interval", 6000)

    return (
        quest_container,
        progress_html_container,
        contributors_html_container,
        carousel_html_container,
        contribute_button_authenticated,
        contribute_button_login,
        carousel_init_trigger,
        carousel_id,
        rotation_interval,
    )
