import { Panel } from '@sagebionetworks/explorers/models';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';

export function getPanels(): Panel[] {
  return [
    { name: 'omics', label: 'Omics', disabled: false },
    { name: 'biomarkers', label: 'Biomarkers', disabled: false },
    { name: 'pathology', label: 'Pathology', disabled: false },
    { name: 'resources', label: 'Resources', disabled: false },
  ];
}

export function getPanelsWithDisabledState(model: MouseModel, panels: Panel[]): Panel[] {
  return panels.map((panel) => ({
    ...panel,
    disabled: isPanelDisabled(model, panel.name),
  }));
}

function isPanelDisabled(model: MouseModel, panelName: string): boolean {
  switch (panelName) {
    case 'biomarkers':
      return model.biomarkers.length === 0;
    case 'pathology':
      return model.pathology.length === 0;
    case 'omics':
      return model.transcriptomics === null && model.disease_correlation === null;
    default:
      return false;
  }
}
