import { Gene } from '@sagebionetworks/agora/api-client';
import { geneMock1 } from '@sagebionetworks/agora/testing';
import { getPanels, getPanelsWithDisabledState } from './gene-details-panels';

function disabledByName(gene: Gene): Record<string, boolean> {
  const result = getPanelsWithDisabledState(gene, getPanels());
  return Object.fromEntries(result.map((panel) => [panel.name, panel.disabled]));
}

describe('gene-details-panels', () => {
  it('returns a fresh array of fresh panel objects each call', () => {
    const first = getPanels();
    const second = getPanels();
    expect(first).not.toBe(second);
    expect(first[0]).not.toBe(second[0]);
    expect(first).toEqual(second);
  });

  it('does not mutate the input panels', () => {
    const panels = getPanels();
    getPanelsWithDisabledState(
      { ...geneMock1, total_nominations: 0, experimental_validation: [] },
      panels,
    );
    expect(panels.every((panel) => panel.disabled === false)).toBe(true);
  });

  it('enables all panels when the gene has nominations and experimental validation', () => {
    expect(disabledByName(geneMock1)).toEqual({
      summary: false,
      evidence: false,
      resources: false,
      nominations: false,
      'experimental-validation': false,
    });
  });

  it('disables nominations when the gene has no nominations', () => {
    expect(disabledByName({ ...geneMock1, total_nominations: 0 })['nominations']).toBe(true);
    expect(disabledByName({ ...geneMock1, total_nominations: null })['nominations']).toBe(true);
  });

  it('disables experimental validation when the gene has none', () => {
    expect(
      disabledByName({ ...geneMock1, experimental_validation: [] })['experimental-validation'],
    ).toBe(true);
    expect(
      disabledByName({ ...geneMock1, experimental_validation: null })['experimental-validation'],
    ).toBe(true);
  });
});
