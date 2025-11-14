import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { ComparisonToolColumn } from '@sagebionetworks/explorers/models';
import { mockComparisonToolDataConfig } from '@sagebionetworks/explorers/testing';
import { FilterService, MessageService } from 'primeng/api';
import { BehaviorSubject } from 'rxjs';
import { COMPARISON_TOOL_URL_SYNC_DEBOUNCE_MS } from './comparison-tool-url.service';
import { ComparisonToolService } from './comparison-tool.service';
import { provideComparisonToolService } from './comparison-tool.service.providers';

describe('ComparisonToolService', () => {
  let service: ComparisonToolService<Record<string, unknown>>;
  let mockRouter: Partial<Router>;
  let queryParamsSubject: BehaviorSubject<any>;

  beforeEach(() => {
    queryParamsSubject = new BehaviorSubject<any>({});

    mockRouter = {
      navigate: jest.fn(),
    };

    const mockActivatedRoute = {
      queryParams: queryParamsSubject.asObservable(),
      snapshot: {
        queryParams: {},
      },
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
              pinned: ['id1'],
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
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual(['id3', 'id1', 'id2']);
      }));

      it('should restore pinned items from URL', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        queryParamsSubject.next({ pinned: ['id1', 'id2'] });
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
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual(['id2']);
      }));

      it('should sync when pinning list of items', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        service.pinList(['id1', 'id2', 'id3']);
        tick();

        const lastCall = getLastNavigateCall();
        expect(lastCall?.[1]?.queryParams?.pinned).toEqual(['id1', 'id2', 'id3']);
      }));

      it('should sync when resetting pinned items', fakeAsync(() => {
        injectService().initialize(mockComparisonToolDataConfig);
        flushInitialUrlSync();

        service.pinItem('id1');
        tick();
        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toEqual(['id1']);

        service.resetPinnedItems();
        tick();

        expect(getLastNavigateCall()?.[1]?.queryParams?.pinned).toBeNull();
      }));
    });
  });
});
