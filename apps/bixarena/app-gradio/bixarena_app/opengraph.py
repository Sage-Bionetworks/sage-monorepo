"""OpenGraph utilities for fixing Gradio's meta tag issues."""

from __future__ import annotations

import logging
import re

from starlette.middleware.base import BaseHTTPMiddleware
from starlette.responses import Response

logger = logging.getLogger(__name__)


def build_opengraph_meta_tags(title: str, description: str, og_image_url: str) -> str:
    """Build OpenGraph meta tags for social media sharing.

    Args:
        title: Page title to use for og:title
        description: Description for meta description and og:description
        og_image_url: Full URL to the OpenGraph image

    Returns:
        HTML string containing all OpenGraph meta tags
    """
    og_tags = f"""
    <!-- Standard Meta Tags -->
    <meta name="description" content="{description}">

    <!-- OpenGraph Meta Tags -->
    <meta property="og:title" content="{title}">
    <meta property="og:description" content="{description}">
    <meta property="og:type" content="website">
    <meta property="og:url" content="https://bioarena.io">
    <meta property="og:image" content="{og_image_url}">
    <meta property="og:site_name" content="BioArena">
    <meta property="og:locale" content="en_US">
    <meta property="og:image:width" content="1200">
    <meta property="og:image:height" content="630">
    <meta property="og:image:type" content="image/png">
    <meta property="og:image:alt" content="BioArena OpenGraph Image">

    <!-- X Card Meta Tags -->
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="{title}">
    <meta name="twitter:description" content="{description}">
    <meta name="twitter:image" content="{og_image_url}">
    <meta name="twitter:image:alt" content="BioArena - AI model benchmarking">
    """

    return og_tags


class OpenGraphFixMiddleware(BaseHTTPMiddleware):
    """Middleware to fix Gradio's OpenGraph meta tag issues.

    Extracts custom meta tags from gradio_config JSON and inserts them into HTML head,
    while removing default Gradio meta tags.
    """

    async def dispatch(self, request, call_next):
        response = await call_next(request)

        # Only process HTML responses
        if response.headers.get("content-type", "").startswith("text/html"):
            body = b""
            async for chunk in response.body_iterator:
                body += chunk

            try:
                html = body.decode("utf-8")
                html = self.fix_opengraph_tags(html)
                body = html.encode("utf-8")
            except Exception as e:
                logger.error(f"Error fixing OpenGraph tags: {e}")

            # Update Content-Length header
            headers = dict(response.headers)
            headers.pop("content-length", None)
            headers.pop("Content-Length", None)
            headers["Content-Length"] = str(len(body))

            return Response(
                content=body,
                status_code=response.status_code,
                headers=headers,
                media_type=response.media_type,
            )

        return response

    def fix_opengraph_tags(self, html: str) -> str:
        """Fix OpenGraph meta tags in HTML."""
        # 1. Extract custom meta tags from gradio_config JSON
        match = re.search(r'"head":"((?:[^"\\]|\\.)*)"', html)
        if match:
            head_json = match.group(1)
            custom_tags = (
                head_json.replace("\\n", "\n")
                .replace("\\u003c", "<")
                .replace("\\u003e", ">")
                .replace('\\"', '"')
            )

            # 2. Insert custom tags after </style>
            marker = "\n\t\t<!-- Custom OpenGraph Meta Tags -->\n"
            style_end = html.find("</style>")
            if style_end > 0:
                insert_pos = html.find("\n", style_end) + 1
                html = (
                    html[:insert_pos] + marker + custom_tags + "\n" + html[insert_pos:]
                )

                # 3. Remove ALL og:* and twitter:* meta tags after custom block
                marker_pos = html.find(marker)
                if marker_pos > 0:
                    # Find where custom tags end
                    search_start = marker_pos + len(marker) + len(custom_tags)
                    script_pos = html.find("<script", search_start)

                    if script_pos > search_start:
                        # Split HTML into sections
                        section_before = html[:search_start]
                        section_to_clean = html[search_start:script_pos]
                        section_after = html[script_pos:]

                        # Remove all og:* and twitter:* meta tags from middle section
                        section_to_clean = re.sub(
                            r'<meta\s+(property|name)="(og:|twitter:)[^"]*"\s+content="[^"]*"\s*/?>',
                            "",
                            section_to_clean,
                        )

                        html = section_before + section_to_clean + section_after

        return html
