import { computed, Injectable, signal, Signal } from '@angular/core';
import {
  ComparisonToolColumn,
  ComparisonToolColumns,
  ComparisonToolConfig,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';

/**
 * Shared state contract for comparison tools.
 *
 * - Call {@link initialize} when configs load to seed dropdowns, reset counts, and wiki params.
 * - Read derived state via {@link configs}, {@link dropdownSelection}, {@link currentConfig}, and {@link columns}.
 * - Update user selections through {@link setDropdownSelection}; consumers can read the latest value via {@link dropdownSelection}.
 * - Manage result totals via {@link totalResultsCount} and {@link pinnedResultsCount}.
 */

@Injectable()
export class ComparisonToolService {
  private readonly DEFAULT_SORT_ORDER = -1;

  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly dropdownSelectionSignal = signal<string[]>([]);
  private readonly isLegendVisibleSignal = signal(false);
  private readonly selectorsWikiParamsSignal = signal<Record<string, SynapseWikiParams>>({});
  private readonly maxPinnedItemsSignal = signal<number>(50);
  private readonly pinnedItemsSignal = signal<Set<string>>(new Set());
  private readonly sortFieldSignal = signal<string | undefined>(undefined);
  private readonly sortOrderSignal = signal<number>(this.DEFAULT_SORT_ORDER);
  private readonly columnsForDropdownsSignal = signal<ComparisonToolColumns[]>([]);

  readonly configs = this.configsSignal.asReadonly();
  readonly dropdownSelection = this.dropdownSelectionSignal.asReadonly();
  readonly selectorsWikiParams = this.selectorsWikiParamsSignal.asReadonly();
  readonly isLegendVisible = this.isLegendVisibleSignal.asReadonly();
  readonly maxPinnedItems = this.maxPinnedItemsSignal.asReadonly();
  readonly pinnedItems = this.pinnedItemsSignal.asReadonly();
  readonly sortField = this.sortFieldSignal.asReadonly();
  readonly sortOrder = this.sortOrderSignal.asReadonly();

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

    // Match on dropdowns only (each service instance is page-specific)
    return (
      this.columnsForDropdownsSignal().find((c) => isEqual(c.dropdowns, config.dropdowns))
        ?.columns ?? []
    );
  });

  selectedColumns = computed(() => {
    return this.columns().filter((col) => col.selected);
  });

  hasHiddenColumns(): boolean {
    return this.columns().some((col) => !col.selected);
  }

  totalResultsCount = signal<number>(0);
  pinnedResultsCount = computed(() => this.pinnedItemsSignal().size);
  hasMaxPinnedItems = computed(() => {
    return this.pinnedResultsCount() >= this.maxPinnedItems();
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

  initialize(
    configs: ComparisonToolConfig[],
    selection?: string[],
    selectorsWikiParams: Record<string, SynapseWikiParams> = {},
  ) {
    this.configsSignal.set(configs ?? []);
    this.totalResultsCount.set(0);
    this.pinnedItemsSignal.set(new Set());
    this.setSelectorsWikiParams(selectorsWikiParams);
    this.setSort(undefined, this.DEFAULT_SORT_ORDER);

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

      this.columnsForDropdownsSignal.update((columnsData) => {
        // Match on dropdowns only (each service instance is page-specific)
        const existingIndex = columnsData.findIndex((c) => isEqual(c.dropdowns, currentDropdowns));

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

  setSelectorsWikiParams(params: Record<string, SynapseWikiParams>) {
    this.selectorsWikiParamsSignal.set(params ?? {});
  }

  isPinned(id: string): boolean {
    return this.pinnedItemsSignal().has(id);
  }

  togglePin(id: string) {
    this.pinnedItemsSignal.update((pinnedItems) => {
      const newSet = new Set(pinnedItems);
      if (newSet.has(id)) {
        newSet.delete(id);
      } else {
        newSet.add(id);
      }
      return newSet;
    });
  }

  pinItem(id: string) {
    this.pinnedItemsSignal.update((pinnedItems) => {
      const newSet = new Set(pinnedItems);
      newSet.add(id);
      return newSet;
    });
  }

  unpinItem(id: string) {
    this.pinnedItemsSignal.update((pinnedItems) => {
      const newSet = new Set(pinnedItems);
      newSet.delete(id);
      return newSet;
    });
  }

  setPinnedItems(items: string[]) {
    this.pinnedItemsSignal.set(new Set(items));
  }

  toggleColumn(column: ComparisonToolColumn) {
    const config = this.currentConfig();
    if (!config) return;

    const currentDropdowns = config.dropdowns;

    this.columnsForDropdownsSignal.update((cols) => {
      // Match on dropdowns only (each service instance is page-specific)
      const columnsIndex = cols.findIndex((c) => isEqual(c.dropdowns, currentDropdowns));

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

  private updateDropdownSelectionIfChanged(selection: string[]) {
    if (isEqual(this.dropdownSelectionSignal(), selection)) {
      return;
    }

    this.dropdownSelectionSignal.set(selection);
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

  setSort(sortField: string | undefined, sortOrder: number | undefined) {
    this.sortFieldSignal.set(sortField);
    this.sortOrderSignal.set(sortOrder || this.DEFAULT_SORT_ORDER);
  }
}
