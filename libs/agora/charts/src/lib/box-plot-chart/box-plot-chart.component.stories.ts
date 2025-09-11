import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { applicationConfig, moduleMetadata, type Meta, type StoryObj } from '@storybook/angular';
import { boxPlotChartItemsMock } from './box-plot-chart-data-mock';
import { BoxPlotComponent } from './box-plot-chart.component';

const meta: Meta<BoxPlotComponent> = {
  component: BoxPlotComponent,
  title: 'Charts/BoxPlot',
  decorators: [
    applicationConfig({
      providers: [provideHttpClient(withInterceptorsFromDi())],
    }),
    moduleMetadata({
      imports: [BoxplotDirective],
    }),
  ],
};
export default meta;
type Story = StoryObj<BoxPlotComponent>;

export const Primary: Story = {
  args: {
    heading: 'AD Diagnosis (males and females)',
    data: boxPlotChartItemsMock,
    xAxisLabel: 'Brain Region',
    yAxisLabel: 'LOG 2 FOLD CHANGE',
    yAxisMin: -1,
    yAxisMax: 1,
  },
};

export const NoData: Story = {
  args: {
    heading: 'AD Diagnosis (males and females)',
    xAxisLabel: 'Brain Region',
    yAxisLabel: 'LOG 2 FOLD CHANGE',
    yAxisMin: -1,
    yAxisMax: 1,
  },
};
