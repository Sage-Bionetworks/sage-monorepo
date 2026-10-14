import { ComparisonToolNouns, PinnedResultsCounts } from '@sagebionetworks/explorers/models';
import {
  BLANK_LABEL_TEXT,
  getNoMatchingPinsMessage,
  getNoResultsMessage,
  getOverwritePinsMessage,
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
// A self-parented view: its rows are their own parents, so it has no parent noun
const SELF_PARENTED_NOUNS: ComparisonToolNouns = { viewNoun: PARENT, parentNoun: null };
// A view noun with no parent/child meaning, for copy that applies to standard CTs
const STANDARD_NOUNS: ComparisonToolNouns = {
  viewNoun: { singular: 'Model', plural: 'Models' },
  parentNoun: null,
};
const CHILD_NOUNS: ComparisonToolNouns = { viewNoun: CHILD, parentNoun: PARENT };
const PIN_LIMIT = 50;

describe('comparison tool copy', () => {
  describe('getViewNounLabel', () => {
    it('should fall back to results', () => {
      expect(getViewNounLabel('All', NO_NOUNS)).toBe('All Results');
    });

    it('should use the view noun', () => {
      expect(getViewNounLabel('Matching', STANDARD_NOUNS)).toBe('Matching Models');
    });

    it('should use the view noun in a child view', () => {
      expect(getViewNounLabel('Displayed', CHILD_NOUNS)).toBe('Displayed Children');
    });
  });

  describe('getPinnedResultsLabels', () => {
    const line = (text: string, note: string | null = null) => ({ text, note });
    const UNKNOWN: PinnedResultsCounts = {
      pinCount: null,
      pinnedRowCount: null,
      hiddenCount: null,
      hiddenCountUnit: 'row',
    };

    it('should fall back to results, with the count in the heading', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 2, hiddenCount: null, hiddenCountUnit: 'row' },
          NO_NOUNS,
        ),
      ).toEqual({
        heading: line('2 Pinned Results'),
        sublabels: [],
      });
      expect(
        getPinnedResultsLabels(
          { pinCount: 1, pinnedRowCount: 1, hiddenCount: null, hiddenCountUnit: 'row' },
          NO_NOUNS,
        ),
      ).toEqual({
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
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 2, hiddenCount: null, hiddenCountUnit: 'row' },
          STANDARD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Models')],
      });
      expect(
        getPinnedResultsLabels(
          { pinCount: 0, pinnedRowCount: 0, hiddenCount: null, hiddenCountUnit: 'row' },
          STANDARD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('0 Models')],
      });
    });

    it('should show the loading message in the view noun sublabel when the count is unknown', () => {
      expect(getPinnedResultsLabels(UNKNOWN, STANDARD_NOUNS)).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line(TABLE_DATA_LOADING_MESSAGE)],
      });
    });

    it('should use the parent noun then the view noun in a child view', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 3, hiddenCount: null, hiddenCountUnit: 'row' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Parents'), line('3 Children')],
      });
      expect(
        getPinnedResultsLabels(
          { pinCount: 1, pinnedRowCount: 1, hiddenCount: null, hiddenCountUnit: 'row' },
          CHILD_NOUNS,
        ),
      ).toEqual({
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
      expect(
        getPinnedResultsLabels(
          { pinCount: null, pinnedRowCount: 3, hiddenCount: null, hiddenCountUnit: 'row' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line(TABLE_DATA_LOADING_MESSAGE), line(BLANK_LABEL_TEXT)],
      });
    });

    it('should show the parent count and the loading message in a child view when only the row count is unknown', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: null, hiddenCount: null, hiddenCountUnit: 'row' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Parents'), line(TABLE_DATA_LOADING_MESSAGE)],
      });
    });

    it('should note hidden pins on the heading count', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 3, pinnedRowCount: 3, hiddenCount: 2, hiddenCountUnit: 'row' },
          NO_NOUNS,
        ),
      ).toEqual({
        heading: line('3 Pinned Results', '2 hidden'),
        sublabels: [],
      });
    });

    it('should note hidden pins on the view noun sublabel', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 2, hiddenCount: 1, hiddenCountUnit: 'row' },
          STANDARD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Models', '1 hidden')],
      });
    });

    it('should note hidden parents on the parent line in a child view', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 3, hiddenCount: 1, hiddenCountUnit: 'parent' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Parents', '1 hidden'), line('3 Children')],
      });
    });

    it('should note hidden rows on the row line in a child view', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 1, pinnedRowCount: 1, hiddenCount: 1, hiddenCountUnit: 'row' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('1 Parent'), line('1 Child', '1 hidden')],
      });
    });

    it('should note hidden parents with the row count unknown after a failed fetch in a child view', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 0, pinnedRowCount: null, hiddenCount: 3, hiddenCountUnit: 'parent' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('0 Parents', '3 hidden'), line(TABLE_DATA_LOADING_MESSAGE)],
      });
    });

    it('should note hidden rows with no rows after a failed fetch of the view rows in a child view', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 0, pinnedRowCount: 0, hiddenCount: 3, hiddenCountUnit: 'row' },
          CHILD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('0 Parents'), line('0 Children', '3 hidden')],
      });
    });

    it('should not note hidden pins when none are hidden', () => {
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 2, hiddenCount: 0, hiddenCountUnit: 'row' },
          STANDARD_NOUNS,
        ),
      ).toEqual({
        heading: line(PINNED_RESULTS_HEADING),
        sublabels: [line('2 Models')],
      });
    });

    it('should normalize lowercase and all-caps nouns to label case', () => {
      const lowercase = { viewNoun: { singular: 'model', plural: 'models' }, parentNoun: null };
      const allCaps = { viewNoun: { singular: 'MODEL', plural: 'MODELS' }, parentNoun: null };
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 2, hiddenCount: null, hiddenCountUnit: 'row' },
          lowercase,
        ).sublabels,
      ).toEqual([line('2 Models')]);
      expect(
        getPinnedResultsLabels(
          { pinCount: 2, pinnedRowCount: 2, hiddenCount: null, hiddenCountUnit: 'row' },
          allCaps,
        ).sublabels,
      ).toEqual([line('2 Models')]);
    });
  });

  describe('getPinLimitTooltip', () => {
    it('should fall back to results', () => {
      expect(getPinLimitTooltip(PIN_LIMIT, NO_NOUNS)).toBe(
        'You have already pinned the maximum number of results (50). You must unpin some results before you can pin more.',
      );
    });

    it('should use the view noun', () => {
      expect(getPinLimitTooltip(PIN_LIMIT, STANDARD_NOUNS)).toBe(
        'You have already pinned the maximum number of results (50 models). You must unpin some results before you can pin more.',
      );
      expect(getPinLimitTooltip(1, STANDARD_NOUNS)).toBe(
        'You have already pinned the maximum number of results (1 model). You must unpin some results before you can pin more.',
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
          viewNoun: { singular: 'MODEL', plural: 'MODELS' },
          parentNoun: null,
        }),
      ).toBe(
        'You have already pinned the maximum number of results (50 models). You must unpin some results before you can pin more.',
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
      expect(getPinLimitWarning(50, PIN_LIMIT, STANDARD_NOUNS)).toBe(
        'Only 50 models were pinned, because you reached the maximum of 50 pinned models.',
      );
      expect(getPinLimitWarning(1, PIN_LIMIT, STANDARD_NOUNS)).toBe(
        'Only 1 model was pinned, because you reached the maximum of 50 pinned models.',
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
          viewNoun: { singular: 'model', plural: 'models' },
          parentNoun: null,
        }),
      ).toBe('Only 2 models were pinned, because you reached the maximum of 50 pinned models.');
    });
  });

  describe('getPinAllTooltip', () => {
    it('should fall back to rows', () => {
      expect(getPinAllTooltip(NO_NOUNS)).toBe('Pin all matching rows to the top.');
    });

    it('should use the view noun', () => {
      expect(getPinAllTooltip(STANDARD_NOUNS)).toBe('Pin all matching models to the top.');
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
      expect(getPinToggleTooltip(true, STANDARD_NOUNS)).toBe('Unpin this model');
      expect(getPinToggleTooltip(false, STANDARD_NOUNS)).toBe(
        'Pin this model to the top of the list',
      );
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
      expect(getNoResultsMessage(STANDARD_NOUNS)).toBe('No models found...');
    });
  });

  describe('getPinnedFetchFailedMessage', () => {
    it('should fall back to results', () => {
      expect(getPinnedFetchFailedMessage(2, NO_NOUNS)).toBe(
        'We encountered a problem loading your pinned results.',
      );
    });

    it('should use the view noun', () => {
      expect(getPinnedFetchFailedMessage(2, STANDARD_NOUNS)).toBe(
        'We encountered a problem loading your pinned models.',
      );
    });

    it('should name the view rows, not their parents, in a child view', () => {
      expect(getPinnedFetchFailedMessage(2, CHILD_NOUNS)).toBe(
        'We encountered a problem loading your pinned children.',
      );
    });

    it('should use the singular for one row', () => {
      expect(getPinnedFetchFailedMessage(1, STANDARD_NOUNS)).toBe(
        'We encountered a problem loading your pinned model.',
      );
    });

    it('should use the plural when the row count is unknown', () => {
      expect(getPinnedFetchFailedMessage(null, CHILD_NOUNS)).toBe(
        'We encountered a problem loading your pinned children.',
      );
    });
  });

  describe('getNoMatchingPinsMessage', () => {
    it('should fall back to results', () => {
      expect(getNoMatchingPinsMessage(2, NO_NOUNS)).toBe(
        'No matching results exist in the current view.',
      );
    });

    it('should name the view rows in a child view', () => {
      expect(getNoMatchingPinsMessage(2, CHILD_NOUNS)).toBe(
        'No matching children exist in the current view.',
      );
    });

    it('should use the singular for one hidden row', () => {
      expect(getNoMatchingPinsMessage(1, STANDARD_NOUNS)).toBe(
        'No matching model exists in the current view.',
      );
    });

    it('should use the plural when the row count is unknown', () => {
      expect(getNoMatchingPinsMessage(null, CHILD_NOUNS)).toBe(
        'No matching children exist in the current view.',
      );
    });
  });

  describe('getOverwritePinsMessage', () => {
    const PINNED_VIEW = ['Category A', 'Option B'];
    const message = (beforeView: string) => ({
      beforeView,
      pinnedView: PINNED_VIEW,
      afterView: 'view.',
    });

    it('should fall back to results without a parent count', () => {
      expect(
        getOverwritePinsMessage(
          { hiddenPinCount: 5, hiddenParentPinCount: null },
          PINNED_VIEW,
          NO_NOUNS,
        ),
      ).toEqual(
        message(
          "You pinned 5 results that can't be shown in this view. Changing your pins here will unpin those results. To view them, go back to the",
        ),
      );
    });

    it('should use the view noun without a parent count', () => {
      expect(
        getOverwritePinsMessage(
          { hiddenPinCount: 1, hiddenParentPinCount: null },
          PINNED_VIEW,
          STANDARD_NOUNS,
        ),
      ).toEqual(
        message(
          "You pinned 1 model that can't be shown in this view. Changing your pins here will unpin that model. To view it, go back to the",
        ),
      );
    });

    it('should count the results per parent noun in a child view', () => {
      expect(
        getOverwritePinsMessage(
          { hiddenPinCount: 2, hiddenParentPinCount: 2 },
          PINNED_VIEW,
          CHILD_NOUNS,
        ),
      ).toEqual(
        message(
          "You pinned 2 results for 2 parents that can't be shown in this view. Changing your pins here will unpin the results for those parents. To view them, go back to the",
        ),
      );
    });

    it('should count the results per view noun in a self-parented view, whose rows are the parents', () => {
      expect(
        getOverwritePinsMessage(
          { hiddenPinCount: 5, hiddenParentPinCount: 1 },
          PINNED_VIEW,
          SELF_PARENTED_NOUNS,
        ),
      ).toEqual(
        message(
          "You pinned 5 results for 1 parent that can't be shown in this view. Changing your pins here will unpin the results for that parent. To view them, go back to the",
        ),
      );
    });

    it('should word one result for one parent in the singular', () => {
      expect(
        getOverwritePinsMessage(
          { hiddenPinCount: 1, hiddenParentPinCount: 1 },
          PINNED_VIEW,
          CHILD_NOUNS,
        ),
      ).toEqual(
        message(
          "You pinned 1 result for 1 parent that can't be shown in this view. Changing your pins here will unpin the result for that parent. To view it, go back to the",
        ),
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
      expect(getUnpinnedFetchFailedMessage(STANDARD_NOUNS)).toBe(
        'We encountered a problem loading models.',
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
      expect(getShareUrlTooltip(STANDARD_NOUNS)).toBe(
        "Copy the URL to capture the table's current filtering, sorting, and pinned models",
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
      expect(getPinnedResultsControlsCopy(STANDARD_NOUNS)).toEqual({
        downloadButtonTooltip: 'Download pinned models',
        downloadPanelHeading: 'Download pinned models as:',
        clearButtonTooltip: 'Clear all pinned models',
      });
    });
  });
});
