import { ComparisonToolNouns } from '@sagebionetworks/explorers/models';
import {
  getNoResultsMessage,
  getPinAllTooltip,
  getPinLimitTooltip,
  getPinnedResultsControlsCopy,
  getPinnedResultsLabels,
  getPinToggleTooltip,
  getViewNounLabel,
  NO_NOUNS,
  PINNED_RESULTS_HEADING,
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
    it('should fall back to results, with the count in the heading', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, NO_NOUNS)).toEqual({
        heading: '2 Pinned Results',
        sublabels: [],
      });
      expect(getPinnedResultsLabels({ pinCount: 1, pinnedRowCount: 1 }, NO_NOUNS)).toEqual({
        heading: '1 Pinned Result',
        sublabels: [],
      });
    });

    it('should use the view noun, with the count in a sublabel', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, VIEW_NOUNS)).toEqual({
        heading: PINNED_RESULTS_HEADING,
        sublabels: ['2 Parents'],
      });
      expect(getPinnedResultsLabels({ pinCount: 0, pinnedRowCount: 0 }, VIEW_NOUNS)).toEqual({
        heading: PINNED_RESULTS_HEADING,
        sublabels: ['0 Parents'],
      });
    });

    it('should use the parent noun then the view noun in a child view', () => {
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 3 }, CHILD_NOUNS)).toEqual({
        heading: PINNED_RESULTS_HEADING,
        sublabels: ['2 Parents', '3 Children'],
      });
      expect(getPinnedResultsLabels({ pinCount: 1, pinnedRowCount: 1 }, CHILD_NOUNS)).toEqual({
        heading: PINNED_RESULTS_HEADING,
        sublabels: ['1 Parent', '1 Child'],
      });
    });

    it('should normalize lowercase and all-caps nouns to label case', () => {
      const lowercase = { viewNoun: { singular: 'parent', plural: 'parents' }, parentNoun: null };
      const allCaps = { viewNoun: { singular: 'PARENT', plural: 'PARENTS' }, parentNoun: null };
      expect(
        getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, lowercase).sublabels,
      ).toEqual(['2 Parents']);
      expect(getPinnedResultsLabels({ pinCount: 2, pinnedRowCount: 2 }, allCaps).sublabels).toEqual(
        ['2 Parents'],
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
