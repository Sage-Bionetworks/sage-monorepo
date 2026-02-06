import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { Component, input, ViewChild } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HeatmapDetailsPanelData } from '@sagebionetworks/explorers/models';
import { provideComparisonToolService } from '@sagebionetworks/explorers/services';
import { heatmapDetailsPanelDataMock } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig, moduleMetadata } from '@storybook/angular';
import { HeatmapDetailsPanelComponent } from './heatmap-details-panel.component';

@Component({
  selector: 'explorers-heatmap-details-panel-wrapper',
  standalone: true,
  imports: [HeatmapDetailsPanelComponent],
  template: `
    <div style="padding: 20px;">
      <button (click)="togglePanel($event)">Toggle Panel</button>
      <explorers-heatmap-details-panel />
    </div>
  `,
})
class HeatmapDetailsPanelWrapperComponent {
  @ViewChild(HeatmapDetailsPanelComponent) panel!: HeatmapDetailsPanelComponent;
  data = input<HeatmapDetailsPanelData>(heatmapDetailsPanelDataMock);

  togglePanel(event: MouseEvent) {
    this.panel.toggle(event, this.data());
  }
}

const meta: Meta<HeatmapDetailsPanelWrapperComponent> = {
  component: HeatmapDetailsPanelWrapperComponent,
  title: 'Comparison Tool/ComparisonToolTable/HeatmapDetailsPanelComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService(),
      ],
    }),
    moduleMetadata({
      imports: [HeatmapDetailsPanelWrapperComponent],
    }),
  ],
  args: {
    data: heatmapDetailsPanelDataMock,
  },
};
export default meta;
type Story = StoryObj<HeatmapDetailsPanelWrapperComponent>;

export const Demo: Story = {};

export const GeneExpression: Story = {
  args: {
    data: {
      label: { left: 'Trem2', right: 'ENSMUSG00000023992' },
      heading: 'Differential RNA Expression (Hippocampus)',
      subHeadings: ['Trem2-R47H_NSS (12 months, Female & Male)', 'Matched Control: C57BL6J'],
      valueLabel: 'Log 2 Fold Change',
      value: 0.29,
      pValue: 0.048,
      footer: 'Significance is considered to be an adjusted p-value < 0.05',
    },
  },
};

export const DiseaseCorrelation: Story = {
  args: {
    data: {
      heading: 'Disease Correlation (Consensus Network Modules)',
      subHeadings: [
        '3xTg-AD (4 months, Female)',
        'Human Consensus Cluster A - ECM Organization | IFG module',
      ],
      valueLabel: 'Correlation',
      value: 0.29,
      pValue: 0.048,
      footer: 'Significance is considered to be an adjusted p-value < 0.05',
    },
  },
};
