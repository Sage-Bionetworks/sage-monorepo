/**
 * Pixel bounds of the ECharts grid, available at render time via `params.coordSys` in a custom
 * series `renderItem` callback. ECharts only types `coordSys` as `{ type: string }` — the
 * `x`, `y`, `width`, and `height` fields exist at runtime but require a cast to access.
 *
 * Usage: `const grid = params.coordSys as unknown as GridCoordSys;`
 */
export type GridCoordSys = { x: number; y: number; width: number; height: number };
