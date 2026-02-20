import { GCTFilter, GCTSelectOption } from '@sagebionetworks/agora/models';

export const categories: GCTSelectOption[] = [
  {
    label: 'RNA - Differential Expression',
    value: 'RNA - Differential Expression',
  },
  {
    label: 'Protein - Differential Expression',
    value: 'Protein - Differential Expression',
  },
];

export const subCategories: { [key: string]: GCTSelectOption[] } = {
  'RNA - Differential Expression': [
    {
      label: 'AD Diagnosis (males and females)',
      value: 'AD Diagnosis (males and females)',
    },
    {
      label: 'AD Diagnosis x AOD (males and females)',
      value: 'AD Diagnosis x AOD (males and females)',
    },
    {
      label: 'AD Diagnosis x Sex (females only)',
      value: 'AD Diagnosis x Sex (females only)',
    },
    {
      label: 'AD Diagnosis x Sex (males only)',
      value: 'AD Diagnosis x Sex (males only)',
    },
  ],
  'Protein - Differential Expression': [
    {
      label: 'Targeted Selected Reaction Monitoring (SRM)',
      value: 'SRM',
    },
    {
      label: 'Genome-wide Tandem Mass Tag (TMT)',
      value: 'TMT',
    },
    {
      label: 'Genome-wide Label-free Quantification (LFQ)',
      value: 'LFQ',
    },
  ],
};

export const filters: GCTFilter[] = [
  {
    name: 'presets',
    field: 'none',
    label: 'Quick Filters',
    description: 'Applying a quick filter will reset your current filter state.',
    options: [
      {
        label: 'All Nominated targets',
        preset: {
          nominations: [1, 2, 3, 4, 5],
        },
      },
      {
        label: 'Genetically Associated with LOAD',
        preset: {
          associations: [1],
        },
      },
      {
        label: 'eQTL in Brain',
        preset: {
          associations: [2],
        },
      },
    ],
  },
  {
    name: 'associations',
    field: 'associations',
    label: 'Association with AD',
    short: 'Association with AD',
    description:
      'Filter for genes that are associated with AD based on GWAS, eQTL, and differential expression results.',
    matchMode: 'intersect',
    options: [
      {
        label: 'Genetically Associated with LOAD',
        value: 1,
      },
      {
        label: 'eQTL in Brain',
        value: 2,
      },
      {
        label: 'RNA Expression Changed in AD Brain',
        value: 3,
      },
      {
        label: 'Protein Expression Changed in AD Brain',
        value: 4,
      },
    ],
  },
  {
    name: 'biodomain',
    field: 'biodomains',
    label: 'Biological Domain',
    short: 'Biodomain',
    description: 'Filter for genes based on the biological domains that they are linked to.',
    matchMode: 'intersect',
    options: [
      {
        label: 'Apoptosis',
        value: 'Apoptosis',
      },
      {
        label: 'APP Metabolism',
        value: 'APP Metabolism',
      },
      {
        label: 'Autophagy',
        value: 'Autophagy',
      },
      {
        label: 'Cell Cycle',
        value: 'Cell Cycle',
      },
      {
        label: 'DNA Repair',
        value: 'DNA Repair',
      },
      {
        label: 'Endolysosome',
        value: 'Endolysosome',
      },
      {
        label: 'Epigenetic',
        value: 'Epigenetic',
      },
      {
        label: 'Immune Response',
        value: 'Immune Response',
      },
      {
        label: 'Lipid Metabolism',
        value: 'Lipid Metabolism',
      },
      {
        label: 'Metal Binding and Homeostasis',
        value: 'Metal Binding and Homeostasis',
      },
      {
        label: 'Mitochondrial Metabolism',
        value: 'Mitochondrial Metabolism',
      },
      {
        label: 'Myelination',
        value: 'Myelination',
      },
      {
        label: 'Oxidative Stress',
        value: 'Oxidative Stress',
      },
      {
        label: 'Proteostasis',
        value: 'Proteostasis',
      },
      {
        label: 'RNA Spliceosome',
        value: 'RNA Spliceosome',
      },
      {
        label: 'Structural Stabilization',
        value: 'Structural Stabilization',
      },
      {
        label: 'Synapse',
        value: 'Synapse',
      },
      {
        label: 'Tau Homeostasis',
        value: 'Tau Homeostasis',
      },
      {
        label: 'Vasculature',
        value: 'Vasculature',
      },
    ],
  },
  {
    name: 'studies',
    field: 'nominations.studies',
    label: 'Cohort Study',
    short: 'Study',
    description:
      'Filter for genes based on which study or cohort the nominating research team analyzed to identify the gene as a potential target for AD.',
    matchMode: 'intersect',
    options: [],
  },
  {
    name: 'validations',
    field: 'nominations.validations',
    label: 'Experimental Validation',
    short: 'Experimental Validation',
    description:
      'Filter for genes based on the experimental validation status indicated by the nominating team(s).',
    order: 'DESC',
    matchMode: 'intersect',
    options: [],
  },
  {
    name: 'inputs',
    field: 'nominations.inputs',
    label: 'Input Data',
    short: 'Data',
    description:
      'Filter for genes based on the type of data that the nominating research team analyzed to identify the gene as a potential target for AD.',
    matchMode: 'intersect',
    options: [],
  },
  {
    name: 'programs',
    field: 'nominations.programs',
    label: 'Nominating Program',
    short: 'Nominating Program',
    description: 'Filter for genes based on the nominating program.',
    matchMode: 'intersect',
    options: [],
  },
  {
    name: 'teams',
    field: 'nominations.teams',
    label: 'Nominating Teams',
    short: 'Team',
    description: 'Filter for genes based on the nominating research team.',
    matchMode: 'intersect',
    options: [],
  },
  {
    name: 'nominations',
    field: 'nominations.count',
    label: 'Number of Nominations',
    short: 'Nominations',
    description:
      'Filter for genes based on how many times they have been nominated as a potential target for AD.',
    matchMode: 'in',
    order: 'DESC',
    options: [],
  },
  {
    name: 'target_enabling_resources',
    field: 'target_enabling_resources',
    label: 'Target Enabling Resources',
    short: 'Resources',
    description:
      'Filter for genes that have Target Enabling Resources to support experimental validation efforts.',
    matchMode: 'intersect',
    options: [
      {
        label: 'AD Informer Set',
        value: 'AD Informer Set',
      },
      {
        label: 'Target Enabling Package',
        value: 'Target Enabling Package',
      },
    ],
  },
  {
    name: 'year',
    field: 'nominations.year',
    label: 'First Nominated',
    short: 'Year',
    description:
      'Filter for genes based on the year that they were first nominated as a potential target for AD.',
    matchMode: 'in',
    order: 'DESC',
    options: [],
  },
];
