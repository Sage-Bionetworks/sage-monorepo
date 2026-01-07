import { computed, DestroyRef, effect, inject, Injectable, signal, Signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolQuery,
  ComparisonToolUrlParams,
  ComparisonToolViewConfig,
  HeatmapDetailsPanelData,
  SortOrder,
} from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { SortMeta } from 'primeng/api';
import { TableLazyLoadEvent } from 'primeng/table';
import type { Observable } from 'rxjs';
import { combineLatest } from 'rxjs';
import { AppCookieService } from './app-cookie.service';
import { ComparisonToolCoordinatorService } from './comparison-tool-coordinator.service';
import { ComparisonToolHelperService } from './comparison-tool-helper.service';
import { ComparisonToolUrlService } from './comparison-tool-url.service';
import { NotificationService } from './notification.service';

/** Core state management service for comparison tool pages. */
@Injectable()
export class ComparisonToolService<T> {
  private readonly notificationService = inject(NotificationService);
  private readonly urlService = inject(ComparisonToolUrlService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly helperService = inject(ComparisonToolHelperService);
  private readonly coordinatorService = inject(ComparisonToolCoordinatorService);
  private readonly appCookieService = inject(AppCookieService);

  // Cache column selections only for dropdown selections up to this length
  // Currently, Gene Expression has 3 dropdowns, but we only want to cache selections
  // for the first 2 levels. Disease Correlation has 2 dropdowns, so all selections are cached.
  // If future tools have more dropdowns and different column selection caching requirements,
  // this depth value may need to be included in the ui_config instead, so the cutoff can be set per tool.
  private readonly COLUMN_CACHE_DEPTH = 2;
  private readonly DEFAULT_MULTI_SORT_META: SortMeta[] = [];
  readonly FIRST_PAGE_NUMBER = 0;
  private readonly DEFAULT_VIEW_CONFIG: ComparisonToolViewConfig = {
    selectorsWikiParams: {},
    headerTitle: '',
    filterResultsButtonTooltip: 'Filter results',
    showSignificanceControls: true,
    viewDetailsTooltip: 'View detailed results',
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    viewDetailsClick: (_id: string, _label: string) => {
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
    rowsPerPage: 10,
    rowIdDataKey: '_id',
    allowPinnedImageDownload: true,
  };

  // Private State Signals
  private readonly viewConfigSignal = signal<ComparisonToolViewConfig>(this.DEFAULT_VIEW_CONFIG);
  private readonly configsSignal = signal<ComparisonToolConfig[]>([]);
  private readonly isLegendVisibleSignal = signal(false);
  private readonly isVisualizationOverviewVisibleSignal = signal(
    !this.appCookieService.isVisualizationOverviewHidden(),
  );
  private readonly maxPinnedItemsSignal = signal<number>(50);
  private readonly columnsForDropdownsSignal = signal<Map<string, ComparisonToolColumn[]>>(
    new Map(),
  );
  private readonly unpinnedDataSignal = signal<T[]>([]);
  private readonly pinnedDataSignal = signal<T[]>([]);
  private readonly querySignal = signal<ComparisonToolQuery>({
    categories: [],
    pinnedItems: [],
    pageNumber: this.FIRST_PAGE_NUMBER,
    pageSize: 10,
    multiSortMeta: this.DEFAULT_MULTI_SORT_META,
    searchTerm: null,
    filters: [],
  });
  private readonly isInitializedSignal = signal(false);
  private readonly heatmapDetailsPanelDataSignal = signal<{
    data: HeatmapDetailsPanelData;
    event: Event;
  } | null>(null);

  // URL Sync State
  private lastSerializedState: string | null = null;
  private lastSyncedPins: string[] = [];
  private initialSelection: string[] | undefined;

  // Public Readonly Signals
  readonly viewConfig = this.viewConfigSignal.asReadonly();
  readonly configs = this.configsSignal.asReadonly();
  readonly isLegendVisible = this.isLegendVisibleSignal.asReadonly();
  readonly isVisualizationOverviewVisible = this.isVisualizationOverviewVisibleSignal.asReadonly();
  readonly maxPinnedItems = this.maxPinnedItemsSignal.asReadonly();
  readonly unpinnedData = this.unpinnedDataSignal.asReadonly();
  readonly pinnedData = this.pinnedDataSignal.asReadonly();
  readonly isInitialized = this.isInitializedSignal.asReadonly();
  readonly heatmapDetailsPanelData = this.heatmapDetailsPanelDataSignal.asReadonly();

  // Computed Query Accessors
  readonly query = this.querySignal.asReadonly();
  readonly dropdownSelection = computed(() => this.querySignal().categories);
  readonly pinnedItems = computed(() => new Set(this.querySignal().pinnedItems));
  readonly pageNumber = computed(() => this.querySignal().pageNumber);
  readonly pageSize = computed(() => this.querySignal().pageSize);
  readonly multiSortMeta = computed(() => this.querySignal().multiSortMeta);
  readonly searchTerm = computed(() => this.querySignal().searchTerm);
  readonly filters = computed(() => this.querySignal().filters);
  readonly first = computed(() => this.pageNumber() * this.pageSize());

  // pinnedItems cache may include more pins than are currently visible
  // Used for the URL serialization
  readonly visiblePinIds = computed(() => {
    const visiblePinnedData = this.pinnedData();
    const rowIdKey = this.viewConfig().rowIdDataKey;
    return visiblePinnedData.map((item: T) => {
      const id = (item as Record<string, unknown>)[rowIdKey];
      return String(id);
    });
  });

  constructor() {
    // Automatically sync state changes to URL.
    // This effect re-runs whenever any of the tracked signals change,
    // keeping the URL in sync with the component's state.
    effect(() => {
      const isInitialized = this.isInitialized();
      const isActive = this.coordinatorService.isActive(this);
      if (!isInitialized || !isActive) {
        return;
      }

      const dropdownSelection = this.dropdownSelection();
      const visiblePinIds = this.visiblePinIds();
      const multiSortMeta = this.multiSortMeta();

      const state = this.serializeSyncState({ dropdownSelection, visiblePinIds, multiSortMeta });
      this.syncStateToUrl(state);

      // Track what we synced so resolveUrlState can detect self-triggered URL changes
      this.lastSyncedPins = visiblePinIds;
    });
  }

  connect(options: {
    config$: Observable<ComparisonToolConfig[]>;
    queryParams$: Observable<ComparisonToolUrlParams>;
    initialSelection?: string[];
  }): void {
    this.coordinatorService.setActive(this);

    if (this.isInitialized()) {
      // Re-entering an already-initialized service (navigating back to this CT)
      // Restore this CT's cached state and sync to the URL, ignoring URL params from other CTs
      this.scheduleUrlSyncFromCurrentState();
      return;
    }

    this.initialSelection = options.initialSelection;

    combineLatest([options.config$, options.queryParams$])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(([configs, params]) => {
        if (!this.isInitializedSignal()) {
          this.initializeFromConfig(configs, params);
          return;
        }

        this.resolveUrlState(params, { isFirstLoad: false });
      });
  }

  disconnect(): void {
    if (this.coordinatorService.isActive(this)) {
      this.coordinatorService.setActive(null);
    }
  }

  readonly currentConfig: Signal<ComparisonToolConfig | null> = computed(() => {
    const configs = this.configsSignal();
    if (!configs.length) {
      return null;
    }

    // if no selection, return first config
    const dropdownSelection = this.dropdownSelection();
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

  private initializeFromConfig(
    configs: ComparisonToolConfig[],
    params: ComparisonToolUrlParams,
  ): void {
    this.configsSignal.set(configs ?? []);

    const selection = this.resolveInitialDropdownSelection(params, configs);
    const initialSort = this.resolveInitialSortMeta(params);

    this.updateQuery({
      categories: selection,
      multiSortMeta: initialSort,
    });

    // Initialize column preferences cache for all configs
    const columnsMap = new Map<string, ComparisonToolColumn[]>();
    for (const config of configs) {
      columnsMap.set(
        this.dropdownKey(config.dropdowns),
        this.applyColumnPreferences(config.columns),
      );
    }

    this.columnsForDropdownsSignal.set(columnsMap);
    this.initialSelection = undefined;

    this.resolveUrlState(params, { isFirstLoad: true });

    this.isInitializedSignal.set(true);
  }

  private resolveInitialDropdownSelection(
    params: ComparisonToolUrlParams,
    configs: ComparisonToolConfig[],
  ): string[] {
    const urlCategories = params.categories ?? undefined;
    const selectionSource = urlCategories ?? this.initialSelection ?? [];
    return this.normalizeSelection(selectionSource, configs);
  }

  private resolveInitialSortMeta(params: ComparisonToolUrlParams): SortMeta[] {
    if (params.sortFields && params.sortFields.length > 0) {
      return this.convertArraysToSortMeta(params.sortFields, params.sortOrders ?? []);
    }

    const defaultSort = this.viewConfigSignal().defaultSort;
    if (defaultSort && defaultSort.length > 0) {
      return defaultSort as SortMeta[];
    }

    return [];
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

  showHeatmapDetailsPanel(rowData: T, cellData: unknown, columnKey: string, event: Event): void {
    const transform = this.viewConfig().heatmapDetailsPanelDataTransform;
    if (!transform) {
      return;
    }

    const data = transform({
      rowData,
      cellData,
      columnKey,
    });

    if (!data) {
      return;
    }

    this.heatmapDetailsPanelDataSignal.set({ data, event });
  }

  hideHeatmapDetailsPanel(): void {
    this.heatmapDetailsPanelDataSignal.set(null);
  }

  setViewConfig(viewConfig: Partial<ComparisonToolViewConfig>) {
    this.viewConfigSignal.set({ ...this.DEFAULT_VIEW_CONFIG, ...viewConfig });
    this.setLegendVisibility(false);

    // If the user checked the option to hide the overview, do not auto-show it
    const isHiddenByUser = this.isVisualizationOverviewHiddenByUser();
    if (!isHiddenByUser) {
      this.setVisualizationOverviewVisibility(true);
    }
  }

  /**
   * Checks the global cookie to determine if the user has chosen to hide
   * the visualization overview panel across all comparison tools.
   */
  isVisualizationOverviewHiddenByUser(): boolean {
    return this.appCookieService.isVisualizationOverviewHidden();
  }

  setVisualizationOverviewHiddenByUser(hidden: boolean): void {
    this.appCookieService.setVisualizationOverviewHidden(hidden);
  }

  isPinned(id: string): boolean {
    return this.pinnedItems().has(id);
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
      return;
    }
    if (!this.isPinned(id)) {
      this.setPinnedItems([...this.visiblePinIds(), id]);
    }
  }

  unpinItem(id: string) {
    this.setPinnedItems(this.visiblePinIds().filter((item) => item !== id));
  }

  pinList(ids: string[]) {
    const visiblePins = new Set(this.visiblePinIds());
    let itemsAdded = 0;

    for (const id of ids) {
      if (visiblePins.size >= this.maxPinnedItems()) {
        const messagePrefix = itemsAdded === 0 ? 'No rows' : `Only ${itemsAdded} rows`;
        this.notificationService.showWarning(
          `${messagePrefix} were pinned, because you reached the maximum of ${this.maxPinnedItems()} pinned items.`,
        );
        break;
      }
      if (!visiblePins.has(id)) {
        visiblePins.add(id);
        itemsAdded++;
      }
    }

    this.setPinnedItems(Array.from(visiblePins));
  }

  setPinnedItems(items: string[] | null) {
    const deduplicatedItems = items ? Array.from(new Set(items)) : [];
    this.updateQuery({
      pinnedItems: deduplicatedItems,
    });
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

  updateQuery(query: Partial<ComparisonToolQuery>) {
    this.querySignal.update((current) => ({
      ...current,
      ...query,
    }));
  }

  handleLazyLoad(event: TableLazyLoadEvent) {
    const defaultRowsPerPage = this.viewConfigSignal().rowsPerPage;
    const { pageNumber, pageSize } = this.helperService.getPaginationParams(
      event,
      defaultRowsPerPage,
    );
    this.updateQuery({ pageNumber, pageSize });
  }

  private updateDropdownSelectionIfChanged(selection: string[]) {
    if (isEqual(this.dropdownSelection(), selection)) {
      return;
    }

    // Preserve current pins when changing dropdown selection
    const currentPins = this.query().pinnedItems;

    this.updateQuery({
      categories: selection,
      pinnedItems: currentPins,
      pageNumber: this.FIRST_PAGE_NUMBER,
    });
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
    if (normalized.length <= this.COLUMN_CACHE_DEPTH) {
      return JSON.stringify(normalized);
    }
    return JSON.stringify(normalized.slice(0, this.COLUMN_CACHE_DEPTH));
  }

  setSort(multiSortMeta: SortMeta[]) {
    const newSort = multiSortMeta || this.DEFAULT_MULTI_SORT_META;
    const currentSort = this.querySignal().multiSortMeta;

    // Early return if arrays are reference-equal
    if (currentSort === newSort || isEqual(currentSort, newSort)) {
      return;
    }

    // If clearing sort (empty array), apply default sort if configured
    if (newSort.length === 0) {
      const defaultSort = this.viewConfigSignal().defaultSort;
      if (defaultSort && defaultSort.length > 0) {
        this.updateQuery({
          pageNumber: this.FIRST_PAGE_NUMBER,
          multiSortMeta: defaultSort as SortMeta[],
        });
        return;
      }
    }

    // Deep clone to create new object references for Angular change detection
    // This ensures immutability and prevents external mutations from affecting our state
    const clonedSort = newSort.map((s) => ({ field: s.field, order: s.order }));

    // reset page to the first page when sort changes
    this.updateQuery({
      pageNumber: this.FIRST_PAGE_NUMBER,
      multiSortMeta: clonedSort,
    });
  }

  private resolveUrlState(
    params: ComparisonToolUrlParams,
    options: { isFirstLoad: boolean },
  ): void {
    // If this service is not active, ignore URL changes from other CTs
    if (!this.coordinatorService.isActive(this)) {
      return;
    }

    // Batch all query changes to avoid multiple updateQuery() calls
    const queryUpdates: Partial<ComparisonToolQuery> = {};

    // Categories are handled in initializeFromConfig for first load,
    // so only process category changes from URL on subsequent navigations
    if (!options.isFirstLoad && params.categories) {
      const normalizedSelection = this.normalizeSelection(params.categories, this.configsSignal());
      if (!isEqual(normalizedSelection, this.dropdownSelection())) {
        queryUpdates.categories = normalizedSelection;
        queryUpdates.pageNumber = this.FIRST_PAGE_NUMBER;
      }
    }

    if (params.sortFields?.length) {
      const sortMeta = this.convertArraysToSortMeta(params.sortFields, params.sortOrders ?? []);
      if (!isEqual(sortMeta, this.multiSortMeta())) {
        queryUpdates.multiSortMeta = sortMeta;
      }
    }

    // Pinned items logic:
    // - If URL pins match what we just synced, this is a self-triggered change - don't update query.pinnedItems
    // - If URL pins differ, this is an external change (user edited URL, link navigation) - accept new pins
    // - First load with no URL pins: start fresh with empty pins
    const urlPinnedItems = params.pinnedItems ?? [];
    const urlPinsMatchLastSync = isEqual(urlPinnedItems, this.lastSyncedPins);

    if (!urlPinsMatchLastSync) {
      // External URL change - update query.pinnedItems
      queryUpdates.pinnedItems =
        urlPinnedItems.length > 0 ? Array.from(new Set(urlPinnedItems)) : [];
    }
    // If urlPinsMatchLastSync, we skip updating query.pinnedItems to preserve the full cache

    // Apply all batched changes in a single update
    if (Object.keys(queryUpdates).length > 0) {
      this.updateQuery(queryUpdates);
    }

    if (!options.isFirstLoad) {
      this.updateSerializedStateCache();
    }
  }

  private syncStateToUrl(state: ComparisonToolUrlParams): void {
    const serializedState = JSON.stringify(state);
    if (this.lastSerializedState === serializedState) {
      return;
    }

    this.lastSerializedState = serializedState;
    this.urlService.syncToUrl(state);
  }

  private serializeSyncState(options: {
    dropdownSelection: string[];
    visiblePinIds: string[];
    multiSortMeta: SortMeta[];
  }): ComparisonToolUrlParams {
    const pinned = options.visiblePinIds;
    const { sortFields, sortOrders } = this.convertSortMetaToArrays(options.multiSortMeta);
    const defaultSort = this.viewConfigSignal().defaultSort;
    const isDefaultSort = defaultSort && isEqual(options.multiSortMeta, defaultSort as SortMeta[]);

    return {
      pinnedItems: pinned.length ? pinned : null,
      categories: options.dropdownSelection.length ? options.dropdownSelection : null,
      sortFields: !isDefaultSort && sortFields.length ? sortFields : null,
      sortOrders: !isDefaultSort && sortOrders.length ? sortOrders : null,
    };
  }

  private syncCurrentStateToUrl(): void {
    this.syncStateToUrl(
      this.serializeSyncState({
        dropdownSelection: this.dropdownSelection(),
        visiblePinIds: this.visiblePinIds(),
        multiSortMeta: this.multiSortMeta(),
      }),
    );
  }

  convertSortMetaToArrays(multiSortMeta: SortMeta[]): {
    sortFields: string[];
    sortOrders: SortOrder[];
  } {
    if (!multiSortMeta || multiSortMeta.length === 0) {
      return { sortFields: [], sortOrders: [] };
    }

    const sortFields: string[] = [];
    const sortOrders: SortOrder[] = [];

    for (const meta of multiSortMeta) {
      if (meta.field) {
        sortFields.push(meta.field);
        sortOrders.push((meta.order ?? 1) as SortOrder);
      }
    }

    return { sortFields, sortOrders };
  }

  private convertArraysToSortMeta(sortFields: string[], sortOrders: SortOrder[]): SortMeta[] {
    return sortFields.map((field, index) => ({
      field,
      order: sortOrders[index] ?? 1,
    }));
  }

  private updateSerializedStateCache(): void {
    this.lastSerializedState = JSON.stringify(
      this.serializeSyncState({
        visiblePinIds: this.visiblePinIds(),
        dropdownSelection: this.dropdownSelection(),
        multiSortMeta: this.multiSortMeta(),
      }),
    );
  }

  /**
   * Schedules URL sync to run after the current navigation completes.
   *
   * This is needed when re-entering an already-initialized comparison tool.
   * The router navigation that triggered connect() must complete before we
   * can update the URL, otherwise our changes would be overwritten.
   */
  private scheduleUrlSyncFromCurrentState(): void {
    queueMicrotask(() => {
      if (!this.coordinatorService.isActive(this)) {
        return;
      }
      this.lastSerializedState = null;
      this.syncCurrentStateToUrl();
    });
  }
}
