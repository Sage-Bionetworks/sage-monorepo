import { GCTGene } from '@sagebionetworks/agora/api-client';
import { GCTDetailsPanelData, GCTFilter, GCTScorePanelData } from '@sagebionetworks/agora/models';
import { distributionMock } from './distribution-mocks';

export const comparisonGeneMock1: GCTGene = {
  ensembl_gene_id: 'ENSG00000147065',
  hgnc_symbol: 'MSN',
  tissues: [
    {
      name: 'ACC',
      logfc: -0.0144678061734664,
      adj_p_val: 0.893263674388766,
      ci_l: -0.133332670728704,
      ci_r: 0.104397058381771,
    },
    {
      name: 'CBE',
      logfc: -0.0751554371582435,
      adj_p_val: 0.530889774959758,
      ci_l: -0.247449816301241,
      ci_r: 0.0971389419847545,
    },
    {
      name: 'DLPFC',
      logfc: 0.0349940606540153,
      adj_p_val: 0.604655110745068,
      ci_l: -0.0564780903179806,
      ci_r: 0.126466211626011,
    },
    {
      name: 'FP',
      logfc: 0.235992067764791,
      adj_p_val: 0.0293685880983672,
      ci_l: 0.0813918568317721,
      ci_r: 0.390592278697809,
    },
    {
      name: 'IFG',
      logfc: 0.393613393602616,
      adj_p_val: 0.0000468370819411619,
      ci_l: 0.236604395922642,
      ci_r: 0.550622391282589,
    },
    {
      name: 'PCC',
      logfc: 0.0870992749746771,
      adj_p_val: 0.275931011190809,
      ci_l: -0.0292845799777793,
      ci_r: 0.203483129927133,
    },
    {
      name: 'PHG',
      logfc: 0.6626818751507,
      adj_p_val: 9.688467425132421e-14,
      ci_l: 0.506746460875332,
      ci_r: 0.818617289426068,
    },
    {
      name: 'STG',
      logfc: 0.421363564302165,
      adj_p_val: 0.0000165742209158385,
      ci_l: 0.26315969917125,
      ci_r: 0.57956742943308,
    },
    {
      name: 'TCX',
      logfc: 0.450452869099202,
      adj_p_val: 0.00000476005829762979,
      ci_l: 0.275315930793305,
      ci_r: 0.625589807405099,
    },
  ],
  nominations: {
    count: 4,
    year: 2018,
    teams: ['Chang Lab', 'Emory', 'MSSM', 'MSSM'],
    studies: ['ROSMAP', 'Kronos', 'MSBB', 'ACT', 'BLSA', 'Banner'],
    inputs: ['Genetics', 'RNA', 'Protein'],
    programs: ['Community Contributed', 'AMP-AD'],
    validations: ['validation studies ongoing', 'not prioritized for experimental validation'],
  },
  associations: [3, 4],
  target_risk_score: 3.1,
  genetics_score: 2.2,
  multi_omics_score: 3.3,
};

export const comparisonGeneMock2: GCTGene = {
  ensembl_gene_id: 'ENSG00000178209',
  hgnc_symbol: 'PLEC',
  tissues: [
    {
      name: 'ACC',
      logfc: 0.1076969346708,
      adj_p_val: 0.177995313027161,
      ci_l: -0.009454410852896,
      ci_r: 0.224848280194496,
    },
    {
      name: 'CBE',
      logfc: -0.036413787526433,
      adj_p_val: 0.725862284283701,
      ci_l: -0.176640226646977,
      ci_r: 0.103812651594111,
    },
    {
      name: 'DLPFC',
      logfc: 0.152757566641215,
      adj_p_val: 0.004472796167185,
      ci_l: 0.0646999550099166,
      ci_r: 0.240815178272514,
    },
    {
      name: 'FP',
      logfc: 0.189793023997375,
      adj_p_val: 0.0761205673631279,
      ci_l: 0.038934911709087,
      ci_r: 0.340651136285662,
    },
    {
      name: 'IFG',
      logfc: 0.261026160646223,
      adj_p_val: 0.0078140745106338,
      ci_l: 0.102064436841011,
      ci_r: 0.419987884451435,
    },
    {
      name: 'PCC',
      logfc: 0.225173895125716,
      adj_p_val: 0.001395661101634,
      ci_l: 0.110884607461715,
      ci_r: 0.339463182789717,
    },
    {
      name: 'PHG',
      logfc: 0.39579231327275,
      adj_p_val: 0.000003515377487028,
      ci_l: 0.241983198003192,
      ci_r: 0.549601428542308,
    },
    {
      name: 'STG',
      logfc: 0.285960523647612,
      adj_p_val: 0.0038476399722116,
      ci_l: 0.125255893461247,
      ci_r: 0.446665153833976,
    },
    {
      name: 'TCX',
      logfc: 0.59548505468465,
      adj_p_val: 1.22363658615194e-13,
      ci_l: 0.454692784021938,
      ci_r: 0.736277325347362,
    },
  ],
  nominations: {
    count: 4,
    year: 2018,
    teams: ['Chang Lab', 'Columbia-Rush', 'Emory', 'MSSM'],
    studies: ['MSBB', 'ROSMAP', 'ACT', 'BLSA', 'Banner'],
    inputs: ['Genetics', 'Protein', 'Clinical'],
    programs: ['Community Contributed', 'AMP-AD'],
    validations: ['not prioritized for experimental validation', 'validation studies ongoing'],
  },
  associations: [2, 3, 4],
  target_risk_score: 2.1,
  genetics_score: 4.2,
  multi_omics_score: 1.3,
};

