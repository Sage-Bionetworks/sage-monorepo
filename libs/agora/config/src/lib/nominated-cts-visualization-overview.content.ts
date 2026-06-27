import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';

/**
 * Visualization overview content shared by Agora's Nominated Targets and Nominated Drugs
 * comparison tools. The text is copied verbatim from Model-AD's tutorial (the heatmap-specific
 * "Customize your view" panel is intentionally omitted since these CTs have no heatmap).
 *
 * TODO: Two references describe features disabled for the Nominated CTs and should be revised
 * after stakeholder review:
 *   - Panel 1 mentions the Legend link, but these CTs set `legendEnabled: false` (no Legend link).
 *   - Panel 3 mentions "download an image of the pinned results", but these CTs set
 *     `allowPinnedImageDownload: false` (no image download control).
 */
export const NOMINATED_CTS_VISUALIZATION_OVERVIEW_PANES: VisualizationOverviewPane[] = [
  {
    heading: 'Comparison Tool Tutorial',
    content: `<p>Comparison Tools allow you to discover, compare and share results and related information. While each Comparison Tool presents different kinds of information, they all support similar exploration features. This tutorial demonstrates how you can use these features to find, download, and share results.</p>
      <p>You can always revisit this tutorial by clicking the Visualization Overview link at the bottom of the page.</p>
      <p>If the tool you're using has a legend, you can view it by clicking on the Legend link at the bottom of the page.</p>
      <div class="image-container"><img src="agora-assets/images/nomination-overview-1.png" /></div>`,
  },
  {
    heading: 'Search & Filter',
    content: `<p>Quickly find results of interest using the tool’s embedded search box. Enter a value to get a list of matching results, or perform a bulk search using a list of comma-separated full values.</p>
      <p>You can also use the tool’s supported filters to find results of interest. The filter region near the top of the page shows which filters are currently applied. You can clear specific filters, or all of the filters at once.</p>
      <div class="video-container"><video autoplay muted loop playsinline preload="none" controls><source src="agora-assets/videos/nomination-overview-2.mp4" type="video/mp4"></video></div>`,
  },
  {
    heading: 'Build, share and export custom comparisons',
    content: `<p>Pin up to 50 rows of results so that you can easily compare them. Once you've built your custom result set, you can share it with your colleagues by Share URL, download an image of the pinned results, and export the underlying data for further analysis.</p>
      <div class="video-container"><video autoplay muted loop playsinline preload="none" controls><source src="agora-assets/videos/nomination-overview-3.mp4" type="video/mp4"></video></div>`,
  },
];
