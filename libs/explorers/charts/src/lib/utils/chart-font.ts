/**
 * CSS custom property holding the app's font family. The explorers styles define it, so charts
 * use the same font as the rest of the app.
 */
export const APP_FONT_FAMILY_PROPERTY = '--font-family-primary';
export const FALLBACK_CHART_FONT_FAMILY = 'sans-serif';

// Size used only to name a font for the Font Loading API; one load covers every size.
const FONT_LOAD_PROBE_SIZE = '12px';

// Charts draw text on a canvas, which can't resolve CSS custom properties, so the app's font
// family is read from the document here.
export function getChartFontFamily(): string {
  if (typeof document === 'undefined') return FALLBACK_CHART_FONT_FAMILY;
  const appFontFamily = getComputedStyle(document.documentElement)
    .getPropertyValue(APP_FONT_FAMILY_PROPERTY)
    .trim();
  return appFontFamily || FALLBACK_CHART_FONT_FAMILY;
}

/**
 * Calls `onReady` once the chart font can be drawn: right away if it's already loaded or the
 * browser has no Font Loading API, otherwise after it loads (or fails to load). Create charts
 * here, because ECharts caches text measurements by font name and would keep the fallback font's
 * sizes if it drew first.
 */
export function whenChartFontReady(onReady: () => void): void {
  const fonts = typeof document === 'undefined' ? undefined : document.fonts;
  const font = `${FONT_LOAD_PROBE_SIZE} ${getChartFontFamily()}`;
  if (!fonts || fonts.check(font)) {
    onReady();
    return;
  }
  fonts.load(font).then(onReady, onReady);
}
