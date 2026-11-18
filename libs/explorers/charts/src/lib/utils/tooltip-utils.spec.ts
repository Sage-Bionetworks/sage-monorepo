import { DARK_TOOLTIP } from '../constants';
import { buildTooltip } from './tooltip-utils';

describe('buildTooltip', () => {
  it('returns the base tooltip when no style is given', () => {
    expect(buildTooltip(DARK_TOOLTIP, undefined)).toBe(DARK_TOOLTIP);
  });

  it('overrides only the given colors', () => {
    const tooltip = buildTooltip(DARK_TOOLTIP, { backgroundColor: '#22252A' });
    expect(tooltip).toEqual({ ...DARK_TOOLTIP, backgroundColor: '#22252A' });
  });

  it('keeps the base tooltip confined to the chart unless pinned above', () => {
    expect(buildTooltip(DARK_TOOLTIP, { backgroundColor: '#22252A' }).confine).toBe(true);
  });

  it('pins the tooltip above the hovered element, free to extend past the chart', () => {
    expect(buildTooltip(DARK_TOOLTIP, { pinAbove: true })).toMatchObject({
      position: 'top',
      confine: false,
      appendTo: 'body',
    });
  });

  it('overrides the text color, font family and line height while keeping the base text style', () => {
    const tooltip = buildTooltip(
      { ...DARK_TOOLTIP, textStyle: { color: 'white', fontSize: 14 } },
      { color: '#FFFFFF', fontFamily: 'var(--font-family-primary)', lineHeight: 16 },
    );
    expect(tooltip.textStyle).toEqual({
      color: '#FFFFFF',
      fontSize: 14,
      fontFamily: 'var(--font-family-primary)',
      lineHeight: 16,
    });
  });

  it('overrides the padding and corner radius', () => {
    expect(buildTooltip(DARK_TOOLTIP, { padding: 8, borderRadius: 2 })).toMatchObject({
      padding: 8,
      borderRadius: 2,
    });
  });

  it('overrides the base text alignment while keeping the rest of its CSS', () => {
    const { extraCssText } = buildTooltip(DARK_TOOLTIP, { textAlign: 'left' });
    expect(extraCssText).toContain(DARK_TOOLTIP.extraCssText);
    expect(extraCssText?.endsWith('text-align: left;')).toBe(true);
  });
});
