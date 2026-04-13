import { forestPlotItems } from '../mocks';
import { ForestPlotProps } from '../models';
import { computeInitialHeight, computeXBounds } from './forest-plot-chart';

describe('computeInitialHeight', () => {
  it('calculates height from item count', () => {
    // 8 × 44 + 80 = 432; 3 × 44 + 80 = 212 — both exceed the 200px floor
    expect(computeInitialHeight(8)).toBe('432px');
    expect(computeInitialHeight(3)).toBe('212px');
  });

  it('enforces minimum height of 200px when computed height is below floor', () => {
    // 2 × 44 + 80 = 168 < 200; 0 × 44 + 80 = 80 < 200
    expect(computeInitialHeight(0)).toBe('200px');
    expect(computeInitialHeight(2)).toBe('200px');
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
