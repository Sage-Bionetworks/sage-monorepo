import { Eqtl } from '@sagebionetworks/qtl/api-client';

/** The astro eQTL for rs2120461 and RERE, as stored in the eqtl_results collection. */
export const eqtlMock: Eqtl = {
  composite_id: 'rs2120461~ENSG00000142599~astro',
  variant: 'rs2120461',
  variant_location: { chr: '1', start: 8387662, end: 8387663 },
  effect_direction: 'Up',
  gene_symbol: 'RERE',
  ensembl_gene_id: 'ENSG00000142599',
  gene_location: { chr: '1', start: 8387662, end: 8387663 },
  beta: 0.575878,
  adj_p_val: '1.05E-136',
  adj_p_val_numeric: -135.977471,
  maf: 0.3321,
  minor_allele: 'C',
  reference_allele: 'T',
  cell_type_id: 'astro',
  cell_class: 'Astrocyte',
  cell_class_short: 'Astro',
  cell_subclass: 'Astrocyte',
  cell_subclass_short: 'Astro',
};
