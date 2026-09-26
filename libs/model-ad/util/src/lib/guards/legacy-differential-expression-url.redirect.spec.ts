import { MAX_PINNED_ITEMS } from '@sagebionetworks/explorers/constants';
import { Sex } from '@sagebionetworks/model-ad/api-client';
import {
  DROPPED_LEGACY_PINS_MESSAGE,
  LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
  LEGACY_SEX_CATEGORY_PREFIX,
  MAX_LEGACY_PINS_FOR_BOTH_SEXES,
  legacyDifferentialExpressionUrlRedirect,
  PIN_SEGMENT_DELIMITER,
  UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
} from './legacy-differential-expression-url.redirect';

const RNA_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';
const TISSUE_CATEGORY = 'Tissue - Hemibrain';

function legacyCategories(cohort: string) {
  return [RNA_CATEGORY, TISSUE_CATEGORY, `${LEGACY_SEX_CATEGORY_PREFIX}${cohort}`];
}

function legacyPins(count: number) {
  return Array.from({ length: count }, (_, index) => `ENSG${index}${PIN_SEGMENT_DELIMITER}APOE4`);
}

function resolveRedirect(categories: string[], pinnedItems: string[] = []) {
  return legacyDifferentialExpressionUrlRedirect({ categories, pinnedItems });
}

