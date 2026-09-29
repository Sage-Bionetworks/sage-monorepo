import { Location } from '@angular/common';
import { provideLocationMocks } from '@angular/common/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  Params,
  provideRouter,
  RedirectCommand,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { LegacyComparisonToolUrlRedirectFn } from '@sagebionetworks/explorers/models';
import { parseCommaSeparatedQueryParam } from '@sagebionetworks/shared/util';
import {
  createLegacyComparisonToolUrlGuard,
  LEGACY_URL_IN_APP_NAVIGATION_MESSAGE,
  LEGACY_URL_REDIRECTED_MESSAGE,
  LEGACY_URL_TRANSLATION_FAILED_MESSAGE,
} from './legacy-comparison-tool-url.guard';
import { LoggerService } from './logger.service';

const CT_URL = '/comparison/expression';

// Old-format URLs carried the sex cohort as a category.
const LEGACY_CATEGORIES_PARAM = 'RNA%20-%20DIFFERENTIAL%20EXPRESSION,Sex%20-%20Females';
const LEGACY_PIN = 'ENSMUSG00000033417~APOE4'; // gene~model, without the sex the current format adds
const TRANSLATED_PIN = 'ENSMUSG00000033417~APOE4~Female';
const TRANSLATION_ERROR_MESSAGE = 'unexpected legacy param shape';

