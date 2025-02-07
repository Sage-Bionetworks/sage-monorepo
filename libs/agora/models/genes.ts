import { Team } from '@sagebionetworks/agora/api-client-angular';
import {
  BioDomains,
  ExperimentalValidation,
  Metabolomics,
  NeuropathologicCorrelation,
  OverallScores,
  ProteinDifferentialExpression,
  RnaDifferentialExpression,
  SimilarGenesNetwork,
} from './';
import { EnsemblInfo } from './ensembl-info';

export interface TargetNomination {
  source: string;
  team: string;
  rank: string;
  hgnc_symbol: string;
  target_choice_justification: string;
  predicted_therapeutic_direction: string;
  data_used_to_support_target_selection: string;
  data_synapseid: string;
  study: string | null;
  input_data: string;
  validation_study_details: string;
  initial_nomination: number;
  team_data?: Team;
}

export interface MedianExpression {
  min?: number;
  first_quartile?: number;
  median?: number;
  mean?: number;
  third_quartile?: number;
  max?: number;
  tissue: string;
}

export interface Druggability {
  sm_druggability_bucket: number;
  safety_bucket: number;
  abability_bucket: number;
  pharos_class?: string[];
  // classification should really be named sm_druggability_bucket_definition
  classification: string;
  safety_bucket_definition: string;
  abability_bucket_definition: string;
}

export interface Gene {
  _id: string;
  ensembl_gene_id: string;
  name: string;
  summary: string;
  hgnc_symbol: string;
  alias: string[];
  uniprotkb_accessions: string[];
  is_igap: boolean;
  is_eqtl: boolean;
  is_any_rna_changed_in_ad_brain: boolean;
  rna_brain_change_studied: boolean;
  is_any_protein_changed_in_ad_brain: boolean;
  protein_brain_change_studied: boolean;
  target_nominations: TargetNomination[] | null;
  median_expression: MedianExpression[];
  druggability: Druggability;
  total_nominations: number | null;
  is_adi: boolean;
  is_tep: boolean;
  resource_url: string | null;

  // Added by API (not in mongo document)
  rna_differential_expression?: RnaDifferentialExpression[];
  proteomics_LFQ?: ProteinDifferentialExpression[];
  proteomics_SRM?: ProteinDifferentialExpression[];
  proteomics_TMT?: ProteinDifferentialExpression[];
  metabolomics?: Metabolomics;
  overall_scores?: OverallScores;
  neuropathologic_correlations?: NeuropathologicCorrelation[];
  experimental_validation?: ExperimentalValidation[];
  links?: { [key: string]: any };

  // Added by app (not in mongo document)
  similar_genes_network?: SimilarGenesNetwork;

  // Similar table (not in mongo document)
  pharos_class_display_value?: string[];
  is_any_rna_changed_in_ad_brain_display_value?: string;
  is_any_protein_changed_in_ad_brain_display_value?: string;
  nominated_target_display_value?: boolean;
  initial_nomination_display_value?: number;

  // Nominated table (not in mongo document)
  teams_display_value?: string;
  study_display_value?: string;
  programs_display_value?: string;
  input_data_display_value?: string;

  bio_domains?: BioDomains;

  ensembl_info: EnsemblInfo;
}

export interface GenesResponse {
  items: Gene[];
}
