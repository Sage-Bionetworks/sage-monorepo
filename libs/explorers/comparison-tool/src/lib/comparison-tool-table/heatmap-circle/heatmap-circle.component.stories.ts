import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { Component, effect, inject, input } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HeatmapCircleData } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  provideComparisonToolFilterService,
} from '@sagebionetworks/explorers/services';
import type { Meta, StoryObj } from '@storybook/angular';
import { HeatmapCircleComponent } from './heatmap-circle.component';

type ExampleResult = HeatmapCircleData<'log2_fc'>;

@Component({
  selector: 'explorers-heatmap-circle-story-host',
  standalone: true,
  imports: [HeatmapCircleComponent],
  template: `<explorers-heatmap-circle [data]="data()" />`,
})
class HeatmapCircleStoryHostComponent {
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  data = input.required<ExampleResult>();
  significanceThreshold = input<number>(0.05);
  significanceThresholdActive = input<boolean>(false);

  constructor() {
    effect(() => {
      this.comparisonToolFilterService.setSignificanceThreshold(this.significanceThreshold());
      this.comparisonToolFilterService.setSignificanceThresholdActive(
        this.significanceThresholdActive(),
      );
    });
  }
}

interface HeatmapCircleStoryArgs {
  data: ExampleResult;
  significanceThreshold: number;
  significanceThresholdActive: boolean;
}

const meta: Meta<HeatmapCircleStoryArgs> = {
  component: HeatmapCircleStoryHostComponent,
  title: 'Comparison Tools/ComparisonToolTable/HeatmapCircleComponent',
  argTypes: {
    significanceThreshold: {
      control: { type: 'number', min: 0, step: 0.01 },
    },
    significanceThresholdActive: { control: 'boolean' },
  },
  args: {
    significanceThreshold: 0.05,
    significanceThresholdActive: false,
  },
  render: (args) => {
    const { significanceThreshold, significanceThresholdActive, data } = args;
    return {
      props: { data, significanceThreshold, significanceThresholdActive },
      applicationConfig: {
        providers: [
          provideRouter([]),
          provideHttpClient(withInterceptorsFromDi()),
          ...provideComparisonToolFilterService({
            significanceThreshold,
            significanceThresholdActive,
          }),
        ],
      },
    };
  },
};
export default meta;
type Story = StoryObj<HeatmapCircleStoryArgs>;

export const Demo: Story = {
  args: {
    data: {
      log2_fc: 1.5,
      adj_p_val: 0.03,
    },
  },
};
