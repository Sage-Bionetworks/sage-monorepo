import logging
import os
from datetime import datetime

import gradio as gr
import pandas as pd
from bixarena_api_client import ApiClient, LeaderboardApi
from bixarena_api_client.exceptions import ApiException

from bixarena_app.api.api_client_helper import get_api_configuration

logger = logging.getLogger(__name__)


def create_subtitle_row_html(updated_at: str | datetime | None) -> str:
    """Create HTML for subtitle row with optional time badge

    Args:
        updated_at: ISO format date string or datetime when the leaderboard
                    was last updated, or None if no timestamp available

    Returns:
        HTML string containing subtitle and time badge (if timestamp provided)
    """
    time_badge_html = ""
    if updated_at is not None:
        # Convert to datetime if it's a string
        if isinstance(updated_at, str):
            dt = datetime.fromisoformat(updated_at.replace("Z", "+00:00"))
        else:
            dt = updated_at

        # Format date, e.g. "Dec 4, 2025"
        formatted_date = dt.strftime("%b %-d, %Y")

        time_badge_html = f"""
        <div style="
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 20px;
            background: var(--panel-background-fill);
            border: 1px solid var(--border-color-primary);
            border-radius: 8px;
        ">
            <div style="
                display: flex;
                flex-direction: column;
                gap: 4px;
            ">
                <span style="
                    font-size: var(--text-sm);
                    color: var(--body-text-color-subdued);
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                ">Last Updated</span>
                <span style="
                    font-size: var(--text-lg);
                    font-weight: 500;
                ">{formatted_date}</span>
            </div>
        </div>
        """

    return f"""
    <div style="
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 40px !important;
        flex-wrap: wrap;
        gap: 16px;
    ">
        <p style="
            font-size: var(--text-xl);
            color: var(--body-text-color-subdued);
            margin: 0;
        ">Community-driven evaluation of AI models on biomedical topics</p>
        {time_badge_html}
    </div>
    """


class LeaderboardView:
    def __init__(self, placeholder, content, table, state, timestamp_badge):
        self.placeholder = placeholder
        self.content = content
        self.table = table
        self.state = state
        self.timestamp_badge = timestamp_badge

    @property
    def outputs(self):
        return [
            self.placeholder,
            self.content,
            self.table,
            self.state,
            self.timestamp_badge,
        ]


def generate_test_leaderboard_data():
    """Generate test leaderboard data for development

    Returns:
        Tuple of (DataFrame, updated_at)
    """
    test_data = {
        "Rank": [1, 2, 3],
        "Model": [
            "[Claude-Opus-4.5](https://openrouter.ai/anthropic/claude-opus-4.5)",
            "[GPT-5.1](https://openrouter.ai/openai/gpt-5.1)",
            "[Grok-4.1](https://openrouter.ai/x-ai/grok-4.1)",
        ],
        "Score": [1250, 1200, 1180],
        "95% CI": [
            "[1240, 1260]",
            "[1185, 1215]",
            "[1165, 1195]",
        ],
        "Total Votes": [1500, 1450, 1400],
        "Organization": [
            "Anthropic",
            "OpenAI",
            "xAI",
        ],
        "License": [
            "Commercial",
            "Commercial",
            "Commercial",
        ],
    }

    # Fixed test timestamp
    test_updated_at = "2025-12-04T12:00:00+00:00"
    logger.info("✅ Generated test leaderboard data")
    return pd.DataFrame(test_data), test_updated_at


