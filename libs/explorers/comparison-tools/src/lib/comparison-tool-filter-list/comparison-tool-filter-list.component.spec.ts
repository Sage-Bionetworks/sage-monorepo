import { provideHttpClient } from '@angular/common/http';
import {
  provideComparisonToolFilterService,
  SvgIconService,
} from '@sagebionetworks/explorers/services';
import {
  mockComparisonToolFiltersWithSelections,
  SvgIconServiceStub,
} from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { ComparisonToolFilterListComponent } from './comparison-tool-filter-list.component';

async function setup() {
  const user = userEvent.setup();
  const component = await render(ComparisonToolFilterListComponent, {
    providers: [
      provideHttpClient(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      ...provideComparisonToolFilterService({
        filters: JSON.parse(JSON.stringify(mockComparisonToolFiltersWithSelections)),
        significanceThreshold: 0.05,
        significanceThresholdActive: true,
      }),
    ],
  });
  return { user, component };
}

describe('Component: Comparison Tool - Filter List', () => {
  it('should display filters', async () => {
    await setup();
    expect(screen.getByText('Significance ≤ 0.05')).toBeVisible();
    for (const filter of mockComparisonToolFiltersWithSelections[0].options) {
      expect(screen.getByText(filter.label)).toBeVisible();
    }
  });

  it('should remove significance threshold filter', async () => {
    const { user } = await setup();

    const significanceThreshold = screen.getByText('Significance ≤ 0.05');
    expect(significanceThreshold).toBeVisible();

    const clearButton = screen.getByRole('button', { name: /Clear Significance/ });
    await user.click(clearButton);

    expect(significanceThreshold).not.toBeVisible();
  });

  it('should remove filter', async () => {
    const filterLabel = mockComparisonToolFiltersWithSelections[0].options[0].label;
    const { user } = await setup();

    const filter = screen.getByText(filterLabel);
    expect(filter).toBeVisible();

    const clearButton = screen.getByRole('button', { name: new RegExp(`Clear ${filterLabel}`) });
    await user.click(clearButton);

    expect(filter).not.toBeVisible();
  });

  it('should remove all filters', async () => {
    const { user } = await setup();

    const clearAllButton = screen.getByRole('button', { name: /Clear all/ });
    await user.click(clearAllButton);

    for (const option of mockComparisonToolFiltersWithSelections[0].options) {
      expect(screen.queryByText(option.label)).toBeNull();
    }
  });
});
