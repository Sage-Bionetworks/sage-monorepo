import { ComparisonToolNouns } from '@sagebionetworks/explorers/models';
import {
  BLANK_LABEL_TEXT,
  getNoResultsMessage,
  getPinAllTooltip,
  getPinLimitTooltip,
  getPinnedFetchFailedMessage,
  getPinnedResultsControlsCopy,
  getPinnedResultsLabels,
  getPinToggleTooltip,
  getShareUrlTooltip,
  getUnpinnedFetchFailedMessage,
  getViewNounLabel,
  NO_NOUNS,
  PINNED_RESULTS_HEADING,
  TABLE_DATA_LOADING_MESSAGE,
} from './comparison-tool-copy';
import { getPinLimitWarning } from './toasts';

const PARENT = { singular: 'Parent', plural: 'Parents' };
const CHILD = { singular: 'Child', plural: 'Children' };
const VIEW_NOUNS: ComparisonToolNouns = { viewNoun: PARENT, parentNoun: null };
const CHILD_NOUNS: ComparisonToolNouns = { viewNoun: CHILD, parentNoun: PARENT };
const PIN_LIMIT = 50;

describe('comparison tool copy', () => {
  describe('getViewNounLabel', () => {
    it('should fall back to results', () => {
      expect(getViewNounLabel('All', NO_NOUNS)).toBe('All Results');
    });

    it('should use the view noun', () => {
      expect(getViewNounLabel('Matching', VIEW_NOUNS)).toBe('Matching Parents');
    });

    it('should use the view noun in a child view', () => {
      expect(getViewNounLabel('Displayed', CHILD_NOUNS)).toBe('Displayed Children');
    });
  });

  describe('getPinnedResultsLabels', () => {
    const line = (text: string, note: string | null = null) => ({ text, note });
    const UNKNOWN = { pinCount: null, pinnedRowCount: null };

    it('should fall back to results, with the count in the heading', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, NO_NOUNS)).toEqual({
        heading: line('2 Pinned Results'),
        sublabels: [],
      });
      expect(getPinnedResultsLabels({ pinCount: 1, pinnedRowCount: 1 }, NO_NOUNS)).toEqual({
        heading: line('1 Pinned Result'),
        sublabels: [],
      });
    });

    it('should note the loading message on the heading when the count is unknown', () => {
      expect(getPinnedResultsLabels(UNKNOWN, NO_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING, TABLE_DATA_LOADING_MESSAGE),
        sublabels: [],
      });
    });

    it('should use the view noun, with the count in a sublabel', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, VIEW_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Parents')],
      });
      expect(getPinnedResultsLabels({ pinCount: 0, pinnedRowCount: 0 }, VIEW_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('0 Parents')],
      });
    });

    it('should show the loading message in the view noun sublabel when the count is unknown', () => {
      expect(getPinnedResultsLabels(UNKNOWN, VIEW_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line(TABLE_DATA_LOADING_MESSAGE)],
      });
    });

    it('should use the parent noun then the view noun in a child view', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 3 }, CHILD_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Parents'), line('3 Children')],
      });
      expect(getPinnedResultsLabels({ pinCount: 1, pinnedRowCount: 1 }, CHILD_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('1 Parent'), line('1 Child')],
      });
    });

    it('should show the loading message once, then a blank line, in a child view when the counts are unknown', () => {
      expect(getPinnedResultsLabels(UNKNOWN, CHILD_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line(TABLE_DATA_LOADING_MESSAGE), line(BLANK_LABEL_TEXT)],
      });
    });

    it('should not show a known row count in a child view when the parent count is unknown', () => {
      expect(getPinnedResultsLabels({ pinCount: null, pinnedRowCount: 3 }, CHILD_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line(TABLE_DATA_LOADING_MESSAGE), line(BLANK_LABEL_TEXT)],
      });
    });

    it('should show the parent count and the loading message in a child view when only the row count is unknown', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: null }, CHILD_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Parents'), line(TABLE_DATA_LOADING_MESSAGE)],
      });
    });

    it('should normalize lowercase and all-caps nouns to label case', () => {
      const lowercase = { viewNoun: { singular: 'parent', plural: 'parents' }, parentNoun: null };
      const allCaps = { viewNoun: { singular: 'PARENT', plural: 'PARENTS' }, parentNoun: null };
      expect(
        getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, lowercase).sublabels,
      ).toEqual([line('2 Parents')]);
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, allCaps).sublabels).toEqual(
        [line('2 Parents')],
      );
    });
  });

  describe('getPinLimitTooltip', () => {
    it('should fall back to results', () => {
      expect(getPinLimitTooltip(PIN_LIMIT, NO_NOUNS)).toBe(
        'You have already pinned the maximum number of results (50). You must unpin some results before you can pin more.',
      );
    });

    it('should use the view noun', () => {
      expect(getPinLimitTooltip(PIN_LIMIT, VIEW_NOUNS)).toBe(
        'You have already pinned the maximum number of results (50 parents). You must unpin some results before you can pin more.',
      );
      expect(getPinLimitTooltip(1, VIEW_NOUNS)).toBe(
        'You have already pinned the maximum number of results (1 parent). You must unpin some results before you can pin more.',
      );
    });

    it('should use the parent noun for the limit in a child view', () => {
      expect(getPinLimitTooltip(PIN_LIMIT, CHILD_NOUNS)).toBe(
        'You have already pinned the maximum number of results (children for 50 parents). You must unpin all children for a parent before you can pin children for a new parent.',
      );
    });

    it('should normalize all-caps nouns to sentence case', () => {
      expect(
        getPinLimitTooltip(PIN_LIMIT, {
          viewNoun: { singular: 'PARENT', plural: 'PARENTS' },
          parentNoun: null,
        }),
      ).toBe(
        'You have already pinned the maximum number of results (50 parents). You must unpin some results before you can pin more.',
      );
    });
  });

  describe('getPinLimitWarning', () => {
    it('should fall back to results', () => {
      expect(getPinLimitWarning(50, PIN_LIMIT)).toBe(
        'Only 50 results were pinned, because you reached the maximum of 50 pinned results.',
      );
      expect(getPinLimitWarning(1, 1)).toBe(
        'Only 1 result was pinned, because you reached the maximum of 1 pinned result.',
      );
    });

    it('should use the view noun', () => {
      expect(getPinLimitWarning(50, PIN_LIMIT, VIEW_NOUNS)).toBe(
        'Only 50 parents were pinned, because you reached the maximum of 50 pinned parents.',
      );
      expect(getPinLimitWarning(1, PIN_LIMIT, VIEW_NOUNS)).toBe(
        'Only 1 parent was pinned, because you reached the maximum of 50 pinned parents.',
      );
    });

    it('should use the parent noun for the limit and explain skipped children in a child view', () => {
      expect(getPinLimitWarning(180, PIN_LIMIT, CHILD_NOUNS)).toBe(
        'Only 180 children were pinned, because you reached the maximum of 50 pinned parents. Some children were skipped because they belong to a parent not already in your pinned list.',
      );
      expect(getPinLimitWarning(1, PIN_LIMIT, CHILD_NOUNS)).toBe(
        'Only 1 child was pinned, because you reached the maximum of 50 pinned parents. Some children were skipped because they belong to a parent not already in your pinned list.',
      );
    });

    it('should normalize lowercase nouns to sentence case', () => {
      expect(
        getPinLimitWarning(2, PIN_LIMIT, {
          viewNoun: { singular: 'parent', plural: 'parents' },
          parentNoun: null,
        }),
      ).toBe('Only 2 parents were pinned, because you reached the maximum of 50 pinned parents.');
    });
  });

  describe('getPinAllTooltip', () => {
    it('should fall back to rows', () => {
      expect(getPinAllTooltip(NO_NOUNS)).toBe('Pin all matching rows to the top.');
    });

    it('should use the view noun', () => {
      expect(getPinAllTooltip(VIEW_NOUNS)).toBe('Pin all matching parents to the top.');
    });

    it('should use the view noun in a child view', () => {
      expect(getPinAllTooltip(CHILD_NOUNS)).toBe('Pin all matching children to the top.');
    });
  });

  describe('getPinToggleTooltip', () => {
    it('should fall back to row', () => {
      expect(getPinToggleTooltip(true, NO_NOUNS)).toBe('Unpin this row');
      expect(getPinToggleTooltip(false, NO_NOUNS)).toBe('Pin this row to the top of the list');
    });

    it('should use the view noun', () => {
      expect(getPinToggleTooltip(true, VIEW_NOUNS)).toBe('Unpin this parent');
      expect(getPinToggleTooltip(false, VIEW_NOUNS)).toBe('Pin this parent to the top of the list');
    });

    it('should use the view noun in a child view', () => {
      expect(getPinToggleTooltip(true, CHILD_NOUNS)).toBe('Unpin this child');
      expect(getPinToggleTooltip(false, CHILD_NOUNS)).toBe('Pin this child to the top of the list');
    });
  });

  describe('getNoResultsMessage', () => {
    it('should fall back to results', () => {
      expect(getNoResultsMessage(NO_NOUNS)).toBe('No results found...');
    });

    it('should use the view noun', () => {
      expect(getNoResultsMessage(VIEW_NOUNS)).toBe('No parents found...');
    });
  });

  describe('getPinnedFetchFailedMessage', () => {
    it('should fall back to results', () => {
      expect(getPinnedFetchFailedMessage(NO_NOUNS)).toBe(
        'We encountered a problem loading your pinned results.',
      );
    });

    it('should use the view noun', () => {
      expect(getPinnedFetchFailedMessage(VIEW_NOUNS)).toBe(
        'We encountered a problem loading your pinned parents.',
      );
    });

    it('should name the view rows, not their parents, in a child view', () => {
      expect(getPinnedFetchFailedMessage(CHILD_NOUNS)).toBe(
        'We encountered a problem loading your pinned children.',
      );
    });
  });

  describe('getUnpinnedFetchFailedMessage', () => {
    it('should fall back to results', () => {
      expect(getUnpinnedFetchFailedMessage(NO_NOUNS)).toBe(
        'We encountered a problem loading results.',
      );
    });

    it('should use the view noun', () => {
      expect(getUnpinnedFetchFailedMessage(VIEW_NOUNS)).toBe(
        'We encountered a problem loading parents.',
      );
    });

    it('should name the view rows, not their parents, in a child view', () => {
      expect(getUnpinnedFetchFailedMessage(CHILD_NOUNS)).toBe(
        'We encountered a problem loading children.',
      );
    });
  });

  describe('getShareUrlTooltip', () => {
    it('should fall back to results', () => {
      expect(getShareUrlTooltip(NO_NOUNS)).toBe(
        "Copy the URL to capture the table's current filtering, sorting, and pinned results",
      );
    });

    it('should use the view noun', () => {
      expect(getShareUrlTooltip(VIEW_NOUNS)).toBe(
        "Copy the URL to capture the table's current filtering, sorting, and pinned parents",
      );
    });

    it('should use the view noun in a child view', () => {
      expect(getShareUrlTooltip(CHILD_NOUNS)).toBe(
        "Copy the URL to capture the table's current filtering, sorting, and pinned children",
      );
    });
  });

  describe('getPinnedResultsControlsCopy', () => {
    it('should fall back to results', () => {
      expect(getPinnedResultsControlsCopy(NO_NOUNS)).toEqual({
        downloadButtonTooltip: 'Download pinned results',
        downloadPanelHeading: 'Download pinned results as:',
        clearButtonTooltip: 'Clear all pinned results',
      });
    });

    it('should use the view noun', () => {
      expect(getPinnedResultsControlsCopy(VIEW_NOUNS)).toEqual({
        downloadButtonTooltip: 'Download pinned parents',
        downloadPanelHeading: 'Download pinned parents as:',
        clearButtonTooltip: 'Clear all pinned parents',
      });
    });
  });
});
