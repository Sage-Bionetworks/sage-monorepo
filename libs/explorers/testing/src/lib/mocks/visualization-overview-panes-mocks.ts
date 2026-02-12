import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';

export const mockVisualizationOverviewPanes: VisualizationOverviewPane[] = [
  {
    heading: 'Comparison Tool Tutorial',
    content: `<p>Comparison Tools allow you to discover, compare and share results and related information. While each Comparison Tool presents different kinds of information, they all support similar exploration features. This tutorial demonstrates how you can use these features to find, download, and share results.</p>
      <p>You can always revisit this tutorial by clicking the Visualization Overview link at the bottom of the page.</p>
      <p>If the tool you're using has a legend, you can view it by clicking on the Legend link at the bottom of the page.</p>
      <div class="image-container"><img src="model-ad-assets/images/visualization-overview-1.png" /></div>`,
  },
  {
    heading: 'Search & Filter',
    content: `<p>Enter a value to get a list of matching results, or perform a bulk search using a list of comma-separated full values.</p>
      <p>You can also use the tool's supported filters to find results of interest. The filter region near the top of the page shows which filters are currently applied. You can clear specific filters, or all of the filters at once.</p>
      <div class="video-container"><video autoplay muted loop playsinline preload="none" controls><source src="model-ad-assets/videos/visualization-overview-2.mp4" type="video/mp4"></video></div>`,
  },
  {
    heading: 'Build, share and export custom comparisons',
    content: `<p>Pin up to 50 rows of results so that you can easily compare them. Once you've built your custom result set, you can share it with your colleagues by Share URL, download an image of the pinned results, and export the underlying data for further analysis.</p>
      <div class="video-container"><video autoplay muted loop playsinline preload="none" controls><source src="model-ad-assets/videos/visualization-overview-3.mp4" type="video/mp4"></video></div>`,
  },
  {
    heading: 'Customize your view',
    content: `<p>Sort results on any column by clicking on the sort control for that column. You can sort by multiple columns by holding down the command (⌘ Mac) or control (Windows) key while clicking on additional sort controls.</p>
      <p>Use the p-value filter control to mask insignificant heatmap results based on a configurable p-value threshold.</p>
      <p>You can also control which columns are visible using the column picker.</p>
      <div class="image-container"><img src="model-ad-assets/images/gct-how-to-3.gif" /></div>`,
  },
];