export const comparisonGeneEmptyHGNCMock: GCTGene = {
  ensembl_gene_id: 'ENSG00000147065',
  hgnc_symbol: '',
  tissues: [
    {
      name: 'ACC',
      logfc: -0.0144678061734664,
      adj_p_val: 0.893263674388766,
      ci_l: -0.133332670728704,
      ci_r: 0.104397058381771,
    },
    {
      name: 'CBE',
      logfc: -0.0751554371582435,
      adj_p_val: 0.530889774959758,
      ci_l: -0.247449816301241,
      ci_r: 0.0971389419847545,
    },
    {
      name: 'DLPFC',
      logfc: 0.0349940606540153,
      adj_p_val: 0.604655110745068,
      ci_l: -0.0564780903179806,
      ci_r: 0.126466211626011,
    },
    {
      name: 'FP',
      logfc: 0.235992067764791,
      adj_p_val: 0.0293685880983672,
      ci_l: 0.0813918568317721,
      ci_r: 0.390592278697809,
    },
    {
      name: 'IFG',
      logfc: 0.393613393602616,
      adj_p_val: 0.0000468370819411619,
      ci_l: 0.236604395922642,
      ci_r: 0.550622391282589,
    },
    {
      name: 'PCC',
      logfc: 0.0870992749746771,
      adj_p_val: 0.275931011190809,
      ci_l: -0.0292845799777793,
      ci_r: 0.203483129927133,
    },
    {
      name: 'PHG',
      logfc: 0.6626818751507,
      adj_p_val: 9.688467425132421e-14,
      ci_l: 0.506746460875332,
      ci_r: 0.818617289426068,
    },
    {
      name: 'STG',
      logfc: 0.421363564302165,
      adj_p_val: 0.0000165742209158385,
      ci_l: 0.26315969917125,
      ci_r: 0.57956742943308,
    },
    {
      name: 'TCX',
      logfc: 0.450452869099202,
      adj_p_val: 0.00000476005829762979,
      ci_l: 0.275315930793305,
      ci_r: 0.625589807405099,
    },
  ],
  nominations: {
    count: 4,
    year: 2018,
    teams: ['Chang Lab', 'Emory', 'MSSM', 'MSSM'],
    studies: ['ROSMAP', 'Kronos', 'MSBB', 'ACT', 'BLSA', 'Banner'],
    inputs: ['Genetics', 'RNA', 'Protein'],
    programs: ['Community Contributed', 'AMP-AD'],
    validations: ['validation studies ongoing', 'not prioritized for experimental validation'],
  },
  associations: [3, 4],
  target_risk_score: 3.1,
  genetics_score: 2.2,
  multi_omics_score: 3.3,
};

export const gctDetailsPanelDataMock: GCTDetailsPanelData = {
  label: 'label',
  heading: 'heading',
  subHeading: 'subHeading',
  value: 5,
  valueLabel: 'valueLabel',
  pValue: 5,
  min: 0,
  max: 10,
  footer: 'footer',
};

export const gctScorePanelDataMock: GCTScorePanelData = {
  geneLabel: 'label',
  scoreName: 'subHeading',
  columnName: 'valueLabel',
  score: 5,
  distributions: distributionMock.overall_scores,
};

export const gctFiltersMocks: GCTFilter[] = [
  {
    name: 'test',
    label: 'Test',
    options: [
      { label: '1', value: 1, selected: true },
      { label: '2', value: 2, selected: true },
      { label: '3', value: 3, selected: true },
    ],
  },
];
