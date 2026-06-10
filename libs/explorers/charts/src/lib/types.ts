import type { EChartsOption } from 'echarts';

/**
 * Pixel bounds of the ECharts grid, available at render time via `params.coordSys` in a custom
 * series `renderItem` callback. ECharts only types `coordSys` as `{ type: string }` — the
 * `x`, `y`, `width`, and `height` fields exist at runtime but require a cast to access.
 *
 * Usage: `const grid = params.coordSys as unknown as GridCoordSys;`
 */
export type GridCoordSys = { x: number; y: number; width: number; height: number };

/**
 * Partial shape of the `updateAxisPointer` event payload emitted by ECharts when the axis
 * pointer moves. ECharts types event listeners loosely, so we narrow to the `axesInfo`
 * field via cast.
 *
 * Usage: `const event = rawEvent as UpdateAxisPointerEvent;`
 */
export type UpdateAxisPointerEvent = {
  axesInfo?: { axisDim: string; value: string | number }[];
};

/**
 * EChartsOption['tooltip'] is `TooltipOption | TooltipOption[] | undefined`. We never use
 * the array or undefined form, so narrow to the single-object case. NonNullable strips
 * `undefined` before Exclude removes the array variant (Exclude alone would leave
 * `undefined` since it isn't assignable to `unknown[]`). Importing from
 * `echarts/components` instead triggers nominal-type mismatches when callers assign the
 * result back into `EChartsOption['tooltip']`.
 */
export type TooltipOption = Exclude<NonNullable<EChartsOption['tooltip']>, unknown[]>;
