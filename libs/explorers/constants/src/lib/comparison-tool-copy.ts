import {
  ComparisonToolNoun,
  ComparisonToolNouns,
  PinnedResultsControlsCopy,
  PinnedResultsLabels,
} from '@sagebionetworks/explorers/models';
import { countLabel, labelCase, nounForCount, sentenceCase } from './noun-format';

export const DEFAULT_VIEW_NOUN: ComparisonToolNoun = { singular: 'Result', plural: 'Results' };
export const DEFAULT_ROW_NOUN: ComparisonToolNoun = { singular: 'Row', plural: 'Rows' };
export const NO_NOUNS: ComparisonToolNouns = { viewNoun: null, parentNoun: null };
export const PINNED_RESULTS_HEADING = 'Pinned Results';

export const getViewNounLabel = (prefix: string, { viewNoun }: ComparisonToolNouns): string =>
  `${prefix} ${labelCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural)}`;

/**
 * Takes the service's `pinCount` and `pinnedRowCount`. Every variant shows `pinCount`; only a
 * child view also shows `pinnedRowCount`, since elsewhere it equals `pinCount` or lags it right
 * after a view switch while the pinned rows still belong to the previous view
 */
export const getPinnedResultsLabels = (
  { pinCount, pinnedRowCount }: { pinCount: number; pinnedRowCount: number },
  { viewNoun, parentNoun }: ComparisonToolNouns,
): PinnedResultsLabels => {
  if (viewNoun && parentNoun) {
    return {
      heading: PINNED_RESULTS_HEADING,
      sublabels: [countLabel(pinCount, parentNoun), countLabel(pinnedRowCount, viewNoun)],
    };
  }
  if (viewNoun) {
    return { heading: PINNED_RESULTS_HEADING, sublabels: [countLabel(pinCount, viewNoun)] };
  }
  return {
    heading: `${pinCount} Pinned ${nounForCount(pinCount, DEFAULT_VIEW_NOUN)}`,
    sublabels: [],
  };
};

export const getPinLimitTooltip = (
  pinLimit: number,
  { viewNoun, parentNoun }: ComparisonToolNouns,
): string => {
  const prefix = 'You have already pinned the maximum number of results';
  if (viewNoun && parentNoun) {
    const views = sentenceCase(viewNoun.plural);
    const parent = sentenceCase(parentNoun.singular);
    const limit = `${pinLimit} ${sentenceCase(nounForCount(pinLimit, parentNoun))}`;
    return `${prefix} (${views} for ${limit}). You must unpin all ${views} for a ${parent} before you can pin ${views} for a new ${parent}.`;
  }
  const limit = viewNoun
    ? `${pinLimit} ${sentenceCase(nounForCount(pinLimit, viewNoun))}`
    : `${pinLimit}`;
  return `${prefix} (${limit}). You must unpin some results before you can pin more.`;
};

export const getPinAllTooltip = ({ viewNoun }: ComparisonToolNouns): string =>
  `Pin all matching ${sentenceCase((viewNoun ?? DEFAULT_ROW_NOUN).plural)} to the top.`;

export const getPinToggleTooltip = (
  isPinned: boolean,
  { viewNoun }: ComparisonToolNouns,
): string => {
  const noun = sentenceCase((viewNoun ?? DEFAULT_ROW_NOUN).singular);
  return isPinned ? `Unpin this ${noun}` : `Pin this ${noun} to the top of the list`;
};

export const getNoResultsMessage = ({ viewNoun }: ComparisonToolNouns): string =>
  `No ${sentenceCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural)} found...`;

export const getPinnedResultsControlsCopy = ({
  viewNoun,
}: ComparisonToolNouns): PinnedResultsControlsCopy => {
  const noun = sentenceCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural);
  return {
    downloadButtonTooltip: `Download pinned ${noun}`,
    downloadPanelHeading: `Download pinned ${noun} as:`,
    clearButtonTooltip: `Clear all pinned ${noun}`,
  };
};
