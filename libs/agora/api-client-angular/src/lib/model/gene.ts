/**
 * Agora REST API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { MedianExpression } from './medianExpression';
import { ExperimentalValidation } from './experimentalValidation';
import { EnsemblInfo } from './ensemblInfo';
import { RnaDifferentialExpression } from './rnaDifferentialExpression';
import { BioDomains } from './bioDomains';
import { TargetNomination } from './targetNomination';
import { NeuropathologicCorrelation } from './neuropathologicCorrelation';
import { SimilarGenesNetwork } from './similarGenesNetwork';
import { Druggability } from './druggability';
import { OverallScores } from './overallScores';
import { ProteinDifferentialExpression } from './proteinDifferentialExpression';

/**
 * Gene
 */
export interface Gene {
  _id: string;
  ensembl_gene_id: string;
  name: string;
  summary: string;
  alias: Array<string>;
  is_igap: boolean;
  is_eqtl: boolean;
  is_any_rna_changed_in_ad_brain: boolean;
  rna_brain_change_studied: boolean;
  is_any_protein_changed_in_ad_brain: boolean;
  protein_brain_change_studied: boolean;
  target_nominations: Array<TargetNomination> | null;
  median_expression: Array<MedianExpression>;
  druggability: Array<Druggability>;
  total_nominations: number | null;
  is_adi?: boolean;
  is_tep?: boolean;
  resource_url?: string | null;
  rna_differential_expression?: Array<RnaDifferentialExpression> | null;
  proteomics_LFQ?: Array<ProteinDifferentialExpression> | null;
  proteomics_SRM?: Array<ProteinDifferentialExpression> | null;
  proteomics_TMT?: Array<ProteinDifferentialExpression> | null;
  metabolomics?: { [key: string]: any } | null;
  overall_scores?: OverallScores;
  neuropathologic_correlations?: Array<NeuropathologicCorrelation> | null;
  experimental_validation?: Array<ExperimentalValidation> | null;
  links?: { [key: string]: object } | null;
  similar_genes_network?: SimilarGenesNetwork;
  ab_modality_display_value?: string | null;
  safety_rating_display_value?: string | null;
  sm_druggability_display_value?: string | null;
  pharos_class_display_value?: string | null;
  is_any_rna_changed_in_ad_brain_display_value?: string | null;
  is_any_protein_changed_in_ad_brain_display_value?: string | null;
  nominated_target_display_value?: boolean | null;
  initial_nomination_display_value?: number | null;
  teams_display_value?: string | null;
  study_display_value?: string | null;
  programs_display_value?: string | null;
  input_data_display_value?: string | null;
  bio_domains?: BioDomains;
  ensembl_info: EnsemblInfo;
}
