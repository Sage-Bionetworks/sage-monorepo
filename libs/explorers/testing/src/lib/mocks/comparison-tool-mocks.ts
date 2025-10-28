import { ComparisonToolConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { mockComparisonToolConfigFilters } from './comparison-tool-config-mocks';

export const mockComparisonToolSelectorsWikiParams: { [key: string]: SynapseWikiParams } = {
  Red: {
    ownerId: 'syn66271427',
    wikiId: '632874',
  },
  Blue: {
    ownerId: 'syn66271427',
    wikiId: '632873',
  },
};

export const mockComparisonToolDataConfig: ComparisonToolConfig[] = [
  {
    page: 'Model Overview',
    dropdowns: [],
    row_count: 'over 200000',
    columns: [
      {
        type: 'primary',
        data_key: 'name',
      },
      {
        name: 'Model Type',
        type: 'text',
        data_key: 'model_type',
        tooltip: '',
        sort_tooltip: 'Sort by Model Type value',
      },
      {
        name: 'Matched Control',
        type: 'text',
        data_key: 'matched_controls',
        tooltip: '',
        sort_tooltip: 'Sort by Matched Control value',
      },
      {
        name: 'Disease Correlation',
        type: 'link_internal',
        data_key: 'disease_correlation',
        tooltip: '',
        sort_tooltip: 'Sort by Disease Correlation value',
        link_text: 'Results',
      },
      {
        name: 'Center',
        type: 'link_external',
        data_key: 'center',
        tooltip: '',
        sort_tooltip: 'Sort by Center value',
        link_url: 'https://www.model-ad.org/',
      },
      {
        name: 'Age',
        type: 'text',
        data_key: 'age',
        tooltip: '',
        sort_tooltip: 'Sort by Age value',
      },
      {
        name: 'IFG',
        type: 'heat_map',
        data_key: 'IFG',
        tooltip: 'Inferior Frontal Gyrus',
        sort_tooltip: 'Sort by correlation value',
      },
      {
        name: 'PHG',
        type: 'heat_map',
        data_key: 'PHG',
        tooltip: 'Parahippocampal Gyrus',
        sort_tooltip: 'Sort by correlation value',
      },
    ],
    filters: mockComparisonToolConfigFilters,
  },
];

export const mockComparisonToolData: Record<string, any>[] = [
  {
    _id: '68fff1aaeb12b9674515fd58',
    name: '3xTg-AD',
    model_type: 'Familial AD',
    matched_controls: ['B6129'],
    disease_correlation: null,
    center: {
      link_text: 'UCI',
      link_url: 'https://www.sagebionetworks.org/',
    },
    age: '12 months',
    IFG: {
      correlation: 0.0970533422952035,
      adj_p_val: 0.388719974861466,
    },
    PHG: {
      correlation: 0.084295638183579,
      adj_p_val: 0.409231075552908,
    },
  },
  {
    _id: '68fff1aaeb12b9674515fd59',
    name: '5xFAD (UCI)',
    model_type: 'Familial AD',
    matched_controls: ['C57BL/6J'],
    disease_correlation: null,
    center: {
      link_text: 'UCI',
      link_url: 'http://model-ad.org/uci-disease-model-development-and-phenotyping-dmp/',
    },
    age: '12 months',
    IFG: {
      correlation: 0.180202547119964,
      adj_p_val: 0.107436343673528,
    },
    PHG: {
      correlation: 0.120000548192961,
      adj_p_val: 0.239210931286464,
    },
  },
  {
    _id: '68fff1aaeb12b9674515fd5a',
    name: '5xFAD (IU/Jax/Pitt)',
    model_type: 'Familial AD',
    matched_controls: ['C57BL/6J'],
    disease_correlation: {
      link_url: 'comparison/correlation?model=5xFAD (IU/Jax/Pitt)',
    },
    center: {
      link_text: 'IU/Jax/Pitt',
      link_url: 'https://www.model-ad.org/iu-jax-pitt-disease-modeling-project/',
    },
    age: '4 months',
    IFG: {
      correlation: 0.0829228557717284,
      adj_p_val: 0.46174597641461,
    },
    PHG: {
      correlation: 0.0555691060937462,
      adj_p_val: 0.586807542376288,
    },
  },
  {
    _id: '68fff1aaeb12b9674515fd5b',
    name: 'Abca7*V1599M',
    model_type: 'Familial AD',
    matched_controls: ['C57BL/6J', '5xFAD'],
    disease_correlation: null,
    center: {
      link_text: 'UCI',
      link_url: 'http://model-ad.org/uci-disease-model-development-and-phenotyping-dmp/',
    },
    age: '4 months',
    IFG: {
      correlation: 0.0341639955699389,
      adj_p_val: 0.762053474034212,
    },
    PHG: {
      correlation: 0.0748918760876008,
      adj_p_val: 0.463613681499704,
    },
  },
  {
    _id: '68fff1aaeb12b9674515fd5c',
    name: 'APOE4',
    model_type: 'Late Onset AD',
    matched_controls: ['C57BL/6J'],
    disease_correlation: {
      link_url: 'comparison/correlation?model=APOE4',
    },
    age: '12 months',
    center: {
      link_text: 'IU/Jax/Pitt',
      link_url: 'https://www.model-ad.org/iu-jax-pitt-disease-modeling-project/',
    },
    IFG: {
      correlation: 0.437347817670545,
      adj_p_val: 3.39986129951837e-7,
    },
    PHG: {
      correlation: 0.0472918655548024,
      adj_p_val: 0.638623415843113,
    },
  },
];
