import { provideHttpClient } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import { ComparisonToolColumn, ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import {
  provideComparisonToolFilterService,
  provideComparisonToolService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolData,
  mockComparisonToolDataConfig,
  mockComparisonToolFiltersWithSelections,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { MessageService } from 'primeng/api';
import { ComparisonToolTableComponent } from './comparison-tool-table.component';
import {
  COLUMN_HEADER_BORDER_WIDTH_PX,
  COLUMN_HEADER_CLASS,
  COLUMN_HEADER_MARGIN_LEFT_PX,
  COLUMN_HEADER_MARGIN_RIGHT_PX,
  COLUMN_HEADER_TEXT_CLASS,
  MAX_COLUMN_WIDTH_PX,
  MIN_COLUMN_WIDTH_PX,
  SORT_BADGE_SPACING_PX,
  SORT_BADGE_WIDTH_PX,
  SORT_ICON_WIDTH_PX,
} from './comparison-tool-table.constants';
import {
  clampAndFormatWidths,
  getCellsByColumn,
  measureCellWidths,
  prepareCellsForMeasurement,
  restoreCellStyles,
} from './comparison-tool-table.helpers';

async function setup(
  ctServiceOptions?: {
    pinnedItems?: string[];
    unpinnedData?: Record<string, unknown>[];
    pinnedData?: Record<string, unknown>[];
    maxPinnedItems?: number;
  },
  ctFilterServiceOptions?: { searchTerm?: string | null; filters?: ComparisonToolFilter[] },
) {
  const user = userEvent.setup();

  const defaultCtOptions = {
    unpinnedData: mockComparisonToolData,
    configs: mockComparisonToolDataConfig,
  };

  const component = await render(ComparisonToolTableComponent, {
    imports: [RouterModule],
    providers: [
      provideHttpClient(),
      provideRouter([]),
      MessageService,
      ...provideComparisonToolService({
        ...defaultCtOptions,
        ...ctServiceOptions,
      }),
      ...provideComparisonToolFilterService({
        searchTerm: ctFilterServiceOptions?.searchTerm,
        filters: ctFilterServiceOptions?.filters,
      }),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
    ],
  });

  return { component, user };
}

describe('ComparisonToolTableComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should show pinned section when there are pinned items', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    await setup({
      pinnedItems: [pinnedItemData['_id']],
      pinnedData: [pinnedItemData],
      maxPinnedItems: 5,
    });
    expect(screen.getByText(/Pinned Results/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /download/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /clear all/i })).toBeInTheDocument();
  });

  it('should not show pinned section when there are no pinned items', async () => {
    await setup();
    expect(screen.queryByText(/Pinned Results/i)).toBeNull();
    expect(screen.queryByRole('button', { name: /clear all/i })).toBeNull();
  });

  it('should show Matching Results and Pin All when search term is active', async () => {
    await setup(undefined, { searchTerm: '5xFAD' });
    expect(screen.getByText(/Matching Results/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /pin all/i })).toBeInTheDocument();
  });

  it('should show Filtered Results and Pin All when selected filters are active', async () => {
    await setup(undefined, {
      filters: mockComparisonToolFiltersWithSelections,
    });
    expect(screen.getByText(/Filtered Results/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /pin all/i })).toBeInTheDocument();
  });

  it('should disable Pin All when max pinned items reached', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    await setup(
      {
        pinnedItems: [pinnedItemData['_id']],
        pinnedData: [pinnedItemData],
        maxPinnedItems: 1,
      },
      {
        searchTerm: '5xFAD',
      },
    );
    const pinAll = screen.getByRole('button', { name: /pin all/i });
    expect(pinAll).toBeDisabled();
  });

  it('should show All Results divider when not searching/filtering and pinned exist', async () => {
    await setup({ pinnedItems: ['68fff1aaeb12b9674515fd58'] });
    expect(screen.getByText(/All Results/i)).toBeInTheDocument();
  });
});

