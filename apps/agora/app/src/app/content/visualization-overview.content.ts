import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';

export const VISUALIZATION_OVERVIEW_PANES: VisualizationOverviewPane[] = [
  {
    heading: 'Gene Comparison Overview',
    content: `<p>Welcome to Agora's Gene Comparison Tool. This overview demonstrates how to use the tool to explore results about genes related to AD. You can revisit this walkthrough by clicking the Visualization Overview link at the bottom of the page.</p>
      <p>Click on the Legend link at the bottom of the page to view the legend for the current visualization.</p>
      <div class="image-container"><img src="agora-assets/images/gct-how-to-0.svg" /></div>`,
  },
  {
    heading: 'View Detailed Expression Info',
    content: `<p>Click on a circle to show detailed information about a result for a specific brain region.</p>
      <div class="image-container"><img src="agora-assets/images/gct-how-to-1.gif" /></div>`,
  },
  {
    heading: 'Compare Multiple Genes',
    content: `<p>You can pin several genes to visually compare them together. Then export the data about your pinned genes as a CSV file for further analysis.</p>
      <div class="image-container"><img src="agora-assets/images/gct-how-to-2.gif" /></div>`,
  },
  {
    heading: 'Filter Gene Selection',
    content: `<p>Filter genes by Nomination, Association with AD, Study and more. Or simply use the search bar to quickly find the genes you are interested in.</p>
      <div class="image-container"><img src="agora-assets/images/gct-how-to-3.gif" /></div>`,
  },
];
