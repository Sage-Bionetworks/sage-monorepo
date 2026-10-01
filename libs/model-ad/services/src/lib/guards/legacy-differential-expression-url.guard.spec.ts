import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  Params,
  RedirectCommand,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import {
  parseCommaSeparatedQueryParam,
  stringifyCommaSeparatedQueryParam,
} from '@sagebionetworks/shared/util';
import { legacyDifferentialExpressionUrlGuard } from './legacy-differential-expression-url.guard';
import {
  DROPPED_LEGACY_PINS_MESSAGE,
  LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
  MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE,
  UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
} from './legacy-differential-expression-url.redirect';

const CT_URL = `/${ROUTE_PATHS.DIFFERENTIAL_EXPRESSION}`;
const RNA_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';
const TISSUE_CATEGORY = 'Tissue - Hippocampus';

// Bulk pins made before the remodel, which identify a row by gene and model only.
function legacyPins(count: number) {
  return Array.from({ length: count }, (_, index) => `ENSMUSG00000033417~Model${index}`);
}

// The guard reads the params off the route snapshot but rebuilds the URL it redirects to from
// `state.url`, so both are derived from one set of decoded values here rather than spelled out twice.
function runGuard(params: Record<string, string[]>, fragment = '') {
  const queryParams = Object.fromEntries(
    Object.entries(params).map(([key, values]) => [key, stringifyCommaSeparatedQueryParam(values)]),
  );
  const route = { queryParams } as unknown as ActivatedRouteSnapshot;
  const state = { url: buildUrl(queryParams, fragment) } as RouterStateSnapshot;

  return TestBed.runInInjectionContext(() =>
    legacyDifferentialExpressionUrlGuard(route, state),
  ) as unknown;
}

