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

  describe('sort parameters', () => {
    describe('serialization', () => {
      it('should serialize sortFields and sortOrders as comma-delimited strings', () => {
        service.syncToUrl({
          sortFields: ['name', 'age', 'sex'],
          sortOrders: [1, -1, 1],
        });

        expect(mockRouter.navigate).toHaveBeenCalledWith(
          [],
          expect.objectContaining({
            queryParams: expect.objectContaining({
              sortFields: 'name,age,sex',
              sortOrders: '1,-1,1',
            }),
          }),
        );
      });

      it('should clear sort params when arrays are empty', () => {
        mockActivatedRoute.snapshot = {
          queryParams: { sortFields: 'name', sortOrders: '1' },
        } as any;

        service.syncToUrl({
          sortFields: [],
          sortOrders: [],
        });

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

      it('should handle single sort field', () => {
        service.syncToUrl({
          sortFields: ['name'],
          sortOrders: [1],
        });

        expect(mockRouter.navigate).toHaveBeenCalledWith(
          [],
          expect.objectContaining({
            queryParams: expect.objectContaining({
              sortFields: 'name',
              sortOrders: '1',
            }),
          }),
        );
      });

      it('should not navigate when sort fields are unchanged', () => {
        mockActivatedRoute.snapshot = {
          queryParams: { sortFields: 'name,age', sortOrders: '1,-1' },
        } as any;

        service.syncToUrl({
          sortFields: ['name', 'age'],
          sortOrders: [1, -1],
        });

        expect(mockRouter.navigate).not.toHaveBeenCalled();
      });

      it('should navigate when sort fields change', () => {
        mockActivatedRoute.snapshot = {
          queryParams: { sortFields: 'name', sortOrders: '1' },
        } as any;

        service.syncToUrl({
          sortFields: ['name', 'age'],
          sortOrders: [1, -1],
        });

        expect(mockRouter.navigate).toHaveBeenCalled();
      });

      it('should navigate when sort orders change', () => {
        mockActivatedRoute.snapshot = {
          queryParams: { sortFields: 'name', sortOrders: '1' },
        } as any;

        service.syncToUrl({
          sortFields: ['name'],
          sortOrders: [-1],
        });

        expect(mockRouter.navigate).toHaveBeenCalled();
      });
    });

    describe('deserialization', () => {
      it('should deserialize comma-delimited sortFields', async () => {
        queryParamsSubject.next({ sortFields: 'name,age,sex' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortFields).toEqual(['name', 'age', 'sex']);
      });

      it('should deserialize comma-delimited sortOrders', async () => {
        queryParamsSubject.next({ sortOrders: '1,-1,1' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortOrders).toEqual([1, -1, 1]);
      });

      it('should handle single sortField', async () => {
        queryParamsSubject.next({ sortFields: 'name' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortFields).toEqual(['name']);
      });

      it('should handle single sortOrder', async () => {
        queryParamsSubject.next({ sortOrders: '1' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortOrders).toEqual([1]);
      });

      it('should filter out invalid sortOrders', async () => {
        const consoleWarnSpy = jest.spyOn(console, 'warn').mockImplementation();

        queryParamsSubject.next({ sortOrders: '1,2,-1,0,1' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);

        // Only 1 and -1 are valid
        expect(params.sortOrders).toEqual([1, -1, 1]);
        expect(consoleWarnSpy).toHaveBeenCalledTimes(2); // for '2' and '0'

        consoleWarnSpy.mockRestore();
      });

      it('should handle empty sortFields param', async () => {
        queryParamsSubject.next({ sortFields: '' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortFields).toBeUndefined();
      });

      it('should handle empty sortOrders param', async () => {
        queryParamsSubject.next({ sortOrders: '' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortOrders).toBeUndefined();
      });

      it('should handle null sortFields', async () => {
        queryParamsSubject.next({ sortFields: null });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortFields).toBeUndefined();
      });

      it('should handle array format from other query param styles', async () => {
        // Some routers might pass as array
        queryParamsSubject.next({ sortFields: ['name', 'age'] });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortFields).toEqual(['name', 'age']);
      });

      it('should handle sortOrders with both ascending and descending', async () => {
        queryParamsSubject.next({ sortOrders: '-1,1,-1,1' });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);
        expect(params.sortOrders).toEqual([-1, 1, -1, 1]);
      });
    });

    describe('combined with other params', () => {
      it('should serialize all params together', () => {
        service.syncToUrl({
          pinnedItems: ['id1', 'id2'],
          categories: ['Cat1', 'Cat2'],
          sortFields: ['name', 'age'],
          sortOrders: [1, -1],
        });

        expect(mockRouter.navigate).toHaveBeenCalledWith(
          [],
          expect.objectContaining({
            queryParams: expect.objectContaining({
              pinned: 'id1,id2',
              categories: 'Cat1,Cat2',
              sortFields: 'name,age',
              sortOrders: '1,-1',
            }),
          }),
        );
      });

      it('should deserialize all params together', async () => {
        queryParamsSubject.next({
          pinned: 'id1,id2',
          categories: 'Cat1,Cat2',
          sortFields: 'name,age',
          sortOrders: '1,-1',
        });

        await new Promise((resolve) => setTimeout(resolve, 100));
        const params = await firstValueFrom(service.params$);

        expect(params.pinnedItems).toEqual(['id1', 'id2']);
        expect(params.categories).toEqual(['Cat1', 'Cat2']);
        expect(params.sortFields).toEqual(['name', 'age']);
        expect(params.sortOrders).toEqual([1, -1]);
      });
    });
  });
});
