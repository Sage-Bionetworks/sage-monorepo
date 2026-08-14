import { MarmosetModel } from '@sagebionetworks/model-ad/api-client';
import { marmosetModelMock } from '@sagebionetworks/model-ad/testing';
import { getPanels, getPanelsWithDisabledState } from './marmoset-model-details-panels';

function disabledByName(model: MarmosetModel): Record<string, boolean> {
  const result = getPanelsWithDisabledState(model, getPanels());
  return Object.fromEntries(result.map((panel) => [panel.name, panel.disabled]));
}

describe('marmoset-model-details-panels', () => {
  it('returns the Plasma Biomarkers and Resources panels', () => {
    const panels = getPanels();
    expect(panels).toEqual([
      { name: 'biomarkers', label: 'Plasma Biomarkers', disabled: false },
      { name: 'resources', label: 'Resources', disabled: false },
    ]);
  });

  it('returns a fresh array of fresh panel objects each call', () => {
    const first = getPanels();
    const second = getPanels();
    expect(first).not.toBe(second);
    expect(first[0]).not.toBe(second[0]);
    expect(first).toEqual(second);
  });

  it('does not mutate the input panels', () => {
    const panels = getPanels();
    getPanelsWithDisabledState({ ...marmosetModelMock, biomarkers: [] }, panels);
    expect(panels[0].disabled).toBe(false);
  });

  it('enables the biomarkers panel when there is biomarker data', () => {
    expect(disabledByName(marmosetModelMock)['biomarkers']).toBe(false);
  });

  it('disables the biomarkers panel when there is no biomarker data', () => {
    expect(disabledByName({ ...marmosetModelMock, biomarkers: [] })['biomarkers']).toBe(true);
  });

  it('keeps the resources panel enabled regardless of model data', () => {
    expect(disabledByName(marmosetModelMock)['resources']).toBe(false);
    expect(disabledByName({ ...marmosetModelMock, biomarkers: [] })['resources']).toBe(false);
  });
});
