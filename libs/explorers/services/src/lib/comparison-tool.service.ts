import { computed, inject, Injectable, signal, Signal } from '@angular/core';
import {
  ComparisonToolColumn,
  ComparisonToolColumns,
  ComparisonToolConfig,
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
  };

  private readonly viewConfigSignal = signal<ComparisonToolViewConfig>(this.DEFAULT_VIEW_CONFIG);
  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly dropdownSelectionSignal = signal<string[]>([]);
  private readonly isLegendVisibleSignal = signal(false);
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

    const dropdownsToMatch = this.getDropdownsForMatching(config.dropdowns);

    // Match on dropdowns only (each service instance is page-specific)
    return (
      this.columnsForDropdownsSignal().find((c) =>
        isEqual(this.getDropdownsForMatching(c.dropdowns), dropdownsToMatch),
      )?.columns ?? []
    );
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

  setLegendVisibility(visible: boolean) {
    this.isLegendVisibleSignal.set(visible);
  }

  setMaxPinnedItems(count: number) {
    this.maxPinnedItemsSignal.set(count);
  }

  toggleLegend() {
    this.isLegendVisibleSignal.update((visible) => !visible);
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

    const columnsData: ComparisonToolColumns[] = this.configs().map((config) => ({
      dropdowns: config.dropdowns,
      columns: config.columns.map((column) => ({ ...column, selected: true })),
    }));
    this.columnsForDropdownsSignal.set(columnsData);
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
      const currentDropdowns = config.dropdowns;
      const dropdownsToMatch = this.getDropdownsForMatching(currentDropdowns);

      this.columnsForDropdownsSignal.update((columnsData) => {
        // Match on dropdowns only (each service instance is page-specific)
        const existingIndex = columnsData.findIndex((c) =>
          isEqual(this.getDropdownsForMatching(c.dropdowns), dropdownsToMatch),
        );

        // If this dropdown combination exists, preserve its column state
        if (existingIndex !== -1) {
          return columnsData; // State already exists, don't modify
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

  setViewConfig(viewConfig: Partial<ComparisonToolViewConfig>) {
    this.viewConfigSignal.set({ ...this.DEFAULT_VIEW_CONFIG, ...viewConfig });
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

    const currentDropdowns = config.dropdowns;
    const dropdownsToMatch = this.getDropdownsForMatching(currentDropdowns);

    this.columnsForDropdownsSignal.update((cols) => {
      // Match on dropdowns only (each service instance is page-specific)
      const columnsIndex = cols.findIndex((c) =>
        isEqual(this.getDropdownsForMatching(c.dropdowns), dropdownsToMatch),
      );

      if (columnsIndex !== -1) {
        const columnsData = cols[columnsIndex];
        const colIndex = columnsData.columns.findIndex((col) => col.data_key === column.data_key);

        if (colIndex !== -1) {
          const newCols = [...columnsData.columns];
          newCols[colIndex] = { ...newCols[colIndex], selected: !newCols[colIndex].selected };

          const updatedCols = [...cols];
          updatedCols[columnsIndex] = { ...columnsData, columns: newCols };
          return updatedCols;
        }
      }
      return cols;
    });
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
   * Returns the dropdown array slice to use for matching column state.
   * For arrays with 2+ elements, returns first n-1 elements.
   * For arrays with 0-1 elements, returns the full array.
   */
  private getDropdownsForMatching(dropdowns: string[] | undefined): string[] {
    const normalized = dropdowns ?? [];
    if (normalized.length >= 2) {
      return normalized.slice(0, -1);
    }
    return normalized;
  }

  setSort(sortField: string | undefined, sortOrder: number | undefined) {
    this.sortFieldSignal.set(sortField);
    this.sortOrderSignal.set(sortOrder || this.DEFAULT_SORT_ORDER);
  }
}
