import { forestPlotItems } from '../mocks';
import { ForestPlotProps } from '../models';
import { computeInitialHeight, computeXBounds } from './forest-plot-chart';

describe('computeInitialHeight', () => {
  it('calculates height from item count when above the 490px floor', () => {
    // 10 × 44 + 60 = 500; 11 × 44 + 60 = 544 — both exceed the 490px floor
    expect(computeInitialHeight(10)).toBe('500px');
    expect(computeInitialHeight(11)).toBe('544px');
  });

  it('enforces minimum height of 490px when computed height is below floor', () => {
    // 9 × 44 + 60 = 456 < 490; 0 × 44 + 60 = 60 < 490
    expect(computeInitialHeight(0)).toBe('490px');
    expect(computeInitialHeight(9)).toBe('490px');
  });
});

describe('computeXBounds', () => {
  it('returns explicit bounds when both xAxisMin and xAxisMax are provided', () => {
    const props: ForestPlotProps = {
      items: forestPlotItems,
      xAxisMin: -0.5,
      xAxisMax: 0.5,
    };
    expect(computeXBounds(props)).toEqual([-0.5, 0.5]);
  });

  it('ignores explicit bounds when only one is provided', () => {
    const propsMinOnly: ForestPlotProps = { items: forestPlotItems, xAxisMin: -0.5 };
    const propsMaxOnly: ForestPlotProps = { items: forestPlotItems, xAxisMax: 0.5 };
    const autoCalculated = computeXBounds({ items: forestPlotItems });
    // Neither case meets the "both defined" condition — auto-calculated instead
    expect(computeXBounds(propsMinOnly)).toEqual(autoCalculated);
    expect(computeXBounds(propsMaxOnly)).toEqual(autoCalculated);
  });

  it('auto-calculates symmetric bounds from CI data (x1.1)', () => {
    // forestPlotItems max |CI bound| is DLPFC ciLeft = -0.178 → 0.178 × 1.1 = 0.1958
    const [xMin, xMax] = computeXBounds({ items: forestPlotItems });
    expect(xMin).toBeCloseTo(-0.1958, 3);
    expect(xMax).toBeCloseTo(0.1958, 3);
  });

  it('returns symmetric bounds (xMin === -xMax)', () => {
    const [xMin, xMax] = computeXBounds({ items: forestPlotItems });
    expect(xMin).toBeCloseTo(-xMax, 10);
  });

  it('handles single item', () => {
    const props: ForestPlotProps = {
      items: [{ yAxisCategory: 'ACC', value: 0, ciLeft: -0.1, ciRight: 0.2 }],
    };
    const [xMin, xMax] = computeXBounds(props);
    expect(xMin).toBeCloseTo(-0.22, 3);
    expect(xMax).toBeCloseTo(0.22, 3);
  });

  it('returns [-1, 1] when all CI bounds are zero', () => {
    const props: ForestPlotProps = {
      items: [{ yAxisCategory: 'ACC', value: 0, ciLeft: 0, ciRight: 0 }],
    };
    expect(computeXBounds(props)).toEqual([-1, 1]);
  });
});
