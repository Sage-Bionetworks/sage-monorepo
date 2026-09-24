import { IndividualData, ProteomicsIndividual } from '@sagebionetworks/model-ad/api-client';

const data: IndividualData[] = [
  {
    genotype: 'LOAD1',
    sex: 'Female',
    individual_id: '46131',
    value: 5.46448,
  },
  {
    genotype: 'LOAD1',
    sex: 'Male',
    individual_id: '46879',
    value: 5.07234,
  },
  {
    genotype: 'LOAD2',
    sex: 'Female',
    individual_id: '47012',
    value: 6.13927,
  },
  {
    genotype: 'LOAD2',
    sex: 'Male',
    individual_id: '47344',
    value: 6.48215,
  },
];

const baseMock = {
  ensembl_gene_id: 'ENSMUSG00000057738',
  gene_symbol: 'Sptan1',
  uniprotid: 'B9EKJ1',
  unique_id: 'ENSMUSG00000057738B9EKJ1',
  display_symbol: 'Sptan1 (B9EKJ1)',
  tissue: 'Hemibrain',
  name: 'LOAD2',
  model_group: 'LOAD2',
  matched_control: 'LOAD1',
  units: 'Log2 Counts per Million',
  result_order: ['LOAD1', 'LOAD2'],
};

export const proteomicsIndividualMocks: ProteomicsIndividual[] = [
  {
    ...baseMock,
    age: '4 months',
    age_numeric: 4,
    data,
  },
  {
    ...baseMock,
    age: '12 months',
    age_numeric: 12,
    data,
  },
  {
    ...baseMock,
    age: '18 months',
    age_numeric: 18,
    data,
  },
];
