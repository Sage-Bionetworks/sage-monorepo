import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { gctFiltersMocks, SvgIconServiceStub } from '@sagebionetworks/agora/testing';
import { render, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { GeneComparisonToolFilterListComponent } from './gene-comparison-tool-filter-list.component';

async function setup() {
  const user = userEvent.setup();
  const component = await render(GeneComparisonToolFilterListComponent, {
    componentProperties: {
      filters: JSON.parse(JSON.stringify(gctFiltersMocks)),
      significanceThreshold: 0.05,
      significanceThresholdActive: true,
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
  return { user, component };
}

describe('Component: Gene Comparison Tool - Filter List', () => {
  it('should display filters', async () => {
    await setup();
    expect(screen.getByText('Significance ≤ 0.05')).toBeVisible();
    for (const filter of gctFiltersMocks[0].options) {
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
    const filterLabel = gctFiltersMocks[0].options[0].label;
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

    for (const option of gctFiltersMocks[0].options) {
      expect(screen.queryByText(option.label)).toBeNull();
    }
  });
});
