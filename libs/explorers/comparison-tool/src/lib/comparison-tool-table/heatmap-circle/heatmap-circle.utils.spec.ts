import { ADJUSTED_P_VALUE_KEY } from '../../comparison-tool.variables';
import {
  canDrawHeatmapCircle,
  isUsableMetricValue,
  resolveHeatmapCircleMetrics,
} from './heatmap-circle.utils';

describe('resolveHeatmapCircleMetrics', () => {
  it('resolves the color key and both values for a well-formed cell', () => {
    expect(resolveHeatmapCircleMetrics({ log2_fc: 1.5, [ADJUSTED_P_VALUE_KEY]: 0.03 })).toEqual({
      colorKey: 'log2_fc',
      colorValue: 1.5,
      adjustedPValue: 0.03,
      isDrawable: true,
    });
  });

  it.each([null, undefined, {}])('is undrawable when nothing is resolvable (%p)', (data) => {
    expect(resolveHeatmapCircleMetrics(data)).toEqual({ isDrawable: false });
  });

  it.each([
    { correlation: null, sample_count: 12, [ADJUSTED_P_VALUE_KEY]: 0.01 },
    { sample_count: 12, correlation: null, [ADJUSTED_P_VALUE_KEY]: 0.01 },
  ])('does not let an unrelated key stand in for the color metric (%p)', (data) => {
    expect(resolveHeatmapCircleMetrics(data).isDrawable).toBe(false);
  });

  it('falls back to an unrecognized key when the cell has no known color metric', () => {
    expect(resolveHeatmapCircleMetrics({ new_metric: 0.5, [ADJUSTED_P_VALUE_KEY]: 0.01 })).toEqual({
      colorKey: 'new_metric',
      colorValue: 0.5,
      adjustedPValue: 0.01,
      isDrawable: true,
    });
  });

  it.each([null, undefined, NaN, Infinity])(
    'is undrawable for an unusable color value (%p)',
    (colorValue) => {
      const metrics = resolveHeatmapCircleMetrics({
        correlation: colorValue,
        [ADJUSTED_P_VALUE_KEY]: 0.01,
      });

      expect(metrics.isDrawable).toBe(false);
    },
  );

  // Values outside [0, 1] are accepted on purpose: validating the p-value range is the data
  // pipeline's job, so this only screens out values that cannot be rendered at all
  it.each([0, 1, 0.5, -0.5, 5])('accepts a numeric adjusted p-value (%p)', (adjustedPValue) => {
    expect(
      resolveHeatmapCircleMetrics({ correlation: 0.5, [ADJUSTED_P_VALUE_KEY]: adjustedPValue }),
    ).toEqual({
      colorKey: 'correlation',
      colorValue: 0.5,
      adjustedPValue,
      isDrawable: true,
    });
  });

  it.each([null, undefined, NaN, Infinity])(
    'is undrawable for an unusable adjusted p-value (%p)',
    (adjustedPValue) => {
      const metrics = resolveHeatmapCircleMetrics({
        correlation: 0.5,
        [ADJUSTED_P_VALUE_KEY]: adjustedPValue,
      });

      expect(metrics.isDrawable).toBe(false);
    },
  );
});

describe('isUsableMetricValue', () => {
  it.each([0, 1, -0.5])('accepts a finite number (%p)', (value) => {
    expect(isUsableMetricValue(value)).toBe(true);
  });

  it.each([null, undefined, NaN, Infinity, '0.5', {}])('rejects %p', (value) => {
    expect(isUsableMetricValue(value)).toBe(false);
  });
});

describe('canDrawHeatmapCircle', () => {
  it('is true only when both values are usable', () => {
    expect(canDrawHeatmapCircle({ correlation: 0.5, [ADJUSTED_P_VALUE_KEY]: 0.03 })).toBe(true);
  });

  it.each([null, undefined, 'not a cell', 42, [1, 2]])(
    'is false for input that is not a cell (%p)',
    (data) => {
      expect(canDrawHeatmapCircle(data)).toBe(false);
    },
  );

  it.each([
    { correlation: null, [ADJUSTED_P_VALUE_KEY]: 0.03 },
    { correlation: 0.5, [ADJUSTED_P_VALUE_KEY]: null },
    { [ADJUSTED_P_VALUE_KEY]: 0.03 },
    { correlation: 0.5 },
    { correlation: 0.5, [ADJUSTED_P_VALUE_KEY]: NaN },
    {},
  ])('is false for %p', (data) => {
    expect(canDrawHeatmapCircle(data)).toBe(false);
  });
});
