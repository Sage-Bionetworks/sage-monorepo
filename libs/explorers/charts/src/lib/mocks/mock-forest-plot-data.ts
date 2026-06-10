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

export const forestPlotItemsColoredByLine: ForestPlotItem[] = [
  { yAxisCategory: 'ENDO', value: 0.35, ciLeft: 0.29, ciRight: 0.41, color: '#FD7446' },
  { yAxisCategory: 'MURAL', value: 0.46, ciLeft: 0.36, ciRight: 0.56, color: '#FD8CC1' },
  { yAxisCategory: 'IMMUNE', value: 0.54, ciLeft: 0.44, ciRight: 0.64, color: '#C80813' },
  { yAxisCategory: 'ASTRO', value: 0.28, ciLeft: 0.21, ciRight: 0.35, color: '#D2AF81' },
  { yAxisCategory: 'OPC', value: 0.38, ciLeft: 0.3, ciRight: 0.46, color: '#FED439' },
  { yAxisCategory: 'OLIGO', value: 0.43, ciLeft: 0.35, ciRight: 0.51, color: '#AEC365' },
  { yAxisCategory: 'IN', value: 0.18, ciLeft: 0.1, ciRight: 0.26, color: '#1A9993' },
  { yAxisCategory: 'EN', value: 0.48, ciLeft: 0.37, ciRight: 0.59, color: '#197EC0' },
];