def fetch_leaderboard_data():
    """Fetch leaderboard data from the BixArena API

    Returns:
        Tuple of (DataFrame or None, updated_at or None)
    """
    # Show test data for development
    env = os.environ.get("ENV", "").lower()
    if env == "dev":
        return generate_test_leaderboard_data()

    try:
        configuration = get_api_configuration()
        with ApiClient(configuration) as api_client:
            api_instance = LeaderboardApi(api_client)

            # Fetch leaderboard entries for "overall" leaderboard
            # API returns the latest visible snapshot by default
            leaderboard_response = api_instance.get_leaderboard(
                leaderboard_id="overall"
            )

            # If no entries, return None to show placeholder
            if not leaderboard_response.entries:
                return None, None

            updated_at = leaderboard_response.updated_at

            # Convert API response to DataFrame
            data = {
                "Rank": [],
                "Model": [],
                "Score": [],
                "95% CI": [],
                "Total Votes": [],
                "Organization": [],
                "License": [],
            }

            for entry in leaderboard_response.entries:
                data["Rank"].append(entry.rank)
                data["Model"].append(f"[{entry.model_id}]({entry.model_url})")
                data["Score"].append(round(entry.bt_score))
                data["95% CI"].append(
                    f"[{round(entry.bootstrap_q025)}, {round(entry.bootstrap_q975)}]"
                )
                data["Total Votes"].append(entry.vote_count)
                data["Organization"].append(entry.model_organization or "")
                data["License"].append(entry.license)

            logger.info("✅ Fetched leaderboard data")
            return pd.DataFrame(data), updated_at

    except ApiException as e:
        logger.error(f"❌ Failed to fetch leaderboard: {e}")
        return None, None


def filter_dataframe(df, model_filter):
    """Filter dataframe by model name or organization"""
    if model_filter:
        model_mask = df["Model"].str.contains(model_filter, case=False, na=False)
        org_mask = df["Organization"].str.contains(model_filter, case=False, na=False)
        mask = model_mask | org_mask
        return df[mask]
    return df


def filter_leaderboard_table(filter_text, df):
    """Filter leaderboard table by model name or organization"""
    if df is None:
        return None
    if not filter_text:
        return df
    return filter_dataframe(df, filter_text)


def refresh_leaderboard():
    """Refresh leaderboard data

    Returns:
        Tuple of (placeholder_visibility, content_visibility, table_data,
                  dataframe_state, badge_html)
    """
    df, updated_at = fetch_leaderboard_data()
    has_rows = df is not None and not df.empty

    placeholder_update = gr.update(visible=not has_rows)
    content_update = gr.update(visible=has_rows)
    table_update = gr.update(value=df if has_rows else None, visible=has_rows)

    # Create subtitle row with time badge HTML
    badge_html = create_subtitle_row_html(updated_at)

    return placeholder_update, content_update, table_update, df, badge_html


