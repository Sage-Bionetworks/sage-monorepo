import {
  APP_FONT_FAMILY_PROPERTY,
  FALLBACK_CHART_FONT_FAMILY,
  getChartFontFamily,
  whenChartFontReady,
} from './chart-font';

const appFontFamily = "'DM Sans Variable', sans-serif";

function stubFonts(fonts: Partial<FontFaceSet> | undefined) {
  Object.defineProperty(document, 'fonts', { value: fonts, configurable: true });
}

describe('getChartFontFamily', () => {
  afterEach(() => document.documentElement.style.removeProperty(APP_FONT_FAMILY_PROPERTY));

  it("uses the app's font family", () => {
    document.documentElement.style.setProperty(APP_FONT_FAMILY_PROPERTY, appFontFamily);
    expect(getChartFontFamily()).toBe(appFontFamily);
  });

  it('falls back to a generic family when the app sets none', () => {
    expect(getChartFontFamily()).toBe(FALLBACK_CHART_FONT_FAMILY);
  });
});

describe('whenChartFontReady', () => {
  afterEach(() => stubFonts(undefined));

  it('runs right away when the browser has no Font Loading API', () => {
    stubFonts(undefined);
    const onReady = jest.fn();
    whenChartFontReady(onReady);
    expect(onReady).toHaveBeenCalledTimes(1);
  });

  it('runs right away when the font is already loaded', () => {
    stubFonts({ check: () => true, load: jest.fn() });
    const onReady = jest.fn();
    whenChartFontReady(onReady);
    expect(onReady).toHaveBeenCalledTimes(1);
  });

  it('waits for the font to load before running', async () => {
    let finishLoading: () => void = () => undefined;
    const load = jest.fn(
      () => new Promise<FontFace[]>((resolve) => (finishLoading = () => resolve([]))),
    );
    stubFonts({ check: () => false, load });
    const onReady = jest.fn();

    whenChartFontReady(onReady);
    expect(onReady).not.toHaveBeenCalled();
    expect(load).toHaveBeenCalledWith(expect.stringContaining(getChartFontFamily()));

    finishLoading();
    await Promise.resolve();
    expect(onReady).toHaveBeenCalledTimes(1);
  });

  it('still runs, in the fallback font, when the font fails to load', async () => {
    stubFonts({ check: () => false, load: () => Promise.reject(new Error('network')) });
    const onReady = jest.fn();
    whenChartFontReady(onReady);
    await Promise.resolve();
    await Promise.resolve();
    expect(onReady).toHaveBeenCalledTimes(1);
  });
});