function buildUrl(queryParams: Params, fragment: string): string {
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

describe('legacyDifferentialExpressionUrlGuard', () => {
  let warn: jest.SpyInstance;
  let log: jest.SpyInstance;

  beforeEach(() => {
    const logger = TestBed.inject(LoggerService);
    warn = jest.spyOn(logger, 'warn').mockImplementation();
    log = jest.spyOn(logger, 'log').mockImplementation();
  });

  describe('when the URL has no Sex category', () => {
    it('should allow navigation without redirecting', () => {
      expect(
        runGuard({
          categories: [RNA_CATEGORY, TISSUE_CATEGORY],
          pinned: ['ENSMUSG00000033417~3xTg-AD~Female'],
        }),
      ).toBe(true);
    });

    it('should allow navigation without redirecting when the URL has no comparison tool params', () => {
      expect(runGuard({})).toBe(true);
    });
  });

  describe('categories', () => {
    it('should redirect to a URL without the Sex category that keeps the modality and tissue', () => {
      const result = runGuard({ categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'] });

      expect(readParam(result, 'categories')).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
    });

    it('should redirect to a URL without the Sex category even when its cohort is not recognized', () => {
      const result = runGuard({ categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Unknown'] });

      expect(readParam(result, 'categories')).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
    });
  });

  describe('pinned items', () => {
    it('should add Female to each pin when the Sex category is Females', () => {
      const result = runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        pinned: ['ENSMUSG00000033417~3xTg-AD', 'ENSMUSG00000033417~Abca7*V1599M'],
      });

      expect(readParam(result, 'pinned')).toEqual([
        'ENSMUSG00000033417~3xTg-AD~Female',
        'ENSMUSG00000033417~Abca7*V1599M~Female',
      ]);
    });

    it('should add Male to each pin when the Sex category is Males', () => {
      const result = runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Males'],
        pinned: ['ENSMUSG00000033417~3xTg-AD'],
      });

      expect(readParam(result, 'pinned')).toEqual(['ENSMUSG00000033417~3xTg-AD~Male']);
    });

    it('should replace each pin with a Female pin followed by a Male pin when the Sex category is Females & Males with 25 pins or fewer', () => {
      const result = runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        pinned: ['ENSMUSG00000033417~3xTg-AD', 'ENSMUSG00000033417~Abca7*V1599M'],
      });

      expect(readParam(result, 'pinned')).toEqual([
        'ENSMUSG00000033417~3xTg-AD~Female',
        'ENSMUSG00000033417~3xTg-AD~Male',
        'ENSMUSG00000033417~Abca7*V1599M~Female',
        'ENSMUSG00000033417~Abca7*V1599M~Male',
      ]);
    });

    it('should add only Female to each pin when the Sex category is Females & Males with more than 25 pins', () => {
      const PIN_COUNT_OVER_LIMIT = MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE + 1;
      const pinned = legacyPins(PIN_COUNT_OVER_LIMIT);

      const result = runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        pinned,
      });

      // A Female and a Male pin for each would exceed the comparison tool's pin budget so only map each pin to Female
      expect(readParam(result, 'pinned')).toEqual(pinned.map((pin) => `${pin}~Female`));
    });

    it('should redirect to a URL without a pinned param when the original URL had no pins', () => {
      const result = runGuard({ categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'] });

      expect(readQueryParams(result)).not.toHaveProperty('pinned');
    });

    it('should redirect to a URL without a pinned param when none of the legacy pins can be translated', () => {
      const result = runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Unknown'],
        pinned: ['ENSMUSG00000033417~3xTg-AD'],
      });

      // An unrecognized cohort gives no sex to add to a legacy pin, so the pin can't be translated and
      // is removed. With no pins left, the 'pinned' param is removed rather than left empty.
      expect(readQueryParams(result)).not.toHaveProperty('pinned');
    });

    it('should preserve pins and categories containing spaces and special characters in the redirect URL', () => {
      const result = runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        pinned: ['ENSMUSG00000033417~Trem2 R47H NSS'], // spaces must be URL-encoded
      });

      expect(readParam(result, 'categories')).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
      expect(readParam(result, 'pinned')).toEqual(['ENSMUSG00000033417~Trem2 R47H NSS~Female']);
    });
  });

  describe('sort, filter and fragment', () => {
    it('should redirect to a URL with the same sort, filter and fragment', () => {
      const SORT_FIELD = '4 months';
      const DESCENDING_SORT_ORDER = '-1';
      const MODELS_FILTER = ['3xTg-AD', 'Abca7*V1599M'];
      const FRAGMENT = '#legend';

      const result = runGuard(
        {
          categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
          pinned: ['ENSMUSG00000033417~3xTg-AD'],
          sortFields: [SORT_FIELD],
          sortOrders: [DESCENDING_SORT_ORDER],
          models: MODELS_FILTER,
        },
        FRAGMENT,
      );

      expect(readParam(result, 'sortFields')).toEqual([SORT_FIELD]);
      expect(readParam(result, 'sortOrders')).toEqual([DESCENDING_SORT_ORDER]);
      expect(readParam(result, 'models')).toEqual(MODELS_FILTER);
      expect(serialize(result)).toContain(FRAGMENT);
    });
  });

  describe('redirect navigation', () => {
    it('should leave history handling to the router so an in-app navigation keeps the referring page', () => {
      const result = runGuard({ categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'] });

      expect(expectRedirect(result).navigationBehaviorOptions).toBeUndefined();
    });

    it('should redirect to the differential expression page', () => {
      const result = runGuard({ categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'] });

      expect(serialize(result)).toContain('/comparison/expression');
    });
  });

  describe('logging', () => {
    it('should log a warning with the removed pin and the original URL when a pin has an extra segment', () => {
      const PIN_WITH_EXTRA_SEGMENT = 'ENSMUSG00000033417~3xTg-AD~Female~x'; // four segments; a current pin has three

      runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        pinned: [PIN_WITH_EXTRA_SEGMENT],
      });

      expect(warn).toHaveBeenCalledWith(DROPPED_LEGACY_PINS_MESSAGE, {
        cohort: 'Females',
        droppedPinnedItems: [PIN_WITH_EXTRA_SEGMENT],
        url: expect.stringContaining(CT_URL),
      });
    });

    it('should log one warning with the cohort and its removed pins when the cohort is not recognized', () => {
      runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Unknown'],
        pinned: ['ENSMUSG00000033417~3xTg-AD'],
      });

      expect(warn).toHaveBeenCalledTimes(1);
      expect(warn).toHaveBeenCalledWith(
        UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
        expect.objectContaining({
          cohort: 'Unknown',
          droppedPinnedItems: ['ENSMUSG00000033417~3xTg-AD'],
        }),
      );
    });

    it('should log a breadcrumb, not a warning, when only Female pins are kept for Females & Males with more than 25 pins', () => {
      const PIN_COUNT_OVER_LIMIT = MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE + 1;

      runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        pinned: legacyPins(PIN_COUNT_OVER_LIMIT),
      });

      // Keeping only Female pins is the designed behavior for large URLs, so it is not a problem.
      expect(log).toHaveBeenCalledWith(
        LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
        expect.objectContaining({ cohort: 'Females & Males' }),
      );
      expect(warn).not.toHaveBeenCalled();
    });

    it('should not log a warning when a sex is added to every pin', () => {
      runGuard({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        pinned: ['ENSMUSG00000033417~3xTg-AD'],
      });

      expect(warn).not.toHaveBeenCalled();
    });
  });
});
