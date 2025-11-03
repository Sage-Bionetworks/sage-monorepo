import { provideHttpClient } from '@angular/common/http';
import { provideRouter, RouterModule } from '@angular/router';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
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
import { ComparisonToolTableComponent } from './comparison-tool-table.component';

async function setup(
  ctServiceOptions?: {
    pinnedItems?: string[];
    unpinnedData?: Record<string, unknown>[];
    pinnedData?: Record<string, unknown>[];
    maxPinnedItems?: number;
  },
  ctFilterServiceOptions?: { searchTerm?: string; filters?: ComparisonToolFilter[] },
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
    expect(screen.getByText(/Pinned Items/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /download/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /clear all/i })).toBeInTheDocument();
  });

  it('should not show pinned section when there are no pinned items', async () => {
    await setup();
    expect(screen.queryByText(/Pinned Items/i)).toBeNull();
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

  it('should show All Items divider when not searching/filtering and pinned exist', async () => {
    await setup({ pinnedItems: ['68fff1aaeb12b9674515fd58'] });
    expect(screen.getByText(/All Items/i)).toBeInTheDocument();
  });
});
