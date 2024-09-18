export interface OverallScores {
  // _id: string; // Not used
  ensembl_gene_id: string;
  // hgnc_symbol: string; // Not used
  target_risk_score: number;
  genetics_score: number;
  multi_omics_score: number;
  literature_score: number;
}
