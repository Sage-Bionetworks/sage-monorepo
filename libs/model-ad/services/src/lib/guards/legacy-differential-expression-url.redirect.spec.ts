import {
  CURRENT_PINS_IN_LEGACY_URL_MESSAGE,
  DROPPED_LEGACY_PINS_MESSAGE,
  LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
  MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE,
  legacyDifferentialExpressionUrlRedirect,
  UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
} from './legacy-differential-expression-url.redirect';

const RNA_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';
const TISSUE_CATEGORY = 'Tissue - Hemibrain';

const PIN_WITH_EXTRA_SEGMENT = 'ENSMUSG00000033417~APOE4~Female~extra'; // four segments; a current pin has three

// Bulk pins made before the remodel, which identify a row by gene and model only.
function legacyPins(count: number) {
  return Array.from({ length: count }, (_, index) => `ENSG${index}~APOE4`);
}

function resolveRedirect(categories: string[], pinnedItems: string[] = []) {
  return legacyDifferentialExpressionUrlRedirect({ categories, pinnedItems });
}

describe('legacyDifferentialExpressionUrlRedirect', () => {
  describe('when the URL has no Sex category', () => {
    it('should not redirect a URL whose pins already include a sex', () => {
      expect(
        resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY], ['ENSMUSG00000033417~APOE4~Female']),
      ).toBeNull();
    });

    it('should not redirect a URL with no comparison tool params', () => {
      expect(legacyDifferentialExpressionUrlRedirect({})).toBeNull();
    });
  });

  describe('categories', () => {
    it('should remove the Sex category and keep the modality and tissue', () => {
      expect(resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'])?.categories).toEqual(
        [RNA_CATEGORY, TISSUE_CATEGORY],
      );
    });

    it('should remove any category that follows the Sex category', () => {
      const categories = [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Males', 'Unexpected - Value'];

      expect(resolveRedirect(categories)?.categories).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
    });

    it('should keep only the modality when the URL has no tissue category', () => {
      expect(resolveRedirect([RNA_CATEGORY, 'Sex - Females'])?.categories).toEqual([RNA_CATEGORY]);
    });

    it('should remove the Sex category even when its cohort is not recognized', () => {
      // 'Unknown' is not one of the cohorts the Sex dropdown offered.
      expect(resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Unknown'])?.categories).toEqual(
        [RNA_CATEGORY, TISSUE_CATEGORY],
      );
    });
  });

  describe('when the Sex category is Females or Males', () => {
    // Pins made before the remodel identify a row by gene and model only; current pins add a sex.
    const PINS_WITHOUT_SEX = ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000038619~3xTg-AD'];

    it('should add Female to each pin when the Sex category is Females', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        PINS_WITHOUT_SEX,
      );

      expect(result?.pinnedItems).toEqual([
        'ENSMUSG00000033417~APOE4~Female',
        'ENSMUSG00000038619~3xTg-AD~Female',
      ]);
    });

    it('should add Male to each pin when the Sex category is Males', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Males'],
        PINS_WITHOUT_SEX,
      );

      expect(result?.pinnedItems).toEqual([
        'ENSMUSG00000033417~APOE4~Male',
        'ENSMUSG00000038619~3xTg-AD~Male',
      ]);
    });

    it('should report no warnings', () => {
      expect(
        resolveRedirect(
          [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
          ['ENSMUSG00000033417~APOE4'],
        ),
      ).not.toHaveProperty('warnings');
    });

    it('should add the sex to every pin even when there are more pins than the both-sexes limit', () => {
      const pinnedItems = legacyPins(MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE + 1);

      const result = resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Males'], pinnedItems);

      // The limit only applies to Females & Males; the comparison tool caps restored pins itself.
      expect(result?.pinnedItems).toEqual(pinnedItems.map((pin) => `${pin}~Male`));
      expect(result).not.toHaveProperty('warnings');
    });
  });

  describe('when the Sex category is Females & Males with 25 pins or fewer', () => {
    it('should replace each pin with a Female pin followed by a Male pin', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000038619~3xTg-AD'],
      );

      expect(result?.pinnedItems).toEqual([
        'ENSMUSG00000033417~APOE4~Female',
        'ENSMUSG00000033417~APOE4~Male',
        'ENSMUSG00000038619~3xTg-AD~Female',
        'ENSMUSG00000038619~3xTg-AD~Male',
      ]);
    });

    it('should return one Female and one Male pin per pin when the pin count equals the limit', () => {
      const PIN_COUNT_AT_LIMIT = MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE;
      const EXPECTED_PIN_COUNT = 2 * PIN_COUNT_AT_LIMIT; // one Female and one Male pin each
      const pinnedItems = legacyPins(PIN_COUNT_AT_LIMIT);

      const currentPinnedItems =
        resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'], pinnedItems)
          ?.pinnedItems ?? [];

      expect(currentPinnedItems).toHaveLength(EXPECTED_PIN_COUNT);
      expect(currentPinnedItems.filter((pin) => pin.endsWith('Female'))).toHaveLength(
        PIN_COUNT_AT_LIMIT,
      );
      expect(currentPinnedItems.filter((pin) => pin.endsWith('Male'))).toHaveLength(
        PIN_COUNT_AT_LIMIT,
      );
    });

    it('should report no warnings or notes when the pin count equals the limit', () => {
      const pinnedItems = legacyPins(MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE);

      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        pinnedItems,
      );

      expect(result).not.toHaveProperty('warnings');
      expect(result).not.toHaveProperty('notes');
    });
  });

  describe('when the Sex category is Females & Males with more than 25 pins', () => {
    const PIN_COUNT_OVER_LIMIT = MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE + 1;

    it('should add only Female to each pin', () => {
      const pinnedItems = legacyPins(PIN_COUNT_OVER_LIMIT);

      const currentPinnedItems =
        resolveRedirect([RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'], pinnedItems)
          ?.pinnedItems ?? [];

      // A Female and a Male pin for each would exceed the comparison tool's pin budget.
      expect(currentPinnedItems).toHaveLength(pinnedItems.length);
      expect(currentPinnedItems.every((pin) => pin.endsWith('Female'))).toBe(true);
    });

    it('should record a note, not a warning, that only Female pins were kept', () => {
      const pinnedItems = legacyPins(PIN_COUNT_OVER_LIMIT);

      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        pinnedItems,
      );

      // Keeping only Female pins is the designed behavior for large URLs, so it is not a problem.
      expect(result?.notes).toEqual([
        {
          message: LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
          data: {
            cohort: 'Females & Males',
            legacyPinCount: pinnedItems.length,
            maxLegacyPinsForMaleOrFemale: MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE,
          },
        },
      ]);
      expect(result).not.toHaveProperty('warnings');
    });
  });

  describe('when a pin already includes a sex', () => {
    it('should keep the pin unchanged', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        ['ENSMUSG00000033417~APOE4~Male'],
      );

      expect(result?.pinnedItems).toEqual(['ENSMUSG00000033417~APOE4~Male']);
    });

    it('should warn with the pins that already include a sex', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000038619~3xTg-AD~Male'],
      );

      // No release of the app builds a URL with both a Sex category and pins that include a sex.
      expect(result?.warnings).toEqual([
        {
          message: CURRENT_PINS_IN_LEGACY_URL_MESSAGE,
          data: { cohort: 'Females', currentPinnedItems: ['ENSMUSG00000038619~3xTg-AD~Male'] },
        },
      ]);
    });

    it('should remove the pin and warn when its sex is neither Female nor Male', () => {
      const PIN_WITH_UNKNOWN_SEX = 'ENSMUSG00000033417~APOE4~Unknown'; // a row's sex is always Female or Male

      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        [PIN_WITH_UNKNOWN_SEX],
      );

      expect(result?.pinnedItems).toEqual([]);
      expect(result?.warnings).toEqual([
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Females', droppedPinnedItems: [PIN_WITH_UNKNOWN_SEX] },
        },
      ]);
    });

    it('should remove the pin and warn when its sex is the "null" a composite id renders for a blank sex', () => {
      const PIN_WITH_BLANK_SEX = 'ENSMUSG00000033417~APOE4~null'; // no row has a blank sex

      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        [PIN_WITH_BLANK_SEX],
      );

      expect(result?.pinnedItems).toEqual([]);
      expect(result?.warnings).toEqual([
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Females', droppedPinnedItems: [PIN_WITH_BLANK_SEX] },
        },
      ]);
    });

    it('should not count the pin toward the Females & Males pin limit', () => {
      const pinnedItems = [
        ...legacyPins(MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE),
        'ENSMUSG00000033417~3xTg-AD~Male',
      ];
      const EXPECTED_MALE_PIN_COUNT = MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE + 1; // one per pin without a sex, plus the pin with a sex

      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females & Males'],
        pinnedItems,
      );

      expect(result?.pinnedItems?.filter((pin) => pin.endsWith('Male'))).toHaveLength(
        EXPECTED_MALE_PIN_COUNT,
      );
      // No note, because the pins without a sex are at the limit rather than over it.
      expect(result).not.toHaveProperty('notes');
    });

    it('should return a single pin when a pin without a sex becomes identical to one with a sex', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000033417~APOE4~Female'],
      );

      expect(result?.pinnedItems).toEqual(['ENSMUSG00000033417~APOE4~Female']);
    });
  });

  describe('when a pin has neither two nor three segments', () => {
    it('should remove a pin with only a gene id and warn with the removed pin', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000038619'],
      );

      expect(result?.pinnedItems).toEqual(['ENSMUSG00000033417~APOE4~Female']);
      expect(result?.warnings).toEqual([
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Females', droppedPinnedItems: ['ENSMUSG00000038619'] },
        },
      ]);
    });

    it('should remove a pin with an extra segment and warn with the removed pin', () => {
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        [PIN_WITH_EXTRA_SEGMENT],
      );

      expect(result?.pinnedItems).toEqual([]);
      expect(result?.warnings).toEqual([
        {
          message: DROPPED_LEGACY_PINS_MESSAGE,
          data: { cohort: 'Females', droppedPinnedItems: [PIN_WITH_EXTRA_SEGMENT] },
        },
      ]);
    });
  });

  describe('when the Sex category has an unrecognized cohort', () => {
    // 'Unknown' is not one of the cohorts the Sex dropdown offered.
    const UNRECOGNIZED_COHORT_CATEGORIES = [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Unknown'];
    const PINS_WITHOUT_SEX = ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000038619~3xTg-AD'];

    it('should remove every pin and still remove the Sex category', () => {
      const result = resolveRedirect(UNRECOGNIZED_COHORT_CATEGORIES, PINS_WITHOUT_SEX);

      expect(result?.categories).toEqual([RNA_CATEGORY, TISSUE_CATEGORY]);
      expect(result?.pinnedItems).toEqual([]);
    });

    it('should report the cohort and the removed pins in one warning', () => {
      const result = resolveRedirect(UNRECOGNIZED_COHORT_CATEGORIES, PINS_WITHOUT_SEX);

      expect(result?.warnings).toEqual([
        {
          message: UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
          data: {
            cohort: 'Unknown',
            pinnedItemCount: 2,
            droppedPinnedItems: ['ENSMUSG00000033417~APOE4', 'ENSMUSG00000038619~3xTg-AD'],
          },
        },
      ]);
    });

    it('should list pins with an extra segment in that same warning', () => {
      const result = resolveRedirect(UNRECOGNIZED_COHORT_CATEGORIES, [
        ...PINS_WITHOUT_SEX,
        PIN_WITH_EXTRA_SEGMENT,
      ]);

      expect(result?.warnings).toHaveLength(1);
      expect(result?.warnings?.[0]?.data?.['droppedPinnedItems']).toEqual([
        'ENSMUSG00000033417~APOE4',
        'ENSMUSG00000038619~3xTg-AD',
        PIN_WITH_EXTRA_SEGMENT,
      ]);
    });

    it('should treat a cohort named after a built-in object property as unrecognized without throwing', () => {
      // 'constructor' exists on every object via its prototype.
      const result = resolveRedirect(
        [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - constructor'],
        PINS_WITHOUT_SEX,
      );

      expect(result?.pinnedItems).toEqual([]);
      expect(result?.warnings?.[0]?.message).toBe(UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE);
    });
  });

  describe('sort and filter params', () => {
    it('should leave the sort and filter params out of the result so the URL keeps them unchanged', () => {
      const result = legacyDifferentialExpressionUrlRedirect({
        categories: [RNA_CATEGORY, TISSUE_CATEGORY, 'Sex - Females'],
        pinnedItems: ['ENSMUSG00000033417~APOE4'],
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
