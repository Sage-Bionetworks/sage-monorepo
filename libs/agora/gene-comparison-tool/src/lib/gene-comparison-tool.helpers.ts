import {
  GCTGene,
  GCTGeneTissue,
  OverallScoresDistribution,
} from '@sagebionetworks/agora/api-client';
import { GCTScorePanelData } from '@sagebionetworks/agora/models';

export const filterOptionLabel = function (value: string | number | string[] | number[]) {
  let label = typeof value === 'string' ? value : value.toString(10);

  switch (label) {
    case 'and Late Onset Alzheimer&quot;s Disease Family Study':
      label = 'Late Onset Alzheimer&quot;s Disease Family Study';
  }

  return label.charAt(0).toUpperCase() + label.slice(1);
};

export const intersectFilterCallback = function (value: any, filters: any): boolean {
  if (filters === undefined || filters === null || filters.length < 1) {
    return true;
  } else if (value === undefined || value === null || filters.length < 1) {
    return false;
  }

  for (const filter of filters) {
    if (value.indexOf(filter) !== -1) {
      return true;
    }
  }

  return false;
};

export const excludeEnsemblGeneIdFilterCallback = function (
  value: string,
  ensemblGeneIds: string[],
): boolean {
  return !ensemblGeneIds.includes(value);
};

export const excludeUniprotIdCallback = function (value: string, uniprotIds: string[]): boolean {
  return !uniprotIds.includes(value);
};

export function getScoreName(columnName: string) {
  columnName = columnName.toUpperCase();
  if (columnName === 'RISK SCORE') return 'Target Risk Score';
  if (columnName === 'GENETIC') return 'Genetic Risk Score';
  if (columnName === 'MULTI-OMIC') return 'Multi-omic Risk Score';
  return '';
}

export function getGeneLabel(gene: GCTGene) {
  return (gene.hgnc_symbol ? gene.hgnc_symbol + ' - ' : '') + gene.ensembl_gene_id;
}

export function getGeneLabelForProteinDifferentialExpression(gene: GCTGene) {
  return (
    (gene.hgnc_symbol ? gene.hgnc_symbol + ' ' : '') +
    (gene.uniprotid ? '(' + gene.uniprotid + ')' : '') +
    ' - ' +
    gene.ensembl_gene_id
  );
}

export function getGeneLabelForSRM(gene: GCTGene) {
  let label = gene.hgnc_symbol ? `${gene.hgnc_symbol} - ` : '';
  label += gene.ensembl_gene_id;
  return label;
}

export function getScore(columnName: string, gene: GCTGene) {
  columnName = columnName.toUpperCase();
  if (columnName === 'RISK SCORE') return gene.target_risk_score;
  if (columnName === 'MULTI-OMIC') return gene.multi_omics_score;
  if (columnName === 'GENETIC') return gene.genetics_score;
  return null;
}

export function lookupScoreDataKey(columnName: string | undefined) {
  if (!columnName) return;

  columnName = columnName.toUpperCase();
  if (columnName === 'RISK SCORE') return 'TARGET RISK SCORE';
  if (columnName === 'MULTI-OMIC') return 'MULTI-OMIC RISK SCORE';
  if (columnName === 'GENETIC') return 'GENETIC RISK SCORE';
  return '';
}

export const getScorePanelData = function (
  columnName: string,
  gene: GCTGene,
  scoresDistributions: OverallScoresDistribution[] | undefined,
) {
  const data: GCTScorePanelData = {
    geneLabel: getGeneLabel(gene),
    scoreName: getScoreName(columnName),
    columnName: columnName,
    score: getScore(columnName, gene),
    distributions: scoresDistributions,
  };
  return data;
};

export const getDetailsPanelData = function (
  category: string,
  subCategory: string,
  gene: GCTGene,
  tissue: GCTGeneTissue,
) {
  let max = 0;

  gene.tissues.forEach((t: GCTGeneTissue) => {
    if (max === undefined || Math.abs(t.ci_l) > max) {
      max = Math.abs(t.ci_l);
    }
    if (max === undefined || t.ci_r > max) {
      max = t.ci_r;
    }
  });

  max = Math.ceil(max);

  const data = {
    gene: gene,
    label: '',
    heading: '',
    subHeading: subCategory,
    valueLabel: 'Log 2 Fold Change',
    value: tissue?.logfc,
    pValue: tissue?.adj_p_val,
    min: max * -1,
    max: max,
    intervalMin: tissue?.ci_l,
    intervalMax: tissue?.ci_r,
    footer: 'Significance is considered to be an adjusted p-value < 0.05',
    allTissueLink: true,
  };

  if (category === 'Protein - Differential Expression') {
    if (subCategory === 'SRM') {
      data.label = getGeneLabelForSRM(gene);
    } else {
      data.label = getGeneLabelForProteinDifferentialExpression(gene);
    }
    data.heading = 'Differential Protein Expression (' + tissue.name + ')';
    data.allTissueLink = false;
  } else {
    data.label = getGeneLabel(gene);
    data.heading = 'Differential RNA Expression (' + tissue.name + ')';
  }

  return data;
};
