import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideLocationMocks } from '@angular/common/testing';
import { Component, computed, effect, inject, input } from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  ComparisonToolConfig,
  ComparisonToolViewConfig,
  LegendPanelConfig,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfigWithDropdowns,
  mockComparisonToolSelectorsWikiParams,
  provideLoadingIconColors,
} from '@sagebionetworks/explorers/testing';
import type { Meta, StoryObj } from '@storybook/angular';
import { applicationConfig } from '@storybook/angular';
import { of } from 'rxjs';
import { ComparisonToolComponent } from './comparison-tool.component';
import DocumentationTemplate from './comparison-tool.component.template.mdx';

type StoryArgs = Omit<
  Partial<ComparisonToolViewConfig>,
  'viewDetailsClick' | 'heatmapCircleClickTransformFn' | 'rowsPerPage'
> & {
  // Data properties
  configs: ComparisonToolConfig[];
  data: Record<string, unknown>[];
  pinnedItems: string[];
  // Panel visibility
  legendVisible?: boolean;
  visualizationOverviewVisible?: boolean;
};

@Component({
  selector: 'explorers-comparison-tool-story-wrapper',
  standalone: true,
  imports: [ComparisonToolComponent],
  template: '<explorers-comparison-tool [isLoading]="false" />',
})
class ComparisonToolStoryWrapperComponent {
  // Header inputs
  headerTitle = input<string>();
  headerTitleWikiParams = input<SynapseWikiParams | undefined, SynapseWikiParams | undefined>(
    undefined,
    {
      transform: (value: SynapseWikiParams | undefined) => (value ? { ...value } : undefined),
    },
  );
  selectorsWikiParams = input<Record<string, SynapseWikiParams>>();
  // Controls inputs
  filterResultsButtonTooltip = input<string>();
  showSignificanceControls = input<boolean>();
  viewDetailsTooltip = input<string>();
  // Legend inputs
  legendEnabled = input<boolean>();
  legendPanelConfig = input<LegendPanelConfig>();
  // Table inputs
  rowIdDataKey = input<string>();
  allowPinnedImageDownload = input<boolean>();
  defaultSort = input<readonly { readonly field: string; readonly order: 1 | -1 }[]>();
  linkExportField = input<'link_url' | 'link_text'>();
  // Data inputs
  configs = input<ComparisonToolConfig[]>();
  data = input<Record<string, unknown>[]>();
  pinnedItems = input<string[]>();
  // Panel visibility inputs
  legendVisible = input<boolean>();
  visualizationOverviewVisible = input<boolean>();

  private readonly comparisonToolService = inject(ComparisonToolService);

  // Computed values for pinned/unpinned data
  private readonly pinnedData = computed(() => {
    const data = this.data() || [];
    const pinnedItems = this.pinnedItems() || [];
    const rowIdKey = this.rowIdDataKey() || '_id';
    return data.filter((item) => pinnedItems.includes(String(item[rowIdKey])));
  });

  private readonly unpinnedData = computed(() => {
    const data = this.data() || [];
    const pinnedItems = this.pinnedItems() || [];
    const rowIdKey = this.rowIdDataKey() || '_id';
    return data.filter((item) => !pinnedItems.includes(String(item[rowIdKey])));
  });

  constructor() {
    // Effect to initialize the service with configs
    effect(() => {
      const configs = this.configs();
      if (configs && configs.length > 0) {
        this.comparisonToolService.connect({
          config$: of(configs),
          queryParams$: of({}),
        });
      }
    });

    // Effect to update view config
    effect(() => {
      const viewConfig: Partial<ComparisonToolViewConfig> = {};

      viewConfig.headerTitle = this.headerTitle();
      viewConfig.headerTitleWikiParams = this.headerTitleWikiParams();
      viewConfig.selectorsWikiParams = this.selectorsWikiParams();
      viewConfig.filterResultsButtonTooltip = this.filterResultsButtonTooltip();
      viewConfig.showSignificanceControls = this.showSignificanceControls();
      viewConfig.viewDetailsTooltip = this.viewDetailsTooltip();
      viewConfig.legendEnabled = this.legendEnabled();
      viewConfig.legendPanelConfig = this.legendPanelConfig();
      viewConfig.rowIdDataKey = this.rowIdDataKey();
      viewConfig.allowPinnedImageDownload = this.allowPinnedImageDownload();
      viewConfig.defaultSort = this.defaultSort();
      viewConfig.linkExportField = this.linkExportField();

      this.comparisonToolService.setViewConfig(viewConfig);
    });

    // Effect to update data
    effect(() => {
      const pinnedData = this.pinnedData();
      const unpinnedData = this.unpinnedData();
      const pinnedItems = this.pinnedItems() || [];

      this.comparisonToolService.setPinnedData(pinnedData);
      this.comparisonToolService.setUnpinnedData(unpinnedData);
      this.comparisonToolService.setPinnedItems(pinnedItems);
      this.comparisonToolService.pinnedResultsCount.set(pinnedData.length);
      this.comparisonToolService.totalResultsCount.set(unpinnedData.length);
    });

    // Effect to update panel visibility
    effect(() => {
      const legendVisible = this.legendVisible();
      if (legendVisible !== undefined) {
        this.comparisonToolService.setLegendVisibility(legendVisible);
      }

      const visualizationOverviewVisible = this.visualizationOverviewVisible();
      if (visualizationOverviewVisible !== undefined) {
        this.comparisonToolService.setVisualizationOverviewVisibility(visualizationOverviewVisible);
      }
    });
  }
}

