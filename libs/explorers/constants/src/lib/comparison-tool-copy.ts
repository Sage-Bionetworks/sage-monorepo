import {
  ComparisonToolNoun,
  ComparisonToolNouns,
  OverwritePinsMessage,
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
  note: string | null = null,
): PinnedResultsLabel =>
  count === null
    ? pinnedResultsLabel(TABLE_DATA_LOADING_MESSAGE)
    : pinnedResultsLabel(countLabel(count, noun), note);

// No note when nothing is hidden, or while that is unknown
const hiddenPinsNote = (hiddenCount: number | null): string | null =>
  hiddenCount ? `${hiddenCount} hidden` : null;

/**
 * Takes the service's `pinnedResultsCounts` and `nouns`. Every variant shows `pinCount`;
 * only a child view also shows `pinnedRowCount`, since elsewhere it equals `pinCount` or lags it
 * right after a view switch while the pinned rows still belong to the previous view
 *
 * A null count is unknown, and `TABLE_DATA_LOADING_MESSAGE` takes its place without changing the
 * header's line count. The standard heading shows it as a note. A child view with an unknown
 * `pinCount` shows it once, then a blank line, so it doesn't read as repeated
 *
 * `hiddenCount` is noted after the count it shares a unit with. In a child view that is the row
 * count or the parent count, per `hiddenCountUnit`. Every other variant shows one count
 */
export const getPinnedResultsLabels = (
  { pinCount, pinnedRowCount, hiddenCount, hiddenCountUnit }: PinnedResultsCounts,
  { viewNoun, parentNoun }: ComparisonToolNouns,
): PinnedResultsLabels => {
  const hiddenNote = hiddenPinsNote(hiddenCount);
  if (viewNoun && parentNoun) {
    const isHiddenInRows = hiddenCountUnit === 'row';
    return {
      heading: pinnedResultsLabel(PINNED_RESULTS_HEADING),
      sublabels:
        pinCount === null
          ? [pinnedResultsLabel(TABLE_DATA_LOADING_MESSAGE), pinnedResultsLabel(BLANK_LABEL_TEXT)]
          : [
              pinnedResultsCountLabel(pinCount, parentNoun, isHiddenInRows ? null : hiddenNote),
              pinnedResultsCountLabel(pinnedRowCount, viewNoun, isHiddenInRows ? hiddenNote : null),
            ],
    };
  }
  if (viewNoun) {
    return {
      heading: pinnedResultsLabel(PINNED_RESULTS_HEADING),
      sublabels: [pinnedResultsCountLabel(pinCount, viewNoun, hiddenNote)],
    };
  }
  return {
    heading:
      pinCount === null
        ? pinnedResultsLabel(PINNED_RESULTS_HEADING, TABLE_DATA_LOADING_MESSAGE)
        : pinnedResultsLabel(
            `${pinCount} Pinned ${nounForCount(pinCount, DEFAULT_VIEW_NOUN)}`,
            hiddenNote,
          ),
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

/**
 * Takes the service's `failedPinnedRowCount`. The noun is singular only for a known count of one, so
 * an unknown count reads as plural.
 */
export const getPinnedFetchFailedMessage = (
  rowCount: number | null,
  { viewNoun }: ComparisonToolNouns,
): string => {
  const noun = viewNoun ?? DEFAULT_VIEW_NOUN;
  return `We encountered a problem loading your pinned ${sentenceCase(rowCount === 1 ? noun.singular : noun.plural)}.`;
};

/**
 * Takes the number of hidden rows and the active view's nouns. Shown in place of the pinned rows
 * when every cached pin is hidden. The noun is singular only for a known count of one, so an unknown
 * count, e.g. in a child view whose hidden parents can have any number of rows, reads as plural.
 */
export const getNoMatchingPinsMessage = (
  hiddenRowCount: number | null,
  { viewNoun }: ComparisonToolNouns,
): string => {
  const isOneRow = hiddenRowCount === 1;
  const noun = viewNoun ?? DEFAULT_VIEW_NOUN;
  return `No matching ${sentenceCase(isOneRow ? noun.singular : noun.plural)} ${isOneRow ? 'exists' : 'exist'} in the current view.`;
};

/**
 * Takes the service's `hiddenPinCount`, `hiddenParentPinCount`, `hiddenPinsReturnView`, and
 * `pinnedFetchFailed`, and the active view's nouns. A null `hiddenParentPinCount` words the message
 * in the active view's rows, for pins made in that view's own row space. Otherwise the pins were made
 * in another view, and the count of hidden results is given per parent, since the other view's noun
 * isn't known here, and users should know how many results they would lose.
 *
 * After a failed pinned fetch, the message says the pins couldn't be loaded rather than that the view
 * can't show them. A null `returnView` means they failed in the view they were made in, so there is
 * no other view to send the user to.
 */
export const getOverwritePinsMessage = (
  {
    hiddenPinCount,
    hiddenParentPinCount,
  }: { hiddenPinCount: number; hiddenParentPinCount: number | null },
  { returnView, pinnedFetchFailed }: { returnView: string[] | null; pinnedFetchFailed: boolean },
  { viewNoun, parentNoun }: ComparisonToolNouns,
): OverwritePinsMessage => {
  const thatOrThose = (count: number) => (count === 1 ? 'that' : 'those');
  const nounsFor = (count: number, noun: ComparisonToolNoun) =>
    sentenceCase(nounForCount(count, noun));
  const pins = nounsFor(hiddenPinCount, viewNoun ?? DEFAULT_VIEW_NOUN);
  const results = nounsFor(hiddenPinCount, DEFAULT_VIEW_NOUN);
  const parents =
    hiddenParentPinCount === null
      ? ''
      : nounsFor(hiddenParentPinCount, parentNoun ?? viewNoun ?? DEFAULT_VIEW_NOUN);
  const whyHidden = pinnedFetchFailed ? "couldn't be loaded" : "can't be shown in this view";
  const lostPins =
    hiddenParentPinCount === null
      ? `You pinned ${hiddenPinCount} ${pins} that ${whyHidden}. Changing your pins here will unpin ${thatOrThose(hiddenPinCount)} ${pins}.`
      : `You pinned ${hiddenPinCount} ${results} for ${hiddenParentPinCount} ${parents} that ${whyHidden}. Changing your pins here will unpin the ${results} for ${thatOrThose(hiddenParentPinCount)} ${parents}.`;
  return {
    lostPins,
    returnToView:
      returnView === null
        ? null
        : {
            beforeView: `To view ${hiddenPinCount === 1 ? 'it' : 'them'}, go back to the`,
            pinnedView: returnView,
            afterView: 'view.',
          },
  };
};

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
