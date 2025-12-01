import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolUrlParams,
} from '@sagebionetworks/explorers/models';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { FilterService, MessageService } from 'primeng/api';
import { BehaviorSubject } from 'rxjs';
import { COMPARISON_TOOL_URL_SYNC_DEBOUNCE_MS } from './comparison-tool-url.service';
import { ComparisonToolService } from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';

describe('ComparisonToolService', () => {
  let service: ComparisonToolService<Record<string, unknown>>;
  let mockRouter: Partial<Router>;
  let mockActivatedRoute: Partial<ActivatedRoute>;
  let queryParamsSubject: BehaviorSubject<any>;

  beforeEach(() => {
    queryParamsSubject = new BehaviorSubject<any>({});

    mockActivatedRoute = {
      queryParams: queryParamsSubject.asObservable(),
      snapshot: {
        queryParams: {},
      } as any,
    };

    mockRouter = {
      navigate: jest.fn((_, extras) => {
        const snapshot = (mockActivatedRoute.snapshot ??= { queryParams: {} } as any);
        const extrasQueryParams = extras?.queryParams;

        if (extrasQueryParams === undefined) {
          snapshot.queryParams = {};
          return Promise.resolve(true);
        }

        const normalizedQueryParams = extrasQueryParams ?? {};

        if (extras?.queryParamsHandling === 'merge') {
          const next = { ...snapshot.queryParams } as Record<string, unknown>;
          for (const [key, value] of Object.entries(normalizedQueryParams)) {
            if (value == null) {
              delete next[key];
            } else {
              next[key] = value;
            }
          }
          snapshot.queryParams = next;
        } else {
          snapshot.queryParams = { ...normalizedQueryParams };
        }

        return Promise.resolve(true);
      }),
    };

    TestBed.configureTestingModule({
      providers: [
        FilterService,
        MessageService,
        ...provideComparisonToolService(),
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    });
  });

  afterEach(() => {
    jest.clearAllMocks();

    const cache = (ComparisonToolService as any).routePinnedCache as
      | Map<string, string[]>
      | undefined;
    cache?.clear();
  });

  // Inject inside each test so fakeAsync zones include the service's timer setup
  const injectService = () => {
    service = TestBed.inject(ComparisonToolService);
    return service;
  };

  it('should create', () => {
    injectService();
    expect(service).toBeDefined();
  });

  it('excludes hidden columns from columns and cached dropdown columns', () => {
    injectService().initialize(mockComparisonToolDataConfig);

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
      injectService().initialize(mockConfigs, ['category1', 'option1']);
    });

    it('should persist pinned items when switching between dropdown selections', () => {
      // Pin items for first selection
      service.pinItem('item1');
      service.pinItem('item2');
      expect(service.pinnedItems().size).toBe(2);
      expect(service.isPinned('item1')).toBe(true);
      expect(service.isPinned('item2')).toBe(true);

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
      injectService().initialize(mockComparisonToolDataConfig);
      service.pinItem('id1');

      expect(service.isPinned('id1')).toBe(true);
      expect(service.isPinned('id2')).toBe(false);
    });

    it('should toggle pin state', () => {
      injectService().initialize(mockComparisonToolDataConfig);
      service.togglePin('id1');

      expect(service.isPinned('id1')).toBe(true);

      service.togglePin('id1');

      expect(service.isPinned('id1')).toBe(false);
    });

    it('should not exceed max pinned items', () => {
      injectService().initialize(mockComparisonToolDataConfig);
      service.setMaxPinnedItems(2);

      service.pinList(['id1', 'id2', 'id3']);

      expect(service.isPinned('id1')).toBe(true);
      expect(service.isPinned('id2')).toBe(true);
      expect(service.isPinned('id3')).toBe(false);
    });
  });

  describe('URL synchronization', () => {
    const flushInitialUrlSync = () => {
      tick(COMPARISON_TOOL_URL_SYNC_DEBOUNCE_MS + 1); // allow debounce to emit initial params
      tick();
    };

    const getLastNavigateCall = () => (mockRouter.navigate as jest.Mock).mock.calls.at(-1);

    describe('pinned items sync', () => {
      it('should sync pinned items to URL', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
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
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        service.pinItem('id3');
        service.pinItem('id1');
        service.pinItem('id2');
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id3,id1,id2');
      }));

      it('should restore pinned items from URL', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        queryParamsSubject.next({ pinned: 'id1,id2' });
        tick(COMPARISON_TOOL_URL_SYNC_DEBOUNCE_MS + 1);
        tick();

        expect(Array.from(service.pinnedItems())).toEqual(['id1', 'id2']);
      }));

      it('should sync when unpinning items', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        service.pinItem('id1');
        service.pinItem('id2');
        service.unpinItem('id1');
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id2');
      }));

      it('should sync when pinning list of items', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        service.pinList(['id1', 'id2', 'id3']);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual('id1,id2,id3');
      }));

      it('should sync when resetting pinned items', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        service.pinItem('id1');
        tick();
        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toEqual('id1');

        service.resetPinnedItems();
        tick();

        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toBeNull();
      }));
    });
  });

  describe('connect precedence resolution', () => {
    const createConnectSubjects = () => {
      const config$ = new BehaviorSubject<ComparisonToolConfig[]>(mockComparisonToolDataConfig);
      const params$ = new BehaviorSubject<ComparisonToolUrlParams>({});
      return { config$, params$ };
    };

    it('prefers URL pins over cached pins when both are present', fakeAsync(() => {
      const { config$, params$ } = createConnectSubjects();
      params$.next({ pinnedItems: ['url-1', 'url-2'] });

      const cache = (ComparisonToolService as any).routePinnedCache as Map<string, string[]>;
      cache.set('test-key', ['cached-1']);

      injectService().connect({
        config$: config$.asObservable(),
        queryParams$: params$.asObservable(),
        cacheKey: 'test-key',
      });
      tick();

      expect(Array.from(service.pinnedItems())).toEqual(['url-1', 'url-2']);
      expect(cache.get('test-key')).toEqual(['url-1', 'url-2']);
    }));

    it('restores cached pins when URL has none', fakeAsync(() => {
      const { config$, params$ } = createConnectSubjects();
      params$.next({});

      const cache = (ComparisonToolService as any).routePinnedCache as Map<string, string[]>;
      cache.set('test-key', ['cached-1', 'cached-2']);

      injectService().connect({
        config$: config$.asObservable(),
        queryParams$: params$.asObservable(),
        cacheKey: 'test-key',
      });
      tick();

      expect(Array.from(service.pinnedItems())).toEqual(['cached-1', 'cached-2']);

      tick();

      expect(mockRouter.navigate).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({ pinned: 'cached-1,cached-2' }),
        }),
      );
    }));

    it('starts with empty pins when neither URL nor cache provide values', fakeAsync(() => {
      const { config$, params$ } = createConnectSubjects();
      params$.next({});

      injectService().connect({
        config$: config$.asObservable(),
        queryParams$: params$.asObservable(),
        cacheKey: 'test-key',
      });
      tick();

      expect(Array.from(service.pinnedItems())).toEqual([]);

      tick();

      expect(mockRouter.navigate).not.toHaveBeenCalled();
    }));

    it('clears the URL pinned param on route exit but preserves cache', fakeAsync(() => {
      const { config$, params$ } = createConnectSubjects();
      params$.next({});

      injectService().connect({
        config$: config$.asObservable(),
        queryParams$: params$.asObservable(),
        cacheKey: 'test-key',
      });
      tick();

      service.pinList(['p1', 'p2']);
      tick();

      const cache = (ComparisonToolService as any).routePinnedCache as Map<string, string[]>;
      expect(cache.get('test-key')).toEqual(['p1', 'p2']);

      (service as any).handleRouteExit();

      const lastCall = (mockRouter.navigate as jest.Mock).mock.calls.at(-1);
      expect(lastCall?.[1]?.queryParams?.pinned).toBeNull();
      expect(cache.get('test-key')).toEqual(['p1', 'p2']);
    }));
  });
});
