import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { ComparisonToolUrlService } from './comparison-tool-url.service';

describe('ComparisonToolUrlService', () => {
  let service: ComparisonToolUrlService;
  let mockRouter: Partial<Router>;
  let mockActivatedRoute: Partial<ActivatedRoute>;
  let queryParamsSubject: BehaviorSubject<Params>;

  beforeEach(() => {
    queryParamsSubject = new BehaviorSubject<Params>({});

    mockRouter = {
      navigate: jest.fn(),
    };

    mockActivatedRoute = {
      queryParams: queryParamsSubject.asObservable(),
      snapshot: {
        queryParams: {},
      } as any,
    };

    TestBed.configureTestingModule({
      providers: [
        ComparisonToolUrlService,
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    });

    service = TestBed.inject(ComparisonToolUrlService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(service).toBeDefined();
  });

  it('should serialize pinned items', () => {
    service.syncToUrl({ pinnedItems: ['id3', 'id1', 'id2'] });

    expect(mockRouter.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          pinned: 'id3,id1,id2',
        }),
      }),
    );
  });

  it('should clear pinned query param when empty', () => {
    mockActivatedRoute.snapshot = {
      queryParams: { pinned: 'id1' },
    } as any;

    service.syncToUrl({ pinnedItems: [] });

    expect(mockRouter.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          pinned: null,
        }),
      }),
    );
  });

  it('should deserialize pinned items', async () => {
    queryParamsSubject.next({ pinned: 'id1,id2,id3' });

    await new Promise((resolve) => setTimeout(resolve, 100));
    const params = await firstValueFrom(service.params$);
    expect(params.pinnedItems).toEqual(['id1', 'id2', 'id3']);
  });

  it('should clear all query parameters', () => {
    service.clearUrl();

    expect(mockRouter.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: {},
        replaceUrl: true,
      }),
    );
  });

  it('should skip navigation when pinned state already empty', () => {
    service.syncToUrl({ pinnedItems: [] });

    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should skip navigation when the pinned state matches the URL', () => {
    mockActivatedRoute.snapshot = {
      queryParams: { pinned: 'id1,id2' },
    } as any;

    service.syncToUrl({ pinnedItems: ['id1', 'id2'] });

    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });
});
