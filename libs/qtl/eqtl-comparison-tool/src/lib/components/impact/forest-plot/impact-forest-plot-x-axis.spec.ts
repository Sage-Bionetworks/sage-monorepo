import { computeEffectSizeXAxis, FALLBACK_INTERVAL } from './impact-forest-plot-x-axis';
import { IMPACT_FOREST_PLOT_TARGET_TICK_INTERVALS } from './impact-forest-plot.constants';

function computeXAxis(confidenceIntervals: { ciLeft: number; ciRight: number }[]) {
  return computeEffectSizeXAxis(confidenceIntervals, IMPACT_FOREST_PLOT_TARGET_TICK_INTERVALS);
}

describe('computeEffectSizeXAxis', () => {
  it('snaps bounds that straddle zero out to a round step', () => {
    const astroLikeIntervals = [
      { ciLeft: -0.786843, ciRight: 0.345663 },
      { ciLeft: 0.137115, ciRight: 0.748307 },
    ];
    expect(computeXAxis(astroLikeIntervals)).toEqual({
      min: -1,
      max: 1,
      interval: 0.5,
      labelPrecision: 1,
    });
  });

  it('starts the axis at zero when every interval is positive', () => {
    const positiveIntervals = [
      { ciLeft: 0.1, ciRight: 0.26 },
      { ciLeft: 0.44, ciRight: 0.66 },
    ];
    expect(computeXAxis(positiveIntervals)).toEqual({
      min: 0,
      max: 0.8,
      interval: 0.2,
      labelPrecision: 1,
    });
  });

  it('ends the axis at zero when every interval is negative', () => {
    const negativeIntervals = [{ ciLeft: -0.46386, ciRight: -0.079467 }];
    expect(computeXAxis(negativeIntervals)).toMatchObject({ min: -0.6, max: 0, interval: 0.2 });
  });

  it('labels finer steps with more decimal places', () => {
    const narrowIntervals = [{ ciLeft: -0.027471, ciRight: 0.11886 }];
    expect(computeXAxis(narrowIntervals)).toEqual({
      min: -0.05,
      max: 0.15,
      interval: 0.05,
      labelPrecision: 2,
    });
  });

  it('falls back to one step either side of zero when every bound is zero', () => {
    expect(computeXAxis([{ ciLeft: 0, ciRight: 0 }])).toEqual({
      min: -FALLBACK_INTERVAL,
      max: FALLBACK_INTERVAL,
      interval: FALLBACK_INTERVAL,
      labelPrecision: 0,
    });
  });
});
