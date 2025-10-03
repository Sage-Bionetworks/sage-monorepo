export interface ComparisonToolFilterOption {
  label: string;
  selected: boolean;
}

export interface ComparisonToolFilter {
  name: string;
  field: string;
  options: ComparisonToolFilterOption[];
}

export interface ComparisonToolFilterConfig {
  name: string;
  field: string;
  values: string[];
}
