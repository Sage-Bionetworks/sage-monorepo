const NICE_STEP_MULTIPLIERS = [1, 2, 5, 10];
const FLOATING_POINT_DECIMALS = 10;
export const FALLBACK_INTERVAL = 1;

export interface EffectSizeXAxis {
  min: number;
  max: number;
  interval: number;
  labelPrecision: number;
}

function roundAwayFloatingPointDrift(value: number): number {
  const scale = 10 ** FLOATING_POINT_DECIMALS;
  // `+ 0` normalizes -0 to 0.
  return Math.round(value * scale) / scale + 0;
}

function computeNiceStep(rawStep: number): number {
  const magnitude = 10 ** Math.floor(Math.log10(rawStep));
  const multiplier =
    NICE_STEP_MULTIPLIERS.find((candidate) => candidate * magnitude >= rawStep) ?? 10;
  return roundAwayFloatingPointDrift(multiplier * magnitude);
}

// Fits the axis to the confidence intervals and always includes 0, snapping both bounds to a
// round step so every tick lands on a labeled grid line.
export function computeEffectSizeXAxis(
  confidenceIntervals: { ciLeft: number; ciRight: number }[],
  targetTickIntervals: number,
): EffectSizeXAxis {
  const lowest = Math.min(0, ...confidenceIntervals.map((ci) => ci.ciLeft));
  const highest = Math.max(0, ...confidenceIntervals.map((ci) => ci.ciRight));
  const range = highest - lowest;
  if (range === 0) {
    return {
      min: -FALLBACK_INTERVAL,
      max: FALLBACK_INTERVAL,
      interval: FALLBACK_INTERVAL,
      labelPrecision: 0,
    };
  }
  const interval = computeNiceStep(range / targetTickIntervals);
  return {
    min: roundAwayFloatingPointDrift(Math.floor(lowest / interval) * interval),
    max: roundAwayFloatingPointDrift(Math.ceil(highest / interval) * interval),
    interval,
    labelPrecision: Math.max(0, -Math.floor(Math.log10(interval))),
  };
}
