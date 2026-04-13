import { ForestPlotItem } from '../models';

export const forestPlotItems: ForestPlotItem[] = [
  { yAxisCategory: 'ACC', value: -0.014, ciLeft: -0.133, ciRight: 0.104 },
  { yAxisCategory: 'CBE', value: 0.038, ciLeft: -0.089, ciRight: 0.165 },
  { yAxisCategory: 'DLPFC', value: -0.052, ciLeft: -0.178, ciRight: 0.074 },
  { yAxisCategory: 'FP', value: 0.021, ciLeft: -0.103, ciRight: 0.145 },
  { yAxisCategory: 'IFG', value: -0.031, ciLeft: -0.157, ciRight: 0.095 },
  { yAxisCategory: 'PHG', value: 0.044, ciLeft: -0.082, ciRight: 0.17 },
  { yAxisCategory: 'STG', value: -0.008, ciLeft: -0.134, ciRight: 0.118 },
  { yAxisCategory: 'TCX', value: 0.027, ciLeft: -0.099, ciRight: 0.153 },
];

export const forestPlotItemsColoredByLine: ForestPlotItem[] = forestPlotItems.map((item, i) => ({
  ...item,
  color: ['#8b8ad1', '#56B4E9', '#009E73', '#E69F00', '#D55E00', '#CC79A7', '#0072B2', '#F0E442'][
    i
  ],
}));
