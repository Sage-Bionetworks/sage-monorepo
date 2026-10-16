import { Gene } from '@sagebionetworks/agora/api-client';
import { Panel } from '@sagebionetworks/explorers/models';

export function getPanels(): Panel[] {
  return [
    { name: 'summary', label: 'Summary', disabled: false },
    {
      name: 'evidence',
      label: 'Evidence',
      disabled: false,
      children: [
        { name: 'rna', label: 'RNA', disabled: false },
        { name: 'protein', label: 'Protein', disabled: false },
        { name: 'metabolomics', label: 'Metabolomics', disabled: false },
      ],
    },
    { name: 'resources', label: 'Resources', disabled: false },
    { name: 'nominations', label: 'Nomination Details', disabled: false },
    { name: 'experimental-validation', label: 'Experimental Validation', disabled: false },
  ];
}

export function getPanelsWithDisabledState(gene: Gene, panels: Panel[]): Panel[] {
  return panels.map((panel) => ({
    ...panel,
    disabled: isPanelDisabled(gene, panel.name),
  }));
}

function isPanelDisabled(gene: Gene, panelName: string): boolean {
  switch (panelName) {
    case 'nominations':
      return !gene.total_nominations;
    case 'experimental-validation':
      return !gene.experimental_validation?.length;
    default:
      return false;
  }
}
