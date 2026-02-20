import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { Component, effect, inject, input } from '@angular/core';
import { provideRouter } from '@angular/router';
import { ComparisonToolViewConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { ComparisonToolHeaderComponent } from './comparison-tool-header.component';

type StoryArgs = Pick<
  ComparisonToolViewConfig,
  'headerTitle' | 'headerTitleWikiParams' | 'filterResultsButtonTooltip'
>;

@Component({
  selector: 'explorers-comparison-tool-header-story-wrapper',
  standalone: true,
  imports: [ComparisonToolHeaderComponent],
  template: '<explorers-comparison-tool-header />',
})
class ComparisonToolHeaderStoryWrapperComponent {
  headerTitle = input<string>();
  headerTitleWikiParams = input<SynapseWikiParams, SynapseWikiParams | undefined>(undefined, {
    transform: (value) => (value ? { ...value } : undefined),
  });
  filterResultsButtonTooltip = input<string>();

  private readonly comparisonToolService = inject(ComparisonToolService);

  constructor() {
    effect(() => {
      const viewConfig: Partial<ComparisonToolViewConfig> = {};
      viewConfig.headerTitle = this.headerTitle();
      viewConfig.headerTitleWikiParams = this.headerTitleWikiParams();
      viewConfig.filterResultsButtonTooltip = this.filterResultsButtonTooltip();
      this.comparisonToolService.setViewConfig(viewConfig);
    });
  }
}

const meta: Meta<StoryArgs> = {
  component: ComparisonToolHeaderStoryWrapperComponent,
  title: 'Comparison Tool/ComparisonToolHeaderComponent',
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideLoadingIconColors(),
        ...provideComparisonToolService({}),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<StoryArgs>;

export const Demo: Story = {
  args: {
    headerTitle: 'Gene Comparison',
    headerTitleWikiParams: { ownerId: 'syn25913473', wikiId: '639222' },
    filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
  },
};
