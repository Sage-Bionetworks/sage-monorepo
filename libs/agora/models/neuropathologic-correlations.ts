export interface NeuropathologicCorrelation {
  _id: string;
  ensg: string;
  gname: string;
  oddsratio: number;
  ci_lower: number;
  ci_upper: number;
  pval: number;
  pval_adj: number;
  neuropath_type: string;
}
