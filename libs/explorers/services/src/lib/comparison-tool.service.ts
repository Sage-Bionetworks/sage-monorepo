import { computed, inject, Injectable, signal, Signal } from '@angular/core';
import {
  ComparisonToolColumn,
  ComparisonToolColumns,
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolViewConfig,
} from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { NotificationService } from './notification.service';

/**
 * Shared state contract for comparison tools.
 */

@Injectable()
export class ComparisonToolService<T> {
  private readonly notificationService = inject(NotificationService);

  private readonly DEFAULT_SORT_ORDER = -1;
  private readonly DEFAULT_VIEW_CONFIG: ComparisonToolViewConfig = {
    selectorsWikiParams: {},
    headerTitle: 'Comparison Tool',
    filterResultsButtonTooltip: 'Filter results',
    showSignificanceControls: true,
    viewDetailsTooltip: 'View detailed results',
    viewDetailsClick: (id: string, label: string) => {
      return;
    },
    legendPanelConfig: {
      colorChartLowerLabel: '',
      colorChartUpperLabel: '',
      colorChartText: '',
      sizeChartLowerLabel: '',
      sizeChartUpperLabel: '',
      sizeChartText: '',
    },
    legendEnabled: true,
  };

  private readonly viewConfigSignal = signal<ComparisonToolViewConfig>(this.DEFAULT_VIEW_CONFIG);
  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly dropdownSelectionSignal = signal<string[]>([]);
  private readonly isLegendVisibleSignal = signal(false);
  private readonly isVisualizationOverviewVisibleSignal = signal(false);
  private readonly maxPinnedItemsSignal = signal<number>(50);
  private readonly pinnedItemsSignal = signal<Set<string>>(new Set());
  private readonly sortFieldSignal = signal<string | undefined>(undefined);
  private readonly sortOrderSignal = signal<number>(this.DEFAULT_SORT_ORDER);
  private readonly columnsForDropdownsSignal = signal<ComparisonToolColumns[]>([]);
  private readonly unpinnedDataSignal = signal<T[]>([]);
  private readonly pinnedDataSignal = signal<T[]>([]);

  readonly viewConfig = this.viewConfigSignal.asReadonly();
  readonly configs = this.configsSignal.asReadonly();
  readonly dropdownSelection = this.dropdownSelectionSignal.asReadonly();
  readonly isLegendVisible = this.isLegendVisibleSignal.asReadonly();
  readonly isVisualizationOverviewVisible = this.isVisualizationOverviewVisibleSignal.asReadonly();
  readonly maxPinnedItems = this.maxPinnedItemsSignal.asReadonly();
  readonly pinnedItems = this.pinnedItemsSignal.asReadonly();
  readonly sortField = this.sortFieldSignal.asReadonly();
  readonly sortOrder = this.sortOrderSignal.asReadonly();
  readonly unpinnedData = this.unpinnedDataSignal.asReadonly();
  readonly pinnedData = this.pinnedDataSignal.asReadonly();

  readonly currentConfig: Signal<ComparisonToolConfig | null> = computed(() => {
    const configs = this.configsSignal();
    if (!configs.length) {
      return null;
    }

    // if no selection, return first config
    const dropdownSelection = this.dropdownSelectionSignal();
    if (!dropdownSelection.length) {
      return configs[0];
    }

    const exactMatch = configs.find((config) => isEqual(config.dropdowns ?? [], dropdownSelection));

    if (exactMatch) {
      return exactMatch;
    }

    const prefixMatch = configs.find((config) =>
      this.isPrefix(dropdownSelection, config.dropdowns),
    );

    return prefixMatch ?? configs[0];
  });

  readonly columns: Signal<ComparisonToolColumn[]> = computed(() => {
    const config = this.currentConfig();
    if (!config) return [];

    // Get columns from current config (the source of truth from database)
    const configColumns = config.columns;
    if (!configColumns || configColumns.length === 0) return [];

    if (!this.hasDropdowns(config.dropdowns)) {
      const savedColumns = this.columnsForDropdownsSignal()[0]?.columns;
      return this.applyColumnPreferences(configColumns, savedColumns);
    }

    // Find saved column preferences for this EXACT dropdown combination
    const savedColumns = this.columnsForDropdownsSignal().find((c) =>
      isEqual(c.dropdowns, config.dropdowns),
    )?.columns;

    return this.applyColumnPreferences(configColumns, savedColumns);
  });

  selectedColumns = computed(() => {
    return this.columns().filter((col) => col.selected);
  });

  hasHiddenColumns(): boolean {
    return this.columns().some((col) => !col.selected);
  }

  loadingResultsCount = computed(() => this.currentConfig()?.row_count ?? '');

  totalResultsCount = signal<number>(0);
  pinnedResultsCount = signal<number>(0);
  hasMaxPinnedItems = computed(() => {
    return this.pinnedResultsCount() >= this.maxPinnedItems();
  });
  disabledPinTooltip = computed(() => {
    return `You have already pinned the maximum number of items (${this.maxPinnedItems()}). You must unpin some items before you can pin more.`;
  });

