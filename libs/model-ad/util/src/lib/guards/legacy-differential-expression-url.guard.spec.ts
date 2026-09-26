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
  LEGACY_SEX_CATEGORY_PREFIX,
  MAX_LEGACY_PINS_FOR_BOTH_SEXES,
  PIN_SEGMENT_DELIMITER,
  UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
} from './legacy-differential-expression-url.redirect';

const CT_URL = `/${ROUTE_PATHS.DIFFERENTIAL_EXPRESSION}`;
const RNA_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';
const TISSUE_CATEGORY = 'Tissue - Hippocampus';
const CACUL1_ENSEMBL_GENE_ID = 'ENSMUSG00000033417';

function legacyCategories(cohort: string) {
  return [RNA_CATEGORY, TISSUE_CATEGORY, `${LEGACY_SEX_CATEGORY_PREFIX}${cohort}`];
}

function legacyPin(model: string) {
  return `${CACUL1_ENSEMBL_GENE_ID}${PIN_SEGMENT_DELIMITER}${model}`;
}

function legacyPins(count: number) {
  return Array.from({ length: count }, (_, index) => legacyPin(`Model${index}`));
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

  beforeEach(() => {
    warn = jest.spyOn(TestBed.inject(LoggerService), 'warn').mockImplementation();
  });

  describe('a URL that is already current', () => {
    it('should allow activation when no legacy sex category is present', () => {
      expect(
        runGuard({
          categories: [RNA_CATEGORY, TISSUE_CATEGORY],
          pinned: [`${legacyPin('3xTg-AD')}${PIN_SEGMENT_DELIMITER}Female`],
        }),
      ).toBe(true);
    });

    it('should allow activation for a URL with no comparison tool params', () => {
      expect(runGuard({})).toBe(true);
    });
  });

  describe('categories', () => {
    it('should redirect with the legacy sex category dropped and the tissue kept', () => {
      const result = runGuard({ categories: legacyCategories('Females') });

      expect(readParam(result, 'categories')).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
    });

    it('should redirect with the categories fixed for an unrecognized cohort', () => {
      const result = runGuard({ categories: legacyCategories('Unknown') });

      expect(readParam(result, 'categories')).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
    });
  });

  describe('pinned items', () => {
    it('should append Female to every pin of a females cohort', () => {
      const result = runGuard({
        categories: legacyCategories('Females'),
        pinned: [legacyPin('3xTg-AD'), legacyPin('Abca7*V1599M')],
      });

      expect(readParam(result, 'pinned')).toEqual([
        `${legacyPin('3xTg-AD')}${PIN_SEGMENT_DELIMITER}Female`,
        `${legacyPin('Abca7*V1599M')}${PIN_SEGMENT_DELIMITER}Female`,
      ]);
    });

    it('should append Male to every pin of a males cohort', () => {
      const result = runGuard({
        categories: legacyCategories('Males'),
        pinned: [legacyPin('3xTg-AD')],
      });

      expect(readParam(result, 'pinned')).toEqual([
        `${legacyPin('3xTg-AD')}${PIN_SEGMENT_DELIMITER}Male`,
      ]);
    });

    it('should expand each pin of a both-sexes cohort within the pin budget', () => {
      const result = runGuard({
        categories: legacyCategories('Females & Males'),
        pinned: [legacyPin('3xTg-AD'), legacyPin('Abca7*V1599M')],
      });

      expect(readParam(result, 'pinned')).toEqual([
        `${legacyPin('3xTg-AD')}${PIN_SEGMENT_DELIMITER}Female`,
        `${legacyPin('3xTg-AD')}${PIN_SEGMENT_DELIMITER}Male`,
        `${legacyPin('Abca7*V1599M')}${PIN_SEGMENT_DELIMITER}Female`,
        `${legacyPin('Abca7*V1599M')}${PIN_SEGMENT_DELIMITER}Male`,
      ]);
    });

    it('should keep one female row per pin for a both-sexes cohort over the pin budget', () => {
      const pinned = legacyPins(MAX_LEGACY_PINS_FOR_BOTH_SEXES + 1);

      const result = runGuard({ categories: legacyCategories('Females & Males'), pinned });

      expect(readParam(result, 'pinned')).toEqual(
        pinned.map((pin) => `${pin}${PIN_SEGMENT_DELIMITER}Female`),
      );
    });

    it('should drop the pinned param when the legacy URL carried no pins', () => {
      const result = runGuard({ categories: legacyCategories('Females') });

      expect(readQueryParams(result)).not.toHaveProperty('pinned');
    });

    it('should drop the pinned param when no pin can be translated', () => {
      const result = runGuard({
        categories: legacyCategories('Unknown'),
        pinned: [legacyPin('3xTg-AD')],
      });

      expect(readQueryParams(result)).not.toHaveProperty('pinned');
    });

    it('should round-trip pins and categories whose values need encoding', () => {
      const categories = legacyCategories('Females');
      const pin = legacyPin('Trem2 R47H NSS');

      const result = runGuard({ categories, pinned: [pin] });

      expect(readParam(result, 'categories')).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
      expect(readParam(result, 'pinned')).toEqual([`${pin}${PIN_SEGMENT_DELIMITER}Female`]);
    });
  });

  describe('params the redirect does not translate', () => {
    it('should redirect with the sort, filter and fragment parts untouched', () => {
      const sortField = '4 months';
      const models = ['3xTg-AD', 'Abca7*V1599M'];

      const result = runGuard(
        {
          categories: legacyCategories('Females'),
          pinned: [legacyPin('3xTg-AD')],
          sortFields: [sortField],
          sortOrders: ['-1'],
          models,
        },
        '#legend',
      );

      expect(readParam(result, 'sortFields')).toEqual([sortField]);
      expect(readParam(result, 'sortOrders')).toEqual(['-1']);
      expect(readParam(result, 'models')).toEqual(models);
      expect(serialize(result)).toContain('#legend');
    });
  });

  describe('redirect semantics', () => {
    it('should replace the legacy URL rather than push a new history entry', () => {
      const result = runGuard({ categories: legacyCategories('Females') });

      expect(expectRedirect(result).navigationBehaviorOptions).toEqual({ replaceUrl: true });
    });

    it('should redirect to the differential expression path', () => {
      const result = runGuard({ categories: legacyCategories('Females') });

      expect(serialize(result)).toContain(CT_URL);
    });
  });

  describe('warnings', () => {
    it('should log each dropped pin along with the legacy URL', () => {
      const untranslatablePin = `${legacyPin('3xTg-AD')}${PIN_SEGMENT_DELIMITER}Female${PIN_SEGMENT_DELIMITER}x`;

      runGuard({ categories: legacyCategories('Females'), pinned: [untranslatablePin] });

      expect(warn).toHaveBeenCalledWith(DROPPED_LEGACY_PINS_MESSAGE, {
        cohort: 'Females',
        droppedPinnedItems: [untranslatablePin],
        url: expect.stringContaining(CT_URL),
      });
    });

    it('should log an unrecognized cohort', () => {
      runGuard({ categories: legacyCategories('Unknown') });

      expect(warn).toHaveBeenCalledWith(
        UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
        expect.objectContaining({ cohort: 'Unknown' }),
      );
    });

    it('should log a both-sexes cohort narrowed to females', () => {
      runGuard({
        categories: legacyCategories('Females & Males'),
        pinned: legacyPins(MAX_LEGACY_PINS_FOR_BOTH_SEXES + 1),
      });

      expect(warn).toHaveBeenCalledWith(
        LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
        expect.objectContaining({ cohort: 'Females & Males' }),
      );
    });

    it('should not log a warning when every pin translates', () => {
      runGuard({
        categories: legacyCategories('Females'),
        pinned: [legacyPin('3xTg-AD')],
      });

      expect(warn).not.toHaveBeenCalled();
    });
  });
});
