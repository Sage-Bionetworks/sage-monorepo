import { RouterModule } from '@angular/router';
import { HeatmapCircleData } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { render } from '@testing-library/angular';
import { HeatmapCircleComponent } from './heatmap-circle.component';

async function setup<T extends HeatmapCircleData>(data: T) {
  const component = await render(HeatmapCircleComponent, {
    imports: [RouterModule],
    providers: [...provideComparisonToolService(), ...provideComparisonToolFilterService()],
    componentInputs: {
      data,
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

  it('should hide the circle when significance threshold is active and exceeded', async () => {
    const { element, fixture, service } = await setup({ log2_fc: 0.2, adj_p_val: 0.2 });

    service.setSignificanceThresholdActive(true);
    service.setSignificanceThreshold(0.05);
    fixture.detectChanges();

    expect(element.style.display).toBe('none');
  });
});
