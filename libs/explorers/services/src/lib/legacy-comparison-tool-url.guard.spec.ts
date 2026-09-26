import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  Params,
  RedirectCommand,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { LegacyComparisonToolUrlRedirectFn } from '@sagebionetworks/explorers/models';
import { parseCommaSeparatedQueryParam } from '@sagebionetworks/shared/util';
import {
  createLegacyComparisonToolUrlGuard,
  LEGACY_URL_TRANSLATION_FAILED_MESSAGE,
} from './legacy-comparison-tool-url.guard';
import { SourceLogger } from './logger.service';

const CT_URL = '/comparison/expression';
const GUARD_SOURCE = 'legacyExpressionUrlGuard';

// The guard reads the params off the route snapshot but rebuilds the URL it redirects to from
// `state.url`, so both are derived from one set of params here rather than spelled out twice.
function runGuard(
  resolveRedirect: LegacyComparisonToolUrlRedirectFn,
  queryParams: Record<string, string>,
  fragment = '',
) {
  const guard = createLegacyComparisonToolUrlGuard(GUARD_SOURCE, resolveRedirect);
  const route = { queryParams } as unknown as ActivatedRouteSnapshot;
  const state = { url: buildUrl(queryParams, fragment) } as RouterStateSnapshot;
  return TestBed.runInInjectionContext(() => guard(route, state));
}

function buildUrl(queryParams: Record<string, string>, fragment: string): string {
  const query = Object.entries(queryParams)
    .map(([key, value]) => `${key}=${value}`)
    .join('&');

  return `${CT_URL}${query ? `?${query}` : ''}${fragment}`;
}

function expectRedirect(result: unknown): RedirectCommand {
  expect(result).toBeInstanceOf(RedirectCommand);
  return result as RedirectCommand;
}

function serialize(result: unknown): string {
  return TestBed.inject(Router).serializeUrl(expectRedirect(result).redirectTo);
}

function readQueryParams(result: unknown): Params {
  return TestBed.inject(Router).parseUrl(serialize(result)).queryParams;
}

function readParam(result: unknown, key: string): string[] {
  return parseCommaSeparatedQueryParam(readQueryParams(result)[key]);
}

