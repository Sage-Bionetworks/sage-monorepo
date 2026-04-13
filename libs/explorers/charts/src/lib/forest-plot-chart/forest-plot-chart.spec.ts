import { forestPlotItems } from '../mocks';
import { ForestPlotProps } from '../models';
import { computeInitialHeight, computeXBounds } from './forest-plot-chart';

describe('computeInitialHeight', () => {
  it('calculates height from item count when above the 450px floor', () => {
    // 10 × 44 + 80 = 520; 9 × 44 + 80 = 476 — both exceed the 450px floor
    expect(computeInitialHeight(10)).toBe('520px');
    expect(computeInitialHeight(9)).toBe('476px');
  });

  it('enforces minimum height of 450px when computed height is below floor', () => {
    // 8 × 44 + 80 = 432 < 450; 0 × 44 + 80 = 80 < 450
    expect(computeInitialHeight(0)).toBe('450px');
    expect(computeInitialHeight(8)).toBe('450px');
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
    // Neither case meets the "both defined" condition — auto-calculated instead
    expect(computeXBounds(propsMinOnly)).not.toEqual([-0.5, expect.anything()]);
    expect(computeXBounds(propsMaxOnly)).not.toEqual([expect.anything(), 0.5]);
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
});
