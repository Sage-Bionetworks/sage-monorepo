import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MAX_PIN_LIMIT } from '@sagebionetworks/explorers/constants';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolQuery,
  ComparisonToolUrlParams,
} from '@sagebionetworks/explorers/models';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { MessageService } from 'primeng/api';
import { BehaviorSubject, EMPTY, of, Subject, throwError } from 'rxjs';
import {
  ComparisonToolService,
  DEFAULT_COLUMN_WIDTH_PX,
  PinAllFetch,
} from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';
import { LoggerService } from './logger.service';
import { ToastNotificationService } from './toast-notification.service';

type Row = Record<string, unknown>;

describe('ComparisonToolService', () => {
  let service: ComparisonToolService<Row>;
  let mockRouter: Partial<Router>;
  let mockActivatedRoute: Partial<ActivatedRoute>;
  let queryParamsSubject: BehaviorSubject<any>;
  let paramsSubject: BehaviorSubject<ComparisonToolUrlParams>;

  beforeEach(() => {
    queryParamsSubject = new BehaviorSubject<any>({});

    mockRouter = {
      navigate: jest.fn().mockImplementation((_, options) => {
        if (mockActivatedRoute.snapshot && options?.queryParams) {
          Object.assign(mockActivatedRoute.snapshot.queryParams, options.queryParams);
        }
      }),
    };

    mockActivatedRoute = {
      queryParams: queryParamsSubject.asObservable(),
      snapshot: {
        queryParams: {},
      } as any,
    };

    TestBed.configureTestingModule({
      providers: [
        MessageService,
        ...provideComparisonToolService({ urlSync: true }),
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  // Inject inside each test so fakeAsync zones include the service's timer setup
  const injectService = () => {
    service = TestBed.inject(ComparisonToolService);
    return service;
  };

  const noMatchingRows: PinAllFetch<Row> = () => of({ rows: [], totalElements: 0 });

  const connectService = (
    configs: ComparisonToolConfig[] = mockComparisonToolDataConfig,
    options: {
      selection?: string[];
      initialParams?: ComparisonToolUrlParams;
      pinAllFetch?: PinAllFetch<Row>;
    } = {},
  ) => {
    paramsSubject = new BehaviorSubject<ComparisonToolUrlParams>(options.initialParams ?? {});
    injectService().connect({
      config$: of(configs),
      queryParams$: paramsSubject.asObservable(),
      pinAllFetch: options.pinAllFetch ?? noMatchingRows,
      initialSelection: options.selection,
    });
  };

  it('should create', () => {
    injectService();
    expect(service).toBeDefined();
  });

  it('excludes hidden columns from columns and cached dropdown columns', () => {
    connectService();

    const columns = service.columns();
    expect(columns.every((column) => !column.is_hidden)).toBe(true);
    expect(columns.find((column) => column.data_key === 'available_data')).toBeUndefined();

    const columnsForDropdowns: Map<string, ComparisonToolColumn[]> = (
      service as any
    ).columnsForDropdownsSignal();
    const cachedColumns = columnsForDropdowns.get(JSON.stringify([]));

    expect(cachedColumns).toBeDefined();
    expect(cachedColumns?.find((column) => column.data_key === 'available_data')).toBeUndefined();
    expect(cachedColumns?.every((column) => column.selected)).toBe(true);
  });

  describe('column_width sanitization', () => {
    const configWithWidths: ComparisonToolConfig[] = [
      {
        page: 'Mouse Model Overview',
        dropdowns: [],
        row_count: null,
        columns: [
          { type: 'primary', data_key: 'name', is_exported: true, is_hidden: false },
          {
            type: 'text',
            data_key: 'good',
            column_width: 200,
            is_exported: true,
            is_hidden: false,
          },
          { type: 'text', data_key: 'bad', column_width: -50, is_exported: true, is_hidden: false },
        ],
        filters: [],
      },
    ];

    it('normalizes a non-positive column_width to the default and warns once', () => {
      const warnSpy = jest
        .spyOn(TestBed.inject(LoggerService), 'warn')
        .mockImplementation(() => undefined);

      connectService(configWithWidths);

      const columns = service.columns();
      expect(columns.find((column) => column.data_key === 'good')?.column_width).toBe(200);
      expect(columns.find((column) => column.data_key === 'bad')?.column_width).toBe(
        DEFAULT_COLUMN_WIDTH_PX,
      );
      expect(warnSpy).toHaveBeenCalledTimes(1);
      expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('Invalid column_width'), {
        dataKey: 'bad',
        columnWidth: -50,
      });
    });

    it('leaves a positive column_width untouched without warning', () => {
      const warnSpy = jest
        .spyOn(TestBed.inject(LoggerService), 'warn')
        .mockImplementation(() => undefined);

      connectService(configWithWidths);

      expect(service.columns().find((column) => column.data_key === 'good')?.column_width).toBe(
        200,
      );
      expect(warnSpy).not.toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ dataKey: 'good' }),
      );
    });
  });

  describe('row and parent id keys', () => {
    const VIEW_CONFIG_ROW_ID_DATA_KEY = '_id';
    const UI_CONFIG_ROW_ID_DATA_KEY = 'composite_id';
    const UI_CONFIG_PARENT_ID_DATA_KEY = 'rna_composite_id';

    const connectWithDataKeys = (keys: Partial<ComparisonToolConfig>) => {
      connectService([{ ...mockComparisonToolDataConfig[0], ...keys }]);
      service.setViewConfig({ rowIdDataKey: VIEW_CONFIG_ROW_ID_DATA_KEY });
    };

    it.each([
      ['no keys', {}],
      ['null keys', { row_id_data_key: null, parent_id_data_key: null }],
      ['undefined keys', { row_id_data_key: undefined, parent_id_data_key: undefined }],
    ])(
      'falls back to the view config row key and no parent key when the ui config has %s',
      (_, keys) => {
        connectWithDataKeys(keys);

        expect(service.rowIdDataKey()).toBe(VIEW_CONFIG_ROW_ID_DATA_KEY);
        expect(service.parentIdDataKey()).toBeNull();
      },
    );

    it('prefers the ui config keys over the view config when set', () => {
      expect(UI_CONFIG_ROW_ID_DATA_KEY).not.toBe(VIEW_CONFIG_ROW_ID_DATA_KEY);

      connectWithDataKeys({
        row_id_data_key: UI_CONFIG_ROW_ID_DATA_KEY,
        parent_id_data_key: UI_CONFIG_PARENT_ID_DATA_KEY,
      });

      expect(service.rowIdDataKey()).toBe(UI_CONFIG_ROW_ID_DATA_KEY);
      expect(service.parentIdDataKey()).toBe(UI_CONFIG_PARENT_ID_DATA_KEY);
    });

    const row = {
      [VIEW_CONFIG_ROW_ID_DATA_KEY]: 'view-config-id',
      [UI_CONFIG_ROW_ID_DATA_KEY]: 'ui-config-id',
    };

    it('reads rowId from the view config row key when the ui config has none', () => {
      connectWithDataKeys({});
      expect(service.rowId(row)).toBe('view-config-id');
    });

    it('reads rowId from the ui config row key when set', () => {
      connectWithDataKeys({ row_id_data_key: UI_CONFIG_ROW_ID_DATA_KEY });
      expect(service.rowId(row)).toBe('ui-config-id');
    });
  });

  describe('pinned items cache identity and pinned parents', () => {
    // Mirrors the Model-AD DE CT: the parent view is RNA and the child view is Protein.
    const UI_CONFIG_ROW_ID_DATA_KEY = 'id';
    const UI_CONFIG_PARENT_VIEW_PARENT_ID_DATA_KEY = 'id';
    const UI_CONFIG_CHILD_VIEW_PARENT_ID_DATA_KEY = 'parent_id';

    const PARENT_VIEW = ['Parent', 'A'];
    const OTHER_PARENT_VIEW = ['Parent', 'B'];
    const CHILD_VIEW = ['Child', 'A'];

    const PARENT_VIEW_IDENTITY = {
      rowIdDataKey: UI_CONFIG_ROW_ID_DATA_KEY,
      parentIdDataKey: UI_CONFIG_PARENT_VIEW_PARENT_ID_DATA_KEY,
    };
    const CHILD_VIEW_IDENTITY = {
      rowIdDataKey: UI_CONFIG_ROW_ID_DATA_KEY,
      parentIdDataKey: UI_CONFIG_CHILD_VIEW_PARENT_ID_DATA_KEY,
    };

    const parentViewConfig = (dropdowns: string[]): ComparisonToolConfig => ({
      ...mockComparisonToolDataConfig[0],
      dropdowns,
      row_id_data_key: UI_CONFIG_ROW_ID_DATA_KEY,
      parent_id_data_key: UI_CONFIG_PARENT_VIEW_PARENT_ID_DATA_KEY,
    });
    const viewConfigs: ComparisonToolConfig[] = [
      parentViewConfig(PARENT_VIEW),
      parentViewConfig(OTHER_PARENT_VIEW),
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: CHILD_VIEW,
        row_id_data_key: UI_CONFIG_ROW_ID_DATA_KEY,
        parent_id_data_key: UI_CONFIG_CHILD_VIEW_PARENT_ID_DATA_KEY,
      },
    ];

    const parentRow = (id: string): Row => ({ [UI_CONFIG_ROW_ID_DATA_KEY]: id });
    const childRow = (id: string, parentId: string): Row => ({
      [UI_CONFIG_ROW_ID_DATA_KEY]: id,
      [UI_CONFIG_CHILD_VIEW_PARENT_ID_DATA_KEY]: parentId,
    });
    const landPinned = (...rows: Row[]) =>
      service.fetchPinned(of({ data: rows, totalCount: rows.length }));
    const recordedIdentity = () => (service as any).pinnedItemsIdentitySignal();

    const pinInParentView = (...ids: string[]) => {
      service.setPinnedItems(ids);
      landPinned(...ids.map(parentRow));
    };

    it('tells the parent and child views apart only by the parent key', () => {
      expect(PARENT_VIEW_IDENTITY.rowIdDataKey).toBe(CHILD_VIEW_IDENTITY.rowIdDataKey);
      expect(PARENT_VIEW_IDENTITY.parentIdDataKey).not.toBe(CHILD_VIEW_IDENTITY.parentIdDataKey);
    });

    describe('identity', () => {
      it('records the active pair on a pin edit', () => {
        connectService(viewConfigs, { selection: CHILD_VIEW });

        service.pinItem('child1a');

        expect(recordedIdentity()).toEqual(CHILD_VIEW_IDENTITY);
      });

      it('records the pair of the view a URL change selects', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });

        paramsSubject.next({ categories: CHILD_VIEW, pinnedItems: ['child1a'] });

        expect(service.dropdownSelection()).toEqual(CHILD_VIEW);
        expect(recordedIdentity()).toEqual(CHILD_VIEW_IDENTITY);
      });

      it('records the active pair on a pin-all', () => {
        const pinAllFetch: PinAllFetch<Row> = () =>
          of({ rows: [childRow('child1a', 'parent1')], totalElements: 1 });
        connectService(viewConfigs, { selection: CHILD_VIEW, pinAllFetch });

        service.pinAll();

        expect(service.pinnedItems()).toEqual(['child1a']);
        expect(recordedIdentity()).toEqual(CHILD_VIEW_IDENTITY);
      });

      it('leaves the recorded pair alone on a dropdown change', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1');

        service.setDropdownSelection(CHILD_VIEW);

        expect(recordedIdentity()).toEqual(PARENT_VIEW_IDENTITY);
      });

      it('records the URL view pair before initialization completes', () => {
        const isInitializedSignal = (injectService() as any).isInitializedSignal;
        const setInitialized = isInitializedSignal.set.bind(isInitializedSignal);
        let identityWhenInitialized: unknown;
        jest.spyOn(isInitializedSignal, 'set').mockImplementation((value) => {
          identityWhenInitialized = recordedIdentity();
          setInitialized(value);
        });

        connectService(viewConfigs, {
          initialParams: { categories: CHILD_VIEW, pinnedItems: ['child1a'] },
        });

        expect(identityWhenInitialized).toEqual(CHILD_VIEW_IDENTITY);
      });
    });

    describe('pinnedItemsQuery', () => {
      it('sends empty items in row space with no pins and no URL', () => {
        connectService(viewConfigs, { selection: CHILD_VIEW });

        expect(recordedIdentity()).toBeNull();
        expect(service.pinnedItemsQuery()).toEqual({ items: [], itemIdSpace: 'row' });
      });

      it('keeps the cache in row space while the recorded view is active', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1', 'parent2');

        expect(service.pinnedItemsQuery()).toEqual({
          items: ['parent1', 'parent2'],
          itemIdSpace: 'row',
        });
      });

      it('keeps the cache in row space across a dropdown change to another view with the same identity', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1', 'parent2');

        service.setDropdownSelection(OTHER_PARENT_VIEW);

        expect(service.pinnedItemsQuery()).toEqual({
          items: ['parent1', 'parent2'],
          itemIdSpace: 'row',
        });
      });

      it('sends the pinned parents in parent space after switching from the parent view to the child view', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1', 'parent2');

        service.setDropdownSelection(CHILD_VIEW);

        expect(service.pinnedItemsQuery()).toEqual({
          items: ['parent1', 'parent2'],
          itemIdSpace: 'parent',
        });
      });

      it('does not change when the child rows land with the same parents in another order', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1', 'parent2');
        service.setDropdownSelection(CHILD_VIEW);
        const queryBeforeLanding = service.pinnedItemsQuery();

        landPinned(
          childRow('child2a', 'parent2'),
          childRow('child1a', 'parent1'),
          childRow('child1b', 'parent1'),
        );

        expect(service.pinnedItemsQuery()).toBe(queryBeforeLanding);
      });

      it('re-records the child view pair on a pin edit in the child view', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1', 'parent2');
        service.setDropdownSelection(CHILD_VIEW);
        landPinned(
          childRow('child1a', 'parent1'),
          childRow('child1b', 'parent1'),
          childRow('child2a', 'parent2'),
        );

        service.unpinItem('child1b');

        expect(recordedIdentity()).toEqual(CHILD_VIEW_IDENTITY);
        expect(service.pinnedItemsQuery()).toEqual({
          items: ['child1a', 'child2a'],
          itemIdSpace: 'row',
        });
      });

      it('sends URL pins in the row space of the active view', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1');
        service.setDropdownSelection(CHILD_VIEW);

        paramsSubject.next({ categories: CHILD_VIEW, pinnedItems: ['child2a'] });

        expect(service.pinnedItemsQuery()).toEqual({ items: ['child2a'], itemIdSpace: 'row' });
      });

      it('settles after one refetch when a parent has no rows in the child view, and restores it in the parent view', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        pinInParentView('parent1', 'parent2');
        service.setDropdownSelection(CHILD_VIEW);

        landPinned(childRow('child1a', 'parent1'));
        const queryAfterFanOut = service.pinnedItemsQuery();
        expect(queryAfterFanOut).toEqual({ items: ['parent1'], itemIdSpace: 'parent' });

        landPinned(childRow('child1a', 'parent1'));
        expect(service.pinnedItemsQuery()).toBe(queryAfterFanOut);

        service.setDropdownSelection(PARENT_VIEW);
        expect(service.pinnedItemsQuery()).toEqual({
          items: ['parent1', 'parent2'],
          itemIdSpace: 'row',
        });
      });
    });

    describe('pinnedParents', () => {
      it('reads unique parents in row order with the parent key of the fetched view', () => {
        connectService(viewConfigs, { selection: CHILD_VIEW });

        landPinned(
          childRow('child2a', 'parent2'),
          childRow('child1a', 'parent1'),
          childRow('child2b', 'parent2'),
        );

        expect(service.pinnedParents()).toEqual(['parent2', 'parent1']);
        expect(service.pinnedParentCount()).toBe(2);
        expect(service.pinnedParentsSet().has('parent1')).toBe(true);
      });

      it('reads the parent key active when the fetch was requested, not when it lands', () => {
        connectService(viewConfigs, { selection: PARENT_VIEW });
        const pinnedResponse$ = new Subject<{ data: Row[]; totalCount: number }>();
        service.fetchPinned(pinnedResponse$);

        service.setDropdownSelection(CHILD_VIEW);
        pinnedResponse$.next({ data: [parentRow('parent1')], totalCount: 1 });

        expect(service.pinnedParents()).toEqual(['parent1']);
      });

      it('is empty when the view has no parent key', () => {
        connectService(mockComparisonToolDataConfig);

        landPinned({ _id: 'id1' }, { _id: 'id2' });

        expect(service.parentIdDataKey()).toBeNull();
        expect(service.pinnedParents()).toEqual([]);
      });
    });
  });

  describe('global pinned items cache', () => {
    const mockConfigs: ComparisonToolConfig[] = [
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: ['category1', 'option1'],
      },
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: ['category1', 'option2'],
      },
      {
        ...mockComparisonToolDataConfig[0],
        dropdowns: ['category2', 'option1'],
      },
    ];

    beforeEach(() => {
      connectService(mockConfigs, { selection: ['category1', 'option1'] });
    });

    it('should carry over pinned items when switching dropdown selections', () => {
      service.pinItem('item1');
      service.fetchPinned(of({ data: [{ _id: 'item1' }], totalCount: 1 }));
      service.pinItem('item2');
      service.fetchPinned(of({ data: [{ _id: 'item1' }, { _id: 'item2' }], totalCount: 2 }));
      expect(service.pinnedItemsSet().size).toBe(2);

      service.setDropdownSelection(['category1', 'option2']);
      expect(service.pinnedItemsSet().size).toBe(2);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
    });

    it('should preserve cache when switching selections without modifying pins', () => {
      service.pinItem('item1');
      service.fetchPinned(of({ data: [{ _id: 'item1' }], totalCount: 1 }));

      service.pinItem('item2');
      service.fetchPinned(of({ data: [{ _id: 'item1' }, { _id: 'item2' }], totalCount: 2 }));
      const initialPins = (service as any).querySignal().pinnedItems;

      service.setDropdownSelection(['category1', 'option2']);

      const pinsAfterSwitch = (service as any).querySignal().pinnedItems;
      expect(pinsAfterSwitch).toEqual(initialPins);
      expect(pinsAfterSwitch).toEqual(['item1', 'item2']);

      service.setDropdownSelection(['category1', 'option1']);

      const pinsAfterSwitchBack = (service as any).querySignal().pinnedItems;
      expect(pinsAfterSwitchBack).toEqual(initialPins);
    });

    it('should handle pinning/unpinning across different selections', () => {
      service.pinItem('item1');
      service.fetchPinned(of({ data: [{ _id: 'item1' }], totalCount: 1 }));
      service.pinItem('item2');
      service.fetchPinned(of({ data: [{ _id: 'item1' }, { _id: 'item2' }], totalCount: 2 }));
      expect(service.pinnedItemsSet().size).toBe(2);

      service.setDropdownSelection(['category1', 'option2']);
      expect(service.pinnedItemsSet().size).toBe(2);

      service.pinItem('item3');
      service.fetchPinned(
        of({ data: [{ _id: 'item1' }, { _id: 'item2' }, { _id: 'item3' }], totalCount: 3 }),
      );
      expect(service.pinnedItemsSet().size).toBe(3);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(true);

      service.setDropdownSelection(['category1', 'option1']);
      expect(service.pinnedItemsSet().size).toBe(3);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(true);
    });

    it('should handle unpinning items and carry over changes', () => {
      service.pinItem('item1');
      service.fetchPinned(of({ data: [{ _id: 'item1' }], totalCount: 1 }));
      service.pinItem('item2');
      service.fetchPinned(of({ data: [{ _id: 'item1' }, { _id: 'item2' }], totalCount: 2 }));
      service.pinItem('item3');
      service.fetchPinned(
        of({ data: [{ _id: 'item1' }, { _id: 'item2' }, { _id: 'item3' }], totalCount: 3 }),
      );

      service.unpinItem('item1');
      service.fetchPinned(of({ data: [{ _id: 'item2' }, { _id: 'item3' }], totalCount: 2 }));
      expect(service.pinnedItemsSet().size).toBe(2);
      expect(service.isPinned('item1')).toBe(false);

      service.setDropdownSelection(['category1', 'option2']);
      expect(service.pinnedItemsSet().size).toBe(2);
      expect(service.isPinned('item1')).toBe(false);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(true);
    });

    it('should handle toggling pins correctly across selections', () => {
      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(true);

      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(false);

      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(true);

      service.setDropdownSelection(['category1', 'option2']);

      expect(service.isPinned('item1')).toBe(true);

      service.setDropdownSelection(['category1', 'option1']);

      expect(service.isPinned('item1')).toBe(true);
    });

    it('should handle pinning multiple items at once', () => {
      service.setPinnedItems(['item1', 'item2', 'item3']);
      expect(service.pinnedItemsSet().size).toBe(3);

      service.setDropdownSelection(['category2', 'option1']);

      expect(service.pinnedItemsSet().size).toBe(3);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(true);

      service.setDropdownSelection(['category1', 'option1']);

      expect(service.pinnedItemsSet().size).toBe(3);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(true);
    });
  });

  describe('pin/unpin functionality', () => {
    it('should track pinned items correctly', () => {
      connectService();
      service.pinItem('id1');

      expect(service.isPinned('id1')).toBe(true);
      expect(service.isPinned('id2')).toBe(false);
    });

    it('should toggle pin state', () => {
      connectService();
      service.togglePin('id1');

      expect(service.isPinned('id1')).toBe(true);

      service.togglePin('id1');

      expect(service.isPinned('id1')).toBe(false);
    });

    it('should not pin beyond the pin limit', () => {
      connectService();
      service.setPinLimit(2);
      service.fetchPinned(of({ data: [{ _id: 'id1' }, { _id: 'id2' }], totalCount: 2 }));
      service.setPinnedItems(['id1', 'id2']);

      service.pinItem('id3');

      expect(service.isPinned('id1')).toBe(true);
      expect(service.isPinned('id2')).toBe(true);
      expect(service.isPinned('id3')).toBe(false);
    });

    it('should clamp the configured pin limit to MAX_PIN_LIMIT', () => {
      connectService();

      service.setPinLimit(MAX_PIN_LIMIT + 10);

      expect(service.pinLimit()).toBe(MAX_PIN_LIMIT);
    });

    it('should not add duplicate items when pinItem is called multiple times with same id', () => {
      connectService();

      service.pinItem('id1');
      service.pinItem('id1');
      service.pinItem('id1');

      expect(service.pinnedItems()).toEqual(['id1']);
      expect(service.isPinned('id1')).toBe(true);
    });

    it('should deduplicate items when setPinnedItems receives array with duplicates', () => {
      connectService();

      service.setPinnedItems(['id1', 'id2', 'id1', 'id3', 'id2']);

      expect(service.pinnedItems()).toEqual(['id1', 'id2', 'id3']);
      expect(service.pinnedItemsSet().size).toBe(3);
      expect(service.isPinned('id1')).toBe(true);
      expect(service.isPinned('id2')).toBe(true);
      expect(service.isPinned('id3')).toBe(true);
    });

    it('should handle setPinnedItems with null and return empty array', () => {
      connectService();

      service.pinItem('id1');
      expect(service.isPinned('id1')).toBe(true);

      service.setPinnedItems(null);

      expect(service.pinnedItems()).toEqual([]);
      expect(service.pinnedItemsSet().size).toBe(0);
    });

    it('should maintain data integrity when pinning, unpinning, and re-pinning same item', () => {
      connectService();

      service.pinItem('id1');
      expect(service.isPinned('id1')).toBe(true);

      service.unpinItem('id1');
      expect(service.isPinned('id1')).toBe(false);

      service.pinItem('id1');
      expect(service.isPinned('id1')).toBe(true);

      expect(service.pinnedItems()).toEqual(['id1']);
    });
  });

  describe('pinned data capping', () => {
    const rows = (...ids: string[]) => ids.map((id) => ({ _id: id }));

    it('should trim pinned data down to the pin limit and warn', () => {
      connectService();
      const warnSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showWarning');
      service.setPinLimit(2);

      service.fetchPinned(of({ data: rows('id1', 'id2', 'id3'), totalCount: 3 }));

      expect(service.pinnedData()).toEqual(rows('id1', 'id2'));
      expect(service.pinnedItems()).toEqual(['id1', 'id2']);
      expect(warnSpy).toHaveBeenCalledTimes(1);
      expect(warnSpy).toHaveBeenCalledWith(
        'Only 2 rows were pinned, because you reached the maximum of 2 pinned items.',
      );
    });

    it('should use singular wording when only one item could be pinned', () => {
      connectService();
      const warnSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showWarning');
      service.setPinLimit(1);

      service.fetchPinned(of({ data: rows('id1', 'id2'), totalCount: 2 }));

      expect(warnSpy).toHaveBeenCalledWith(
        'Only 1 row was pinned, because you reached the maximum of 1 pinned items.',
      );
    });

    it('should leave pinned data at the pin limit untouched', () => {
      connectService();
      const warnSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showWarning');
      service.setPinLimit(2);
      service.setPinnedItems(['id1', 'id2']);

      service.fetchPinned(of({ data: rows('id1', 'id2'), totalCount: 2 }));

      expect(service.pinnedData()).toEqual(rows('id1', 'id2'));
      expect(service.pinnedItems()).toEqual(['id1', 'id2']);
      expect(warnSpy).not.toHaveBeenCalled();
    });

    it('should not republish the pins when the row id is not unique in the data', () => {
      connectService();
      service.setPinLimit(2);
      service.setPinnedItems(['id1', 'id2']);
      const pinsBeforeTrim = service.pinnedItems();

      // A duplicated row id means trimming can never bring the row count down to the id count, so
      // republishing the trimmed ids here would refetch the same over-limit data indefinitely
      service.fetchPinned(of({ data: rows('id1', 'id2', 'id2'), totalCount: 3 }));

      expect(service.pinnedItems()).toBe(pinsBeforeTrim);
    });

    it('should cap pins restored from the URL', () => {
      connectService(mockComparisonToolDataConfig, {
        initialParams: { pinnedItems: ['id1', 'id2', 'id3'] },
      });
      service.setPinLimit(2);
      expect(service.pinnedItems()).toEqual(['id1', 'id2', 'id3']);

      service.fetchPinned(of({ data: rows('id1', 'id2', 'id3'), totalCount: 3 }));

      expect(service.pinnedItems()).toEqual(['id1', 'id2']);
    });
  });

  describe('pinAll', () => {
    const rows = (...ids: string[]) => ids.map((id) => ({ _id: id }));

    const pinnedRows = (...ids: string[]) => {
      service.setPinnedItems(ids);
      service.fetchPinned(of({ data: rows(...ids), totalCount: ids.length }));
    };

    const stubFetch = (result: { rows: Row[]; totalElements: number }) =>
      jest.fn<ReturnType<PinAllFetch<Row>>, Parameters<PinAllFetch<Row>>>(() => of(result));

    it('should union the returned rows with the existing pins', () => {
      const pinAllFetch = stubFetch({ rows: rows('id2', 'id3'), totalElements: 2 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      pinnedRows('id1');

      service.pinAll();

      expect(service.pinnedItems()).toEqual(['id1', 'id2', 'id3']);
    });

    it('should request only the pins the user has left', () => {
      const pinAllFetch = stubFetch({ rows: [], totalElements: 0 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      service.setPinLimit(3);
      pinnedRows('id1');

      service.pinAll();

      expect(pinAllFetch).toHaveBeenCalledWith(service.query(), 2);
    });

    it('should never request more than MAX_PIN_LIMIT', () => {
      const pinAllFetch = stubFetch({ rows: [], totalElements: 0 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      service.setPinLimit(MAX_PIN_LIMIT + 10);

      service.pinAll();

      expect(pinAllFetch).toHaveBeenCalledWith(service.query(), MAX_PIN_LIMIT);
    });

    it('should show an error when the fetch fails', () => {
      const pinAllFetch = jest.fn<ReturnType<PinAllFetch<Row>>, Parameters<PinAllFetch<Row>>>(() =>
        throwError(() => new Error('boom')),
      );
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      const errorSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showError');

      service.pinAll();

      expect(errorSpy).toHaveBeenCalledWith(
        'Something went wrong while pinning all matching rows. Please try again.',
      );
    });

    it('should warn when there were more matching rows than could be pinned', () => {
      const pinAllFetch = stubFetch({ rows: rows('id1', 'id2'), totalElements: 5 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      const warnSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showWarning');
      service.setPinLimit(2);

      service.pinAll();

      expect(warnSpy).toHaveBeenCalledWith(
        'Only 2 rows were pinned, because you reached the maximum of 2 pinned items.',
      );
    });

    it('should not warn when every matching row was pinned', () => {
      const pinAllFetch = stubFetch({ rows: rows('id1', 'id2'), totalElements: 2 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      const warnSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showWarning');

      service.pinAll();

      expect(service.pinnedItems()).toEqual(['id1', 'id2']);
      expect(warnSpy).not.toHaveBeenCalled();
    });

    it('should do nothing when already at the pin limit', () => {
      const pinAllFetch = stubFetch({ rows: rows('id2'), totalElements: 1 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      service.setPinLimit(1);
      pinnedRows('id1');

      service.pinAll();

      expect(pinAllFetch).not.toHaveBeenCalled();
      expect(service.pinnedItems()).toEqual(['id1']);
    });

    it('should do nothing while table data is loading', () => {
      const pinAllFetch = stubFetch({ rows: rows('id1'), totalElements: 1 });
      connectService(mockComparisonToolDataConfig, { pinAllFetch });
      service.fetchUnpinned(new Subject<never>());

      service.pinAll();

      expect(pinAllFetch).not.toHaveBeenCalled();
    });

    it('should clear the loading state when the fetch fails', () => {
      const pinAllFetch = jest.fn<ReturnType<PinAllFetch<Row>>, Parameters<PinAllFetch<Row>>>(() =>
        throwError(() => new Error('boom')),
      );
      connectService(mockComparisonToolDataConfig, { pinAllFetch });

      service.pinAll();

      expect(pinAllFetch).toHaveBeenCalled();
      expect(service.isLoadingTableData()).toBe(false);
      expect(service.pinnedItems()).toEqual([]);
    });

    it('should clear the loading state when the fetch completes without emitting', () => {
      const pinAllFetch = jest.fn<ReturnType<PinAllFetch<Row>>, Parameters<PinAllFetch<Row>>>(
        () => EMPTY,
      );
      connectService(mockComparisonToolDataConfig, { pinAllFetch });

      service.pinAll();

      expect(pinAllFetch).toHaveBeenCalled();
      expect(service.isLoadingTableData()).toBe(false);
    });
  });

  describe('URL synchronization', () => {
    const flushInitialUrlSync = () => {
      tick();
    };

    const getLastNavigateCall = () => (mockRouter.navigate as jest.Mock).mock.calls.at(-1);

    describe('pinned items sync', () => {
      it('should sync pinned items to URL', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id1' }], totalCount: 1 }));
        tick();

        expect(mockRouter.navigate).toHaveBeenCalledWith(
          [],
          expect.objectContaining({
            queryParams: expect.objectContaining({
              pinned: 'id1',
            }),
          }),
        );
      }));

      it('should sync multiple pinned items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id3');
        service.fetchPinned(of({ data: [{ _id: 'id3' }], totalCount: 1 }));
        service.pinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id3' }, { _id: 'id1' }], totalCount: 2 }));
        service.pinItem('id2');
        service.fetchPinned(
          of({ data: [{ _id: 'id3' }, { _id: 'id1' }, { _id: 'id2' }], totalCount: 3 }),
        );
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id3,id1,id2');
      }));

      it('should restore pinned items from URL', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        paramsSubject.next({ pinnedItems: ['id1', 'id2'] });
        tick();

        expect(service.pinnedItems()).toEqual(['id1', 'id2']);
      }));

      it('should sync when unpinning items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id1' }], totalCount: 1 }));
        service.pinItem('id2');
        service.fetchPinned(of({ data: [{ _id: 'id1' }, { _id: 'id2' }], totalCount: 2 }));
        service.unpinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id2' }], totalCount: 1 }));
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id2');
      }));

      it('should sync when pinning list of items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.setPinnedItems(['id1', 'id2', 'id3']);
        service.fetchPinned(
          of({ data: [{ _id: 'id1' }, { _id: 'id2' }, { _id: 'id3' }], totalCount: 3 }),
        );
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1,id2,id3');
      }));

      it('should sync when resetting pinned items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id1' }], totalCount: 1 }));
        tick();
        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toEqual('id1');

        service.resetPinnedItems();
        service.fetchPinned(of({ data: [], totalCount: 0 }));
        tick();

        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toBeNull();
      }));
    });

    describe('categories sync', () => {
      const mockConfigsWithDropdowns: ComparisonToolConfig[] = [
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category A', 'Option 1'],
        },
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category A', 'Option 2'],
        },
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category B', 'Option 1'],
        },
      ];

      it('should sync dropdown selection to URL as categories', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, { selection: ['Category A', 'Option 1'] });
        flushInitialUrlSync();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.categories).toEqual('Category%20A,Option%201');
      }));

      it('should restore categories from URL on initialization', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, {
          initialParams: { categories: ['Category B', 'Option 1'] },
        });
        flushInitialUrlSync();

        expect(service.dropdownSelection()).toEqual(['Category B', 'Option 1']);
      }));

      it('should sync when changing dropdown selection', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, { selection: ['Category A', 'Option 1'] });
        flushInitialUrlSync();

        service.setDropdownSelection(['Category A', 'Option 2']);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.categories).toEqual('Category%20A,Option%202');
      }));

      it('should handle categories with special characters', fakeAsync(() => {
        const configsWithSpecialChars: ComparisonToolConfig[] = [
          {
            ...mockComparisonToolDataConfig[0],
            dropdowns: ['Category A', 'Option 1, with comma'],
          },
          {
            ...mockComparisonToolDataConfig[0],
            dropdowns: ['Category B', 'Option 2+plus'],
          },
        ];

        connectService(configsWithSpecialChars, {
          selection: ['Category A', 'Option 1, with comma'],
        });
        flushInitialUrlSync();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.categories).toContain('Option%201%2C%20with%20comma');
      }));

      it('should prioritize URL categories over initialSelection', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, {
          selection: ['Category A', 'Option 1'],
          initialParams: { categories: ['Category B', 'Option 1'] },
        });
        flushInitialUrlSync();

        expect(service.dropdownSelection()).toEqual(['Category B', 'Option 1']);
      }));

      it('should use initialSelection when no URL categories present', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, {
          selection: ['Category A', 'Option 2'],
        });
        flushInitialUrlSync();

        expect(service.dropdownSelection()).toEqual(['Category A', 'Option 2']);
      }));

      it('should fall back to default when categories are invalid', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, {
          initialParams: { categories: ['Invalid', 'Category'] },
        });
        flushInitialUrlSync();

        expect(service.dropdownSelection()).toEqual(['Category A', 'Option 1']);
      }));
    });

    describe('combined pinned items and categories', () => {
      const mockConfigsWithDropdowns: ComparisonToolConfig[] = [
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category A', 'Option 1'],
        },
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category A', 'Option 2'],
        },
      ];

      it('should sync both categories and pinned items', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, { selection: ['Category A', 'Option 1'] });
        flushInitialUrlSync();

        service.pinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id1' }], totalCount: 1 }));
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.categories).toEqual('Category%20A,Option%201');
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1');
      }));

      it('should restore both from URL', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, {
          initialParams: {
            categories: ['Category A', 'Option 2'],
            pinnedItems: ['id1', 'id2'],
          },
        });
        flushInitialUrlSync();

        expect(service.dropdownSelection()).toEqual(['Category A', 'Option 2']);
        expect(service.pinnedItems()).toEqual(['id1', 'id2']);
      }));

      it('should carry over pinned items across dropdown selections', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, { selection: ['Category A', 'Option 1'] });
        flushInitialUrlSync();

        service.pinItem('id1');
        service.fetchPinned(of({ data: [{ _id: 'id1' }], totalCount: 1 }));
        tick();

        service.setDropdownSelection(['Category A', 'Option 2']);
        tick();
        expect(service.pinnedItems().length).toBe(1);
        expect(service.pinnedItems()).toEqual(['id1']);

        service.pinItem('id2');
        service.fetchPinned(of({ data: [{ _id: 'id1' }, { _id: 'id2' }], totalCount: 2 }));
        tick();

        service.setDropdownSelection(['Category A', 'Option 1']);
        tick();
        expect(service.pinnedItems()).toEqual(['id1', 'id2']);

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.categories).toEqual('Category%20A,Option%201');
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1,id2');
      }));
    });

    describe('sort state sync', () => {
      it('should sync sort state to URL when not using default sort', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.setSort([{ field: 'name', order: 1 }]);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.sortFields).toEqual('name');
        expect(lastCall?.[1]?.queryParams?.sortOrders).toEqual('1');
      }));

      it('should sync multiple sort fields to URL', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.setSort([
          { field: 'name', order: 1 },
          { field: 'score', order: -1 },
        ]);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.sortFields).toEqual('name,score');
        expect(lastCall?.[1]?.queryParams?.sortOrders).toEqual('1,-1');
      }));

      it('should restore sort state from URL on initialization', fakeAsync(() => {
        connectService(mockComparisonToolDataConfig, {
          initialParams: {
            sortFields: ['gene_symbol', 'name'],
            sortOrders: [-1, 1],
          },
        });
        flushInitialUrlSync();

        expect(service.multiSortMeta()).toEqual([
          { field: 'gene_symbol', order: -1 },
          { field: 'name', order: 1 },
        ]);
      }));

      it('should not include default sort in URL', fakeAsync(() => {
        service = injectService();
        service.setViewConfig({
          defaultSort: [
            { field: 'name', order: 1 },
            { field: 'age', order: -1 },
          ],
        });

        paramsSubject = new BehaviorSubject<ComparisonToolUrlParams>({});
        service.connect({
          config$: of(mockComparisonToolDataConfig),
          queryParams$: paramsSubject.asObservable(),
          pinAllFetch: noMatchingRows,
        });
        flushInitialUrlSync();

        // Default sort should not be in URL - check that sortFields is not included or is null
        const lastCall = getLastNavigateCall();
        const sortFields = lastCall?.[1]?.queryParams?.sortFields;
        // sortFields should either be null or undefined (not included in URL)
        expect(sortFields === null || sortFields === undefined).toBe(true);
      }));

      it('should include non-default sort in URL', fakeAsync(() => {
        service = injectService();
        service.setViewConfig({
          defaultSort: [{ field: 'name', order: 1 }],
        });

        paramsSubject = new BehaviorSubject<ComparisonToolUrlParams>({});
        service.connect({
          config$: of(mockComparisonToolDataConfig),
          queryParams$: paramsSubject.asObservable(),
          pinAllFetch: noMatchingRows,
        });
        flushInitialUrlSync();

        // Change to a different sort
        service.setSort([{ field: 'score', order: -1 }]);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.sortFields).toEqual('score');
        expect(lastCall?.[1]?.queryParams?.sortOrders).toEqual('-1');
      }));
    });
  });

  describe('page number reset behavior', () => {
    it('should reset page to 0 when filter changes', () => {
      connectService();

      service.updateQuery({ pageNumber: 5 });
      expect(service.pageNumber()).toBe(5);

      const filters = [
        {
          name: 'Test Filter',
          query_param_key: 'testField',
          options: [
            { label: 'Option 1', selected: true },
            { label: 'Option 2', selected: false },
          ],
        },
      ];
      service.updateQuery({ filters, pageNumber: service.FIRST_PAGE_NUMBER });

      expect(service.pageNumber()).toBe(0);
    });

    it('should reset page to 0 when sort changes', () => {
      connectService();

      service.updateQuery({ pageNumber: 3 });
      expect(service.pageNumber()).toBe(3);

      service.setSort([{ field: 'name', order: 1 }]);

      expect(service.pageNumber()).toBe(0);
    });

    it('should reset page to 0 when search term changes', () => {
      connectService();

      service.updateQuery({ pageNumber: 4 });
      expect(service.pageNumber()).toBe(4);

      service.updateQuery({ searchTerm: 'test search', pageNumber: service.FIRST_PAGE_NUMBER });

      expect(service.pageNumber()).toBe(0);
    });

    it('should reset page to 0 when dropdown selection changes', () => {
      const mockConfigsWithDropdowns: ComparisonToolConfig[] = [
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category A', 'Option 1'],
        },
        {
          ...mockComparisonToolDataConfig[0],
          dropdowns: ['Category A', 'Option 2'],
        },
      ];

      connectService(mockConfigsWithDropdowns, { selection: ['Category A', 'Option 1'] });

      expect(service.dropdownSelection()).toEqual(['Category A', 'Option 1']);

      service.updateQuery({ pageNumber: 2 });
      expect(service.pageNumber()).toBe(2);

      service.setDropdownSelection(['Category A', 'Option 2']);

      expect(service.pageNumber()).toBe(0);
      expect(service.dropdownSelection()).toEqual(['Category A', 'Option 2']);
    });

    it('should not reset page when item is pinned', () => {
      connectService();

      service.updateQuery({ pageNumber: 6 });
      expect(service.pageNumber()).toBe(6);

      service.pinItem('id1');

      expect(service.pageNumber()).toBe(6);
    });

    it('should not reset page when item is unpinned', () => {
      connectService();

      service.pinItem('id1');

      service.updateQuery({ pageNumber: 7 });
      expect(service.pageNumber()).toBe(7);

      service.unpinItem('id1');

      expect(service.pageNumber()).toBe(7);
    });

    it('should not reset page when multiple items are pinned at once', () => {
      connectService();

      service.updateQuery({ pageNumber: 3 });
      expect(service.pageNumber()).toBe(3);

      service.setPinnedItems(['id1', 'id2', 'id3']);

      expect(service.pageNumber()).toBe(3);
    });
  });

  describe('heatmap details panel', () => {
    const mockRowData = { _id: 'row-123', gene_symbol: 'APOE', tissue: 'cortex' };
    const mockCellData = { log2_fc: 1.5, adj_p_val: 0.01 };
    const mockEvent = new MouseEvent('click');

    it('should have null panel data initially', () => {
      connectService();
      expect(service.heatmapDetailsPanelData()).toBeNull();
    });

    it('should not show panel when no transform is configured', () => {
      connectService();
      service.showHeatmapDetailsPanel(mockRowData, mockCellData, 'age_4mo', mockEvent);
      expect(service.heatmapDetailsPanelData()).toBeNull();
    });

    it('should show panel with transformed data when transform is configured', () => {
      connectService();
      const mockTransform = jest.fn().mockReturnValue({
        heading: 'Test Heading',
        value: 1.5,
      });
      service.setViewConfig({
        heatmapCircleClickTransformFn: mockTransform,
      });

      service.showHeatmapDetailsPanel(mockRowData, mockCellData, 'age_4mo', mockEvent);

      expect(mockTransform).toHaveBeenCalledWith({
        rowData: mockRowData,
        cellData: mockCellData,
        columnKey: 'age_4mo',
      });
      expect(service.heatmapDetailsPanelData()).toEqual({
        data: { heading: 'Test Heading', value: 1.5 },
        event: mockEvent,
      });
    });

    it('should not show panel when transform returns null', () => {
      connectService();
      const mockTransform = jest.fn().mockReturnValue(null);
      service.setViewConfig({
        heatmapCircleClickTransformFn: mockTransform,
      });

      service.showHeatmapDetailsPanel(mockRowData, mockCellData, 'age_4mo', mockEvent);

      expect(mockTransform).toHaveBeenCalled();
      expect(service.heatmapDetailsPanelData()).toBeNull();
    });

    it('should hide panel and clear data', () => {
      connectService();
      const mockTransform = jest.fn().mockReturnValue({ heading: 'Test' });
      service.setViewConfig({
        heatmapCircleClickTransformFn: mockTransform,
      });
      service.showHeatmapDetailsPanel(mockRowData, mockCellData, 'age_4mo', mockEvent);
      expect(service.heatmapDetailsPanelData()).not.toBeNull();

      service.hideHeatmapDetailsPanel();

      expect(service.heatmapDetailsPanelData()).toBeNull();
    });
  });

  describe('filter panel', () => {
    it('should have filter panel closed initially', () => {
      connectService();
      expect(service.isFilterPanelOpen()).toBe(false);
    });

    it('should open filter panel', () => {
      connectService();
      service.openFilterPanel();
      expect(service.isFilterPanelOpen()).toBe(true);
    });

    it('should close filter panel', () => {
      connectService();
      service.openFilterPanel();
      expect(service.isFilterPanelOpen()).toBe(true);

      service.closeFilterPanel();
      expect(service.isFilterPanelOpen()).toBe(false);
    });

    it('should toggle filter panel', () => {
      connectService();
      expect(service.isFilterPanelOpen()).toBe(false);

      service.toggleFilterPanel();
      expect(service.isFilterPanelOpen()).toBe(true);

      service.toggleFilterPanel();
      expect(service.isFilterPanelOpen()).toBe(false);
    });
  });

  describe('row selection', () => {
    it('selectRow() sets selectedRowId', () => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true });
      service.selectRow('row-1');
      expect(service.selectedRowId()).toBe('row-1');
    });

    it('selectRow() is a no-op when same ID is already selected', () => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true });
      service.selectRow('row-1');
      expect(service.selectedRowId()).toBe('row-1');
      service.selectRow('row-1');
      expect(service.selectedRowId()).toBe('row-1');
    });

    it('selectRow() is a no-op when rowSelectionEnabled is false', () => {
      connectService();
      service.selectRow('row-1');
      expect(service.selectedRowId()).toBeNull();
    });

    it('setHoveredRowId() sets hoveredRowId', () => {
      connectService();
      service.setViewConfig({ rowHoverEnabled: true });
      service.setHoveredRowId('row-1');
      expect(service.hoveredRowId()).toBe('row-1');
    });

    it('setHoveredRowId() clears hoveredRowId when null is passed', () => {
      connectService();
      service.setViewConfig({ rowHoverEnabled: true });
      service.setHoveredRowId('row-1');
      service.setHoveredRowId(null);
      expect(service.hoveredRowId()).toBeNull();
    });

    it('setHoveredRowId() is a no-op when rowHoverEnabled is false', () => {
      connectService();
      service.setHoveredRowId('row-1');
      expect(service.hoveredRowId()).toBeNull();
    });

    it('auto-selects first unpinned row when fetches complete and rowSelectionEnabled=true', fakeAsync(() => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }, { _id: 'row-2' }], totalCount: 2 }));
      tick();
      expect(service.selectedRowId()).toBe('row-1');
    }));

    it('falls back to first pinned row when unpinned is empty and fetches complete', fakeAsync(() => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchPinned(of({ data: [{ _id: 'pinned-1' }], totalCount: 1 }));
      service.fetchUnpinned(of({ data: [], totalCount: 0 }));
      tick();
      expect(service.selectedRowId()).toBe('pinned-1');
    }));

    it('auto-selects regardless of which fetch completes first', fakeAsync(() => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      // Keep pinned in flight while unpinned completes
      const pendingPinned$ = new Subject<{ data: Row[]; totalCount: number }>();
      service.fetchPinned(pendingPinned$);
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      tick();
      expect(service.selectedRowId()).toBeNull();
      pendingPinned$.next({ data: [], totalCount: 0 });
      pendingPinned$.complete();
      tick();
      expect(service.selectedRowId()).toBe('row-1');
    }));

    it('auto-selects unpinned[0] whether pinned or unpinned data arrives first', fakeAsync(() => {
      // pinned arrives first
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchPinned(of({ data: [{ _id: 'pinned-1' }], totalCount: 1 }));
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      tick();
      expect(service.selectedRowId()).toBe('row-1');
    }));

    it('does not auto-select when rowSelectionEnabled is false', fakeAsync(() => {
      connectService();
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      tick();
      expect(service.selectedRowId()).toBeNull();
    }));

    it('does not overwrite an existing selection when new data arrives', fakeAsync(() => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      tick();
      expect(service.selectedRowId()).toBe('row-1');
      service.selectRow('row-2');
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }, { _id: 'row-2' }], totalCount: 2 }));
      tick();
      expect(service.selectedRowId()).toBe('row-2');
    }));

    it('notifySelectedRowValidity(false) resets and re-selects first row', () => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }, { _id: 'row-2' }], totalCount: 2 }));
      service.selectRow('row-2');
      service.notifySelectedRowValidity(false);
      expect(service.selectedRowId()).toBe('row-1');
    });

    it('notifySelectedRowValidity(false) is a no-op when selected row is in pinned data', () => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchPinned(of({ data: [{ _id: 'pinned-1' }], totalCount: 1 }));
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      service.selectRow('pinned-1');
      service.notifySelectedRowValidity(false);
      expect(service.selectedRowId()).toBe('pinned-1');
    });

    it('notifySelectedRowValidity(true) is a no-op', () => {
      connectService();
      service.setViewConfig({ rowSelectionEnabled: true, rowIdDataKey: '_id' });
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      service.selectRow('row-1');
      service.notifySelectedRowValidity(true);
      expect(service.selectedRowId()).toBe('row-1');
    });

    it('notifySelectedRowValidity() is a no-op when rowSelectionEnabled is false', () => {
      connectService();
      service.fetchUnpinned(of({ data: [{ _id: 'row-1' }], totalCount: 1 }));
      service.notifySelectedRowValidity(false);
      expect(service.selectedRowId()).toBeNull();
    });
  });

  describe('loading state', () => {
    type FetchResult = { data: Row[]; totalCount: number };

    it('should have isLoading false initially', () => {
      connectService();
      expect(service.isLoadingTableData()).toBe(false);
    });

    it('should set isLoading to true when an unpinned fetch is in flight', () => {
      connectService();
      service.fetchUnpinned(new Subject<FetchResult>());
      expect(service.isLoadingTableData()).toBe(true);
    });

    it('should set isLoading to false when the unpinned fetch completes', () => {
      connectService();
      const pending$ = new Subject<FetchResult>();
      service.fetchUnpinned(pending$);
      expect(service.isLoadingTableData()).toBe(true);

      pending$.next({ data: [], totalCount: 0 });
      pending$.complete();
      expect(service.isLoadingTableData()).toBe(false);
    });

    it('should set isLoading to false when the pinned fetch completes', () => {
      connectService();
      const pending$ = new Subject<FetchResult>();
      service.fetchPinned(pending$);
      expect(service.isLoadingTableData()).toBe(true);

      pending$.next({ data: [], totalCount: 0 });
      pending$.complete();
      expect(service.isLoadingTableData()).toBe(false);
    });

    it('should track multiple concurrent fetches', () => {
      connectService();
      const unpinned$ = new Subject<FetchResult>();
      const pinned$ = new Subject<FetchResult>();

      // Start two fetches (simulating parallel pinned and unpinned data fetches)
      service.fetchUnpinned(unpinned$);
      service.fetchPinned(pinned$);
      expect(service.isLoadingTableData()).toBe(true);

      // Complete first fetch - should still be loading
      unpinned$.next({ data: [], totalCount: 0 });
      unpinned$.complete();
      expect(service.isLoadingTableData()).toBe(true);

      // Complete second fetch - should no longer be loading
      pinned$.next({ data: [], totalCount: 0 });
      pinned$.complete();
      expect(service.isLoadingTableData()).toBe(false);
    });

    it('should balance the pending fetch counter across start and complete', () => {
      connectService();
      expect(service.pendingFetches()).toBe(0);

      const pending$ = new Subject<FetchResult>();
      service.fetchUnpinned(pending$);
      expect(service.pendingFetches()).toBe(1);

      pending$.next({ data: [], totalCount: 0 });
      pending$.complete();
      expect(service.pendingFetches()).toBe(0);
    });
  });

  describe('fetch streams (latest-wins)', () => {
    type Result = { data: Record<string, unknown>[]; totalCount: number };

    it('applies the latest unpinned fetch and ignores a superseded response', () => {
      connectService();

      const first$ = new Subject<Result>();
      const second$ = new Subject<Result>();

      service.fetchUnpinned(first$);
      service.fetchUnpinned(second$);

      // Newer request resolves first...
      second$.next({ data: [{ _id: 'newest' }], totalCount: 1 });
      second$.complete();
      // ...then the superseded older request resolves late and must be ignored.
      first$.next({ data: [{ _id: 'stale' }], totalCount: 99 });
      first$.complete();

      expect(service.unpinnedData()).toEqual([{ _id: 'newest' }]);
      expect(service.unpinnedRowCount()).toBe(1);
    });

    it('keeps the pending-fetch counter balanced when a request is superseded before it emits', () => {
      connectService();

      const first$ = new Subject<Result>();
      const second$ = new Subject<Result>();

      service.fetchUnpinned(first$);
      // Superseding the first fetch cancels it, so the counter drops back to a single pending fetch.
      service.fetchUnpinned(second$);
      expect(service.pendingFetches()).toBe(1);

      second$.next({ data: [], totalCount: 0 });
      second$.complete();

      expect(service.pendingFetches()).toBe(0);
      expect(service.isLoadingTableData()).toBe(false);
    });

    it('does not cancel an in-flight pinned fetch when an unpinned fetch starts', () => {
      connectService();

      const pinned$ = new Subject<Result>();
      const unpinned$ = new Subject<Result>();

      service.fetchPinned(pinned$);
      service.fetchUnpinned(unpinned$);

      unpinned$.next({ data: [{ _id: 'unpinned' }], totalCount: 5 });
      unpinned$.complete();
      pinned$.next({ data: [{ _id: 'pinned' }], totalCount: 1 });
      pinned$.complete();

      expect(service.unpinnedData()).toEqual([{ _id: 'unpinned' }]);
      expect(service.pinnedData()).toEqual([{ _id: 'pinned' }]);
      expect(service.pinnedRowCount()).toBe(1);
    });

    it('caps pinned data from the fetch stream at the pin limit and warns', () => {
      connectService();
      const warnSpy = jest.spyOn(TestBed.inject(ToastNotificationService), 'showWarning');
      service.setPinLimit(2);

      const pinned$ = new Subject<Result>();
      service.fetchPinned(pinned$);
      pinned$.next({
        data: [{ _id: 'id1' }, { _id: 'id2' }, { _id: 'id3' }],
        totalCount: 3,
      });
      pinned$.complete();

      expect(service.pinnedData()).toEqual([{ _id: 'id1' }, { _id: 'id2' }]);
      expect(service.pinnedItems()).toEqual(['id1', 'id2']);
      expect(service.pinnedRowCount()).toBe(2);
      expect(warnSpy).toHaveBeenCalledTimes(1);
    });

    it('maps a failed fetch to an empty result and stays alive for the next fetch', () => {
      connectService();

      const failing$ = new Subject<Result>();
      service.fetchUnpinned(failing$);
      failing$.error(new Error('request failed'));

      expect(service.unpinnedData()).toEqual([]);
      expect(service.unpinnedRowCount()).toBe(0);
      expect(service.isLoadingTableData()).toBe(false);

      const recovered$ = new Subject<Result>();
      service.fetchUnpinned(recovered$);
      recovered$.next({ data: [{ _id: 'recovered' }], totalCount: 1 });
      recovered$.complete();

      expect(service.unpinnedData()).toEqual([{ _id: 'recovered' }]);
      expect(service.unpinnedRowCount()).toBe(1);
    });
  });

  describe('buildPaginationOrBudget', () => {
    const query: ComparisonToolQuery = {
      categories: [],
      pinnedItems: [],
      pageNumber: 3,
      pageSize: 25,
      multiSortMeta: [],
      searchTerm: null,
      filters: [],
    };

    it('should send the current page when no budget is given', () => {
      injectService();

      expect(service.buildPaginationOrBudget(query)).toEqual({ pageNumber: 3, pageSize: 25 });
    });

    it('should send only the budget when a budget is given', () => {
      injectService();

      expect(service.buildPaginationOrBudget(query, 10)).toEqual({ remainingBudget: 10 });
    });
  });
});
