import logging

import gradio as gr
import pandas as pd
from bixarena_api_client import ApiClient, LeaderboardApi
from bixarena_api_client.exceptions import ApiException

from bixarena_app.api.api_client_helper import get_api_configuration

logger = logging.getLogger(__name__)


class LeaderboardView:
    def __init__(self, placeholder, content, table, state):
        self.placeholder = placeholder
        self.content = content
        self.table = table
        self.state = state

    @property
    def outputs(self):
        return [self.placeholder, self.content, self.table, self.state]


def fetch_leaderboard_data():
    """Fetch leaderboard data from the BixArena API

    Returns:
        DataFrame with leaderboard data, or None if no data available
    """
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
                return None

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
            return pd.DataFrame(data)

    except ApiException as e:
        logger.error(f"❌ Failed to fetch leaderboard: {e}")
        return None


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
        Tuple of (placeholder_visibility, content_visibility, table_data, dataframe_state)
    """
    df = fetch_leaderboard_data()
    has_rows = df is not None and not df.empty

    placeholder_update = gr.update(visible=not has_rows)
    content_update = gr.update(visible=has_rows)
    table_update = gr.update(value=df if has_rows else None, visible=has_rows)

    return placeholder_update, content_update, table_update, df


def build_leaderboard_page():
    """Build the BixArena leaderboard page"""
    initial_df = None

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
        setTimeout(customizeTooltips, 500);
        setTimeout(customizeTooltips, 1000);
        setTimeout(customizeTooltips, 2000);
        setTimeout(customizeTooltips, 3000);
    }
    """

    with gr.Blocks() as blocks:
        with gr.Column():
            # Title and stats
            gr.HTML(
                """
            <h1 style="
                font-size: var(--text-section-title);
                color: var(--body-text-color);
                margin-bottom: 0.5rem;
                font-weight: 600;
            ">🏆 Leaderboard</h1>
            <style>
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
            gr.HTML(
                """
                <p style="
                    font-size: var(--text-xl);
                    color: var(--body-text-color-subdued);
                    margin-bottom: 40px !important;
                ">Community-driven evaluation of biomedical AI models</p>
                """
            )

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
    )
