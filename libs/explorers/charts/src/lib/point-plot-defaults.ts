// Shared styling defaults for point-and-CI plots (forest-plot, candlestick).
// Boxplot and legend charts use a separate theme system in `chart-theme/`.

export const CI_LINE_WIDTH = 1.5;

export const TEXT_STYLE = {
  title: { color: '#24334f', fontSize: 14, fontWeight: 'bold' as const },
  axisName: { color: '#24334f', fontSize: 18, fontWeight: 'bold' as const },
};

export const AXIS_STYLE = {
  tickLabelColor: '#000',
  line: { width: 2, color: '#989898' },
  // Distance from the axis line to the title text; also used as grid padding so the title isn't clipped.
  titleGap: 50,
  valueAxisSplitNumber: 10,
};

export const GRID_TOP = {
  withoutTitle: 20,
  withTitle: 60,
};
