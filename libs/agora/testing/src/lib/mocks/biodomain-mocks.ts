/* eslint-disable */

import { BioDomain, BioDomainInfo, BioDomains } from '@sagebionetworks/agora/api-client';

export const bioDomainInfoMock: BioDomainInfo[] = [
  {
    name: 'Cell Cycle',
  },
  {
    name: 'Lipid Metabolism',
  },
  {
    name: 'Structural Stabilization',
  },
];

export const emptyBioDomainMock: BioDomain = {
  biodomain: 'Autophagy',
  go_terms: [],
  n_biodomain_terms: 0,
  n_gene_biodomain_terms: 0,
  pct_linking_terms: 0,
};

export const bioDomainMock1: BioDomain = {
  biodomain: 'Cell Cycle',
  go_terms: ['G1/S transition of mitotic cell cycle'],
  n_biodomain_terms: 211,
  n_gene_biodomain_terms: 1,
  pct_linking_terms: 25,
};

export const bioDomainMock2: BioDomain = {
  biodomain: 'Lipid Metabolism',
  go_terms: ['lateral plasma membrane'],
  n_biodomain_terms: 827,
  n_gene_biodomain_terms: 1,
  pct_linking_terms: 25,
};

export const bioDomainMock3: BioDomain = {
  biodomain: 'Structural Stabilization',
  go_terms: ['cell-cell junction', 'regulation of actin cytoskeleton organization'],
  n_biodomain_terms: 466,
  n_gene_biodomain_terms: 2,
  pct_linking_terms: 50,
};

export const bioDomainsMock: BioDomains = {
  ensembl_gene_id: 'ENSG00000183856',
  gene_biodomains: [bioDomainMock1, bioDomainMock2, bioDomainMock3],
};
