import { computed, DestroyRef, effect, inject, Injectable, signal, Signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolUrlParams,
  ComparisonToolViewConfig,
} from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { SortEvent, SortMeta } from 'primeng/api';
import type { Observable } from 'rxjs';
import { combineLatest, startWith } from 'rxjs';
import { ComparisonToolUrlService } from './comparison-tool-url.service';
import { NotificationService } from './notification.service';

/**
 * Shared state contract for comparison tools.
 */
@Injectable()
export class ComparisonToolService<T> {
  private static readonly routePinnedCache = new Map<string, string[]>();

  private readonly notificationService = inject(NotificationService);
  private readonly urlService = inject(ComparisonToolUrlService);
  private readonly destroyRef = inject(DestroyRef);

  // Cache column selections only for dropdown selections up to this length
  // Currently, Gene Expression has 3 dropdowns, but we only want to cache selections
  // for the first 2 levels. Disease Correlation has 2 dropdowns, so all selections are cached.
  // If future tools have more dropdowns and different column selection caching requirements,
  // this cutoff may need to be included in the ui_config instead, so the cutoff can be set per tool.
  private readonly DEFAULT_DROPDOWNS_COLUMN_SELECTION_CACHE_CUTOFF_LEVEL = 2;
  private readonly DEFAULT_MULTI_SORT_META: SortMeta[] = [];
  private readonly DEFAULT_VIEW_CONFIG: ComparisonToolViewConfig = {
    selectorsWikiParams: {},
    headerTitle: '',
    filterResultsButtonTooltip: 'Filter results',
    showSignificanceControls: true,
    viewDetailsTooltip: 'View detailed results',
    viewDetailsClick: (id: string, label: string) => {
      return;
    },
    legendEnabled: true,
    legendPanelConfig: {
      colorChartLowerLabel: '',
      colorChartUpperLabel: '',
      colorChartText: '',
      sizeChartLowerLabel: '',
      sizeChartUpperLabel: '',
      sizeChartText: '',
    },
    visualizationOverviewPanes: [],
  };

  private readonly viewConfigSignal = signal<ComparisonToolViewConfig>(this.DEFAULT_VIEW_CONFIG);
  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly dropdownSelectionSignal = signal<string[]>([]);
  private readonly isLegendVisibleSignal = signal(false);
  private readonly isVisualizationOverviewVisibleSignal = signal(true);
  private readonly isVisualizationOverviewHiddenByUserSignal = signal(false);
  private readonly maxPinnedItemsSignal = signal<number>(50);
  private readonly pinnedItemsSignal = signal<Set<string>>(new Set());
  private readonly multiSortMetaSignal = signal<SortMeta[]>(this.DEFAULT_MULTI_SORT_META);
  private readonly columnsForDropdownsSignal = signal<Map<string, ComparisonToolColumn[]>>(
    new Map(),
  );
  private readonly pinnedItemsForDropdownsSignal = signal<Map<string, Set<string>>>(new Map());
  private readonly unpinnedDataSignal = signal<T[]>([]);
  private readonly pinnedDataSignal = signal<T[]>([]);

  readonly viewConfig = this.viewConfigSignal.asReadonly();
  readonly configs = this.configsSignal.asReadonly();
  readonly dropdownSelection = this.dropdownSelectionSignal.asReadonly();
  readonly isLegendVisible = this.isLegendVisibleSignal.asReadonly();
  readonly isVisualizationOverviewVisible = this.isVisualizationOverviewVisibleSignal.asReadonly();
  readonly isVisualizationOverviewHiddenByUser =
    this.isVisualizationOverviewHiddenByUserSignal.asReadonly();
  readonly maxPinnedItems = this.maxPinnedItemsSignal.asReadonly();
  readonly pinnedItems = this.pinnedItemsSignal.asReadonly();
  readonly multiSortMeta = this.multiSortMetaSignal.asReadonly();
  readonly unpinnedData = this.unpinnedDataSignal.asReadonly();
  readonly pinnedData = this.pinnedDataSignal.asReadonly();

  private readonly syncToUrlInProgress = signal(false);
  private readonly isInitializedSignal = signal(false);
  readonly isInitialized = this.isInitializedSignal.asReadonly();
  private readonly hasBootstrappedSignal = signal(false);
  private lastSerializedState: string | null = null;
  private hasInitializedConfig = false;
  private connectActivated = false;
  private initialSelection: string[] | undefined;
  private cacheKey: string | undefined;
  private initialPinsResolved = false;

  constructor() {
    effect(() => {
      const hasBootstrapped = this.hasBootstrappedSignal();
      if (!hasBootstrapped) {
        return;
      }

      const isInitialized = this.isInitialized();
      const syncingToUrl = this.syncToUrlInProgress();
      if (!isInitialized || syncingToUrl) {
        return;
      }

      const pinnedItems = this.pinnedItems();
      const state = this.serializeState(pinnedItems);

      this.cacheRoutePinnedItems(pinnedItems);
      this.syncStateToUrl(state);
    });
  }

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

