import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolUrlParams,
} from '@sagebionetworks/explorers/models';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { MessageService } from 'primeng/api';
import { BehaviorSubject, of } from 'rxjs';
import { ComparisonToolService } from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';

describe('ComparisonToolService', () => {
  let service: ComparisonToolService<Record<string, unknown>>;
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

  const connectService = (
    configs: ComparisonToolConfig[] = mockComparisonToolDataConfig,
    options: { selection?: string[]; initialParams?: ComparisonToolUrlParams } = {},
  ) => {
    paramsSubject = new BehaviorSubject<ComparisonToolUrlParams>(options.initialParams ?? {});
    injectService().connect({
      config$: of(configs),
      queryParams$: paramsSubject.asObservable(),
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
      service.setPinnedData([{ _id: 'item1' }] as any[]);
      service.pinItem('item2');
      service.setPinnedData([{ _id: 'item1' }, { _id: 'item2' }] as any[]);
      expect(service.pinnedItemsSet().size).toBe(2);

      service.setDropdownSelection(['category1', 'option2']);
      expect(service.pinnedItemsSet().size).toBe(2);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
    });

    it('should preserve cache when switching selections without modifying pins', () => {
      service.pinItem('item1');
      service.setPinnedData([{ _id: 'item1' }] as any[]);

      service.pinItem('item2');
      service.setPinnedData([{ _id: 'item1' }, { _id: 'item2' }] as any[]);
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
      service.setPinnedData([{ _id: 'item1' }] as any[]);
      service.pinItem('item2');
      service.setPinnedData([{ _id: 'item1' }, { _id: 'item2' }] as any[]);
      expect(service.pinnedItemsSet().size).toBe(2);

      service.setDropdownSelection(['category1', 'option2']);
      expect(service.pinnedItemsSet().size).toBe(2);

      service.pinItem('item3');
      service.setPinnedData([{ _id: 'item1' }, { _id: 'item2' }, { _id: 'item3' }] as any[]);
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
      service.setPinnedData([{ _id: 'item1' }] as any[]);
      service.pinItem('item2');
      service.setPinnedData([{ _id: 'item1' }, { _id: 'item2' }] as any[]);
      service.pinItem('item3');
      service.setPinnedData([{ _id: 'item1' }, { _id: 'item2' }, { _id: 'item3' }] as any[]);

      service.unpinItem('item1');
      service.setPinnedData([{ _id: 'item2' }, { _id: 'item3' }] as any[]);
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
      service.pinList(['item1', 'item2', 'item3']);
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

    it('should not exceed max pinned items', () => {
      connectService();
      service.setMaxPinnedItems(2);

      service.pinList(['id1', 'id2', 'id3']);

      expect(service.isPinned('id1')).toBe(true);
      expect(service.isPinned('id2')).toBe(true);
      expect(service.isPinned('id3')).toBe(false);
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
        service.setPinnedData([{ _id: 'id1' }] as any);
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
        service.setPinnedData([{ _id: 'id3' }] as any);
        service.pinItem('id1');
        service.setPinnedData([{ _id: 'id3' }, { _id: 'id1' }] as any);
        service.pinItem('id2');
        service.setPinnedData([{ _id: 'id3' }, { _id: 'id1' }, { _id: 'id2' }] as any);
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
        service.setPinnedData([{ _id: 'id1' }] as any);
        service.pinItem('id2');
        service.setPinnedData([{ _id: 'id1' }, { _id: 'id2' }] as any);
        service.unpinItem('id1');
        service.setPinnedData([{ _id: 'id2' }] as any);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id2');
      }));

      it('should sync when pinning list of items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinList(['id1', 'id2', 'id3']);
        service.setPinnedData([{ _id: 'id1' }, { _id: 'id2' }, { _id: 'id3' }] as any);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1,id2,id3');
      }));

      it('should sync when resetting pinned items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id1');
        service.setPinnedData([{ _id: 'id1' }] as any);
        tick();
        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toEqual('id1');

        service.resetPinnedItems();
        service.setPinnedData([] as any);
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
        service.setPinnedData([{ _id: 'id1' }] as any);
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
        service.setPinnedData([{ _id: 'id1' }] as any);
        tick();

        service.setDropdownSelection(['Category A', 'Option 2']);
        tick();
        expect(service.pinnedItems().length).toBe(1);
        expect(service.pinnedItems()).toEqual(['id1']);

        service.pinItem('id2');
        service.setPinnedData([{ _id: 'id1' }, { _id: 'id2' }] as any);
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
          data_key: 'testField',
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

      service.pinList(['id1', 'id2', 'id3']);

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
});