const meta: Meta<StoryArgs> = {
  component: ComparisonToolStoryWrapperComponent,
  title: 'Comparison Tool/ComparisonToolComponent',
  tags: ['autodocs'],
  parameters: {
    docs: {
      page: DocumentationTemplate,
    },
  },
  argTypes: {
    // Header category
    headerTitle: {
      control: 'text',
      description: 'The main title displayed in the comparison tool header',
      table: { category: 'Header' },
    },
    headerTitleWikiParams: {
      control: 'object',
      description: 'Synapse wiki parameters for the header title info button',
      table: { category: 'Header' },
    },
    selectorsWikiParams: {
      control: 'object',
      description: 'Map of category dropdown options to Synapse wiki parameters for info buttons',
      table: { category: 'Header', disable: true },
    },
    // Controls category
    filterResultsButtonTooltip: {
      control: 'text',
      description: 'Tooltip text for the filter results button',
      table: { category: 'Controls' },
    },
    showSignificanceControls: {
      control: 'boolean',
      description: 'Whether to show significance threshold controls',
      table: { category: 'Controls' },
    },
    viewDetailsTooltip: {
      control: 'text',
      description: 'Tooltip text for the view details button in each row',
      table: { category: 'Controls' },
    },
    // Legend category
    legendEnabled: {
      control: 'boolean',
      description:
        'Whether the legend feature is enabled. ' +
        'If false, the legend is completely disabled and cannot be shown. ' +
        'If true, the legend can be toggled on/off by the user.',
      table: { category: 'Legend' },
    },
    legendVisible: {
      control: 'boolean',
      description:
        'Whether the legend panel is initially visible when the page loads. ' +
        'Only applies if legendEnabled is true. ' +
        'User can toggle visibility afterward.',
      table: { category: 'Legend' },
    },
    legendPanelConfig: {
      control: 'object',
      description: 'Configuration for the legend panel including labels and descriptions',
      table: { category: 'Legend', disable: true },
    },
    // Table category
    rowIdDataKey: {
      control: 'text',
      description:
        'The data key used to uniquely identify rows (e.g., `_id`, `name`, `composite_id`)',
      table: { category: 'Table' },
    },
    allowPinnedImageDownload: {
      control: 'boolean',
      description: 'Whether to allow downloading an image of the pinned items section',
      table: { category: 'Table' },
    },
    defaultSort: {
      control: 'object',
      description: 'Default sort configuration (array of `{field, order}` objects)',
      table: { category: 'Table', disable: true },
    },
    linkExportField: {
      control: 'select',
      options: ['link_url', 'link_text'],
      description: 'Which field to export for link columns: the URL or the display text',
      table: { category: 'Table' },
    },
    // Data category
    configs: {
      control: 'object',
      description:
        'Array of comparison tool configurations defining columns, filters, and dropdowns. ' +
        'Each config defines the table structure with columns, filters, and dropdown options for different data views. ' +
        'Should only include one value for `page`.',
      table: { category: 'Data' },
    },
    data: {
      control: 'object',
      description:
        'Array of data objects to display in the comparison tool. ' +
        'Each object represents a row and should contain properties matching the column `data_key` values defined in configs.',
      table: { category: 'Data' },
    },
    pinnedItems: {
      control: 'object',
      description: 'Array of row IDs (matching `rowIdDataKey`) to pin to the top of the table',
      table: { category: 'Data' },
    },
    visualizationOverviewVisible: {
      control: 'boolean',
      description:
        'Whether the visualization overview help panel is initially visible when the page loads. ' +
        'User can toggle visibility afterward.',
      table: { category: 'Controls' },
    },
  },
  decorators: [
    applicationConfig({
      providers: [
        provideRouter([]),
        provideLocationMocks(),
        provideHttpClient(withInterceptorsFromDi()),
        ...provideComparisonToolService(),
        ...provideComparisonToolFilterService(),
        ...provideLoadingIconColors(),
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<StoryArgs>;

export const Demo: Story = {
  args: {
    // Header
    headerTitle: 'Gene Comparison',
    headerTitleWikiParams: { ownerId: 'syn25913473', wikiId: '639222' },
    selectorsWikiParams: mockComparisonToolSelectorsWikiParams,
    // Controls
    filterResultsButtonTooltip: 'Filter the results based on the selected criteria',
    showSignificanceControls: true,
    viewDetailsTooltip: 'View details',
    // Legend
    legendEnabled: true,
    legendPanelConfig: {
      colorChartLowerLabel: 'Downregulated',
      colorChartUpperLabel: 'Upregulated',
      colorChartText:
        'Circle color indicates the value. Red shades indicate negative values, while blue shades indicate positive values.',
      sizeChartLowerLabel: 'Significant',
      sizeChartUpperLabel: 'Insignificant',
      sizeChartText:
        'Circle diameter indicates P-value. Larger circles indicate higher statistical significance.',
    },
    // Table
    rowIdDataKey: 'name',
    allowPinnedImageDownload: true,
    defaultSort: [{ field: 'name', order: 1 }],
    linkExportField: 'link_text',
    // Data
    configs: mockComparisonToolDataConfigWithDropdowns,
    data: mockComparisonToolData,
    pinnedItems: ['3xTg-AD', '5xFAD (UCI)', '5xFAD (IU/Jax/Pitt)'],
    // Panel visibility
    legendVisible: false,
    visualizationOverviewVisible: false,
  },
};