    const savedColumns = this.columnsForDropdownsSignal().get(this.dropdownKey(config.dropdowns));

    return this.applyColumnPreferences(configColumns, savedColumns);
  });

  selectedColumns = computed(() => {
    return this.columns().filter((col) => col.selected);
  });

  hasUnselectedColumns(): boolean {
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

  connect(options: {
    config$: Observable<ComparisonToolConfig[]>;
    queryParams$: Observable<ComparisonToolUrlParams>;
    cacheKey?: string;
    initialSelection?: string[];
  }): void {
    if (this.connectActivated) {
      return;
    }

    this.connectActivated = true;
    this.cacheKey = options.cacheKey;
    this.initialSelection = options.initialSelection;

    this.destroyRef.onDestroy(() => {
      this.handleRouteExit();
    });

    const queryParams$ = options.queryParams$.pipe(startWith({} as ComparisonToolUrlParams));

    combineLatest([options.config$, queryParams$])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(([configs, params]) => {
        if (!this.hasBootstrappedSignal()) {
          this.bootstrapFromConfig(configs, params);
          return;
        }

        this.resolvePinnedState(params, { isInitial: false });
      });
  }
  setDropdownSelection(selection: string[]) {
    const configs = this.configsSignal();
    if (!configs.length) {
      this.updateDropdownSelectionIfChanged(selection ?? []);
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection, configs);
    this.updateDropdownSelectionIfChanged(normalizedSelection);

    const config = this.currentConfig();
    if (config?.columns && config.columns.length > 0) {
      const key = this.dropdownKey(config.dropdowns);

      this.columnsForDropdownsSignal.update((columnsMap) => {
        const next = new Map(columnsMap);
        const savedColumns = columnsMap.get(key);

        next.set(key, this.applyColumnPreferences(config.columns, savedColumns));

        return next;
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

  setVisualizationOverviewHiddenByUser(hidden: boolean) {
    this.isVisualizationOverviewHiddenByUserSignal.set(hidden);
  }

  setViewConfig(viewConfig: Partial<ComparisonToolViewConfig>) {
    this.viewConfigSignal.set({ ...this.DEFAULT_VIEW_CONFIG, ...viewConfig });
    this.setLegendVisibility(false);

    // If the user checked the option to hide the overview, do not auto-show it
    if (!this.isVisualizationOverviewHiddenByUserSignal()) {
      this.setVisualizationOverviewVisibility(true);
    }
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

  setPinnedItems(items: string[] | null) {
    this.pinnedItemsSignal.set(new Set(items ?? []));
  }

  resetPinnedItems() {
    this.setPinnedItems([]);
  }

  toggleColumn(column: ComparisonToolColumn) {
    const config = this.currentConfig();
    if (!config) return;

    this.columnsForDropdownsSignal.update((cols) => {
      const key = this.dropdownKey(config.dropdowns);
      const columns = cols.get(key);
      if (!columns) {
        return cols;
      }

      const targetIndex = columns.findIndex((col) => col.data_key === column.data_key);
      if (targetIndex === -1) {
        return cols;
      }

      const updatedColumns = [...columns];
      updatedColumns[targetIndex] = {
        ...updatedColumns[targetIndex],
        selected: !updatedColumns[targetIndex].selected,
      };

      const next = new Map(cols);
      next.set(key, updatedColumns);
      return next;
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

    // Save current pinned items before switching
    const previousSelection = this.dropdownSelectionSignal();
    const previousKey = this.dropdownKey(previousSelection);
    const currentPinnedItems = this.pinnedItemsSignal();

    this.pinnedItemsForDropdownsSignal.update((cache) => {
      const next = new Map(cache);
      next.set(previousKey, new Set(currentPinnedItems));
      return next;
    });

    // Switch to new selection
    this.dropdownSelectionSignal.set(selection);

    // Restore pinned items for new selection
    const newKey = this.dropdownKey(selection);
    const cachedPinnedItems = this.pinnedItemsForDropdownsSignal().get(newKey);

    if (cachedPinnedItems) {
      this.pinnedItemsSignal.set(new Set(cachedPinnedItems));
    } else {
      this.pinnedItemsSignal.set(new Set());
    }
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
    const visibleColumns = configColumns.filter((column) => !column.is_hidden);

    if (!savedColumns?.length) {
      return visibleColumns.map((column) => ({ ...column, selected: true }));
    }

    const selectionMap = new Map(savedColumns.map((col) => [col.data_key, col.selected]));

    return visibleColumns.map((column) => ({
      ...column,
      selected: selectionMap.get(column.data_key) ?? true,
    }));
  }

  private dropdownKey(dropdowns: string[] | undefined): string {
    const normalized = dropdowns ?? [];
    if (normalized.length <= this.DEFAULT_DROPDOWNS_COLUMN_SELECTION_CACHE_CUTOFF_LEVEL) {
      return JSON.stringify(normalized);
    }
    return JSON.stringify(
      normalized.slice(0, this.DEFAULT_DROPDOWNS_COLUMN_SELECTION_CACHE_CUTOFF_LEVEL),
    );
  }

  setSort(event: SortEvent) {
    this.multiSortMetaSignal.set(event.multiSortMeta || this.DEFAULT_MULTI_SORT_META);
  }

  private shouldUpdateFromUrl<T>(currentValue: T, newValue: T): boolean {
    const isFirstSync = !this.isInitialized();
    const hasChanged = !isEqual(currentValue, newValue);
    return isFirstSync || hasChanged;
  }

  private bootstrapFromConfig(
    configs: ComparisonToolConfig[],
    params: ComparisonToolUrlParams,
  ): void {
    this.configsSignal.set(configs ?? []);
    this.totalResultsCount.set(0);

    if (this.hasInitializedConfig) {
      this.resetPinnedItems();
    }

    this.multiSortMetaSignal.set(this.DEFAULT_MULTI_SORT_META);
    this.setUnpinnedData([]);
    this.setPinnedData([]);

    const initialSelection = this.initialSelection ?? [];
    const normalizedSelection = this.normalizeSelection(initialSelection, configs);
    this.dropdownSelectionSignal.set(normalizedSelection);

    const columnsMap = new Map<string, ComparisonToolColumn[]>();
    for (const config of configs) {
      columnsMap.set(
        this.dropdownKey(config.dropdowns),
        this.applyColumnPreferences(config.columns),
      );
    }

    this.columnsForDropdownsSignal.set(columnsMap);
    this.pinnedItemsForDropdownsSignal.set(new Map());
    this.hasInitializedConfig = true;
    this.initialSelection = undefined;

    this.resolvePinnedState(params, { isInitial: true });

    this.isInitializedSignal.set(true);
    this.hasBootstrappedSignal.set(true);
  }

  private resolvePinnedState(
    params: ComparisonToolUrlParams,
    options: { isInitial: boolean },
  ): void {
    this.syncToUrlInProgress.set(true);

    const urlPinnedItems = params.pinnedItems ?? undefined;
    const hasUrlPins = Array.isArray(urlPinnedItems) && urlPinnedItems.length > 0;

    if (hasUrlPins) {
      this.setPinnedItems(urlPinnedItems);
      this.updateSerializedStateCacheFromPins();
      this.initialPinsResolved = true;
      this.syncToUrlInProgress.set(false);
      return;
    }

    if (options.isInitial && this.hasRouteCachedPins()) {
      const cachedPins = this.getRouteCachedPins();
      this.setPinnedItems(cachedPins);
      this.syncToUrlInProgress.set(false);

      this.cacheRoutePinnedItems(this.pinnedItems());
      this.syncStateToUrlFromCurrentPins();

      this.initialPinsResolved = true;
      return;
    }

    if (options.isInitial && !this.initialPinsResolved) {
      this.resetPinnedItems();
      this.syncToUrlInProgress.set(false);

      this.cacheRoutePinnedItems(this.pinnedItems());
      this.syncStateToUrlFromCurrentPins();

      this.initialPinsResolved = true;
      return;
    }

    this.updateSerializedStateCacheFromPins();
    this.syncToUrlInProgress.set(false);
  }

  private syncStateToUrl(state: ComparisonToolUrlParams): void {
    const serializedState = JSON.stringify(state);
    if (this.lastSerializedState === serializedState) {
      return;
    }

    this.lastSerializedState = serializedState;
    this.urlService.syncToUrl(state);
  }

  private serializeState(pinnedItems: Set<string>): ComparisonToolUrlParams {
    const pinned = Array.from(pinnedItems);
    return {
      pinnedItems: pinned.length ? pinned : null,
    };
  }

  private hasRouteCachedPins(): boolean {
    if (!this.cacheKey) {
      return false;
    }

    return ComparisonToolService.routePinnedCache.has(this.cacheKey);
  }

  private getRouteCachedPins(): string[] {
    if (!this.cacheKey) {
      return [];
    }

    const cached = ComparisonToolService.routePinnedCache.get(this.cacheKey) ?? [];
    return [...cached];
  }

  private cacheRoutePinnedItems(pinnedItems: Set<string>): void {
    if (!this.cacheKey) {
      return;
    }

    ComparisonToolService.routePinnedCache.set(this.cacheKey, Array.from(pinnedItems));
  }

  private syncStateToUrlFromCurrentPins(): void {
    this.syncStateToUrl(this.serializeState(this.pinnedItems()));
  }

  private updateSerializedStateCacheFromPins(): void {
    this.lastSerializedState = JSON.stringify(this.serializeState(this.pinnedItems()));
  }

  private handleRouteExit(): void {
    if (!this.cacheKey) {
      return;
    }

    const clearedState = { pinnedItems: null } satisfies ComparisonToolUrlParams;
    this.urlService.clearPinnedParam();
    this.lastSerializedState = JSON.stringify(clearedState);
  }
}
