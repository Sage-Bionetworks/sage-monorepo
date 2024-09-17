export interface Metabolomics {
  _id: string;
  associated_gene_name: string;
  ensembl_gene_id: string;
  metabolite_id: string;
  metabolite_full_name: string;
  association_p: number;
  gene_wide_p_threshold_1kgp: number;
  n_per_group: number[];
  boxplot_group_names: string[];
  ad_diagnosis_p_value: number[];
  transposed_boxplot_stats: number[][];
}
