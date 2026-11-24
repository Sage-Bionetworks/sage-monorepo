import { CellClassEffectSize, EqtlPhenotype } from '@sagebionetworks/qtl/api-client';
import { findCellClass } from '@sagebionetworks/qtl/util';

/** The astro eQTL for rs2120461 and RERE, as stored in the eqtl_phenotype collection. */
export const eqtlPhenotypeMock: EqtlPhenotype = {
  variant: 'rs2120461',
  ensembl_gene_id: 'ENSG00000142599',
  cell_type_id: 'astro',
  expression_summaries: [
    {
      genotype: 'CC',
      min: -1.4006,
      first_quartile: 0.5802,
      median: 1.354,
      third_quartile: 1.9008,
      max: 3.8816,
    },
    {
      genotype: 'CT',
      min: -1.3446,
      first_quartile: 0.0496,
      median: 0.4904,
      third_quartile: 0.9791,
      max: 2.3732,
    },
    {
      genotype: 'TT',
      min: -2.4852,
      first_quartile: -0.989,
      median: -0.5503,
      third_quartile: 0.0085,
      max: 1.5046,
    },
  ],
  effect_size_cell_class_data: [
    {
      cell_type_id: 'class_astro',
      cell_class_short: 'Astro',
      cell_class: 'Astrocyte',
      value: 0.442711,
      ci_left: 0.137115,
      ci_right: 0.748307,
    },
    {
      cell_type_id: 'class_en',
      cell_class_short: 'EN',
      cell_class: 'Excitatory Neuron',
      value: -0.033462,
      ci_left: -0.638222,
      ci_right: 0.571298,
    },
    {
      cell_type_id: 'class_immune',
      cell_class_short: 'Immune',
      cell_class: 'Immune Cell',
      value: -0.22059,
      ci_left: -0.786843,
      ci_right: 0.345663,
    },
    {
      cell_type_id: 'class_in',
      cell_class_short: 'IN',
      cell_class: 'Inhibitory Neuron',
      value: -0.271663,
      ci_left: -0.46386,
      ci_right: -0.079467,
    },
    {
      cell_type_id: 'class_oligo',
      cell_class_short: 'Oligo',
      cell_class: 'Oligodendrocyte',
      value: 0.045695,
      ci_left: -0.027471,
      ci_right: 0.11886,
    },
    {
      cell_type_id: 'class_opc',
      cell_class_short: 'OPC',
      cell_class: 'Oligodendrocyte Progenitor Cell',
      value: 0.515378,
      ci_left: 0.375932,
      ci_right: 0.654825,
    },
    {
      cell_type_id: 'class_vascular',
      cell_class_short: 'Vascular',
      cell_class: 'Vascular Cell',
      value: 0.124516,
      ci_left: -0.434836,
      ci_right: 0.683868,
    },
  ],
};

/** `eqtlPhenotypeMock`'s cell class effect sizes, each with its cell class color. */
export const coloredCellClassEffectSizesMock: (CellClassEffectSize & { color: string })[] =
  eqtlPhenotypeMock.effect_size_cell_class_data.map((effectSize) => {
    const cellClass = findCellClass(effectSize.cell_type_id);
    if (!cellClass) throw new Error(`Unknown cell class ID: ${effectSize.cell_type_id}`);
    return { ...effectSize, color: cellClass.color };
  });
