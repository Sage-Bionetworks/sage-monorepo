import { Filter } from '@sagebionetworks/openchallenges/ui';

/**
 * A generic function used to retrieve the label for values of challenge properties
 */

export function getLabelByFilterValue(
  filter: Filter[],
  value: any
): string | undefined {
  const filterItem = filter.find((item) => item.value === value);
  return filterItem ? filterItem.label : undefined;
}
