import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolUrlParams,
} from '@sagebionetworks/explorers/models';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { FilterService, MessageService } from 'primeng/api';
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
        FilterService,
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

  describe('pinned items cache', () => {
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

    it('should persist pinned items when switching between dropdown selections', () => {
      // Pin items for first selection
      service.pinItem('item1');
      service.pinItem('item2');
      expect(service.pinnedItems().size).toBe(2);

      // Switch to second selection
      service.setDropdownSelection(['category1', 'option2']);
      // Should start with no pinned items for new selection
      expect(service.pinnedItems().size).toBe(0);
      expect(service.isPinned('item1')).toBe(false);

      // Pin different items
      service.pinItem('item3');
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item3')).toBe(true);

      // Switch back to first selection
      service.setDropdownSelection(['category1', 'option1']);
      // Should restore originally pinned items
      expect(service.pinnedItems().size).toBe(2);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);
      expect(service.isPinned('item3')).toBe(false);

      // Switch to second selection again
      service.setDropdownSelection(['category1', 'option2']);
      // Should restore pinned items from second selection
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item3')).toBe(true);
      expect(service.isPinned('item1')).toBe(false);
    });

    it('should handle unpinning items and persist changes', () => {
      // Pin and unpin items
      service.pinItem('item1');
      service.pinItem('item2');
      service.unpinItem('item1');
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item2')).toBe(true);

      // Switch and come back
      service.setDropdownSelection(['category1', 'option2']);
      service.setDropdownSelection(['category1', 'option1']);

      // Should remember that item1 was unpinned
      expect(service.pinnedItems().size).toBe(1);
      expect(service.isPinned('item1')).toBe(false);
      expect(service.isPinned('item2')).toBe(true);
    });

    it('should handle toggling pins correctly', () => {
      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(true);

      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(false);

      // Pin again and switch selections
      service.togglePin('item1');
      expect(service.isPinned('item1')).toBe(true);

      service.setDropdownSelection(['category1', 'option2']);
      service.setDropdownSelection(['category1', 'option1']);

      // Should still be pinned after switching back
      expect(service.isPinned('item1')).toBe(true);
    });

    it('should handle pinning multiple items at once', () => {
      service.pinList(['item1', 'item2', 'item3']);
      expect(service.pinnedItems().size).toBe(3);

      // Switch and return
      service.setDropdownSelection(['category2', 'option1']);
      service.setDropdownSelection(['category1', 'option1']);

      // Should restore all pinned items
      expect(service.pinnedItems().size).toBe(3);
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
        service.pinItem('id1');
        service.pinItem('id2');
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id3,id1,id2');
      }));

      it('should restore pinned items from URL', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        paramsSubject.next({ pinnedItems: ['id1', 'id2'] });
        tick();

        expect(Array.from(service.pinnedItems())).toEqual(['id1', 'id2']);
      }));

      it('should sync when unpinning items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id1');
        service.pinItem('id2');
        service.unpinItem('id1');
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id2');
      }));

      it('should sync when pinning list of items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinList(['id1', 'id2', 'id3']);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1,id2,id3');
      }));

      it('should sync when resetting pinned items', fakeAsync(() => {
        connectService();
        flushInitialUrlSync();

        service.pinItem('id1');
        tick();
        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toEqual('id1');

        service.resetPinnedItems();
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
        expect(Array.from(service.pinnedItems())).toEqual(['id1', 'id2']);
      }));

      it('should maintain separate pinned items cache per dropdown selection', fakeAsync(() => {
        connectService(mockConfigsWithDropdowns, { selection: ['Category A', 'Option 1'] });
        flushInitialUrlSync();

        service.pinItem('id1');
        tick();

        service.setDropdownSelection(['Category A', 'Option 2']);
        tick();
        expect(service.pinnedItems().size).toBe(0);

        service.pinItem('id2');
        tick();

        service.setDropdownSelection(['Category A', 'Option 1']);
        tick();
        expect(Array.from(service.pinnedItems())).toEqual(['id1']);

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.categories).toEqual('Category%20A,Option%201');
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1');
      }));
    });
  });
});
