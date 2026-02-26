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
    end_date = datetime.strptime(QUEST_CONFIG["end_date"], "%Y-%m-%d")
    days_remaining = max(0, (end_date - datetime.now()).days)
    return {
        "current_blocks": 0,
        "goal_blocks": QUEST_CONFIG["goal"],
        "percentage": 0.0,
        "days_remaining": days_remaining,
    }


# Quest configuration - hardcoded for Season 1
QUEST_CONFIG = {
    "quest_id": "build-bioarena-together",  # Quest ID for API calls
    "title": "Build BioArena Together",
    "description": "We are constructing a medieval arena in Minecraft to symbolize our collective effort. Every battle counts towards the build.",
    "goal": 2850,
    "start_date": "2026-02-01",
    "end_date": "2026-04-30",
    "conversion_text": "1 Battle = 1 Block",
    "conversion_description": "Every time you evaluate a model, you earn a block that will be placed by the BioArena team in Minecraft.",
    "carousel_rotation_interval": 6000,  # Duration in milliseconds for each image
    # Index of update to show expanded by default (0 = newest, -1 or None = latest)
    "active_update_index": None,
    "minecraft_arena_designer": {
        "name": "NeatCraft",
        "url": "https://www.youtube.com/@Neatcraft",
    },
    "quest_architect": {
        "name": "Thomas Schaffter",
        "url": "https://www.linkedin.com/in/tschaffter",
    },
    "updates": [
        # Updates are displayed in chronological order (newest first)
        {
            "date": "2026-02-20",
            "title": "Chapter 2: Shadows Over Stone",
            "description": (
                "At first light, I stood above the village, standing atop the old church tower as the wind rolled in from the surrounding waters. Our hill rose like a crown from the sea, protected on all sides by water save for the western stretch where the land sloped down to meet the greater continent beyond. It was a natural fortress, a fitting cradle for an arena meant to test champions.\n\n"
                "Below me, the village stirred to life.\n\n"
                "Earlier, I had wandered its narrow paths, stepping into quiet houses still scented with wood and bread. In one corner, I discovered a brewing stand, its glass bottles empty but full of promise. With the right ingredients, it could yield draughts of strength, swiftness, even potions to breathe beneath the waves that encircle our hill.\n\n"
                "Beyond the village, to the south, stretched something extraordinary.\n\n"
                "In the distance shimmered a forest of cherry trees, their pale blossoms glowing whenever the sun brushed their crowns. Even from the tower, I could almost see petals drifting like slow-falling embers of light. I have decided to call it the Sunpetal Expanse, a place I will explore once the arena's inner wall stands complete.\n\n"
                "As I stood there, watching the Sunpetal Expanse blaze softly in the morning light, I felt it. The faint but undeniable sense that I was not alone in my vigil.\n\n"
                "The feeling passed as quickly as it came.\n\n"
                "New blocks had arrived since the last count, forty contributions carried into this world through shared effort. I descended from the tower and made my way to the site.\n\n"
                "Stone met stone in steady rhythm. With these additions, the full perimeter of the inner wall now stood one block high, complete. It felt significant, seeing the arena's heart fully outlined at last. Without pause, I began raising sections to a second block. Already, the difference was tangible. Two blocks high, and most wandering creatures would think twice before crossing into the arena's bounds.\n\n"
                "Progress is a powerful thing. It makes one bold.\n\n"
                "Too bold, perhaps.\n\n"
                "I worked longer than I intended. By the time I stepped back to admire the rising wall, dusk had crept across the sky and drained the color from the world. Shadows pooled in the spaces I had neglected, the unwatched corners, the unlit stretches of ground.\n\n"
                "In my eagerness to build, I had failed to secure the site with proper lighting.\n\n"
                "Night answered swiftly.\n\n"
                "A groan echoed between the houses. Then another. Shapes emerged at the village's edge, shambling figures and the sharp silhouettes of archers drawing unseen bows. I ran to the square, sword in hand, where the iron golem already stood braced for impact.\n\n"
                "Steel flashed. The first zombie fell beneath my blade. I struck the village bell beside the fountain, its urgent toll rolling through the streets, a warning for every villager to seek shelter indoors.\n\n"
                "Arrows hissed through the dark. The golem moved with heavy, resolute force, sending skeletons scattering into bone and silence. Together we pressed outward, driving back the intruders before they could reach the livestock pens or batter down wooden doors.\n\n"
                "Only when the square fell quiet did I notice movement near the arena.\n\n"
                "A lone creeper had slipped inside the unfinished wall.\n\n"
                "The golem and I rushed toward it as the creature advanced with dreadful intent. Its body began to tremble, swelling with volatile energy, a rising hiss escaping from within as though the very air around it were being devoured. It was not rage that drove it, but inevitability, a creature built for a single catastrophic purpose.\n\n"
                "The golem lunged.\n\n"
                "The impact came at the same instant as the detonation. A violent burst of force rippled across the arena floor, scattering fragments of stone and earth. When the smoke cleared, the creeper was gone, replaced by a shallow crater torn into the very center of our work.\n\n"
                "The wall still stood.\n\n"
                "By the time the last shadows retreated, dawn had broken across the hilltop. The rising sun burned away any lingering threats beyond the village, leaving only drifting ash where night's creatures once stood.\n\n"
                "Only then did I kneel at the heart of the arena and fill the wound with fresh dirt. In time, grass would reclaim it, and little evidence of the blast would remain. But I would remember. Even the strongest walls demand foresight. Light will come, in its proper time. For now, the stones must continue to rise.\n\n"
                "I stood once more in the center of the arena, the inner wall catching the morning light. Two blocks high in places. One block elsewhere. Incomplete but rising.\n\n"
                "Again, that feeling returned. A quiet awareness at the edge of perception. As though unseen eyes measured each stone, each decision.\n\n"
                "I shook it off and turned toward the village.\n\n"
                "There will be time to wonder later. For now, there is breakfast to share, walls to raise, and many days still ahead.\n\n"
                "The arena grows."
            ),
            "images": [
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-1.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-2.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-3.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-4.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-5.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-6.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-7.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-2/minecraft-arena-update-2-final-8.jpg",
            ],
        },
        {
            "date": "2026-02-13",
            "title": "Chapter 1: Laying the First Stones",
            "description": (
                "I arrived at the edge of the village just as the morning light touched the snowy mountain and the waterfall beyond it. A quiet stretch of land lay waiting beside the houses. Even the bees seemed curious, circling lazily over the grass where something new was about to rise.\n\n"
                "An iron golem stood nearby, steady and watchful. One by one, the first contributions had made their way here, carried from afar by unseen hands and tireless golems. Soon, a small stack of cobblestone rested between the village houses. Thirty-eight blocks in total, each one sent by someone who believed this arena should exist.\n\n"
                "I stepped forward and set the first stone into the ground. Then another. The outline of the inner wall began to take shape. One day it will stand three blocks high, enclosing the heart of the arena. For now, it rises only a single block in most places, incomplete and waiting for more stone to continue its climb.\n\n"
                "When I stepped back, the foundation was modest but real. What began as a shared effort had taken solid form in stone, now part of the world itself.\n\n"
                "And this is only the beginning."
            ),
            "images": [
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-1/minecraft-arena-update-1-1.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-1/minecraft-arena-update-1-2.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-1/minecraft-arena-update-1-3.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-1/minecraft-arena-update-1-4.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-1/minecraft-arena-update-1-5.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/update-1/minecraft-arena-update-1-6.jpg",
            ],
        },
        {
            "date": "2026-02-03",
            "title": "Launch: Build BioArena Together",
            "description": (
                "Welcome to our Community Quest! AI is evolving fast. The best "
                "model today might be overtaken tomorrow. That's why we're "
                "building BioArena: a living leaderboard powered by researchers "
                "like you, continuously identifying the top AI models across "
                "biomedical topics. To celebrate this launch and the community "
                "we're building together, we're constructing a medieval arena in "
                "Minecraft, block by block, battle by battle. Starting today from "
                "0 blocks, every evaluation you complete adds another stone to our "
                "foundation. The screenshots above offer a glimpse into the future "
                "arena we'll build together. We'll share progress updates every "
                "week with new screenshots showing how far we've come. "
                "Let's build something legendary!"
            ),
            "images": [
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/minecraft-arena-demo-1.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/minecraft-arena-demo-2.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/minecraft-arena-demo-3.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/minecraft-arena-demo-4.jpg",
                "https://raw.githubusercontent.com/tschaffter/sage-monorepo/refs/heads/feat/bixarena/arena-demo-screenshots/apps/bixarena/images/minecraft-arena-demo-5.jpg",
            ],
        },
    ],
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
                    The configured quest "<strong>{QUEST_CONFIG["quest_id"]}</strong>" doesn't exist in the database.
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
                       margin: 0 0 0.75rem 0; font-size: 0.9375rem;">
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
                   margin: 0 0 0.75rem 0; font-size: 0.9375rem;">
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
                           margin: 0 0 0.75rem 0; font-size: 0.9375rem;">
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
                       margin: 0 0 0.75rem 0; font-size: 0.9375rem;">
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
            <h4 style="color: var(--body-text-color); font-weight: 600; margin: 0 0 0.75rem 0; font-size: 0.9375rem;">
                Credits
            </h4>
            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem;">
                    <span style="color: var(--body-text-color-subdued);">Minecraft Arena Designer:</span>
                    <a href="{QUEST_CONFIG["minecraft_arena_designer"]["url"]}"
                       style="color: #3b82f6; text-decoration: none;">
                       {QUEST_CONFIG["minecraft_arena_designer"]["name"]}
                    </a>
                </div>
                <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem;">
                    <span style="color: var(--body-text-color-subdued);">Quest Architect:</span>
                    <a href="{QUEST_CONFIG["quest_architect"]["url"]}"
                       style="color: #3b82f6; text-decoration: none;">
                       {QUEST_CONFIG["quest_architect"]["name"]}
                    </a>
                </div>
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
                       margin: 0 0 0.75rem 0; font-size: 0.9375rem;">
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
                <a href="{QUEST_CONFIG["minecraft_arena_designer"]["url"]}"
                   target="_blank"
                   rel="noopener noreferrer"
                   class="credit-link"
                   style="color: var(--color-accent);
                          text-decoration: none; font-weight: 500;">
                    {QUEST_CONFIG["minecraft_arena_designer"]["name"]}
                </a>
            </div>
            <div style="font-size: 0.875rem;
                        color: var(--body-text-color-subdued);">
                <span>Quest Architect: </span>
                <a href="{QUEST_CONFIG["quest_architect"]["url"]}"
                   target="_blank"
                   rel="noopener noreferrer"
                   class="credit-link"
                   style="color: var(--color-accent);
                          text-decoration: none; font-weight: 500;">
                    {QUEST_CONFIG["quest_architect"]["name"]}
                </a>
            </div>
        </div>
    </div>
    """


def _build_carousel_html(carousel_id: str) -> str:
    """Build the carousel HTML for quest images.

    Args:
        carousel_id: Unique ID for this carousel instance

    Returns:
        HTML string for the carousel with images and controls
    """
    # Get the updates and determine which one should be active by default
    updates = QUEST_CONFIG.get("updates", [])
    if not updates:
        # Fallback if no updates configured
        updates = [
            {
                "date": "",
                "title": "No updates yet",
                "description": "Check back soon for progress updates!",
                "images": [],
            }
        ]

    # Get the active update index (defaults to 0 if not specified)
    active_index = QUEST_CONFIG.get("active_update_index", 0)
    if active_index is None or active_index < 0:
        active_index = 0
    # Ensure index is within bounds
    active_index = min(active_index, len(updates) - 1)

    # Get the active update to display initially in the carousel
    active_update = updates[active_index]

    # Generate carousel images HTML from active update
    images_html = "".join(
        f'<img src="{image_url}" class="carousel-image {"active" if i == 0 else ""}" alt="Minecraft arena progress" />\n'
        for i, image_url in enumerate(active_update["images"])
    )
    indicators_html = "".join(
        f'<span class="indicator {"active" if i == 0 else ""}" data-index="{i}" role="button" tabindex="0" aria-label="View image {i + 1}"></span>\n'
        for i, _ in enumerate(active_update["images"])
    )

    # Only show indicators if more than one image
    indicators_display = "" if len(active_update["images"]) > 1 else "display: none;"

    # Generate update cards HTML (accordion style)
    def format_update_card(i: int, update: dict) -> str:
        """Format a single update accordion item HTML."""
        is_expanded = i == active_index  # Active update expanded by default
        active_class = "active" if is_expanded else ""
        expanded_class = "expanded" if is_expanded else ""
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
        <div class="quest-update-accordion {active_class} {expanded_class}"
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

    update_cards_html = "".join(
        format_update_card(i, update) for i, update in enumerate(updates)
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
            <div class="carousel-indicators" id="{carousel_id}-indicators">
                {indicators_html}
            </div>
        </div>

        <!-- Update cards below indicators -->
        <div class="quest-updates-container">
            {update_cards_html}
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
            gap: 12px;
            padding: 8px 12px;
            background: var(--background-fill-secondary);
            border-radius: 20px;
        }}

        .indicator {{
            width: 24px;
            height: 8px;
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
            overflow: hidden;
            transition: all 0.2s ease;
        }}

        .quest-update-accordion.active {{
            border-color: var(--color-accent);
        }}

        /* Accordion header (always visible) */
        .accordion-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.875rem 1rem;
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
            margin: 0;
            font-size: 0.9375rem;
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
    progress_data: dict | None = None,
    contributors_data: dict | None = None,
) -> tuple[
    gr.Column, gr.HTML, gr.HTML, gr.HTML, gr.Button, gr.Button, gr.Button, str, int
]:
    """Build the Community Quest section for home page.

    Args:
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

    # Build carousel HTML
    carousel_html = _build_carousel_html(carousel_id)

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
    rotation_interval = QUEST_CONFIG.get("carousel_rotation_interval", 6000)

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
