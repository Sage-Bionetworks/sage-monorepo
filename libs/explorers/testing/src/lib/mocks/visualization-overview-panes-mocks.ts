import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';

export const mockVisualizationOverviewPanes: VisualizationOverviewPane[] = [
  {
    heading: 'Comparison Tool Tutorial',
    content: `<p>Comparison Tools allow you to discover, compare and share results.</p>
      <div class="image-container"><img src="/test-image.png" /></div>`,
  },
  {
    heading: 'Search & Filter',
    content: `<p>Enter a value to get a list of matching results, or perform a bulk search using a list of comma-separated full values.</p>
      <div class="video-container"><video autoplay muted loop playsinline><source src="/test-video.mp4" type="video/mp4"></video></div>`,
  },
  {
    heading: 'Build, share and export',
    content: `<p>Pin rows of results to compare them.</p>
      <div class="video-container"><video autoplay muted loop playsinline><source src="/test-video2.mp4" type="video/mp4"></video></div>`,
  },
  {
    heading: 'Customize your view',
    content: `<p>Sort results on any column.</p>
      <div class="image-container"><img src="/test-image2.gif" /></div>`,
  },
];