  setMaxPinnedItems(count: number) {
    this.maxPinnedItemsSignal.set(count);
  }

  initialize(configs: ComparisonToolConfig[], selection?: string[]) {
    this.configsSignal.set(configs ?? []);
    this.totalResultsCount.set(0);
    this.resetPinnedItems();
    this.setSort(undefined, this.DEFAULT_SORT_ORDER);
    this.setUnpinnedData([]);
    this.setPinnedData([]);

    if (!configs?.length) {
      this.updateDropdownSelectionIfChanged([]);
      this.columnsForDropdownsSignal.set([]);
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection ?? [], configs);
    this.updateDropdownSelectionIfChanged(normalizedSelection);

    // Initialize columns for dropdowns based on the first config.
    const firstConfig = configs[0];

    // If there are no dropdowns or only one dropdown, we can just set the columns for that config.
    if (!this.hasDropdowns(firstConfig.dropdowns)) {
      const columnsData: ComparisonToolColumns[] = [
        {
          dropdowns: [],
          columns: firstConfig.columns.map((column) => ({ ...column, selected: true })),
        },
      ];
      this.columnsForDropdownsSignal.set(columnsData);
    } else {
      // For multiple dropdowns, we need to initialize the columns for each dropdown combination.
      const columnsData: ComparisonToolColumns[] = this.configs().map((config) => ({
        dropdowns: config.dropdowns,
        columns: config.columns.map((column) => ({ ...column, selected: true })),
      }));
      this.columnsForDropdownsSignal.set(columnsData);
    }
  }

  setDropdownSelection(selection: string[]) {
    const configs = this.configsSignal();
    if (!configs.length) {
      const normalizedSelection = selection ?? [];
      this.updateDropdownSelectionIfChanged(normalizedSelection);
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection, configs);
    this.updateDropdownSelectionIfChanged(normalizedSelection);

    const config = this.currentConfig();
    if (config?.columns && config.columns.length > 0) {
      if (!this.hasDropdowns(config.dropdowns)) {
        // No dropdowns: always reset to all columns selected
        this.columnsForDropdownsSignal.set([
          {
            dropdowns: [],
            columns: config.columns.map((column) => ({ ...column, selected: true })),
          },
        ]);
        return;
      }

      // For dropdowns, we need to find the matching dropdown combination and update the columns accordingly.
      const currentDropdowns = config.dropdowns;

      this.columnsForDropdownsSignal.update((columnsData) => {
        // Match on EXACT dropdown combination
        const existingIndex = columnsData.findIndex((c) => isEqual(c.dropdowns, currentDropdowns));

        // If this dropdown combination exists, update it with current config columns
        // while preserving user preferences
        if (existingIndex !== -1) {
          const updatedCols = [...columnsData];
          updatedCols[existingIndex] = {
            dropdowns: currentDropdowns,
            columns: this.applyColumnPreferences(
              config.columns,
              columnsData[existingIndex].columns,
            ),
          };
          return updatedCols;
        }

        // New dropdown combination - initialize with all columns selected
        const newColumnsData: ComparisonToolColumns = {
          dropdowns: currentDropdowns,
          columns: config.columns.map((column) => ({ ...column, selected: true })),
        };

        return [...columnsData, newColumnsData];
      });
    }
  }

  setLegendVisibility(visible: boolean) {
    this.isLegendVisibleSignal.set(visible);
  }

  toggleLegend() {
    this.isLegendVisibleSignal.update((visible) => !visible);
  }

  setVisualizationOverviewVisibility(visible: boolean) {
    this.isVisualizationOverviewVisibleSignal.set(visible);
  }

  toggleVisualizationOverview() {
    this.isVisualizationOverviewVisibleSignal.update((visible) => !visible);
  }

  setViewConfig(viewConfig: Partial<ComparisonToolViewConfig>) {
    this.viewConfigSignal.set({ ...this.DEFAULT_VIEW_CONFIG, ...viewConfig });
    this.setLegendVisibility(false);
    this.setVisualizationOverviewVisibility(false);
  }

  isPinned(id: string): boolean {
    return this.pinnedItemsSignal().has(id);
  }

  togglePin(id: string) {
    if (this.isPinned(id)) {
      this.unpinItem(id);
      return;
    }
    this.pinItem(id);
  }

  pinItem(id: string) {
    if (this.hasMaxPinnedItems()) {
      this.notificationService.showWarning(
        `You have reached the maximum number of pinned items (${this.maxPinnedItems()}). Please unpin an item before pinning a new one.`,
      );
    } else {
      this.pinnedItemsSignal.update((pinnedItems) => {
        const newSet = new Set(pinnedItems);
        newSet.add(id);
        return newSet;
      });
    }
  }

  unpinItem(id: string) {
    this.pinnedItemsSignal.update((pinnedItems) => {
      const newSet = new Set(pinnedItems);
      newSet.delete(id);
      return newSet;
    });
  }

