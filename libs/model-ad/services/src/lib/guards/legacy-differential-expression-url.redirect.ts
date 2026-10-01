import {
  LegacyComparisonToolUrlLogEntry,
  LegacyComparisonToolUrlRedirectFn,
} from '@sagebionetworks/explorers/models';

// Sex used to be the deepest differential expression category; it is now a table column. A share
// URL created before that change carries a trailing `Sex - <cohort>` category and pinned ids that
// predate the sex segment, so the presence of the category is what marks a URL as legacy.
//
// That shape check is only reliable while no current URL can carry a `Sex - ` category or a
// two-segment pin. The next change that breaks existing share URLs must therefore introduce an
// explicit URL version rather than another shape check, with this translation as its first step.
//
// Everything below is a literal rather than a live constant or generated enum, so legacy links
// keep resolving the same way however the current app changes.
export const LEGACY_SEX_CATEGORY_PREFIX = 'Sex - ';

const FEMALE = 'Female';
const MALE = 'Male';

export const LEGACY_SEX_COHORT_SEXES: Record<string, readonly string[]> = {
  Females: [FEMALE],
  Males: [MALE],
  'Females & Males': [FEMALE, MALE],
};

export const PIN_SEGMENT_DELIMITER = '~';
const KNOWN_SEXES: ReadonlySet<string> = new Set([FEMALE, MALE]);

export const LEGACY_PIN_SEGMENT_COUNT = 2;
export const CURRENT_PIN_SEGMENT_COUNT = 3;

// The most legacy pins a `Females & Males` URL can carry and still give each pin both a Male and a
// Female row within the pin budget of 50 that the remodel shipped with; above it, each pin gets a
// Female row only. Single-sex URLs are never limited here, since the comparison tool caps the pins
// it restores from the URL.
export const MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE = 25;

export const UNRECOGNIZED_LEGACY_SEX_COHORT_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: unrecognized legacy sex cohort';
export const LEGACY_BOTH_SEXES_NARROWED_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: narrowed a both-sexes cohort to females to fit the pin budget';
export const DROPPED_LEGACY_PINS_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: dropped pinned items that could not be translated';
export const CURRENT_PINS_IN_LEGACY_URL_MESSAGE =
  'legacyDifferentialExpressionUrlRedirect: legacy URL carried pinned items that already have a sex';

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
  const legacyPinCount = legacyPinnedItems.filter(
    (pinnedItem) => countPinSegments(pinnedItem) === LEGACY_PIN_SEGMENT_COUNT,
  ).length;
  const isNarrowedToFemales =
    cohortSexes !== undefined &&
    cohortSexes.length > 1 &&
    legacyPinCount > MAX_LEGACY_PINS_FOR_MALE_OR_FEMALE;
  const sexes = isNarrowedToFemales ? [FEMALE] : (cohortSexes ?? []);

  const currentPinnedItems = new Set<string>();
  const passedThroughPinnedItems: string[] = [];
  const droppedPinnedItems: string[] = [];

  for (const pinnedItem of legacyPinnedItems) {
    const segmentCount = countPinSegments(pinnedItem);

    if (segmentCount === CURRENT_PIN_SEGMENT_COUNT && hasKnownSex(pinnedItem)) {
      currentPinnedItems.add(pinnedItem);
      passedThroughPinnedItems.push(pinnedItem);
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
        pinnedItemCount: legacyPinnedItems.length,
        droppedPinnedItems,
      },
    });
  } else if (droppedPinnedItems.length > 0) {
    warnings.push({
      message: DROPPED_LEGACY_PINS_MESSAGE,
      data: { cohort: legacyCohort, droppedPinnedItems },
    });
  }

  // Web releases ship with a compatible data release, so no share URL should pair the legacy sex
  // category with pins that already carry a sex. They are kept as they are, but one arriving means
  // a URL was built by something other than either release of the app.
  if (passedThroughPinnedItems.length > 0) {
    warnings.push({
      message: CURRENT_PINS_IN_LEGACY_URL_MESSAGE,
      data: { cohort: legacyCohort, currentPinnedItems: passedThroughPinnedItems },
    });
  }

  if (isNarrowedToFemales) {
    notes.push({
      message: LEGACY_BOTH_SEXES_NARROWED_MESSAGE,
      data: {
        cohort: legacyCohort,
        legacyPinCount,
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

function countPinSegments(pinnedItem: string): number {
  return pinnedItem.split(PIN_SEGMENT_DELIMITER).length;
}

// A current pin's last segment must be a sex a row can have, or the pin can never match a row. That
// includes the literal "null" a composite id renders for a blank sex: sex is a required Female or
// Male enum on every differential expression row, so a "null" sex identifies no row.
function hasKnownSex(pinnedItem: string): boolean {
  const sex = pinnedItem.split(PIN_SEGMENT_DELIMITER).at(-1);
  return sex !== undefined && KNOWN_SEXES.has(sex);
}
