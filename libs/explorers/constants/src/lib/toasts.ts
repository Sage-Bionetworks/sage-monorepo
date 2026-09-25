import { ComparisonToolNouns } from '@sagebionetworks/explorers/models';
import { DEFAULT_VIEW_NOUN, NO_NOUNS } from './comparison-tool-copy';
import { nounForCount, sentenceCase } from './noun-format';

export const TOAST_DURATION_MS = 5000; // milliseconds

export const getPinLimitWarning = (
  pinnedCount: number,
  pinLimit: number,
  { viewNoun, parentNoun }: ComparisonToolNouns = NO_NOUNS,
): string => {
  const pinnedNoun = viewNoun ?? DEFAULT_VIEW_NOUN;
  const limitNoun = viewNoun && parentNoun ? parentNoun : pinnedNoun;
  const pinned = `${pinnedCount} ${sentenceCase(nounForCount(pinnedCount, pinnedNoun))} ${pinnedCount === 1 ? 'was' : 'were'}`;
  const limit = `${pinLimit} pinned ${sentenceCase(nounForCount(pinLimit, limitNoun))}`;
  const warning = `Only ${pinned} pinned, because you reached the maximum of ${limit}.`;
  if (!viewNoun || !parentNoun) return warning;
  return `${warning} Some ${sentenceCase(viewNoun.plural)} were skipped because they belong to a ${sentenceCase(parentNoun.singular)} not already in your pinned list.`;
};
