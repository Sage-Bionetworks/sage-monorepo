import { BoxPlotChartItem } from '@sagebionetworks/agora/models';

export const boxPlotChartItemsMock: BoxPlotChartItem[] = [
  {
    key: 'CBE',
    value: [-0.5, -0.08, 0.5],
    circle: {
      value: -0.0752,
      tooltip:
        'MSN is not significantly differentially expressed in CBE with a log fold change value of -0.0752 and an adjusted p-value of 0.531.',
    },
    quartiles: [-0.1, -0.08, 0.1],
  },
  {
    key: 'ACC',
    value: [-0.256, -0.0036, 0.2503],
    circle: {
      value: -0.0144,
      tooltip:
        'MSN is not significantly differentially expressed in ACC with a log fold change value of -0.0145 and an adjusted p-value of 0.893.',
    },
    quartiles: [-0.0661, -0.0036, 0.0604],
  },
];
