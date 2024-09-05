import { Team } from './';

export interface ExperimentalValidation {
  _id: string;
  ensembl_gene_id: string;
  hgnc_symbol: string;
  hypothesis_tested: string;
  summary_findings: string;
  published: string;
  reference: string;
  species: string;
  model_system: string;
  outcome_measure: string;
  outcome_measure_details: string;
  balanced_for_sex: string;
  contributors: string;
  team: string;
  reference_doi: string;
  date_report: string;
  //
  team_data?: Team;
}
