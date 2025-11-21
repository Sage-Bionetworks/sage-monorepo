import logging

import gradio as gr
import pandas as pd
from bixarena_api_client import ApiClient, LeaderboardApi
from bixarena_api_client.exceptions import ApiException

from bixarena_app.api.api_client_helper import get_api_configuration

logger = logging.getLogger(__name__)


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
                "License": [],
            }

            for entry in leaderboard_response.entries:
                data["Rank"].append(entry.rank)
                data["Model"].append(entry.model_name)
                data["Score"].append(entry.bt_score)
                data["95% CI"].append(
                    f"[{entry.bootstrap_q025}, {entry.bootstrap_q975}]"
                )
                data["Total Votes"].append(entry.vote_count)
                data["License"].append(entry.license)

            logger.info("✅ Fetched leaderboard data")
            return pd.DataFrame(data)

    except ApiException as e:
        logger.error(f"❌ Failed to fetch leaderboard: {e}")
        return None


def filter_dataframe(df, model_filter):
    """Filter dataframe by model name"""
    if model_filter:
        mask = df["Model"].str.contains(model_filter, case=False, na=False)
        return df[mask]
    return df


def load_leaderboard_stats_on_page_load() -> dict:
    """Load leaderboard stats and update the metrics HTML.

    Returns:
        Gradio update dict for the metrics HTML component
    """
    metrics_html = ""
    return gr.update(value=metrics_html)


def build_leaderboard_page():
    """Build the BixArena leaderboard page"""
    # Get initial data from API
    df = fetch_leaderboard_data()

    with gr.Column():
        # Title and stats
        gr.HTML(
            '<h1 style="font-size: var(--text-section-title); color: var(--body-text-color); margin-bottom: 0.5rem; font-weight: 600;">🏆 Leaderboard</h1>'
        )
        gr.HTML(
            '<p style="font-size: var(--text-xl); color: var(--body-text-color-subdued); margin: 0;">Community-driven evaluation of biomedical AI models</p>'
        )

        # Metrics - will be populated dynamically on page load
        leaderboard_metrics = gr.HTML("")

        if df is None or len(df) == 0:
            # Show placeholder when no data is available
            gr.HTML("""
            <div style="
                background: var(--panel-background-fill);
                border: 2px solid var(--border-color-primary);
                border-radius: 12px;
                padding: 64px 48px;
            ">
                <div style="max-width: 800px; margin: 0 auto; text-align: center;">
                    <!-- Icon -->
                    <div style="display: flex; justify-content: center; margin-bottom: 24px;">
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
                        The leaderboard will be published once we have sufficient evaluations to
                        ensure statistically meaningful model rankings.
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
            """)
        else:
            # Show leaderboard table when data is available
            # Filter controls
            with gr.Row():
                model_filter = gr.Textbox(
                    show_label=False, placeholder="Search models...", scale=3
                )

            # Main leaderboard table
            leaderboard_table = gr.Dataframe(
                value=df,
                interactive=False,
                wrap=True,
                headers=[
                    "Rank",
                    "Model",
                    "Score",
                    "95% CI",
                    "Total Votes",
                    "License",
                ],
            )

            # Update functions
            def update_table(filter_text):
                filtered_df = filter_dataframe(df, filter_text)
                return filtered_df

            # Event handlers
            model_filter.change(
                fn=update_table, inputs=[model_filter], outputs=[leaderboard_table]
            )

    return leaderboard_metrics
