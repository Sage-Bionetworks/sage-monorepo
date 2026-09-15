import { RouterModule } from '@angular/router';
import { HeatmapCircleData } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import {
  HeatmapCircleComponent,
  MAX_CIRCLE_SIZE,
  MIN_CIRCLE_SIZE,
} from './heatmap-circle.component';

type ExampleCellData = Partial<HeatmapCircleData<'log2_fc'>>;

async function setup(data: ExampleCellData) {
  const component = await render(HeatmapCircleComponent, {
    imports: [RouterModule],
    providers: [
      MessageService,
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
    ],
    componentInputs: {
      data: data as HeatmapCircleData,
    },
  });
  const fixture = component.fixture;
  const service = fixture.debugElement.injector.get(ComparisonToolFilterService);
  const element = fixture.nativeElement.querySelector('div') as HTMLDivElement;

  return { component, fixture, service, element };
}

describe('HeatmapCircleComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup({ log2_fc: 0.1, adj_p_val: 0.01 });
    expect(component).toBeTruthy();
  });

  it('should render a plus class and visible circle for positive values', async () => {
    const { element } = await setup({ log2_fc: 0.35, adj_p_val: 0.01 });

    expect(element.className).toContain('heatmap-circle');
    expect(element.className).toContain('plus');
    expect(element.style.display).toBe('block');
    expect(element.style.backgroundColor).toBe('rgb(62, 104, 170)');
  });

  it('should render a minus class for negative values', async () => {
    const { element } = await setup({ log2_fc: -0.25, adj_p_val: 0.01 });

    expect(element.className).toContain('heatmap-circle');
    expect(element.className).toContain('minus');
    expect(element.style.backgroundColor).toBe('rgb(241, 102, 129)');
  });

  it('should render a zero class for a value of zero', async () => {
    const { element } = await setup({ log2_fc: 0, adj_p_val: 0.01 });

    expect(element.className).toContain('heatmap-circle');
    expect(element.className).toContain('zero');
    expect(element.className).not.toContain('plus');
    expect(element.className).not.toContain('minus');
    expect(element.style.backgroundColor).toBe('var(--color-gray-400)');
  });

  // resolveHeatmapCircleMetrics only screens out non-finite values, leaving range validation to
  // the data pipeline, so out-of-range values reach the size calculation and must not overflow
  it.each([
    { label: 'a negative adjusted p-value', adj_p_val: -0.5, expectedSize: MAX_CIRCLE_SIZE },
    { label: 'an adjusted p-value above one', adj_p_val: 1000, expectedSize: MIN_CIRCLE_SIZE },
  ])('should clamp the circle size for $label', async ({ adj_p_val, expectedSize }) => {
    const { element } = await setup({ log2_fc: 0.2, adj_p_val });

    expect(element.style.width).toBe(`${expectedSize}px`);
    expect(element.style.height).toBe(`${expectedSize}px`);
  });

  it('should hide the circle when significance threshold is active and exceeded', async () => {
    const { element, fixture, service } = await setup({ log2_fc: 0.2, adj_p_val: 0.2 });

    service.setSignificanceThresholdActive(true);
    service.setSignificanceThreshold(0.05);
    fixture.detectChanges();

    expect(element.style.display).toBe('none');
  });

  describe('unusable cell data', () => {
    const unusableCells: Record<string, ExampleCellData> = {
      'a null color value': { log2_fc: null, adj_p_val: 0.03 },
      'a null adjusted p-value': { log2_fc: 1.5, adj_p_val: null },
      'a missing color value': { adj_p_val: 0.03 },
      'a missing adjusted p-value': { log2_fc: 1.5 },
      'no values at all': {},
      'a NaN adjusted p-value': { log2_fc: 1.5, adj_p_val: NaN },
    };

    it.each(Object.entries(unusableCells))('should not draw a circle for %s', async (_, data) => {
      const { element } = await setup(data);

      expect(element.style.display).toBe('none');
      expect(element.className).toBe('heatmap-circle');
    });
  });
});
