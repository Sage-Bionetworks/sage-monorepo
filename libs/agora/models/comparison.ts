import { GCTGene, OverallScoresDistribution } from '@sagebionetworks/agora/api-client';
import { SelectItem } from 'primeng/api';

export interface GCTGeneResponse {
  items: GCTGene[];
}

export interface GCTSelectOption extends SelectItem {
  label?: string;
  value: any;
  disabled?: boolean;

  name?: string;
}

export interface GCTFilterOption {
  label: string;
  value?: any;
  preset?: any;
  selected?: boolean;
}

export interface GCTFilter {
  name: string;
  label: string;
  short?: string;
  description?: string;
  field?: string;
  matchMode?: string;
  order?: string;
  options: GCTFilterOption[];
}

export interface GCTDetailsPanelData {
  label?: string;
  heading?: string;
  subHeading?: string;
  value?: number;
  valueLabel?: string;
  pValue?: number;
  min?: number;
  max?: number;
  intervalMin?: number;
  intervalMax?: number;
  footer?: string;
}

export interface GCTScorePanelData {
  geneLabel?: string;
  scoreName?: string;
  columnName?: string;
  score: number | null;
  distributions?: OverallScoresDistribution[];
}

export interface GCTSortEvent {
  //data: string[] | number[];
  field: string;
  mode: string;
  order: number;
}
