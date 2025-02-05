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

/**
 * Metabolomics
 */
export interface Metabolomics {
  _id: string;
  associated_gene_name: string;
  ensembl_gene_id: string;
  metabolite_id: string;
  metabolite_full_name: string;
  association_p: number;
  gene_wide_p_threshold_1kgp: number;
  n_per_group: Array<number>;
  boxplot_group_names: Array<string>;
  ad_diagnosis_p_value: Array<number>;
  transposed_boxplot_stats: Array<Array<number>>;
}
