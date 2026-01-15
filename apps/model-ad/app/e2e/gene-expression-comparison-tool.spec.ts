import { expect, test } from '@playwright/test';
import {
  ColumnConfig,
  expectPinnedParams,
  expectPinnedRows,
  getPinnedTable,
  getQueryParamFromValues,
  getUnpinnedTable,
  pinByName,
  searchViaFilterbox,
  testClickColumnTogglesSortOrder,
  testClickColumnUpdatesSortUrl,
  testClickDifferentColumnsReplacesSingleSort,
  testFullCaseInsensitiveMatch,
  testMetaClickBuildsMultiColumnSort,
  testMetaClickTogglesExistingSortOrder,
  testMultiColumnSortRestoredFromUrl,
  testPartialCaseInsensitiveSearch,
  testPinLastItemLastPageGoesToPreviousPage,
  testSearchExcludesPinnedItems,
  testSortRestoredFromUrl,
  testTableReturnsToFirstPageWhenCategoriesChanged,
  testTableReturnsToFirstPageWhenFilterSelectedAndRemoved,
  testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared,
  testTableReturnsToFirstPageWhenSortChanged,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { fetchComparisonToolConfig, navigateToComparison } from './helpers/comparison-tool';

const CT_PAGE = 'Gene Expression';
const categories = [
  'RNA - DIFFERENTIAL EXPRESSION',
  'Tissue - Hippocampus',
  'Sex - Females & Males',
];
const categoriesQueryParams = getQueryParamFromValues(categories, 'categories');
const cacul1Matches = [
  'ENSMUSG00000033417~3xTg-AD',
  'ENSMUSG00000033417~5xFAD (UCI)',
  'ENSMUSG00000033417~Abca7*V1599M',
  'ENSMUSG00000033417~Abca7*V1599M.5xFAD',
  'ENSMUSG00000033417~Trem2-R47H_NSS',
  'ENSMUSG00000033417~Trem2-R47H_NSS.5xFAD',
];

test.describe('gene expression', () => {
  test.describe('filter panel', () => {
    test('clicking Filter Results button opens the filter panel', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      const filterButton = page.getByRole('button', { name: 'Filter Results' });
      await expect(filterButton).toBeVisible();

      await filterButton.click();

      const filterPanel = page.locator('.filter-panel');
      await expect(filterPanel).toHaveClass(/open/);
    });

    test('clicking close button closes the filter panel', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      const filterButton = page.getByRole('button', { name: 'Filter Results' });
      await filterButton.click();

      const filterPanel = page.locator('.filter-panel');
      await expect(filterPanel).toHaveClass(/open/);

      const closeButton = page.getByRole('button', { name: 'close' });
      await closeButton.click();

      await expect(filterPanel).not.toHaveClass(/open/);
    });

    test('clicking Filter Results button again closes the filter panel', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      const filterButton = page.getByRole('button', { name: 'Filter Results' });
      await filterButton.click();

      const filterPanel = page.locator('.filter-panel');
      await expect(filterPanel).toHaveClass(/open/);

      // Click the filter button again to close
      await filterButton.click();

      await expect(filterPanel).not.toHaveClass(/open/);
    });
  });

  test('filterbox search without comma returns partial case-insensitive matches', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testPartialCaseInsensitiveSearch(page, 'acul', cacul1Matches);
  });

  test('filterbox search excludes pinned items from results', async ({ page }) => {
    const queryParameters = [
      categoriesQueryParams,
      getQueryParamFromValues(cacul1Matches, 'pinned'),
    ].join('&');
    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await testSearchExcludesPinnedItems(page, cacul1Matches, 'acul', 'cacul1,');
  });

  test('filterbox search with commas returns full, case-insensitive matches', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testFullCaseInsensitiveMatch(
      page,
      'fer,',
      [
        'ENSMUSG00000000127~3xTg-AD',
        'ENSMUSG00000000127~5xFAD (UCI)',
        'ENSMUSG00000000127~Abca7*V1599M',
        'ENSMUSG00000000127~Abca7*V1599M.5xFAD',
        'ENSMUSG00000000127~Trem2-R47H_NSS',
        'ENSMUSG00000000127~Trem2-R47H_NSS.5xFAD',
      ], // Fer
      'ENSMUSG00000027356~3xTg-AD', // Fermt1
    );
  });

  test('filterbox search partial case-insensitive matches with special characters', async ({
    page,
  }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testPartialCaseInsensitiveSearch(page, '(r', [
      'ENSMUSG00000086429~3xTg-AD',
      'ENSMUSG00000086429~5xFAD (UCI)',
      'ENSMUSG00000086429~Abca7*V1599M',
      'ENSMUSG00000086429~Abca7*V1599M.5xFAD',
      'ENSMUSG00000086429~Trem2-R47H_NSS',
      'ENSMUSG00000086429~Trem2-R47H_NSS.5xFAD',
    ]); // Gt(ROSA)26Sor
  });

  test('pinned items are cached when switching between categories', async ({ page }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const pinnedItems = ['ENSMUSG00000000001~3xTg-AD', 'ENSMUSG00000000001~5xFAD (UCI)']; // Gnai3
    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(pinnedItems, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, pinnedItems);
    await expectPinnedParams(page, pinnedItems);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const malesOption = page.getByRole('option', { name: /sex - males/i });
    await malesOption.click();
    await expect(malesOption).toBeHidden();

    await expectPinnedRows(page, pinnedItems);
    await expectPinnedParams(page, pinnedItems);

    await dropdown.click();
    const femalesAndMalesOption = page.getByRole('option', { name: /sex - females & males/i });
    await femalesAndMalesOption.click();
    await expect(femalesAndMalesOption).toBeHidden();

    await expectPinnedRows(page, pinnedItems);
    await expectPinnedParams(page, pinnedItems);
  });

  test('pinned items cache is reset when new item is pinned', async ({ page }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const initialPinned = ['ENSMUSG00000000001~3xTg-AD']; // Gnai3
    const afterPinPinned = ['ENSMUSG00000000001~3xTg-AD', 'ENSMUSG00000000001~5xFAD (UCI)'];

    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(initialPinned, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const malesOption = page.getByRole('option', { name: /sex - males/i });
    await malesOption.click();
    await expect(malesOption).toBeHidden();

    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    await searchViaFilterbox(page, 'gnai3');
    await pinByName(getUnpinnedTable(page), page, 'ENSMUSG00000000001~5xFAD (UCI)');
    await expectPinnedRows(page, afterPinPinned);
    await expectPinnedParams(page, afterPinPinned);

    await dropdown.click();
    const femalesAndMalesOption = page.getByRole('option', { name: /sex - females & males/i });
    await femalesAndMalesOption.click();
    await expect(femalesAndMalesOption).toBeHidden();

    await expectPinnedRows(page, afterPinPinned);
    await expectPinnedParams(page, afterPinPinned);
  });

  test('pinned items cache is reset when new item is unpinned', async ({ page }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const initialPinned = ['ENSMUSG00000000001~3xTg-AD', 'ENSMUSG00000000001~5xFAD (UCI)']; // Gnai3
    const afterUnpinPinned = ['ENSMUSG00000000001~3xTg-AD'];

    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(initialPinned, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const malesOption = page.getByRole('option', { name: /sex - males/i });
    await malesOption.click();
    await expect(malesOption).toBeHidden();

    await expectPinnedRows(page, initialPinned);
    await expectPinnedParams(page, initialPinned);

    await unPinByName(getPinnedTable(page), page, 'ENSMUSG00000000001~5xFAD (UCI)');
    await expectPinnedRows(page, afterUnpinPinned);
    await expectPinnedParams(page, afterUnpinPinned);

    await dropdown.click();
    const femalesAndMalesOption = page.getByRole('option', { name: /sex - females & males/i });
    await femalesAndMalesOption.click();
    await expect(femalesAndMalesOption).toBeHidden();

    await expectPinnedRows(page, afterUnpinPinned);
    await expectPinnedParams(page, afterUnpinPinned);
  });

  test('pinned table and URL should only include currently visible pinned items from cache', async ({
    page,
  }) => {
    const firstCategories = [
      'RNA - DIFFERENTIAL EXPRESSION',
      'Tissue - Hippocampus',
      'Sex - Females & Males',
    ];
    const firstPinned = [
      'ENSMUSG00000000001~3xTg-AD',
      'ENSMUSG00000000001~5xFAD (UCI)',
      'ENSMUSG00000000001~Abca7*V1599M',
    ]; // Gnai3
    const queryParameters = [
      getQueryParamFromValues(firstCategories, 'categories'),
      getQueryParamFromValues(firstPinned, 'pinned'),
    ].join('&');
    const expectedSecondPinned = ['ENSMUSG00000000001~5xFAD (UCI)'];

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedRows(page, firstPinned);
    await expectPinnedParams(page, firstPinned);

    const dropdown = page.getByRole('combobox').first();
    await dropdown.click();
    const cerebralCortexOption = page.getByRole('option', { name: /tissue - cerebral cortex/i });
    await cerebralCortexOption.click();
    await expect(cerebralCortexOption).toBeHidden();

    await expectPinnedRows(page, expectedSecondPinned);
    await expectPinnedParams(page, expectedSecondPinned);

    await dropdown.click();
    const hippocampusOption = page.getByRole('option', { name: /tissue - hippocampus/i });
    await hippocampusOption.click();
    await expect(hippocampusOption).toBeHidden();

    await expectPinnedRows(page, firstPinned);
    await expectPinnedParams(page, firstPinned);
  });

  test('table loads previous page when last item on last page is pinned', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);
    await testPinLastItemLastPageGoesToPreviousPage(page);
  });

  test('table returns to first page when filter selected and removed', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const config = configs[0];
    const categories = config.dropdowns;
    const filters = config.filters;
    expect(filters.length).toBeGreaterThan(1);
    const filter = filters[0];

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );
    await testTableReturnsToFirstPageWhenFilterSelectedAndRemoved(
      page,
      filter.name,
      filter.values[0],
    );
  });

  test('table returns to first page when search term entered and cleared', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared(page);
  });

  test('table returns to first page when categories changed', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenCategoriesChanged(page);
  });

  test('table returns to first page when sort changed', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testTableReturnsToFirstPageWhenSortChanged(page);
  });

  test.describe('sort URL sync', () => {
    // Columns: 0=Gene (gene_symbol), 1=Model (name), 2=Control, 3=4 months, 4=12 months
    // Default sort: gene_symbol ASC, name ASC

    const sortColumns: ColumnConfig[] = [
      { name: '4 months', field: '4 months' },
      { name: '12 months', field: '12 months' },
      { name: 'Control', field: 'matched_control' },
    ];

    test('clicking column updates URL with sortFields and sortOrders', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickColumnUpdatesSortUrl(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('clicking same column toggles between descending and ascending', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickColumnTogglesSortOrder(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('sort is restored from URL on page load', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        `${categoriesQueryParams}&sortFields=4%20months&sortOrders=1`,
      );
      await testSortRestoredFromUrl(page, sortColumns[0].name, sortColumns[0].field);
    });

    test('clicking different columns in sequence replaces single-column sort', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testClickDifferentColumnsReplacesSingleSort(page, sortColumns);
    });

    test('Meta+click builds multi-column sort and regular click resets', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickBuildsMultiColumnSort(page, sortColumns);
    });

    test('multi-column sort is restored from URL', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        `${categoriesQueryParams}&sortFields=4%20months,12%20months&sortOrders=-1,1`,
      );
      await testMultiColumnSortRestoredFromUrl(page, sortColumns.slice(0, 2), sortColumns[2]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);
      await testMetaClickTogglesExistingSortOrder(page, sortColumns.slice(0, 2));
    });
  });

  test.describe('heatmap details panel', () => {
    test('clicking a heatmap circle opens the details panel', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      // Find a visible heatmap circle button (some may be hidden due to no data or significance filter)
      // The circle div inside must be visible (display: block)
      const heatmapButton = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') })
        .first();
      await heatmapButton.scrollIntoViewIfNeeded();
      await expect(heatmapButton).toBeVisible();
      await heatmapButton.click();

      // Verify a details panel heading is visible
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();
    });

    test('clicking the same heatmap circle again closes the details panel', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      const heatmapButton = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') })
        .first();
      await heatmapButton.scrollIntoViewIfNeeded();
      await expect(heatmapButton).toBeVisible();

      // First click - open panel
      await heatmapButton.click();
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Second click - close panel (toggle behavior)
      await heatmapButton.click();
      await expect(page.locator('.heatmap-details-panel-heading')).toBeHidden();
    });

    test('clicking a different heatmap circle updates the panel content', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      const heatmapButtons = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') });
      const firstButton = heatmapButtons.first();
      // Use a button further down the list to avoid popover overlap
      const secondButton = heatmapButtons.nth(10);

      // Click first button (scrolls into view automatically on click)
      await firstButton.scrollIntoViewIfNeeded();
      await firstButton.click();
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Click second button - use force: true in case popover still overlaps during transition
      await secondButton.scrollIntoViewIfNeeded();
      await secondButton.click({ force: true });

      // Panel should still be visible (showing different content)
      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();
    });

    test('clicking outside the panel closes it', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', categoriesQueryParams);

      const heatmapButton = page
        .locator('button.heatmap-circle-button')
        .filter({ has: page.locator('.heatmap-circle[style*="display: block"]') })
        .first();
      await heatmapButton.scrollIntoViewIfNeeded();
      await heatmapButton.click();

      await expect(page.locator('.heatmap-details-panel-heading').first()).toBeVisible();

      // Click outside the panel (on the page body)
      await page.locator('body').click({ position: { x: 10, y: 10 } });
      await expect(page.locator('.heatmap-details-panel-heading')).toBeHidden();
    });
  });
});
