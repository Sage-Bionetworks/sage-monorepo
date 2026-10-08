import {
  ComparisonToolNoun,
  ComparisonToolNouns,
  PinnedResultsControlsCopy,
  PinnedResultsCounts,
  PinnedResultsLabel,
  PinnedResultsLabels,
} from '@sagebionetworks/explorers/models';
import { countLabel, labelCase, nounForCount, sentenceCase } from './noun-format';

export const DEFAULT_VIEW_NOUN: ComparisonToolNoun = { singular: 'Result', plural: 'Results' };
export const DEFAULT_ROW_NOUN: ComparisonToolNoun = { singular: 'Row', plural: 'Rows' };
export const NO_NOUNS: ComparisonToolNouns = { viewNoun: null, parentNoun: null };
export const PINNED_RESULTS_HEADING = 'Pinned Results';

export const getViewNounLabel = (prefix: string, { viewNoun }: ComparisonToolNouns): string =>
  `${prefix} ${labelCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural)}`;

// Shown on the pinned results controls and the share URL button while table data loads, and in
// place of the pinned results counts while the pinned rows load
export const TABLE_DATA_LOADING_MESSAGE = 'Waiting for data to load...';
// `TABLE_DATA_LOADING_MESSAGE` for spots too narrow for the full message
export const TABLE_DATA_LOADING_MESSAGE_SHORT = 'Loading...';
// A non-breaking space, so a header line with nothing to show still takes up its height
export const BLANK_LABEL_TEXT = '\u00a0';

const pinnedResultsLabel = (text: string, note: string | null = null): PinnedResultsLabel => ({
  text,
  note,
});

const pinnedResultsCountLabel = (
  count: number | null,
  noun: ComparisonToolNoun,
): PinnedResultsLabel =>
  pinnedResultsLabel(count === null ? TABLE_DATA_LOADING_MESSAGE : countLabel(count, noun));

/**
 * Takes the service's `pinnedResultsCounts` and `nounsForPinCount`. Every variant shows `pinCount`;
 * only a child view also shows `pinnedRowCount`, since elsewhere it equals `pinCount` or lags it
 * right after a view switch while the pinned rows still belong to the previous view
 *
 * A null count is unknown, and `TABLE_DATA_LOADING_MESSAGE` takes its place without changing the
 * header's line count. The standard heading shows it as a note. A child view with an unknown
 * `pinCount` shows it once, then a blank line, so it doesn't read as repeated
 */
export const getPinnedResultsLabels = (
  { pinCount, pinnedRowCount }: PinnedResultsCounts,
  { viewNoun, parentNoun }: ComparisonToolNouns,
): PinnedResultsLabels => {
  if (viewNoun && parentNoun) {
    return {
      heading: pinnedResultsLabel(PINNED_RESULTS_HEADING),
      sublabels:
        pinCount === null
          ? [pinnedResultsLabel(TABLE_DATA_LOADING_MESSAGE), pinnedResultsLabel(BLANK_LABEL_TEXT)]
          : [
              pinnedResultsCountLabel(pinCount, parentNoun),
              pinnedResultsCountLabel(pinnedRowCount, viewNoun),
            ],
    };
  }
  if (viewNoun) {
    return {
      heading: pinnedResultsLabel(PINNED_RESULTS_HEADING),
      sublabels: [pinnedResultsCountLabel(pinCount, viewNoun)],
    };
  }
  return {
    heading:
      pinCount === null
        ? pinnedResultsLabel(PINNED_RESULTS_HEADING, TABLE_DATA_LOADING_MESSAGE)
        : pinnedResultsLabel(`${pinCount} Pinned ${nounForCount(pinCount, DEFAULT_VIEW_NOUN)}`),
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

export const getPinnedFetchFailedMessage = ({ viewNoun }: ComparisonToolNouns): string =>
  `We encountered a problem loading your pinned ${sentenceCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural)}.`;

export const getUnpinnedFetchFailedMessage = ({ viewNoun }: ComparisonToolNouns): string =>
  `We encountered a problem loading ${sentenceCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural)}.`;

export const getShareUrlTooltip = ({ viewNoun }: ComparisonToolNouns): string =>
  `Copy the URL to capture the table's current filtering, sorting, and pinned ${sentenceCase((viewNoun ?? DEFAULT_VIEW_NOUN).plural)}`;

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
