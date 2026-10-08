import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter, RouterModule } from '@angular/router';
import {
  PINNED_RESULTS_HEADING,
  TABLE_DATA_LOADING_MESSAGE,
} from '@sagebionetworks/explorers/constants';
import {
  ComparisonToolColumn,
  ComparisonToolConfig,
  ComparisonToolFilter,
  ComparisonToolNoun,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolService,
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
import { Subject } from 'rxjs';
import { ComparisonToolTableComponent } from './comparison-tool-table.component';
import {
  COLUMN_HEADER_BORDER_WIDTH_PX,
  COLUMN_HEADER_CLASS,
  COLUMN_HEADER_MARGIN_LEFT_PX,
  COLUMN_HEADER_MARGIN_RIGHT_PX,
  COLUMN_HEADER_TEXT_CLASS,
  MAX_COLUMN_WIDTH_PX,
  MIN_COLUMN_WIDTH_PX,
  PINNED_RESULTS_CONTROLS,
  SORT_BADGE_SPACING_PX,
  SORT_BADGE_WIDTH_PX,
  SORT_ICON_WIDTH_PX,
} from './comparison-tool-table.constants';
import {
  clampAndFormatWidths,
  getCellsByColumn,
  measureCellWidths,
  prepareCellsForMeasurement,
  resolveFixedColumnWidths,
  restoreCellStyles,
} from './comparison-tool-table.helpers';

const PARENT_NOUN: ComparisonToolNoun = { singular: 'Parent', plural: 'Parents' };
const CHILD_NOUN: ComparisonToolNoun = { singular: 'Child', plural: 'Children' };

const viewNounConfigs: ComparisonToolConfig[] = [
  { ...mockComparisonToolDataConfig[0], view_noun: PARENT_NOUN },
];
const childViewConfigs: ComparisonToolConfig[] = [
  {
    ...mockComparisonToolDataConfig[0],
    row_id_data_key: '_id',
    parent_id_data_key: 'model_type',
    view_noun: CHILD_NOUN,
    parent_noun: PARENT_NOUN,
  },
];
// Three rows under two distinct model_type parents
const childViewPinnedData = [
  mockComparisonToolData[0],
  mockComparisonToolData[1],
  mockComparisonToolData[4],
];

function pinnedOptions(pinnedData: Record<string, unknown>[]) {
  return { pinnedItems: pinnedData.map((row) => row['_id'] as string), pinnedData };
}

function getPinnedResultsHeaderLines(container: Element): (string | undefined)[] {
  const header = container.querySelector('#pinned-results-header');
  return Array.from(header?.querySelectorAll('span') ?? []).map((span) => span.textContent?.trim());
}

async function setup(
  ctServiceOptions?: {
    configs?: ComparisonToolConfig[];
    pinnedItems?: string[];
    unpinnedData?: Record<string, unknown>[];
    unpinnedFetchFails?: boolean;
    pinnedData?: Record<string, unknown>[];
    pinLimit?: number;
    pinnedFetchFails?: boolean;
    pinnedFetchPending?: boolean;
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
      provideNoopAnimations(),
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
      pinLimit: 5,
    });
    expect(screen.getByText('1 Pinned Result')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /download/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /clear all/i })).toBeInTheDocument();
  });

  it('should enable Download Pins and Clear All Pins when no fetch is in flight', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    await setup({ pinnedItems: [pinnedItemData['_id']], pinnedData: [pinnedItemData] });

    expect(screen.getByRole('button', { name: /download/i })).toBeEnabled();
    expect(screen.getByRole('button', { name: /clear all/i })).toBeEnabled();
  });

  it('should disable Download Pins and Clear All Pins while table data is loading', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    const { component } = await setup({
      pinnedItems: [pinnedItemData['_id']],
      pinnedData: [pinnedItemData],
    });

    TestBed.inject(ComparisonToolService).fetchUnpinned(new Subject<never>());
    component.detectChanges();

    expect(screen.getByRole('button', { name: /download/i })).toBeDisabled();
    expect(screen.getByRole('button', { name: /clear all/i })).toBeDisabled();
  });

  it('should not clear pins when Clear All Pins is clicked while table data is loading', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    const { component, user } = await setup({
      pinnedItems: [pinnedItemData['_id']],
      pinnedData: [pinnedItemData],
    });
    const service = TestBed.inject(ComparisonToolService);
    const resetPinnedItemsSpy = jest.spyOn(service, 'resetPinnedItems');

    service.fetchUnpinned(new Subject<never>());
    component.detectChanges();
    await user.click(screen.getByRole('button', { name: /clear all/i }));

    expect(resetPinnedItemsSpy).not.toHaveBeenCalled();
  });

  it('should explain that Clear All Pins is waiting on table data while a fetch is in flight', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    const { component, user } = await setup({
      pinnedItems: [pinnedItemData['_id']],
      pinnedData: [pinnedItemData],
    });

    TestBed.inject(ComparisonToolService).fetchUnpinned(new Subject<never>());
    component.detectChanges();
    await user.hover(screen.getByRole('button', { name: /clear all/i }));

    expect(screen.getByRole('tooltip', { name: TABLE_DATA_LOADING_MESSAGE })).toBeVisible();
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

  it('should disable Pin All when the pin limit is reached', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    await setup(
      {
        pinnedItems: [pinnedItemData['_id']],
        pinnedData: [pinnedItemData],
        pinLimit: 1,
      },
      {
        searchTerm: '5xFAD',
      },
    );
    const pinAll = screen.getByRole('button', { name: /pin all/i });
    expect(pinAll).toBeDisabled();
  });

  it('should disable Pin All while table data is loading', async () => {
    const { component } = await setup(undefined, { searchTerm: '5xFAD' });

    TestBed.inject(ComparisonToolService).fetchUnpinned(new Subject<never>());
    component.detectChanges();

    expect(screen.getByRole('button', { name: /pin all/i })).toBeDisabled();
  });

  it('should explain what Pin All does when no fetch is in flight', async () => {
    const { user } = await setup(undefined, { searchTerm: '5xFAD' });

    await user.hover(screen.getByRole('button', { name: /pin all/i }));

    expect(
      screen.getByRole('tooltip', { name: 'Pin all matching rows to the top.' }),
    ).toBeVisible();
  });

  it('should explain that Pin All is waiting on table data while a fetch is in flight', async () => {
    const { component, user } = await setup(undefined, { searchTerm: '5xFAD' });

    TestBed.inject(ComparisonToolService).fetchUnpinned(new Subject<never>());
    component.detectChanges();
    await user.hover(screen.getByRole('button', { name: /pin all/i }));

    expect(screen.getByRole('tooltip', { name: TABLE_DATA_LOADING_MESSAGE })).toBeVisible();
  });

  it('should explain the pin limit rather than the loading state when both apply', async () => {
    const pinnedItemData = mockComparisonToolData[0];
    const { component, user } = await setup(
      {
        pinnedItems: [pinnedItemData['_id']],
        pinnedData: [pinnedItemData],
        pinLimit: 1,
      },
      { searchTerm: '5xFAD' },
    );
    const service = TestBed.inject(ComparisonToolService);

    service.fetchUnpinned(new Subject<never>());
    component.detectChanges();
    await user.hover(screen.getByRole('button', { name: /pin all/i }));

    expect(screen.getByRole('tooltip', { name: service.disabledPinTooltip() })).toBeVisible();
  });

  it('should delegate Pin All to the comparison tool service', async () => {
    const { user } = await setup(undefined, { searchTerm: '5xFAD' });
    const pinAllSpy = jest.spyOn(TestBed.inject(ComparisonToolService), 'pinAll');

    await user.click(screen.getByRole('button', { name: /pin all/i }));

    expect(pinAllSpy).toHaveBeenCalled();
  });

  it('should show All Results divider when not searching/filtering and pinned exist', async () => {
    await setup({ pinnedItems: ['68fff1aaeb12b9674515fd58'] });
    expect(screen.getByText(/All Results/i)).toBeInTheDocument();
  });

  describe('pinned results header', () => {
    it('should fall back to results', async () => {
      const { component } = await setup(pinnedOptions(mockComparisonToolData.slice(0, 2)));

      expect(getPinnedResultsHeaderLines(component.container)).toEqual(['2 Pinned Results']);
    });

    it('should use the view noun', async () => {
      const { component } = await setup({
        configs: viewNounConfigs,
        ...pinnedOptions(mockComparisonToolData.slice(0, 2)),
      });

      expect(getPinnedResultsHeaderLines(component.container)).toEqual([
        PINNED_RESULTS_HEADING,
        '2 Parents',
      ]);
    });

    it('should count parents and rows separately in a child view', async () => {
      const { component } = await setup({
        configs: childViewConfigs,
        ...pinnedOptions(childViewPinnedData),
      });

      expect(getPinnedResultsHeaderLines(component.container)).toEqual([
        PINNED_RESULTS_HEADING,
        '2 Parents',
        '3 Children',
      ]);
    });
  });

  describe('pinned results header while pinned rows load', () => {
    it('should note that it is waiting for data on the heading', async () => {
      const { component } = await setup({
        ...pinnedOptions(mockComparisonToolData.slice(0, 2)),
        pinnedFetchPending: true,
      });

      expect(getPinnedResultsHeaderLines(component.container)).toEqual([
        `${PINNED_RESULTS_HEADING} ${TABLE_DATA_LOADING_MESSAGE}`,
      ]);
    });

    it('should show that it is waiting for data in place of the view noun count', async () => {
      const { component } = await setup({
        configs: viewNounConfigs,
        ...pinnedOptions(mockComparisonToolData.slice(0, 2)),
        pinnedFetchPending: true,
      });

      expect(getPinnedResultsHeaderLines(component.container)).toEqual([
        PINNED_RESULTS_HEADING,
        TABLE_DATA_LOADING_MESSAGE,
      ]);
    });

    it('should show that it is waiting for data once, then a blank line, in a child view', async () => {
      const { component } = await setup({
        configs: childViewConfigs,
        ...pinnedOptions(childViewPinnedData),
        pinnedFetchPending: true,
      });

      expect(getPinnedResultsHeaderLines(component.container)).toEqual([
        PINNED_RESULTS_HEADING,
        TABLE_DATA_LOADING_MESSAGE,
        '',
      ]);
    });
  });

  describe('pinned fetch failure', () => {
    const failedPinnedItems = mockComparisonToolData.slice(0, 3).map((row) => row['_id']);
    const failedOptions = { pinnedItems: failedPinnedItems, pinnedFetchFails: true };
    const retryButtonName = PINNED_RESULTS_CONTROLS.retryButtonLabel;

    it('should tell the user their pins could not be loaded', async () => {
      await setup(failedOptions);

      expect(screen.getByRole('alert')).toHaveTextContent(
        'We encountered a problem loading your pinned results.',
      );
    });

    it('should show the failure message in place of the pinned table', async () => {
      const { component } = await setup(failedOptions);

      expect(component.container.querySelectorAll('explorers-base-table')).toHaveLength(1);
    });

    it('should keep counting the unloaded pins in the pinned results header', async () => {
      const { component } = await setup(failedOptions);

      expect(getPinnedResultsHeaderLines(component.container)).toEqual(['3 Pinned Results']);
    });

    it('should name the view rows in a child view', async () => {
      await setup({ ...failedOptions, configs: childViewConfigs });

      expect(screen.getByRole('alert')).toHaveTextContent(
        'We encountered a problem loading your pinned children.',
      );
    });

    it('should count the unloaded pins by row in the header of a child view whose parents never loaded', async () => {
      const { component } = await setup({ ...failedOptions, configs: childViewConfigs });

      expect(getPinnedResultsHeaderLines(component.container)).toEqual([
        PINNED_RESULTS_HEADING,
        '3 Children',
      ]);
    });

    it('should retry the pinned fetch when Retry is clicked', async () => {
      const { user } = await setup(failedOptions);
      const retryPinnedFetchSpy = jest.spyOn(
        TestBed.inject(ComparisonToolService),
        'retryPinnedFetch',
      );

      await user.click(screen.getByRole('button', { name: retryButtonName }));

      expect(retryPinnedFetchSpy).toHaveBeenCalled();
    });

    it('should disable Download Pins but keep Clear All Pins enabled', async () => {
      await setup(failedOptions);

      expect(screen.getByRole('button', { name: /download/i })).toBeDisabled();
      expect(screen.getByRole('button', { name: /clear all/i })).toBeEnabled();
    });

    it('should remove the pinned section when Clear All Pins is clicked', async () => {
      const { user } = await setup(failedOptions);

      await user.click(screen.getByRole('button', { name: /clear all/i }));

      expect(screen.queryByRole('alert')).toBeNull();
      expect(screen.queryByRole('button', { name: /clear all/i })).toBeNull();
    });

    it('should keep the All Results divider', async () => {
      await setup(failedOptions);

      expect(screen.getByText('All Results')).toBeInTheDocument();
    });

    it('should not show the failure message when the pinned fetch succeeds', async () => {
      await setup(pinnedOptions(mockComparisonToolData.slice(0, 3)));

      expect(screen.queryByRole('alert')).toBeNull();
      expect(screen.queryByRole('button', { name: retryButtonName })).toBeNull();
    });
  });

  describe('unpinned fetch failure', () => {
    const failedOptions = { unpinnedFetchFails: true };

    it('should tell the user the rows could not be loaded', async () => {
      await setup(failedOptions);

      expect(screen.getByRole('alert')).toHaveTextContent(
        'We encountered a problem loading results.',
      );
      expect(screen.getByRole('button', { name: 'Retry Loading Results' })).toBeInTheDocument();
    });

    it('should name the view rows', async () => {
      await setup({ ...failedOptions, configs: viewNounConfigs });

      expect(screen.getByRole('alert')).toHaveTextContent(
        'We encountered a problem loading parents.',
      );
      expect(screen.getByRole('button', { name: 'Retry Loading Parents' })).toBeInTheDocument();
    });

    it('should show the failure message in place of the unpinned table', async () => {
      const { component } = await setup(failedOptions);

      expect(component.container.querySelectorAll('explorers-base-table')).toHaveLength(1);
      expect(screen.queryByText('No results found...')).toBeNull();
    });

    it('should retry the unpinned fetch when Retry is clicked', async () => {
      const { user } = await setup(failedOptions);
      const retryUnpinnedFetchSpy = jest.spyOn(
        TestBed.inject(ComparisonToolService),
        'retryUnpinnedFetch',
      );

      await user.click(screen.getByRole('button', { name: 'Retry Loading Results' }));

      expect(retryUnpinnedFetchSpy).toHaveBeenCalled();
    });

    it('should keep the pinned rows', async () => {
      await setup({ ...failedOptions, ...pinnedOptions(mockComparisonToolData.slice(0, 1)) });

      expect(screen.getByText('1 Pinned Result')).toBeInTheDocument();
      expect(screen.getAllByRole('alert')).toHaveLength(1);
    });

    it('should show both failure messages when both fetches fail', async () => {
      await setup({
        ...failedOptions,
        pinnedItems: mockComparisonToolData.slice(0, 1).map((row) => row['_id']),
        pinnedFetchFails: true,
      });

      expect(screen.getAllByRole('alert')).toHaveLength(2);
    });

    it('should not show the failure message when the unpinned fetch succeeds', async () => {
      await setup();

      expect(screen.queryByRole('alert')).toBeNull();
    });
  });

  describe('divider labels', () => {
    it('should fall back to results', async () => {
      await setup(pinnedOptions(mockComparisonToolData.slice(0, 1)));

      expect(screen.getByText('All Results')).toBeInTheDocument();
    });

    it('should use the view noun in the Matching divider', async () => {
      await setup({ configs: viewNounConfigs }, { searchTerm: '5xFAD' });

      expect(screen.getByText('Matching Parents')).toBeInTheDocument();
    });

    it('should use the view noun in the Filtered divider', async () => {
      await setup(
        { configs: viewNounConfigs },
        { filters: mockComparisonToolFiltersWithSelections },
      );

      expect(screen.getByText('Filtered Parents')).toBeInTheDocument();
    });

    it('should use the view noun in the All divider', async () => {
      await setup({
        configs: viewNounConfigs,
        ...pinnedOptions(mockComparisonToolData.slice(0, 1)),
      });

      expect(screen.getByText('All Parents')).toBeInTheDocument();
    });

    it('should use the view noun rather than the parent noun in a child view', async () => {
      await setup({ configs: childViewConfigs, ...pinnedOptions(childViewPinnedData) });

      expect(screen.getByText('All Children')).toBeInTheDocument();
    });
  });
  describe('Pin All tooltip', () => {
    it('should use the view noun', async () => {
      const { user } = await setup({ configs: viewNounConfigs }, { searchTerm: '5xFAD' });

      await user.hover(screen.getByRole('button', { name: /pin all/i }));

      expect(
        screen.getByRole('tooltip', { name: 'Pin all matching parents to the top.' }),
      ).toBeVisible();
    });

    it('should use the view noun rather than the parent noun in a child view', async () => {
      const { user } = await setup({ configs: childViewConfigs }, { searchTerm: '5xFAD' });

      await user.hover(screen.getByRole('button', { name: /pin all/i }));

      expect(
        screen.getByRole('tooltip', { name: 'Pin all matching children to the top.' }),
      ).toBeVisible();
    });
  });

  describe('pinned results controls', () => {
    const fallbackOptions = pinnedOptions(mockComparisonToolData.slice(0, 1));
    const viewNounOptions = {
      configs: viewNounConfigs,
      ...pinnedOptions(mockComparisonToolData.slice(0, 1)),
    };
    const childViewOptions = { configs: childViewConfigs, ...pinnedOptions(childViewPinnedData) };

    it('should fall back to results in the Download tooltip', async () => {
      const { user } = await setup(fallbackOptions);

      await user.hover(screen.getByRole('button', { name: /download/i }));

      expect(screen.getByRole('tooltip', { name: 'Download pinned results' })).toBeVisible();
    });

    it('should fall back to results in the Clear All tooltip', async () => {
      const { user } = await setup(fallbackOptions);

      await user.hover(screen.getByRole('button', { name: /clear all/i }));

      expect(screen.getByRole('tooltip', { name: 'Clear all pinned results' })).toBeVisible();
    });

    it('should fall back to results in the download menu', async () => {
      const { user } = await setup(fallbackOptions);

      await user.click(screen.getByRole('button', { name: /download/i }));

      expect(screen.getByText('Download pinned results as:')).toBeVisible();
    });

    it('should use the view noun in the Download tooltip', async () => {
      const { user } = await setup(viewNounOptions);

      await user.hover(screen.getByRole('button', { name: /download/i }));

      expect(screen.getByRole('tooltip', { name: 'Download pinned parents' })).toBeVisible();
    });

    it('should use the view noun in the Clear All tooltip', async () => {
      const { user } = await setup(viewNounOptions);

      await user.hover(screen.getByRole('button', { name: /clear all/i }));

      expect(screen.getByRole('tooltip', { name: 'Clear all pinned parents' })).toBeVisible();
    });

    it('should use the view noun in the download menu', async () => {
      const { user } = await setup(viewNounOptions);

      await user.click(screen.getByRole('button', { name: /download/i }));

      expect(screen.getByText('Download pinned parents as:')).toBeVisible();
    });

    it('should use the view noun rather than the parent noun in the Download tooltip in a child view', async () => {
      const { user } = await setup(childViewOptions);

      await user.hover(screen.getByRole('button', { name: /download/i }));

      expect(screen.getByRole('tooltip', { name: 'Download pinned children' })).toBeVisible();
    });

    it('should use the view noun rather than the parent noun in the Clear All tooltip in a child view', async () => {
      const { user } = await setup(childViewOptions);

      await user.hover(screen.getByRole('button', { name: /clear all/i }));

      expect(screen.getByRole('tooltip', { name: 'Clear all pinned children' })).toBeVisible();
    });

    it('should use the view noun rather than the parent noun in the download menu in a child view', async () => {
      const { user } = await setup(childViewOptions);

      await user.click(screen.getByRole('button', { name: /download/i }));

      expect(screen.getByText('Download pinned children as:')).toBeVisible();
    });
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

  // --- resolveFixedColumnWidths ---

  describe('resolveFixedColumnWidths', () => {
    const fixedCol = (data_key: string, column_width: number): ComparisonToolColumn => ({
      ...col(data_key),
      column_width,
    });

    it('routes columns with column_width to fixedWidthByColumn as px strings', () => {
      const { fixedWidthByColumn, columnsToMeasure } = resolveFixedColumnWidths([
        fixedCol('nominating_teams', 300),
      ]);

      expect(fixedWidthByColumn).toEqual({ nominating_teams: '300px' });
      expect(columnsToMeasure).toEqual([]);
    });

    it('routes columns without column_width to columnsToMeasure', () => {
      const gene = col('gene');
      const { fixedWidthByColumn, columnsToMeasure } = resolveFixedColumnWidths([gene]);

      expect(fixedWidthByColumn).toEqual({});
      expect(columnsToMeasure).toEqual([gene]);
    });

    it('splits a mixed list into fixed and measured columns', () => {
      const gene = col('gene');
      const { fixedWidthByColumn, columnsToMeasure } = resolveFixedColumnWidths([
        gene,
        fixedCol('nominating_teams', 300),
      ]);

      expect(fixedWidthByColumn).toEqual({ nominating_teams: '300px' });
      expect(columnsToMeasure).toEqual([gene]);
    });

    it('applies column_width verbatim, bypassing the MIN/MAX clamp', () => {
      const { fixedWidthByColumn } = resolveFixedColumnWidths([
        fixedCol('narrow', MIN_COLUMN_WIDTH_PX - 30),
        fixedCol('wide', MAX_COLUMN_WIDTH_PX + 200),
      ]);

      expect(fixedWidthByColumn).toEqual({
        narrow: `${MIN_COLUMN_WIDTH_PX - 30}px`,
        wide: `${MAX_COLUMN_WIDTH_PX + 200}px`,
      });
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
