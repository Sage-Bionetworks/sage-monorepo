import { HeatmapCircleColorKey, HeatmapCircleData } from '@sagebionetworks/explorers/models';

export const ADJUSTED_P_VALUE_KEY = 'adj_p_val';

export type HeatmapCircleMetrics<T extends HeatmapCircleData = HeatmapCircleData> =
  | {
      isDrawable: true;
      colorKey: HeatmapCircleColorKey<T>;
      colorValue: number;
      adjustedPValue: number;
    }
  | { isDrawable: false };

const UNDRAWABLE = { isDrawable: false } as const;

export function isUsableMetricValue(value: unknown): value is number {
  return typeof value === 'number' && Number.isFinite(value);
}

export function resolveHeatmapCircleMetrics<T extends HeatmapCircleData = HeatmapCircleData>(
  data: Partial<T> | null | undefined,
): HeatmapCircleMetrics<T> {
  if (data === null || typeof data !== 'object' || Array.isArray(data)) {
    return UNDRAWABLE;
  }

  const cell = data as Record<string, unknown>;
  // A cell holds exactly one color metric alongside adj_p_val, so the first other key is it
  const colorKey = Object.keys(cell).find((key) => key !== ADJUSTED_P_VALUE_KEY);
  if (colorKey === undefined) {
    return UNDRAWABLE;
  }

  const colorValue = cell[colorKey];
  const adjustedPValue = cell[ADJUSTED_P_VALUE_KEY];
  if (!isUsableMetricValue(colorValue) || !isUsableMetricValue(adjustedPValue)) {
    return UNDRAWABLE;
  }

  return {
    isDrawable: true,
    colorKey: colorKey as HeatmapCircleColorKey<T>,
    colorValue,
    adjustedPValue,
  };
}

export function canDrawHeatmapCircle(data: unknown): boolean {
  return resolveHeatmapCircleMetrics(data as HeatmapCircleData).isDrawable;
}
