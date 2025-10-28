import {
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolConfigFilter,
  ComparisonToolFilter,
} from '@sagebionetworks/explorers/models';

export const mockComparisonToolConfigFilters: ComparisonToolConfigFilter[] = [
  {
    name: 'Age',
    data_key: 'age',
    values: ['4 months', '8 months', '12 months'],
  },
  {
    name: 'Model Type',
    data_key: 'model_type',
    short_name: 'Type',
    values: ['Familial AD', 'Late Onset AD'],
  },
  {
    name: 'Sex',
    data_key: 'sex',
    values: ['Female', 'Male'],
  },
];

export const mockComparisonToolFilters: ComparisonToolFilter[] = [
  {
    name: 'Age',
    data_key: 'age',
    options: [
      { label: '4 months', selected: false },
      { label: '8 months', selected: false },
      { label: '12 months', selected: false },
    ],
  },
  {
    name: 'Model Type',
    data_key: 'model_type',
    short_name: 'Type',
    options: [
      { label: 'Familial AD', selected: false },
      { label: 'Late Onset AD', selected: false },
    ],
  },
  {
    name: 'Sex',
    data_key: 'sex',
    options: [
      { label: 'Female', selected: false },
      { label: 'Male', selected: false },
    ],
  },
];

export const mockComparisonToolFiltersWithSelections: ComparisonToolFilter[] = [
  {
    name: 'Available Data',
    data_key: 'available_data',
    short_name: 'Data',
    options: [
      { label: 'Biomarkers', selected: true },
      { label: 'Disease Correlation', selected: true },
      { label: 'Gene Expression', selected: true },
      { label: 'Pathology', selected: true },
    ],
  },
  {
    name: 'Contributing Center',
    data_key: 'center',
    short_name: 'Center',
    options: [
      { label: 'IU/Jax/Pitt', selected: false },
      { label: 'UCI', selected: false },
    ],
  },
  {
    name: 'Model Type',
    data_key: 'model_type',
    short_name: 'Type',
    options: [
      { label: 'Familial AD', selected: false },
      { label: 'Late Onset AD', selected: false },
    ],
  },
];

export const mockComparisonToolColumns: ComparisonToolConfigColumn[] = [
  {
    name: 'Age',
    type: 'text',
    data_key: 'age',
    tooltip: '',
    sort_tooltip: 'Sort by Age value',
  },
  {
    name: 'Sex',
    type: 'text',
    data_key: 'sex',
    tooltip: '',
    sort_tooltip: 'Sort by Sex value',
  },
  {
    name: 'CBE',
    type: 'heat_map',
    data_key: 'CBE',
    tooltip: 'Cerebellum',
    sort_tooltip: 'Sort by correlation value',
  },
  {
    name: 'DLPFC',
    type: 'heat_map',
    data_key: 'DLPFC',
    tooltip: 'Dorsolateral Prefrontal Cortex',
    sort_tooltip: 'Sort by correlation value',
  },
  {
    name: 'PHG',
    type: 'heat_map',
    data_key: 'PHG',
    tooltip: 'Parahippocampal Gyrus',
    sort_tooltip: 'Sort by correlation value',
  },
];

export const mockComparisonToolConfigs: ComparisonToolConfig[] = [
  {
    page: 'Disease Correlation',
    dropdowns: ['Red', 'Crimson'],
    row_count: 'over 200000',
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Red', 'Maroon'],
    row_count: 'over 200000',
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Light Blue', 'Powder Blue'],
    row_count: 'over 200000',
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Light Blue', 'Sky Blue'],
    row_count: 'over 200000',
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Dark Blue', 'Navy Blue'],
    row_count: 'over 200000',
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Dark Blue', 'Midnight Blue'],
    row_count: 'over 200000',
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
];