  pinList(ids: string[]) {
    this.pinnedItemsSignal.update((pinnedItems) => {
      const newSet = new Set(pinnedItems);
      let itemsAdded = 0;
      for (const id of ids) {
        if (newSet.size >= this.maxPinnedItems()) {
          const messagePrefix = itemsAdded === 0 ? 'No rows' : `Only ${itemsAdded} rows`;
          this.notificationService.showWarning(
            `${messagePrefix} were pinned, because you reached the maximum of ${this.maxPinnedItems()} pinned items.`,
          );
          break;
        }
        if (newSet.has(id)) {
          continue;
        }
        newSet.add(id);
        itemsAdded++;
      }
      return newSet;
    });
  }

  setPinnedItems(items: string[]) {
    this.pinnedItemsSignal.set(new Set(items));
  }

  resetPinnedItems() {
    this.setPinnedItems([]);
  }

  toggleColumn(column: ComparisonToolColumn) {
    const config = this.currentConfig();
    if (!config) return;

    const hasCategoryDropdowns = this.hasDropdowns(config.dropdowns);

    this.columnsForDropdownsSignal.update((cols) => {
      // If there are category dropdowns, find the index of the current dropdown combination.
      // If not, we will always target the first (and only) entry in columnsForDropdowns.
      const targetIndex = hasCategoryDropdowns
        ? cols.findIndex((c) => isEqual(c.dropdowns, config.dropdowns))
        : 0;

      // If no dropdowns or single dropdown, targetIndex will be 0. If multiple dropdowns, find the matching dropdown combination.
      if (targetIndex === -1 || targetIndex >= cols.length) return cols;

      return this.updateColumnSelection(cols, targetIndex, column.data_key);
    });
  }

  private updateColumnSelection(
    cols: ComparisonToolColumns[],
    targetIndex: number,
    dataKey: string,
  ): ComparisonToolColumns[] {
    const columnsData = cols[targetIndex];
    const colIndex = columnsData.columns.findIndex((col) => col.data_key === dataKey);

    if (colIndex === -1) return cols;

    const updatedColumns = [...columnsData.columns];
    updatedColumns[colIndex] = {
      ...updatedColumns[colIndex],
      selected: !updatedColumns[colIndex].selected,
    };

    const updatedCols = [...cols];
    updatedCols[targetIndex] = { ...columnsData, columns: updatedColumns };
    return updatedCols;
  }

  setUnpinnedData(unpinnedData: T[]) {
    this.unpinnedDataSignal.set(unpinnedData);
  }

  setPinnedData(pinnedData: T[]) {
    this.pinnedDataSignal.set(pinnedData);
  }

  private updateDropdownSelectionIfChanged(selection: string[]) {
    if (isEqual(this.dropdownSelectionSignal(), selection)) {
      return;
    }

    this.dropdownSelectionSignal.set(selection);
    this.resetPinnedItems();
  }

  private normalizeSelection(selection: string[], configs: ComparisonToolConfig[]): string[] {
    if (!configs.length) {
      return [];
    }

    const defaultSelection = [...(configs[0].dropdowns ?? [])];

    if (!selection || selection.length === 0) {
      return defaultSelection;
    }

    const exactMatch = configs.find((config) => isEqual(config.dropdowns ?? [], selection));
    if (exactMatch) {
      return [...exactMatch.dropdowns];
    }

    const prefixMatch = configs.find((config) => this.isPrefix(selection, config.dropdowns));
    if (prefixMatch) {
      return [...prefixMatch.dropdowns];
    }

    return defaultSelection;
  }

  private isPrefix(prefix: string[], target: string[] | undefined): boolean {
    const normalizedTarget = target ?? [];
    if (prefix.length > normalizedTarget.length) {
      return false;
    }

    return prefix.every((value, index) => normalizedTarget[index] === value);
  }

  /**
   * Applies saved column selection preferences to config columns.
   * If no saved preferences exist, all columns are marked as selected.
   * This ensures we always show the columns from the config, but preserve user's show/hide preferences.
   */
  private applyColumnPreferences(
    configColumns: ComparisonToolConfigColumn[],
    savedColumns?: ComparisonToolColumn[],
  ): ComparisonToolColumn[] {
    if (!savedColumns?.length) {
      return configColumns.map((column) => ({ ...column, selected: true }));
    }

    const selectionMap = new Map(savedColumns.map((col) => [col.data_key, col.selected]));

    return configColumns.map((column) => ({
      ...column,
      selected: selectionMap.get(column.data_key) ?? true,
    }));
  }

  /**
   * Determines if there are category dropdowns present in the config.
   */
  private hasDropdowns(dropdowns: string[] | undefined): boolean {
    if (!dropdowns || dropdowns.length === 0) {
      return false;
    }
    return dropdowns.length >= 1;
  }

  setSort(sortField: string | undefined, sortOrder: number | undefined) {
    this.sortFieldSignal.set(sortField);
    this.sortOrderSignal.set(sortOrder || this.DEFAULT_SORT_ORDER);
  }
}
