import { Panel } from '@sagebionetworks/explorers/models';
import { MarmosetModel } from '@sagebionetworks/model-ad/api-client';

export function getPanels(): Panel[] {
  return [
    { name: 'biomarkers', label: 'Plasma Biomarkers', disabled: false },
    { name: 'resources', label: 'Resources', disabled: false },
  ];
}

export function getPanelsWithDisabledState(model: MarmosetModel, panels: Panel[]): Panel[] {
  return panels.map((panel) => ({
    ...panel,
    disabled: isPanelDisabled(model, panel.name),
  }));
}

function isPanelDisabled(model: MarmosetModel, panelName: string): boolean {
  switch (panelName) {
    case 'biomarkers':
      return model.biomarkers.length === 0;
    default:
      return false;
  }
}
