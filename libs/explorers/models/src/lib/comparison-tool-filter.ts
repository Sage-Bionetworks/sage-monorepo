import { ComparisonToolFilterOption } from './comparison-tool-filter-option';

export interface ComparisonToolFilter {
  name: string;
  field: string;
  matchMode?: string;
  options: ComparisonToolFilterOption[];
}