describe('createLegacyComparisonToolUrlGuard', () => {
  it('should allow activation when there is nothing to redirect', () => {
    expect(runGuard(() => null, { categories: 'RNA' })).toBe(true);
  });

  it('should pass every decoded comparison tool param to the redirect rules', () => {
    const resolveRedirect = jest.fn().mockReturnValue(null);

    runGuard(resolveRedirect, {
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION,Tissue%20-%20Hemibrain',
      pinned: 'ENSG1~APOE4,ENSG2~3xTg-AD',
      sortFields: 'gene_symbol',
      sortOrders: '-1',
      models: 'APOE4',
    });

    expect(resolveRedirect).toHaveBeenCalledWith({
      categories: ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain'],
      pinnedItems: ['ENSG1~APOE4', 'ENSG2~3xTg-AD'],
      sortFields: ['gene_symbol'],
      sortOrders: [-1],
      filterSelections: { models: ['APOE4'] },
    });
  });

  it('should omit the params the URL does not carry', () => {
    const resolveRedirect = jest.fn().mockReturnValue(null);

    runGuard(resolveRedirect, {});

    expect(resolveRedirect).toHaveBeenCalledWith({});
  });

  it('should redirect with the resolved categories and pinned items', () => {
    const result = runGuard(() => ({ categories: ['RNA'], pinnedItems: ['ENSG1~APOE4~Female'] }), {
      categories: 'RNA,Sex%20-%20Females',
      pinned: 'ENSG1~APOE4',
    });

    expect(readParam(result, 'categories')).toEqual(['RNA']);
    expect(readParam(result, 'pinned')).toEqual(['ENSG1~APOE4~Female']);
  });

  it('should replace the legacy URL rather than push a new history entry', () => {
    const result = runGuard(() => ({ categories: ['RNA'], pinnedItems: [] }), {
      categories: 'RNA,Sex%20-%20Females',
    });

    expect(expectRedirect(result).navigationBehaviorOptions).toEqual({ replaceUrl: true });
  });

  it('should round-trip values that need encoding', () => {
    const pinnedItems = ['ENSG00000130203~5xFAD (IU/Jax/Pitt)~Female'];
    const categories = ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain'];

    const result = runGuard(() => ({ categories, pinnedItems }), {
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION,Sex%20-%20Females',
    });

    expect(readParam(result, 'pinned')).toEqual(pinnedItems);
    expect(readParam(result, 'categories')).toEqual(categories);
  });

  it('should drop a param whose resolved list is empty', () => {
    const result = runGuard(() => ({ categories: ['RNA'], pinnedItems: [] }), {
      categories: 'RNA,Sex%20-%20Females',
      pinned: 'ENSG1~APOE4',
    });

    expect(serialize(result)).not.toContain('pinned');
  });

  it('should leave the params the redirect omits untouched, along with the fragment', () => {
    const result = runGuard(
      () => ({ categories: ['RNA'], pinnedItems: ['ENSG1~APOE4~Female'] }),
      {
        categories: 'RNA,Sex%20-%20Females',
        sortFields: 'gene_symbol',
        sortOrders: '1',
        models: 'APOE4',
      },
      '#legend',
    );

    expect(readQueryParams(result)).toEqual({
      categories: 'RNA',
      pinned: 'ENSG1~APOE4~Female',
      sortFields: 'gene_symbol',
      sortOrders: '1',
      models: 'APOE4',
    });
    expect(serialize(result)).toContain('#legend');
  });

  it('should rewrite only the param a partial redirect returns', () => {
    const result = runGuard(() => ({ sortFields: ['model'] }), {
      categories: 'RNA',
      sortFields: 'gene_symbol',
      sortOrders: '1',
    });

    expect(readQueryParams(result)).toEqual({
      categories: 'RNA',
      sortFields: 'model',
      sortOrders: '1',
    });
  });

  it('should remove the key of a param the redirect nulls out', () => {
    const result = runGuard(() => ({ sortOrders: null }), {
      sortFields: 'gene_symbol',
      sortOrders: '5',
    });

    expect(readQueryParams(result)).toEqual({ sortFields: 'gene_symbol' });
  });

  it('should replace the filter params when the redirect returns filter selections', () => {
    const result = runGuard(() => ({ filterSelections: { sex: ['Female'] } }), {
      categories: 'RNA',
      models: 'APOE4',
      sex: 'Male',
    });

    expect(readQueryParams(result)).toEqual({ categories: 'RNA', sex: 'Female' });
  });

  describe('logging', () => {
    const legacyQueryParams = { categories: 'RNA,Sex%20-%20Females' };
    let warn: jest.SpyInstance;
    let error: jest.SpyInstance;

    beforeEach(() => {
      warn = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
      error = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    });

    afterEach(() => {
      jest.restoreAllMocks();
    });

    it('should report under the source it was built with', () => {
      runGuard(() => {
        throw new Error('unexpected legacy param shape');
      }, legacyQueryParams);

      expect(error.mock.contexts[0]).toMatchObject({ source: GUARD_SOURCE });
    });

    it('should log each redirect warning with its data and the legacy URL', () => {
      const droppedPinsWarning = {
        message: 'Dropped pinned items that could not be translated',
        data: { droppedPinnedItems: ['ENSG1'] },
      };
      const unrecognizedCohortWarning = { message: 'Unrecognized legacy sex cohort' };

      runGuard(
        () => ({
          categories: ['RNA'],
          pinnedItems: [],
          warnings: [droppedPinsWarning, unrecognizedCohortWarning],
        }),
        legacyQueryParams,
      );

      const url = buildUrl(legacyQueryParams, '');
      expect(warn).toHaveBeenCalledTimes(2);
      expect(warn).toHaveBeenCalledWith(droppedPinsWarning.message, {
        ...droppedPinsWarning.data,
        url,
      });
      expect(warn).toHaveBeenCalledWith(unrecognizedCohortWarning.message, { url });
    });

    it('should not log when the redirect reports no warnings', () => {
      runGuard(() => ({ categories: ['RNA'], pinnedItems: [] }), legacyQueryParams);

      expect(warn).not.toHaveBeenCalled();
      expect(error).not.toHaveBeenCalled();
    });

    it('should log an error and allow activation when the redirect rules throw', () => {
      const cause = new Error('unexpected legacy param shape');

      const result = runGuard(() => {
        throw cause;
      }, legacyQueryParams);

      expect(result).toBe(true);
      expect(error).toHaveBeenCalledWith(LEGACY_URL_TRANSLATION_FAILED_MESSAGE, {
        error: cause,
        data: { url: buildUrl(legacyQueryParams, '') },
      });
    });
  });
});
