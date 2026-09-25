import { MAX_PINNED_ITEMS } from '@sagebionetworks/explorers/constants';
import { LegacyComparisonToolUrlRedirectFn } from '@sagebionetworks/explorers/models';
import { Sex } from '@sagebionetworks/model-ad/api-client';

// Sex used to be the deepest differential expression category; it is now a table column. A share
// URL created before that change carries a trailing `Sex - <cohort>` category and pinned ids that
// predate the sex segment, so the presence of the category is what marks a URL as legacy.
export const LEGACY_SEX_CATEGORY_PREFIX = 'Sex - ';

export const LEGACY_SEX_COHORT_SEXES: Record<string, Sex[]> = {
  Females: [Sex.Female],
  Males: [Sex.Male],
  'Females & Males': [Sex.Female, Sex.Male],
};

export const PIN_SEGMENT_DELIMITER = '~';
export const LEGACY_PIN_SEGMENT_COUNT = 2;
export const CURRENT_PIN_SEGMENT_COUNT = 3;

// Above this many legacy pins, expanding a both-sexes cohort into one row per sex would exceed the
// pin budget, so those URLs keep a single row per pin instead.
export const MAX_LEGACY_PINS_FOR_BOTH_SEXES = MAX_PINNED_ITEMS / 2;

export const legacyDifferentialExpressionUrlRedirect: LegacyComparisonToolUrlRedirectFn = (
  params,
) => {
  const legacyCategories = params.categories ?? [];
  const legacyPinnedItems = params.pinnedItems ?? [];

  const legacySexCategoryIndex = legacyCategories.findIndex((category) =>
    category.startsWith(LEGACY_SEX_CATEGORY_PREFIX),
  );

  if (legacySexCategoryIndex === -1) {
    return null;
  }

  const legacyCohort = legacyCategories[legacySexCategoryIndex].slice(
    LEGACY_SEX_CATEGORY_PREFIX.length,
  );
  const sexes = resolveSexes(legacyCohort, legacyPinnedItems.length);

  const currentPinnedItems = new Set<string>();
  let droppedPinCount = 0;

  for (const pinnedItem of legacyPinnedItems) {
    const segmentCount = pinnedItem.split(PIN_SEGMENT_DELIMITER).length;

    if (segmentCount === CURRENT_PIN_SEGMENT_COUNT) {
      currentPinnedItems.add(pinnedItem);
    } else if (segmentCount === LEGACY_PIN_SEGMENT_COUNT && sexes.length > 0) {
      // One row per sex, kept adjacent per pin so the pin budget can't trim the sexes unevenly.
      for (const sex of sexes) {
        currentPinnedItems.add(`${pinnedItem}${PIN_SEGMENT_DELIMITER}${sex}`);
      }
    } else {
      droppedPinCount++;
    }
  }

  return {
    categories: legacyCategories.slice(0, legacySexCategoryIndex),
    pinnedItems: [...currentPinnedItems],
    ...(droppedPinCount > 0 && {
      warning: `legacyDifferentialExpressionUrlRedirect: dropped ${droppedPinCount} pinned item(s) that could not be translated`,
    }),
  };
};

function resolveSexes(legacyCohort: string, legacyPinCount: number): Sex[] {
  const sexes = LEGACY_SEX_COHORT_SEXES[legacyCohort] ?? [];
  return sexes.length > 1 && legacyPinCount > MAX_LEGACY_PINS_FOR_BOTH_SEXES ? [Sex.Female] : sexes;
}