// The guard reads the params off the route snapshot but rebuilds the URL it redirects to from
// `state.url`, so both are derived from one set of params here rather than spelled out twice.
function runGuard(
  resolveRedirect: LegacyComparisonToolUrlRedirectFn,
  queryParams: Record<string, string>,
  fragment = '',
) {
  const guard = createLegacyComparisonToolUrlGuard(resolveRedirect);
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
  beforeEach(() => {
    jest.spyOn(TestBed.inject(LoggerService), 'log').mockImplementation();
  });

  it('should load the URL unchanged when the redirect rules find nothing to translate', () => {
    const NOTHING_TO_TRANSLATE = null; // the redirect rules return null for a current-format URL

    const result = runGuard(() => NOTHING_TO_TRANSLATE, {
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION',
    });

    // `true` lets the navigation continue to the URL as requested.
    expect(result).toBe(true);
  });

  it('should give the redirect rules every comparison tool param from the URL, decoded into lists', () => {
    const resolveRedirect = jest.fn().mockReturnValue(null);

    runGuard(resolveRedirect, {
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION,Tissue%20-%20Hemibrain',
      pinned: 'ENSMUSG00000033417~APOE4,ENSMUSG00000024401~3xTg-AD',
      sortFields: 'gene_symbol',
      sortOrders: '-1',
      models: 'APOE4',
    });

    // Any URL param that isn't one of the fixed comparison tool params is a filter selection.
    expect(resolveRedirect).toHaveBeenCalledWith({
      categories: ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain'],
      pinnedItems: ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000024401~3xTg-AD'],
      sortFields: ['gene_symbol'],
      sortOrders: [-1],
      filterSelections: { models: ['APOE4'] },
    });
  });

  it('should give the redirect rules no params when the URL has none', () => {
    const resolveRedirect = jest.fn().mockReturnValue(null);

    runGuard(resolveRedirect, {});

    expect(resolveRedirect).toHaveBeenCalledWith({});
  });

  it('should redirect to the categories and pinned items the redirect rules return', () => {
    const result = runGuard(
      () => ({ categories: ['RNA - DIFFERENTIAL EXPRESSION'], pinnedItems: [TRANSLATED_PIN] }),
      { categories: LEGACY_CATEGORIES_PARAM, pinned: LEGACY_PIN },
    );

    expect(readParam(result, 'categories')).toEqual(['RNA - DIFFERENTIAL EXPRESSION']);
    expect(readParam(result, 'pinned')).toEqual([TRANSLATED_PIN]);
  });

  it('should leave history handling to the router so an in-app navigation keeps the referring page', () => {
    const result = runGuard(
      () => ({ categories: ['RNA - DIFFERENTIAL EXPRESSION'], pinnedItems: [] }),
      { categories: LEGACY_CATEGORIES_PARAM },
    );

    expect(expectRedirect(result).navigationBehaviorOptions).toBeUndefined();
  });

  it('should keep pinned items and categories containing spaces and special characters intact in the redirect URL', () => {
    const pinnedItems = ['ENSG00000130203~5xFAD (IU/Jax/Pitt)~Female'];
    const categories = ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain'];

    const result = runGuard(() => ({ categories, pinnedItems }), {
      categories: LEGACY_CATEGORIES_PARAM,
    });

    expect(readParam(result, 'pinned')).toEqual(pinnedItems);
    expect(readParam(result, 'categories')).toEqual(categories);
  });

  it('should remove a param from the URL when the redirect rules return an empty list for it', () => {
    const NO_PINNED_ITEMS: string[] = [];

    const result = runGuard(
      () => ({ categories: ['RNA - DIFFERENTIAL EXPRESSION'], pinnedItems: NO_PINNED_ITEMS }),
      { categories: LEGACY_CATEGORIES_PARAM, pinned: LEGACY_PIN },
    );

    expect(serialize(result)).not.toContain('pinned');
  });

  it('should keep the params the redirect rules do not return, and the fragment, unchanged', () => {
    const result = runGuard(
      () => ({ categories: ['RNA - DIFFERENTIAL EXPRESSION'], pinnedItems: [TRANSLATED_PIN] }),
      {
        categories: LEGACY_CATEGORIES_PARAM,
        sortFields: 'gene_symbol',
        sortOrders: '1',
        models: 'APOE4',
      },
      '#legend',
    );

    // Each list item is encoded on its own before joining, so a rewritten list param still reads
    // back encoded once the URL itself is decoded.
    expect(readQueryParams(result)).toEqual({
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION',
      pinned: TRANSLATED_PIN,
      sortFields: 'gene_symbol',
      sortOrders: '1',
      models: 'APOE4',
    });
    expect(serialize(result)).toContain('#legend');
  });

  it('should change only the param the redirect rules return and keep the others', () => {
    const TRANSLATED_SORT_FIELD = 'model';

    const result = runGuard(() => ({ sortFields: [TRANSLATED_SORT_FIELD] }), {
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION',
      sortFields: 'gene_symbol',
      sortOrders: '1',
    });

    expect(readQueryParams(result)).toEqual({
      categories: 'RNA - DIFFERENTIAL EXPRESSION',
      sortFields: TRANSLATED_SORT_FIELD,
      sortOrders: '1',
    });
  });

  it('should remove a param from the URL when the redirect rules return null for it', () => {
    const INVALID_SORT_ORDER = '5'; // only 1 (ascending) and -1 (descending) are valid

    const result = runGuard(() => ({ sortOrders: null }), {
      sortFields: 'gene_symbol',
      sortOrders: INVALID_SORT_ORDER,
    });

    expect(readQueryParams(result)).toEqual({ sortFields: 'gene_symbol' });
  });

  it('should replace all filter params with the filter selections the redirect rules return', () => {
    const TRANSLATED_SEX = 'Female';

    const result = runGuard(() => ({ filterSelections: { sex: [TRANSLATED_SEX] } }), {
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION',
      models: 'APOE4',
      sex: 'Male',
    });

    // The models filter is dropped because the returned selections don't include it.
    expect(readQueryParams(result)).toEqual({
      categories: 'RNA - DIFFERENTIAL EXPRESSION',
      sex: TRANSLATED_SEX,
    });
  });

  describe('logging', () => {
    const legacyQueryParams = { categories: LEGACY_CATEGORIES_PARAM };
    const LEGACY_URL =
      '/comparison/expression?categories=RNA%20-%20DIFFERENTIAL%20EXPRESSION,Sex%20-%20Females';
    const translateToRnaOnly = () => ({
      categories: ['RNA - DIFFERENTIAL EXPRESSION'],
      pinnedItems: [],
    });
    let logger: LoggerService;
    let log: jest.SpyInstance;
    let warn: jest.SpyInstance;
    let error: jest.SpyInstance;

    beforeEach(() => {
      logger = TestBed.inject(LoggerService);
      log = jest.spyOn(logger, 'log').mockImplementation();
      warn = jest.spyOn(logger, 'warn').mockImplementation();
      error = jest.spyOn(logger, 'error').mockImplementation();
    });

    it('should log the old and new URLs as a Sentry breadcrumb, without a warning, when an old share link is opened', () => {
      const result = runGuard(translateToRnaOnly, legacyQueryParams);

      expect(log).toHaveBeenCalledWith(LEGACY_URL_REDIRECTED_MESSAGE, {
        from: LEGACY_URL,
        to: serialize(result),
      });
      expect(warn).not.toHaveBeenCalled();
    });

    it('should warn with the old and new URLs when an old-format URL is reached by navigating within the app', () => {
      // A navigated router means the app was already loaded, so the URL came from an in-app link.
      TestBed.inject(Router).navigated = true;

      const result = runGuard(translateToRnaOnly, legacyQueryParams);

      expect(warn).toHaveBeenCalledWith(LEGACY_URL_IN_APP_NAVIGATION_MESSAGE, {
        from: LEGACY_URL,
        to: serialize(result),
      });
      expect(log).not.toHaveBeenCalledWith(LEGACY_URL_REDIRECTED_MESSAGE, expect.anything());
    });

    it('should log each note from the redirect rules as a Sentry breadcrumb, without a warning', () => {
      const narrowedNote = {
        message: 'Narrowed a both-sexes cohort to females',
        data: { legacyPinCount: 26 },
      };

      runGuard(
        () => ({
          categories: ['RNA - DIFFERENTIAL EXPRESSION'],
          pinnedItems: [],
          notes: [narrowedNote],
        }),
        legacyQueryParams,
      );

      expect(log).toHaveBeenCalledWith(narrowedNote.message, {
        ...narrowedNote.data,
        url: LEGACY_URL,
      });
      expect(warn).not.toHaveBeenCalled();
    });

    it('should log nothing when the redirect rules find nothing to translate', () => {
      runGuard(() => null, legacyQueryParams);

      expect(log).not.toHaveBeenCalled();
      expect(warn).not.toHaveBeenCalled();
      expect(error).not.toHaveBeenCalled();
    });

    it('should log each warning from the redirect rules with its data and the old URL', () => {
      const droppedPinsWarning = {
        message: 'Dropped pinned items that could not be translated',
        data: { droppedPinnedItems: ['ENSMUSG00000033417'] },
      };
      const unrecognizedCohortWarning = { message: 'Unrecognized legacy sex cohort' };
      const EXPECTED_WARNING_COUNT = 2; // one per warning the redirect rules report

      runGuard(
        () => ({
          categories: ['RNA - DIFFERENTIAL EXPRESSION'],
          pinnedItems: [],
          warnings: [droppedPinsWarning, unrecognizedCohortWarning],
        }),
        legacyQueryParams,
      );

      expect(warn).toHaveBeenCalledTimes(EXPECTED_WARNING_COUNT);
      expect(warn).toHaveBeenCalledWith(droppedPinsWarning.message, {
        ...droppedPinsWarning.data,
        url: LEGACY_URL,
      });
      expect(warn).toHaveBeenCalledWith(unrecognizedCohortWarning.message, { url: LEGACY_URL });
    });

    it('should log no warnings or errors when the redirect rules report no warnings', () => {
      runGuard(translateToRnaOnly, legacyQueryParams);

      expect(warn).not.toHaveBeenCalled();
      expect(error).not.toHaveBeenCalled();
    });

    it('should log an error with the failing URL and redirect to the default view when the redirect rules throw', () => {
      const cause = new Error(TRANSLATION_ERROR_MESSAGE);
      const DEFAULT_VIEW_URL = '/comparison/expression#legend'; // every query param removed

      const result = runGuard(
        () => {
          throw cause;
        },
        legacyQueryParams,
        '#legend',
      );

      expect(error).toHaveBeenCalledWith(LEGACY_URL_TRANSLATION_FAILED_MESSAGE, expect.any(Error));
      // Sentry receives only the error object, so the URL has to be in the error's own message.
      const reportedError = error.mock.calls[0][1] as Error;
      expect(reportedError.message).toContain(
        '/comparison/expression?categories=RNA%20-%20DIFFERENTIAL%20EXPRESSION,Sex%20-%20Females#legend',
      );
      expect(reportedError.cause).toBe(cause);
      expect(serialize(result)).toBe(DEFAULT_VIEW_URL);
      expect(expectRedirect(result).navigationBehaviorOptions).toBeUndefined();
    });

    it('should load the URL without redirecting again when a URL with no params fails to translate', () => {
      const result = runGuard(() => {
        throw new Error(TRANSLATION_ERROR_MESSAGE);
      }, {});

      // A URL with no params is already the default view, so redirecting to it would loop.
      expect(result).toBe(true);
      expect(error).toHaveBeenCalledTimes(1);
    });
  });
});