describe('legacyDifferentialExpressionUrlRedirect', () => {
  describe('when the URL is already current', () => {
    it('should report nothing to redirect', () => {
      expect(resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY], ['ENSG1~APOE4~Female'])).toBeNull();
    });

    it('should report nothing to redirect for an empty URL', () => {
      expect(legacyDifferentialExpressionUrlRedirect({})).toBeNull();
    });
  });

  describe('categories', () => {
    it('should drop the legacy sex category and keep the tissue', () => {
      expect(resolveRedirect(legacyCategories('Females'))?.categories).toEqual([
        RNA_CATEGORY,
        TISSUE_CATEGORY,
      ]);
    });

    it('should drop everything after the legacy sex category', () => {
      const categories = [...legacyCategories('Males'), 'Unexpected - Value'];

      expect(resolveRedirect(categories)?.categories).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
    });

    it('should keep only the modality when the tissue is absent', () => {
      const categories = [RNA_CATEGORY, `${LEGACY_SEX_CATEGORY_PREFIX}Females`];

      expect(resolveRedirect(categories)?.categories).toEqual([RNA_CATEGORY]);
    });

    it('should drop the legacy sex category for an unrecognized cohort', () => {
      expect(resolveRedirect(legacyCategories('Unknown'))?.categories).toEqual([
        RNA_CATEGORY,
        TISSUE_CATEGORY,
      ]);
    });
  });

  describe('a single-sex cohort', () => {
    it('should append Female to every pin', () => {
      const result = resolveRedirect(legacyCategories('Females'), ['ENSG1~APOE4', 'ENSG2~3xTg-AD']);

      expect(result?.pinnedItems).toEqual(['ENSG1~APOE4~Female', 'ENSG2~3xTg-AD~Female']);
    });

    it('should append Male to every pin', () => {
      const result = resolveRedirect(legacyCategories('Males'), ['ENSG1~APOE4', 'ENSG2~3xTg-AD']);

      expect(result?.pinnedItems).toEqual(['ENSG1~APOE4~Male', 'ENSG2~3xTg-AD~Male']);
    });

    it('should report no warnings', () => {
      expect(resolveRedirect(legacyCategories('Females'), ['ENSG1~APOE4'])).not.toHaveProperty(
        'warnings',
      );
    });
  });

  describe('a both-sexes cohort within the pin budget', () => {
    it('should expand each pin into a female and a male row, kept adjacent', () => {
      const result = resolveRedirect(legacyCategories('Females & Males'), [
        'ENSG1~APOE4',
        'ENSG2~3xTg-AD',
      ]);

      expect(result?.pinnedItems).toEqual([
        'ENSG1~APOE4~Female',
        'ENSG1~APOE4~Male',
        'ENSG2~3xTg-AD~Female',
        'ENSG2~3xTg-AD~Male',
      ]);
    });

    it('should fill the pin budget exactly at the threshold', () => {
      const pinnedItems = legacyPins(MAX_LEGACY_PINS_FOR_BOTH_SEXES);

      const currentPinnedItems =
        resolveRedirect(legacyCategories('Females & Males'), pinnedItems)?.pinnedItems ?? [];

      expect(currentPinnedItems).toHaveLength(MAX_PINNED_ITEMS);
      expect(currentPinnedItems.filter((pin) => pin.endsWith(Sex.Female))).toHaveLength(
        MAX_LEGACY_PINS_FOR_BOTH_SEXES,
      );
      expect(currentPinnedItems.filter((pin) => pin.endsWith(Sex.Male))).toHaveLength(
        MAX_LEGACY_PINS_FOR_BOTH_SEXES,
      );
    });

    it('should report no warnings at the threshold', () => {
      const pinnedItems = legacyPins(MAX_LEGACY_PINS_FOR_BOTH_SEXES);

      expect(resolveRedirect(legacyCategories('Females & Males'), pinnedItems)).not.toHaveProperty(
        'warnings',
      );
    });
  });

  describe('a both-sexes cohort over the pin budget', () => {
    it('should keep one female row per pin rather than doubling', () => {
      const pinnedItems = legacyPins(MAX_LEGACY_PINS_FOR_BOTH_SEXES + 1);

      const currentPinnedItems =
        resolveRedirect(legacyCategories('Females & Males'), pinnedItems)?.pinnedItems ?? [];

      expect(currentPinnedItems).toHaveLength(pinnedItems.length);
      expect(currentPinnedItems.every((pin) => pin.endsWith(Sex.Female))).toBe(true);
    });

    it('should warn that the cohort was narrowed to females', () => {
      const pinnedItems = legacyPins(MAX_LEGACY_PINS_FOR_BOTH_SEXES + 1);

      expect(resolveRedirect(legacyCategories('Females & Males'), pinnedItems)?.warnings).toEqual([
        {
          message: LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
          data: {
            cohort: 'Females & Males',
            legacyPinCount: pinnedItems.length,
            maxLegacyPinsForBothSexes: MAX_LEGACY_PINS_FOR_BOTH_SEXES,
          },
        },
      ]);
    });
  });

  describe('pins that need no translation', () => {
    it('should pass through pins that already carry a sex', () => {
      const result = resolveRedirect(legacyCategories('Females'), ['ENSG1~APOE4~Male']);

      expect(result?.pinnedItems).toEqual(['ENSG1~APOE4~Male']);
    });

    it('should deduplicate pins that translate to the same id', () => {
      const result = resolveRedirect(legacyCategories('Females'), [
        'ENSG1~APOE4',
        'ENSG1~APOE4~Female',
      ]);

      expect(result?.pinnedItems).toEqual(['ENSG1~APOE4~Female']);
    });
  });

  describe('pins that cannot be translated', () => {
    it('should drop a pin with no delimiter and warn with the dropped pin', () => {
      const result = resolveRedirect(legacyCategories('Females'), ['ENSG1~APOE4', 'ENSG2']);

      expect(result?.pinnedItems).toEqual(['ENSG1~APOE4~Female']);
      expect(result?.warnings).toEqual([
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Females', droppedPinnedItems: ['ENSG2'] },
        },
      ]);
    });

    it('should drop a pin with too many segments and warn with the dropped pin', () => {
      const result = resolveRedirect(legacyCategories('Females'), ['ENSG1~APOE4~Female~extra']);

      expect(result?.pinnedItems).toEqual([]);
      expect(result?.warnings).toEqual([
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Females', droppedPinnedItems: ['ENSG1~APOE4~Female~extra'] },
        },
      ]);
    });
  });

  describe('an unrecognized cohort', () => {
    const legacyPinnedItems = ['ENSG1~APOE4', 'ENSG2~3xTg-AD'];

    it('should drop every legacy pin while still fixing categories', () => {
      const result = resolveRedirect(legacyCategories('Unknown'), legacyPinnedItems);

      expect(result?.categories).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
      expect(result?.pinnedItems).toEqual([]);
    });

    it('should warn about the cohort and the pins it dropped', () => {
      const result = resolveRedirect(legacyCategories('Unknown'), legacyPinnedItems);

      expect(result?.warnings).toEqual([
        {
          message: UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
          data: { cohort: 'Unknown', legacyPinCount: legacyPinnedItems.length },
        },
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Unknown', droppedPinnedItems: legacyPinnedItems },
        },
      ]);
    });

    it('should treat an inherited object key as unrecognized rather than throwing', () => {
      const result = resolveRedirect(legacyCategories('constructor'), legacyPinnedItems);

      expect(result?.pinnedItems).toEqual([]);
      expect(result?.warnings?.[0]?.message).toBe(UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE);
    });
  });

  describe('params it does not translate', () => {
    it('should omit the sort and filter params so the URL keeps them', () => {
      const result = legacyDifferentialExpressionUrlRedirect({
        categories: legacyCategories('Females'),
        pinnedItems: ['ENSG1~APOE4'],
        sortFields: ['gene_symbol'],
        sortOrders: [-1],
        filterSelections: { models: ['APOE4'] },
      });

      expect(result).not.toHaveProperty('sortFields');
      expect(result).not.toHaveProperty('sortOrders');
      expect(result).not.toHaveProperty('filterSelections');
    });
  });
});
