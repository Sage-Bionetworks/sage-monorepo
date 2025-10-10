import {
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