describe('createLegacyComparisonToolUrlGuard browser history', () => {
  @Component({ template: '' })
  class EmptyPageComponent {}

  const REFERRING_PAGE_URL = '/home';

  beforeEach(() => {
    const guard = createLegacyComparisonToolUrlGuard(({ categories }) =>
      categories?.some((category) => category.startsWith('Sex - '))
        ? { categories: ['RNA - DIFFERENTIAL EXPRESSION'] }
        : null,
    );

    TestBed.configureTestingModule({
      providers: [
        provideLocationMocks(),
        provideRouter([
          { path: 'home', component: EmptyPageComponent },
          {
            path: CT_URL.slice(1),
            component: EmptyPageComponent,
            canActivate: [guard],
            runGuardsAndResolvers: 'paramsOrQueryParamsChange',
          },
        ]),
      ],
    });
    jest.spyOn(TestBed.inject(LoggerService), 'log').mockImplementation();
    jest.spyOn(TestBed.inject(LoggerService), 'warn').mockImplementation();
  });

  it('should return to the referring page on Back after an in-app link to an old-format URL', async () => {
    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);
    await router.navigateByUrl(REFERRING_PAGE_URL);

    await router.navigateByUrl(`${CT_URL}?categories=${LEGACY_CATEGORIES_PARAM}`);
    expect(location.path()).toMatch(new RegExp(`^${CT_URL}\\?`));

    location.back();
    expect(location.path()).toBe(REFERRING_PAGE_URL);
  });
});
