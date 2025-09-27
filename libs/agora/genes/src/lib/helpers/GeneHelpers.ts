import { Gene } from '@sagebionetworks/agora/api-client';

export function getStatisticalModels(gene: Gene) {
  const models: string[] = [];

  gene.rna_differential_expression?.forEach((item: any) => {
    if (!models.includes(item.model)) {
      models.push(item.model);
    }
  });

  return models;
}