describe('ComparisonToolTableComponent — column-width helpers', () => {
  const col = (data_key: string): ComparisonToolColumn => ({
    data_key,
    type: 'text',
    selected: true,
    is_exported: false,
    is_hidden: false,
  });

  // --- clampAndFormatWidths ---

  describe('clampAndFormatWidths', () => {
    it('returns empty object for empty input', () => {
      expect(clampAndFormatWidths({})).toEqual({});
    });

    it('clamps values below MIN up to MIN_COLUMN_WIDTH_PX', () => {
      expect(clampAndFormatWidths({ a: 10 })).toEqual({ a: `${MIN_COLUMN_WIDTH_PX}px` });
    });

    it('clamps values above MAX down to MAX_COLUMN_WIDTH_PX', () => {
      expect(clampAndFormatWidths({ a: 9999 })).toEqual({
        a: `${MAX_COLUMN_WIDTH_PX}px`,
      });
    });

    it('formats in-range values as px strings', () => {
      expect(clampAndFormatWidths({ a: 150 })).toEqual({ a: '150px' });
    });
  });

  // --- getCellsByColumn ---

  describe('getCellsByColumn', () => {
    it('groups cells by data_key attribute', () => {
      const container = document.createElement('div');
      const td1 = document.createElement('td');
      td1.setAttribute('data-column-key', 'gene');
      const td2 = document.createElement('td');
      td2.setAttribute('data-column-key', 'score');
      container.append(td1, td2);

      const result = getCellsByColumn(container, [col('gene'), col('score')]);

      expect(result.get('gene')).toEqual([td1]);
      expect(result.get('score')).toEqual([td2]);
    });

    it('returns empty array for a column with no matching cells', () => {
      const container = document.createElement('div');
      const result = getCellsByColumn(container, [col('missing')]);
      expect(result.get('missing')).toEqual([]);
    });
  });

  // --- prepareCellsForMeasurement ---

  describe('prepareCellsForMeasurement', () => {
    it('sets cells to position absolute, visibility hidden, and nowrap', () => {
      const el = document.createElement('td');
      const { saved } = prepareCellsForMeasurement(new Map([['a', [el]]]), [col('a')]);

      expect(el.style.position).toBe('absolute');
      expect(el.style.visibility).toBe('hidden');
      expect(el.style.whiteSpace).toBe('nowrap');
      expect(saved).toHaveLength(1);
    });

    it('saves original inline style string', () => {
      const el = document.createElement('td');
      el.setAttribute('style', 'color: red');
      const { saved } = prepareCellsForMeasurement(new Map([['a', [el]]]), [col('a')]);
      expect(saved[0].style).toBe('color: red');
    });

    it('saves original style as empty string when none was set', () => {
      const el = document.createElement('td');
      const { saved } = prepareCellsForMeasurement(new Map([['a', [el]]]), [col('a')]);
      expect(saved[0].style).toBe('');
    });

    it('saves and resets column-header descendant width to auto', () => {
      const el = document.createElement('td');
      const header = document.createElement('div');
      header.className = COLUMN_HEADER_CLASS;
      header.style.width = '100%';
      el.appendChild(header);

      const { savedHeaderWidths } = prepareCellsForMeasurement(new Map([['a', [el]]]), [col('a')]);

      expect(savedHeaderWidths.get(header)).toBe('100%');
      expect(header.style.width).toBe('auto');
    });
  });

  // --- measureCellWidths ---

  describe('measureCellWidths', () => {
    it('returns the max cell width (ceil + 1) per column', () => {
      const el1 = document.createElement('td');
      const el2 = document.createElement('td');
      jest.spyOn(el1, 'getBoundingClientRect').mockReturnValue({ width: 99.4 } as DOMRect);
      jest.spyOn(el2, 'getBoundingClientRect').mockReturnValue({ width: 120.1 } as DOMRect);

      const result = measureCellWidths(new Map([['a', [el1, el2]]]), [col('a')]);

      expect(result['a']).toBe(122); // Math.ceil(120.1) + 1
    });

    it('computes TH width from column-header-text plus sort chrome', () => {
      const th = document.createElement('th');
      const textDiv = document.createElement('div');
      textDiv.className = COLUMN_HEADER_TEXT_CLASS;
      th.appendChild(textDiv);
      jest.spyOn(textDiv, 'getBoundingClientRect').mockReturnValue({ width: 60 } as DOMRect);

      const result = measureCellWidths(new Map([['a', [th]]]), [col('a')]);

      const expectedTextWidth = Math.ceil(60) + 1; // 61
      const expected =
        COLUMN_HEADER_BORDER_WIDTH_PX +
        COLUMN_HEADER_MARGIN_LEFT_PX +
        expectedTextWidth +
        SORT_ICON_WIDTH_PX +
        SORT_BADGE_WIDTH_PX +
        SORT_BADGE_SPACING_PX +
        COLUMN_HEADER_MARGIN_RIGHT_PX +
        COLUMN_HEADER_BORDER_WIDTH_PX;
      expect(result['a']).toBe(expected);
    });

    it('falls back to TH element width when column-header-text is missing', () => {
      const th = document.createElement('th');
      jest.spyOn(th, 'getBoundingClientRect').mockReturnValue({ width: 80 } as DOMRect);

      const result = measureCellWidths(new Map([['a', [th]]]), [col('a')]);

      const expectedTextWidth = Math.ceil(80) + 1; // 81
      const expected =
        COLUMN_HEADER_BORDER_WIDTH_PX +
        COLUMN_HEADER_MARGIN_LEFT_PX +
        expectedTextWidth +
        SORT_ICON_WIDTH_PX +
        SORT_BADGE_WIDTH_PX +
        SORT_BADGE_SPACING_PX +
        COLUMN_HEADER_MARGIN_RIGHT_PX +
        COLUMN_HEADER_BORDER_WIDTH_PX;
      expect(result['a']).toBe(expected);
    });

    it('returns 0 for a column with no cells', () => {
      const result = measureCellWidths(new Map([['a', []]]), [col('a')]);
      expect(result['a']).toBe(0);
    });
  });

  // --- restoreCellStyles ---

  describe('restoreCellStyles', () => {
    it('restores the style attribute when the original had styles', () => {
      const el = document.createElement('td');
      el.style.position = 'absolute';

      restoreCellStyles([{ el, style: 'color: red', descendants: [] }], new Map());

      expect(el.getAttribute('style')).toBe('color: red');
    });

    it('removes the style attribute when the original had none', () => {
      const el = document.createElement('td');
      el.style.position = 'absolute';

      restoreCellStyles([{ el, style: '', descendants: [] }], new Map());

      expect(el.getAttribute('style')).toBeNull();
    });

    it('restores descendant whiteSpace', () => {
      const desc = document.createElement('span');
      desc.style.whiteSpace = 'nowrap';

      restoreCellStyles(
        [
          {
            el: document.createElement('td'),
            style: '',
            descendants: [{ el: desc, ws: 'normal' }],
          },
        ],
        new Map(),
      );

      expect(desc.style.whiteSpace).toBe('normal');
    });

    it('restores column-header descendant width from savedHeaderWidths', () => {
      const header = document.createElement('div');
      header.className = COLUMN_HEADER_CLASS;
      header.style.width = 'auto';
      const savedHeaderWidths = new Map([[header, '100%']]);

      restoreCellStyles(
        [{ el: document.createElement('td'), style: '', descendants: [{ el: header, ws: '' }] }],
        savedHeaderWidths,
      );

      expect(header.style.width).toBe('100%');
    });
  });
});
