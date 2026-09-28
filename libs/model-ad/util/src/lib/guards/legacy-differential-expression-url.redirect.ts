import { MAX_PINNED_ITEMS } from '@sagebionetworks/explorers/constants';
import {
  LegacyComparisonToolUrlLogEntry,
  LegacyComparisonToolUrlRedirectFn,
} from '@sagebionetworks/explorers/models';
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

// The most legacy pins a `Females & Males` URL can carry and still give each pin both a Male and a
// Female row within the pin budget; above it, each pin gets a Female row only. Single-sex URLs are
// never limited here, since the comparison tool caps the pins it restores from the URL.
export const MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE = MAX_PINNED_ITEMS / 2;

export const UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: unrecognized legacy sex cohort';
export const LEGACY_BOTH_SEXES_NARROWED_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: narrowed a both-sexes cohort to females to fit the pin budget';
export const DROPPED_LEGACY_PINS_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: dropped pinned items that could not be translated';

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
  const cohortSexes = Object.hasOwn(LEGACY_SEX_COHORT_SEXES, legacyCohort)
    ? LEGACY_SEX_COHORT_SEXES[legacyCohort]
    : undefined;
  const isNarrowedToFemales =
    cohortSexes !== undefined &&
    cohortSexes.length > 1 &&
    legacyPinnedItems.length > MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE;
  const sexes = isNarrowedToFemales ? [Sex.Female] : (cohortSexes ?? []);

  const currentPinnedItems = new Set<string>();
  const droppedPinnedItems: string[] = [];

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
      droppedPinnedItems.push(pinnedItem);
    }
  }

  const warnings: LegacyComparisonToolUrlLogEntry[] = [];
  const notes: LegacyComparisonToolUrlLogEntry[] = [];

  // An unrecognized cohort is the root cause of every legacy pin it drops, so it reports those pins
  // itself rather than raising a second warning for the same URL.
  if (cohortSexes === undefined) {
    warnings.push({
      message: UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE,
      data: {
        cohort: legacyCohort,
        legacyPinCount: legacyPinnedItems.length,
        droppedPinnedItems,
      },
    });
  } else if (droppedPinnedItems.length > 0) {
    warnings.push({
      message: DROPPED_LEGACY_PINS_MESSAGE,
      data: { cohort: legacyCohort, droppedPinnedItems },
    });
  }

  if (isNarrowedToFemales) {
    notes.push({
      message: LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
      data: {
        cohort: legacyCohort,
        legacyPinCount: legacyPinnedItems.length,
        maxLegacyPinsForMaleOrFemale: MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE,
      },
    });
  }

  return {
    categories: legacyCategories.slice(0, legacySexCategoryIndex),
    pinnedItems: [...currentPinnedItems],
    ...(warnings.length > 0 && { warnings }),
    ...(notes.length > 0 && { notes }),
  };
};
