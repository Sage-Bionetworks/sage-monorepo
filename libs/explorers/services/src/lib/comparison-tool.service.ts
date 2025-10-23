import { computed, signal, Signal } from '@angular/core';
import {
  ComparisonToolConfig,
  ComparisonToolColumn,
  SynapseWikiParams,
  ComparisonToolColumns,
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
export class ComparisonToolService {
  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly dropdownSelectionSignal = signal<string[]>([]);
  private readonly isLegendVisibleSignal = signal(false);
  private readonly selectorsWikiParamsSignal = signal<Record<string, SynapseWikiParams>>({});
  private readonly columnsForAllPagesSignal = signal<ComparisonToolColumns[]>([]);

  readonly configs = this.configsSignal.asReadonly();
  readonly dropdownSelection = this.dropdownSelectionSignal.asReadonly();
  readonly selectorsWikiParams = this.selectorsWikiParamsSignal.asReadonly();
  readonly isLegendVisible = this.isLegendVisibleSignal.asReadonly();

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

    // Match on BOTH page AND dropdowns
    return (
      this.columnsForAllPagesSignal().find(
        (c) => c.page === config.page && isEqual(c.dropdowns, config.dropdowns),
      )?.columns ?? []
    );
  });

  hasHiddenColumns(): boolean {
    return this.columns().some((col) => !col.selected);
  }

  totalResultsCount = signal<number>(0);
  pinnedResultsCount = signal<number>(0);

  setLegendVisibility(visible: boolean) {
    this.isLegendVisibleSignal.set(visible);
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
    this.pinnedResultsCount.set(0);
    this.setSelectorsWikiParams(selectorsWikiParams);

    if (!configs?.length) {
      this.updateDropdownSelectionIfChanged([]);
      this.columnsForAllPagesSignal.set([]);
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection ?? [], configs);
    this.updateDropdownSelectionIfChanged(normalizedSelection);

    const columnsData: ComparisonToolColumns[] = this.configs().map((config) => ({
      page: config.page,
      dropdowns: config.dropdowns,
      columns: config.columns.map((column) => ({
        name: column.name,
        selected: true,
      })),
    }));
    this.columnsForAllPagesSignal.set(columnsData);
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
      const currentPage = config.page;
      const currentDropdowns = config.dropdowns;

      this.columnsForAllPagesSignal.update((columnsData) => {
        // Match on BOTH page AND dropdowns
        const existingIndex = columnsData.findIndex(
          (c) => c.page === currentPage && isEqual(c.dropdowns, currentDropdowns),
        );

        // If this dropdown combination exists, preserve its column state
        if (existingIndex !== -1) {
          return columnsData; // State already exists, don't modify
        }

        // New dropdown combination - initialize with all columns selected
        const newPageData: ComparisonToolColumns = {
          page: currentPage,
          dropdowns: currentDropdowns,
          columns: config.columns.map((column) => ({
            name: column.name,
            selected: true,
          })),
        };

        return [...columnsData, newPageData];
      });
    }
  }

  setSelectorsWikiParams(params: Record<string, SynapseWikiParams>) {
    this.selectorsWikiParamsSignal.set(params ?? {});
  }

  toggleColumn(column: ComparisonToolColumn) {
    const config = this.currentConfig();
    if (!config) return;

    const currentPage = config.page;
    const currentDropdowns = config.dropdowns;

    this.columnsForAllPagesSignal.update((cols) => {
      // Match on BOTH page AND dropdowns
      const pageColumnsIndex = cols.findIndex(
        (c) => c.page === currentPage && isEqual(c.dropdowns, currentDropdowns),
      );

      if (pageColumnsIndex !== -1) {
        const pageColumns = cols[pageColumnsIndex];
        const colIndex = pageColumns.columns.findIndex((col) => col.name === column.name);

        if (colIndex !== -1) {
          const newCols = [...pageColumns.columns];
          newCols[colIndex] = { ...newCols[colIndex], selected: !newCols[colIndex].selected };

          const updatedCols = [...cols];
          updatedCols[pageColumnsIndex] = { ...pageColumns, columns: newCols };
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
}
