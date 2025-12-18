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

  describe('categories', () => {
    it('should serialize categories', () => {
      service.syncToUrl({ categories: ['Cat1', 'Cat2', 'Cat3'] });

      expect(mockRouter.navigate).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            categories: 'Cat1,Cat2,Cat3',
          }),
        }),
      );
    });

    it('should clear categories query param when empty', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { categories: 'Cat1' },
      } as any;

      service.syncToUrl({ categories: [] });

      expect(mockRouter.navigate).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            categories: null,
          }),
        }),
      );
    });

    it('should deserialize categories', async () => {
      queryParamsSubject.next({ categories: 'Cat1,Cat2,Cat3' });

      await new Promise((resolve) => setTimeout(resolve, 100));
      const params = await firstValueFrom(service.params$);
      expect(params.categories).toEqual(['Cat1', 'Cat2', 'Cat3']);
    });

    it('should deserialize categories with URL encoding', async () => {
      queryParamsSubject.next({ categories: 'Immune%20Response,CD8%2B%20T%20Cells' });

      await new Promise((resolve) => setTimeout(resolve, 100));
      const params = await firstValueFrom(service.params$);
      expect(params.categories).toEqual(['Immune Response', 'CD8+ T Cells']);
    });

    it('should skip navigation when category state matches the URL', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { categories: 'Cat1,Cat2' },
      } as any;

      service.syncToUrl({ categories: ['Cat1', 'Cat2'] });

      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should skip navigation when categories state is already empty', () => {
      service.syncToUrl({ categories: [] });

      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });
  });

  describe('combined pinned items and categories', () => {
    it('should serialize both pinned items and categories', () => {
      service.syncToUrl({
        pinnedItems: ['id1', 'id2'],
        categories: ['Cat1', 'Cat2'],
      });

      expect(mockRouter.navigate).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            pinned: 'id1,id2',
            categories: 'Cat1,Cat2',
          }),
        }),
      );
    });

    it('should deserialize both pinned items and categories', async () => {
      queryParamsSubject.next({
        pinned: 'id1,id2',
        categories: 'Cat1,Cat2',
      });

      await new Promise((resolve) => setTimeout(resolve, 100));
      const params = await firstValueFrom(service.params$);
      expect(params.pinnedItems).toEqual(['id1', 'id2']);
      expect(params.categories).toEqual(['Cat1', 'Cat2']);
    });

    it('should skip navigation when both pinned and categories match URL', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { pinned: 'id1,id2', categories: 'Cat1,Cat2' },
      } as any;

      service.syncToUrl({
        pinnedItems: ['id1', 'id2'],
        categories: ['Cat1', 'Cat2'],
      });

      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should navigate when pinned matches but categories differ', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { pinned: 'id1,id2', categories: 'Cat1' },
      } as any;

      service.syncToUrl({
        pinnedItems: ['id1', 'id2'],
        categories: ['Cat1', 'Cat2'],
      });

      expect(mockRouter.navigate).toHaveBeenCalled();
    });

    it('should navigate when categories match but pinned differs', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { pinned: 'id1', categories: 'Cat1,Cat2' },
      } as any;

      service.syncToUrl({
        pinnedItems: ['id1', 'id2'],
        categories: ['Cat1', 'Cat2'],
      });

      expect(mockRouter.navigate).toHaveBeenCalled();
    });
  });

  describe('sortFields and sortOrders', () => {
    it('should serialize sortFields and sortOrders', () => {
      service.syncToUrl({
        sortFields: ['name', 'score'],
        sortOrders: [1, -1],
      });

      expect(mockRouter.navigate).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sortFields: 'name,score',
            sortOrders: '1,-1',
          }),
        }),
      );
    });

    it('should clear sortFields and sortOrders when empty', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { sortFields: 'name', sortOrders: '1' },
      } as any;

      service.syncToUrl({ sortFields: [], sortOrders: [] });

      expect(mockRouter.navigate).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sortFields: null,
            sortOrders: null,
          }),
        }),
      );
    });

    it('should deserialize sortFields and sortOrders', async () => {
      queryParamsSubject.next({ sortFields: 'name,score', sortOrders: '1,-1' });

      await new Promise((resolve) => setTimeout(resolve, 100));
      const params = await firstValueFrom(service.params$);
      expect(params.sortFields).toEqual(['name', 'score']);
      expect(params.sortOrders).toEqual([1, -1]);
    });

    it('should skip navigation when sort state matches URL', () => {
      mockActivatedRoute.snapshot = {
        queryParams: { sortFields: 'name,score', sortOrders: '1,-1' },
      } as any;

      service.syncToUrl({
        sortFields: ['name', 'score'],
        sortOrders: [1, -1],
      });

      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });
  });
});