def build_leaderboard_page():
    """Build the BixArena leaderboard page"""
    # Don't fetch data initially
    initial_df = None
    initial_updated_at = None

    # JavaScript to customize column header tooltips
    tooltips_js = """
    () => {
        function customizeTooltips() {
            const table = document.querySelector('#leaderboard_table');
            if (table) {
                const headers = table.querySelectorAll('thead th');
                for (let i = 0; i < headers.length; i++) {
                    const header = headers[i];
                    const headerButton = header.querySelector('.header-button');

                    if (headerButton) {
                        const span = headerButton.querySelector('span');
                        const text = span ?
                            span.textContent.replace(' ⓘ', '').trim() : '';

                        if (text === 'Score') {
                            // Customize Score column tooltip and add info icon
                            const scoreLabel =
                                "Bradley–Terry rating from pairwise battles";
                            header.setAttribute('title', scoreLabel);
                            headerButton.setAttribute('title', scoreLabel);

                            // Add info icon
                            if (span && !span.querySelector('.info-icon')) {
                                const infoIcon = document.createElement('span');
                                infoIcon.className = 'info-icon';
                                infoIcon.textContent = ' ⓘ';
                                infoIcon.style.cssText =
                                    'opacity: 0.6; font-size: 0.9em;';
                                span.appendChild(infoIcon);
                            }
                        } else {
                            // Remove tooltips from all other columns
                            header.removeAttribute('title');
                            headerButton.removeAttribute('title');
                        }
                    }
                }
            }
        }

        setTimeout(customizeTooltips, 1500);
    }
    """

    with gr.Blocks() as blocks:
        with gr.Column(elem_classes="leaderboard-header"):
            # Title and stats
            gr.HTML(
                """
            <h1 style="
                font-size: var(--text-section-title);
                color: var(--body-text-color);
                font-weight: 600;
            ">🏆 Leaderboard</h1>
            <style>
            /* Prevent header from growing vertically */
            .leaderboard-header {
                flex-grow: 0 !important;
                gap: 0 !important;
            }

            /* Search box styling */
            .leaderboard_search {
                border-radius: 12px !important;
            }

            .leaderboard_search textarea {
                overflow-y: auto !important;
                padding: 16px 20px !important;
                line-height: 1.5 !important;
            }

            /* Table links Styling */
            #leaderboard_table .md a {
                color: var(--body-text-color) !important;
                text-decoration: none !important;
                transition: color 0.2s ease;
            }

            #leaderboard_table .md a:hover {
                color: var(--color-accent) !important;
            }

            </style>
            """
            )

            timestamp_badge = gr.HTML(create_subtitle_row_html(initial_updated_at))

            # State to store the full dataframe for filtering
            df_state = gr.State(initial_df)

            # Placeholder - shown when no data
            leaderboard_placeholder = gr.HTML(
                """
            <div style="
                background: var(--panel-background-fill);
                border: 2px solid var(--border-color-primary);
                border-radius: 12px;
                padding: 64px 48px;
            ">
                <div style="max-width: 800px; margin: 0 auto; text-align: center;">
                    <!-- Icon -->
                    <div style="
                        display: flex;
                        justify-content: center;
                        margin-bottom: 24px;
                    ">
                        <div style="
                            width: 56px;
                            height: 56px;
                            border-radius: 50%;
                            background: color-mix(
                                in srgb, var(--color-accent) 10%, transparent
                            );
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        ">
                            <span style="font-size: 28px;">⏰</span>
                        </div>
                    </div>

                    <!-- Title -->
                    <h2 style="
                        font-weight: 500;
                        margin-bottom: 16px;
                        line-height: 1.5;
                        color: var(--body-text-color);
                    ">
                        Leaderboard Rankings Coming Soon
                    </h2>

                    <!-- Description -->
                    <p style="
                        color: var(--body-text-color);
                        line-height: 1.625;
                        margin-bottom: 0;
                    ">
                        The leaderboard will be published once we have
                        sufficient evaluations to ensure statistically
                        meaningful model rankings.
                    </p>
                    <div style="padding-top: 16px;">
                        <p style="
                            color: var(--body-text-color-subdued);
                            line-height: 1.625;
                            margin: 0;
                        ">
                            Keep battling to help us build a comprehensive benchmark
                        </p>
                    </div>
                </div>
            </div>
            """,
            )

        # Search filter + table
        with gr.Column(visible=False) as leaderboard_content:
            with gr.Row():
                model_filter = gr.Textbox(
                    show_label=False,
                    placeholder="Search models or organizations...",
                    elem_classes="leaderboard_search",
                    container=False,
                )

            # Main leaderboard table
            leaderboard_table = gr.Dataframe(
                value=initial_df,
                interactive=False,
                wrap=True,
                headers=[
                    "Rank",
                    "Model",
                    "Score",
                    "95% CI",
                    "Total Votes",
                    "Organization",
                    "License",
                ],
                datatype=[
                    "number",
                    "markdown",
                    "number",
                    "str",
                    "number",
                    "str",
                    "str",
                ],
                elem_id="leaderboard_table",
            )

            # Connect filter to table
            model_filter.change(
                fn=filter_leaderboard_table,
                inputs=[model_filter, df_state],
                outputs=[leaderboard_table],
            )

        # Customize column header tooltips on load
        blocks.load(fn=None, js=tooltips_js)

    return LeaderboardView(
        leaderboard_placeholder,
        leaderboard_content,
        leaderboard_table,
        df_state,
        timestamp_badge,
    )
