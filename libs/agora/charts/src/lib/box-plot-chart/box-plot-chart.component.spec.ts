import { HelperService } from '@sagebionetworks/agora/services';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { render, screen } from '@testing-library/angular';
import { boxPlotChartItemsMock } from './box-plot-chart-data-mock';
import { BoxPlotComponent } from './box-plot-chart.component';

class MockHelperService {
  getGCTColumnTooltipText(column: string): string {
    return column + '-mock-text';
  }
}

describe('Component: Chart - Box Plot', () => {
  it('should render chart with data', async () => {
    await render(BoxPlotComponent, {
      componentProperties: {
        data: boxPlotChartItemsMock,
      },
      imports: [BoxplotDirective],
      providers: [{ provide: HelperService, useClass: MockHelperService }],
    });

    // keys are sorted alphabetically
    const tooltipTextRegExp = new RegExp(
      `${boxPlotChartItemsMock[1].circle?.tooltip}.*${boxPlotChartItemsMock[0].circle?.tooltip}`,
    );
    expect(screen.getByLabelText(tooltipTextRegExp)).toBeVisible();
  });

  it('should render no data placeholder when no data is passed', async () => {
    await render(BoxPlotComponent, {
      componentProperties: {
        data: undefined,
      },
      imports: [BoxplotDirective],
      providers: [{ provide: HelperService, useClass: MockHelperService }],
    });
    expect(screen.getByLabelText('No data is currently available.')).toBeVisible();
  });
});
