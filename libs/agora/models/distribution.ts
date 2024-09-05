export interface RnaDistribution {
  _id: string;
  model: string;
  tissue: string;
  min: number;
  max: number;
  first_quartile: number;
  median: number;
  third_quartile: number;
}

export interface ProteomicsDistribution {
  _id: string;
  tissue: string;
  min: number;
  max: number;
  median: number;
  first_quartile: number;
  third_quartile: number;
  type: string;
}

export interface OverallScoresDistribution {
  //_id: string;
  distribution: number[];
  bins: number[][]; // Array of [binStart, binEnd]
  min: number;
  max: number;
  mean: number;
  first_quartile: number;
  third_quartile: number;
  name: string;
  syn_id: string; // Wiki's ownerId
  wiki_id: string;
}

export interface ScoreData {
  distribution: number;
  bins: number[];
}

export interface Distribution {
  rna_differential_expression: RnaDistribution[];
  proteomics_LFQ: ProteomicsDistribution[];
  proteomics_SRM: ProteomicsDistribution[];
  proteomics_TMT: ProteomicsDistribution[];
  overall_scores: OverallScoresDistribution[];
}
