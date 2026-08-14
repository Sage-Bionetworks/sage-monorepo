import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { getPanels, getPanelsWithDisabledState } from './mouse-model-details-panels';

function disabledByName(model: MouseModel): Record<string, boolean> {
  const result = getPanelsWithDisabledState(model, getPanels());
  return Object.fromEntries(result.map((panel) => [panel.name, panel.disabled]));
}

describe('mouse-model-details-panels', () => {
  it('returns a fresh array of fresh panel objects each call', () => {
    const first = getPanels();
    const second = getPanels();
    expect(first).not.toBe(second);
    expect(first[0]).not.toBe(second[0]);
    expect(first).toEqual(second);
  });

  it('does not mutate the input panels', () => {
    const panels = getPanels();
    const modelWithoutData: MouseModel = {
      ...mouseModelMock,
      biomarkers: [],
      pathology: [],
      transcriptomics: null,
      disease_correlation: null,
    };
    getPanelsWithDisabledState(modelWithoutData, panels);
    expect(panels.every((panel) => panel.disabled === false)).toBe(true);
  });

  it('enables all panels when the model has data for each', () => {
    const disabled = disabledByName(mouseModelMock);
    expect(disabled).toEqual({
      omics: false,
      biomarkers: false,
      pathology: false,
      resources: false,
    });
  });

  it('disables biomarkers when there is no biomarker data', () => {
    const disabled = disabledByName({ ...mouseModelMock, biomarkers: [] });
    expect(disabled['biomarkers']).toBe(true);
  });

  it('disables pathology when there is no pathology data', () => {
    const disabled = disabledByName({ ...mouseModelMock, pathology: [] });
    expect(disabled['pathology']).toBe(true);
  });

  it('disables omics only when both transcriptomics and disease correlation are null', () => {
    expect(
      disabledByName({ ...mouseModelMock, transcriptomics: null, disease_correlation: null })[
        'omics'
      ],
    ).toBe(true);
    expect(
      disabledByName({ ...mouseModelMock, transcriptomics: null, disease_correlation: 'x' })[
        'omics'
      ],
    ).toBe(false);
  });
});
