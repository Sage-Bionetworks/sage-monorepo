import {
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolConfigFilter,
  ComparisonToolFilter,
} from '@sagebionetworks/explorers/models';

export const mockComparisonToolConfigFilters: ComparisonToolConfigFilter[] = [
  {
    name: 'Age',
    field: 'age',
    values: ['4 months', '8 months', '12 months'],
  },
  {
    name: 'Model Type',
    field: 'model_type',
    values: ['Familial AD', 'Late Onset AD'],
  },
  {
    name: 'Sex',
    field: 'sex',
    values: ['Female', 'Male'],
  },
];

export const mockComparisonToolFilters: ComparisonToolFilter[] = [
  {
    name: 'Age',
    field: 'age',
    options: [
      { label: '4 months', selected: false },
      { label: '8 months', selected: false },
      { label: '12 months', selected: false },
    ],
  },
  {
    name: 'Model Type',
    field: 'model_type',
    options: [
      { label: 'Familial AD', selected: false },
      { label: 'Late Onset AD', selected: false },
    ],
  },
  {
    name: 'Sex',
    field: 'sex',
    options: [
      { label: 'Female', selected: false },
      { label: 'Male', selected: false },
    ],
  },
];

export const mockComparisonToolFiltersWithSelections: ComparisonToolFilter[] = [
  {
    name: 'Available Data',
    field: 'available_data',
    options: [
      { label: 'Biomarkers', selected: true },
      { label: 'Disease Correlation', selected: true },
      { label: 'Gene Expression', selected: true },
      { label: 'Pathology', selected: true },
    ],
  },
  {
    name: 'Contributing Center',
    field: 'center',
    options: [
      { label: 'IU/Jax/Pitt', selected: false },
      { label: 'UCI', selected: false },
    ],
  },
  {
    name: 'Model Type',
    field: 'model_type',
    options: [
      { label: 'Familial AD', selected: false },
      { label: 'Late Onset AD', selected: false },
    ],
  },
];

export const mockComparisonToolColumns: ComparisonToolConfigColumn[] = [
  {
    name: 'Age',
    field: 'age',
    type: 'text',
    tooltip: '',
    sort_tooltip: 'Sort by Age value',
  },
  {
    name: 'Sex',
    field: 'sex',
    type: 'text',
    tooltip: '',
    sort_tooltip: 'Sort by Sex value',
  },
  {
    name: 'CBE',
    field: 'CBE',
    type: 'heat_map',
    tooltip: 'Cerebellum',
    sort_tooltip: 'Sort by correlation value',
  },
  {
    name: 'DLPFC',
    field: 'DLPFC',
    type: 'heat_map',
    tooltip: 'Dorsolateral Prefrontal Cortex',
    sort_tooltip: 'Sort by correlation value',
  },
  {
    name: 'PHG',
    field: 'PHG',
    type: 'heat_map',
    tooltip: 'Parahippocampal Gyrus',
    sort_tooltip: 'Sort by correlation value',
  },
];

export const mockComparisonToolConfigs: ComparisonToolConfig[] = [
  {
    page: 'Disease Correlation',
    dropdowns: ['Red', 'Crimson'],
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Red', 'Maroon'],
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Light Blue', 'Powder Blue'],
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Light Blue', 'Sky Blue'],
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Dark Blue', 'Navy Blue'],
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
  {
    page: 'Disease Correlation',
    dropdowns: ['Blue', 'Dark Blue', 'Midnight Blue'],
    columns: mockComparisonToolColumns,
    filters: mockComparisonToolConfigFilters,
  },
];
