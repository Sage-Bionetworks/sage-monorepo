export interface ProteinDifferentialExpression {
  _id: string;
  uniqid: string;
  hgnc_symbol: string;
  uniprotid: string;
  ensembl_gene_id: string;
  tissue: string;
  log2_fc: number;
  ci_upr: number;
  ci_lwr: number;
  pval: number;
  cor_pval: number;
}
