import { DARK_TOOLTIP } from '../constants';
import {
  buildTooltip,
  PIN_ABOVE_GAP_PX,
  pinAbovePosition,
  TOOLTIP_NOTCH_CLASS,
  TOOLTIP_NOTCH_COLOR_PROPERTY,
} from './tooltip-utils';

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

  describe('pinAbove', () => {
    const notchColor = '#22252A';
    const tooltip = buildTooltip(DARK_TOOLTIP, { pinAbove: true, backgroundColor: notchColor });

    it('lets the tooltip extend past the chart and marks it for the notch styles', () => {
      expect(tooltip).toMatchObject({
        position: pinAbovePosition,
        confine: false,
        appendTo: 'body',
        className: TOOLTIP_NOTCH_CLASS,
      });
    });

    it('colors the notch like the tooltip background', () => {
      expect(tooltip.extraCssText).toContain(`${TOOLTIP_NOTCH_COLOR_PROPERTY}: ${notchColor};`);
    });

    it('falls back to the base background color for the notch', () => {
      const { extraCssText } = buildTooltip(DARK_TOOLTIP, { pinAbove: true });
      expect(extraCssText).toContain(
        `${TOOLTIP_NOTCH_COLOR_PROPERTY}: ${DARK_TOOLTIP.backgroundColor};`,
      );
    });
  });

  describe('pinAbovePosition', () => {
    const contentSize: [number, number] = [100, 50];
    const size = { contentSize, viewSize: [260, 228] as [number, number] };
    const position = (rect: { x: number; y: number; width: number; height: number } | null) =>
      (pinAbovePosition as (...args: unknown[]) => [number, number])(
        [30, 80],
        [],
        null,
        rect,
        size,
      );

    it('centers the tooltip above the hovered element', () => {
      const elementRect = { x: 160, y: 80, width: 10, height: 10 };
      expect(position(elementRect)).toEqual([165 - 50, 80 - 50 - PIN_ABOVE_GAP_PX]);
    });

    it('centers the tooltip above the pointer when there is no element', () => {
      expect(position(null)).toEqual([30 - 50, 80 - 50 - PIN_ABOVE_GAP_PX]);
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
