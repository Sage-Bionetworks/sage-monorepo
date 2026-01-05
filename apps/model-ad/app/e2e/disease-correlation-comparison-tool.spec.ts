import { expect, test } from '@playwright/test';
import {
  expectCategories,
  expectCategoriesParams,
  expectPinnedParams,
  expectPinnedRows,
  expectSortFieldsParams,
  expectSortOrdersParams,
  expectUnpinnedTableOnly,
  getPinnedTable,
  getQueryParamFromValues,
  getRowByName,
  getSortFieldsQueryParams,
  getSortOrdersQueryParams,
  testFullCaseInsensitiveMatch,
  testPartialCaseInsensitiveSearch,
  testPinLastItemLastPageGoesToPreviousPage,
  testSearchExcludesPinnedItems,
  testTableReturnsToFirstPageWhenCategoriesChanged,
  testTableReturnsToFirstPageWhenFilterSelectedAndRemoved,
  testTableReturnsToFirstPageWhenSearchTermEnteredAndCleared,
  testTableReturnsToFirstPageWhenSortChanged,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../playwright.config';
import { COMPARISON_TOOL_PATHS } from './constants';
import {
  fetchComparisonToolConfig,
  fetchDiseaseCorrelations,
  navigateToComparison,
} from './helpers/comparison-tool';

const CT_PAGE = 'Disease Correlation';

test.describe('disease correlation', () => {
  test('pins are cached across dropdown selections and can be unpinned', async ({ page }) => {
    const correlations = await fetchDiseaseCorrelations(page);
    const [firstCorrelation] = correlations;
    const pinnedItems = [firstCorrelation.composite_id];

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      `pinned=${firstCorrelation.composite_id}`,
    );

    await expectPinnedParams(page, pinnedItems);
    await expectPinnedRows(page, pinnedItems);

    const dropdown = page.getByRole('combobox');
    await dropdown.click();
    const options = page.getByRole('option');
    const secondOption = options.nth(1);
    await secondOption.click();

    await expectPinnedParams(page, pinnedItems);
    await expectPinnedRows(page, pinnedItems);

    await dropdown.click();
    await options.first().click();

    await expectPinnedParams(page, pinnedItems);
    await expectPinnedRows(page, pinnedItems);

    const pinnedTable = getPinnedTable(page);
    await unPinByName(pinnedTable, page, firstCorrelation.composite_id);
    await expectPinnedParams(page, []);
  });

  test('categories are added to URL on load', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const defaultCategories = configs[0]?.dropdowns;
    expect(defaultCategories.length).toBeGreaterThan(1); // disease correlation has dropdown selections

    await navigateToComparison(page, 'Disease Correlation', true);

    const shareUrlButton = page.getByRole('button', { name: 'Share URL' });
    await expect(shareUrlButton).toBeVisible();

    await expect(page).toHaveURL(
      `${baseURL}${COMPARISON_TOOL_PATHS[CT_PAGE]}?${getQueryParamFromValues(defaultCategories, 'categories')}`,
    );
  });

  test('pinned items and categories in the URL are restored in the UI', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    const [firstCorrelation] = await fetchDiseaseCorrelations(page, categories);
    expect(firstCorrelation).toBeDefined();

    const queryParameters = [
      getQueryParamFromValues(categories, 'categories'),
      getQueryParamFromValues([firstCorrelation.composite_id], 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);

    await expect(page.locator('explorers-base-table')).toHaveCount(2);
    await expect(
      getRowByName(getPinnedTable(page), page, firstCorrelation.composite_id),
    ).toHaveCount(1);
    await expectPinnedParams(page, [firstCorrelation.composite_id]);
    await expectCategoriesParams(page, categories);
    await expectCategories(page, categories);
  });

  test('changing dropdown updates categories in URL', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);

    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const defaultCategories = configs[0]?.dropdowns;
    expect(defaultCategories.length).toBeGreaterThan(1);

    await expectCategoriesParams(page, defaultCategories);

    const dropdown = page.getByRole('combobox').last();
    await dropdown.click();
    const options = page.getByRole('option');
    const secondOption = options.nth(1);
    const secondOptionText = await secondOption.textContent();
    await secondOption.click();
    await expect(secondOption).toBeHidden();

    const newCategories = [...defaultCategories.slice(0, -1), secondOptionText ?? ''];
    await expectCategoriesParams(page, newCategories);
    await expectCategories(page, newCategories);
  });

  test('categories persist across navigation between comparison tools', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );

    await expectCategoriesParams(page, categories);
    await expectCategories(page, categories);

    await navigateToComparison(page, 'Model Overview', true, 'link');
    await expectCategoriesParams(page, []);

    await navigateToComparison(page, CT_PAGE, true, 'link');
    await expectCategoriesParams(page, categories);
    await expectCategories(page, categories);
  });

  test('invalid categories in URL fallback to default', async ({ page }) => {
    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(['Invalid Category', 'Another Invalid'], 'categories'),
    );

    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const defaultCategories = configs[0]?.dropdowns;

    await expectCategoriesParams(page, defaultCategories);
    await expectCategories(page, defaultCategories);
  });

  test('categories with commas are URL encoded and decoded correctly', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categoriesWithCommas =
      configs.find((config) => config.dropdowns.some((cat) => cat.includes(',')))?.dropdowns || [];
    expect(categoriesWithCommas.some((cat) => cat.includes(','))).toBe(true);

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categoriesWithCommas, 'categories'),
    );

    await expectCategoriesParams(page, categoriesWithCommas);
    await expectCategories(page, categoriesWithCommas);

    const url = page.url();
    expect(url).toContain('%252C');
  });

  test('query parameters are removed from URL when navigating to a non-comparison tool page', async ({
    page,
  }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    const [firstCorrelation] = await fetchDiseaseCorrelations(page, categories);
    expect(firstCorrelation).toBeDefined();

    const queryParameters = [
      getQueryParamFromValues(categories, 'categories'),
      getQueryParamFromValues([firstCorrelation.composite_id], 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
    await expectPinnedParams(page, [firstCorrelation.composite_id]);
    await expectCategoriesParams(page, categories);

    await page.getByRole('link', { name: 'Home' }).click();
    await expect(page).toHaveURL(`${baseURL}/`);
  });

  test('filterbox search without comma returns partial case-insensitive matches', async ({
    page,
  }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );

    await testPartialCaseInsensitiveSearch(page, 'apo', [
      'APOE4~12 months~Female',
      'APOE4~12 months~Male',
      'APOE4~4 months~Female',
      'APOE4~4 months~Male',
      'APOE4~8 months~Female',
      'APOE4~8 months~Male',
    ]);
  });

  test('filterbox search excludes pinned items from results', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    const pinnedCorrelations = [
      '5xFAD (IU/Jax/Pitt)~12 months~Female',
      '5xFAD (IU/Jax/Pitt)~12 months~Male',
      '5xFAD (IU/Jax/Pitt)~4 months~Female',
      '5xFAD (IU/Jax/Pitt)~4 months~Male',
    ];

    const queryParameters = [
      getQueryParamFromValues(categories, 'categories'),
      getQueryParamFromValues(pinnedCorrelations, 'pinned'),
    ].join('&');

    await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);

    await testSearchExcludesPinnedItems(page, pinnedCorrelations, 'fad', '5xfad (iu/jax/pitt),');
  });

  test('filterbox search with commas returns full, case-insensitive matches', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );

    await testFullCaseInsensitiveMatch(
      page,
      'load1,',
      [
        'LOAD1~12 months~Female',
        'LOAD1~12 months~Male',
        'LOAD1~4 months~Female',
        'LOAD1~4 months~Male',
        'LOAD1~8 months~Female',
        'LOAD1~8 months~Male',
      ],
      'LOAD1.Clasp2L163P~12 months~Female',
    );
  });

  test('filterbox search partial case-insensitive matches with special characters', async ({
    page,
  }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const categories = configs[1]?.dropdowns;
    expect(categories.length).toBeGreaterThan(1);

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      getQueryParamFromValues(categories, 'categories'),
    );

    await testPartialCaseInsensitiveSearch(page, '(iu', [
      '5xFAD (IU/Jax/Pitt)~12 months~Female',
      '5xFAD (IU/Jax/Pitt)~12 months~Male',
      '5xFAD (IU/Jax/Pitt)~4 months~Female',
      '5xFAD (IU/Jax/Pitt)~4 months~Male',
    ]);
  });

  test('table loads previous page when last item on last page is pinned', async ({ page }) => {
    await navigateToComparison(page, CT_PAGE, true);
    await testPinLastItemLastPageGoesToPreviousPage(page);
  });

  test('table returns to first page when filter selected and removed', async ({ page }) => {
    const configs = await fetchComparisonToolConfig(page, CT_PAGE);
    const filters = configs[0]?.filters;
    expect(filters.length).toBeGreaterThan(1);
    const filter = filters[0];

    await navigateToComparison(page, CT_PAGE, true);
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
    // Columns: 0=Model (name), 1=Age, 2=Sex, 3=IFG, 4=PHG, 5=TCX
    // Default sort: name ASC, age ASC, sex ASC

    test('clicking IFG column updates URL with sortFields and sortOrders', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      // Initial state: no sort params (default sort applied internally)
      expect(getSortFieldsQueryParams(page.url())).toEqual([]);
      expect(getSortOrdersQueryParams(page.url())).toEqual([]);

      // Click IFG column header (brain region - not part of default sort)
      await page.getByRole('columnheader', { name: 'IFG' }).click();

      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [-1]);
    });

    test('clicking same column toggles between descending and ascending', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      const ifgHeader = page.getByRole('columnheader', { name: 'IFG' });

      // First click - descending
      await ifgHeader.click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [-1]);

      // Second click - ascending
      await ifgHeader.click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [1]);

      // Third click - back to descending
      await ifgHeader.click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [-1]);

      // Fourth click - ascending again
      await ifgHeader.click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [1]);
    });

    test('sort is restored from URL on page load', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true, 'url', 'sortFields=PHG&sortOrders=1');

      await expectSortFieldsParams(page, ['PHG']);
      await expectSortOrdersParams(page, [1]);

      // Click the sorted column to toggle it
      await page.getByRole('columnheader', { name: 'PHG' }).click();
      await expectSortFieldsParams(page, ['PHG']);
      await expectSortOrdersParams(page, [-1]);

      // Click again to toggle back
      await page.getByRole('columnheader', { name: 'PHG' }).click();
      await expectSortFieldsParams(page, ['PHG']);
      await expectSortOrdersParams(page, [1]);
    });

    test('clicking different columns in sequence replaces single-column sort', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      // Click IFG
      await page.getByRole('columnheader', { name: 'IFG' }).click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [-1]);

      // Click PHG - should replace the sort
      await page.getByRole('columnheader', { name: 'PHG' }).click();
      await expectSortFieldsParams(page, ['PHG']);
      await expectSortOrdersParams(page, [-1]);

      // Click TCX - should replace again
      await page.getByRole('columnheader', { name: 'TCX' }).click();
      await expectSortFieldsParams(page, ['TCX']);
      await expectSortOrdersParams(page, [-1]);

      // Click IFG again - back to first column
      await page.getByRole('columnheader', { name: 'IFG' }).click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [-1]);
    });

    test('Meta+click builds multi-column sort with all brain regions', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      // Click IFG first
      await page.getByRole('columnheader', { name: 'IFG' }).click();
      await expectSortFieldsParams(page, ['IFG']);
      await expectSortOrdersParams(page, [-1]);

      // Meta+click PHG to add to sort
      await page.getByRole('columnheader', { name: 'PHG' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG']);
      await expectSortOrdersParams(page, [-1, -1]);

      // Meta+click TCX to add third column
      await page.getByRole('columnheader', { name: 'TCX' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [-1, -1, -1]);

      // Regular click on Age - should reset to single column sort
      await page.getByRole('columnheader', { name: 'Age' }).click();
      await expectSortFieldsParams(page, ['age']);
      await expectSortOrdersParams(page, [-1]);

      // Build multi-column sort again to verify cycle works
      await page.getByRole('columnheader', { name: 'IFG' }).click();
      await page.getByRole('columnheader', { name: 'PHG' }).click({ modifiers: ['Meta'] });
      await page.getByRole('columnheader', { name: 'TCX' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [-1, -1, -1]);
    });

    test('multi-column sort is restored from URL', async ({ page }) => {
      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        'sortFields=IFG,PHG,TCX&sortOrders=-1,1,-1',
      );

      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [-1, 1, -1]);

      // Toggle one of the columns
      await page.getByRole('columnheader', { name: 'IFG' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [1, 1, -1]);
    });

    test('Meta+click on existing sort columns toggles their order', async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      // Set up multi-column sort with all 3 brain regions
      await page.getByRole('columnheader', { name: 'IFG' }).click();
      await page.getByRole('columnheader', { name: 'PHG' }).click({ modifiers: ['Meta'] });
      await page.getByRole('columnheader', { name: 'TCX' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [-1, -1, -1]);

      // Meta+click IFG to toggle its order
      await page.getByRole('columnheader', { name: 'IFG' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [1, -1, -1]);

      // Meta+click PHG to toggle its order
      await page.getByRole('columnheader', { name: 'PHG' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [1, 1, -1]);

      // Meta+click TCX to toggle its order
      await page.getByRole('columnheader', { name: 'TCX' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [1, 1, 1]);

      // Toggle IFG back to descending
      await page.getByRole('columnheader', { name: 'IFG' }).click({ modifiers: ['Meta'] });
      await expectSortFieldsParams(page, ['IFG', 'PHG', 'TCX']);
      await expectSortOrdersParams(page, [-1, 1, 1]);
    });

    test('sort params persist with categories in URL', async ({ page }) => {
      const configs = await fetchComparisonToolConfig(page, CT_PAGE);
      const categories = configs[1]?.dropdowns;
      expect(categories.length).toBeGreaterThan(1);

      const queryParams = `${getQueryParamFromValues(categories, 'categories')}&sortFields=PHG&sortOrders=-1`;
      await navigateToComparison(page, CT_PAGE, true, 'url', queryParams);

      await expectCategoriesParams(page, categories);
      await expectSortFieldsParams(page, ['PHG']);
      await expectSortOrdersParams(page, [-1]);

      // Click to toggle sort and verify categories still present
      await page.getByRole('columnheader', { name: 'PHG' }).click();
      await expectCategoriesParams(page, categories);
      await expectSortFieldsParams(page, ['PHG']);
      await expectSortOrdersParams(page, [1]);

      // Add another sort column and verify categories persist
      await page.getByRole('columnheader', { name: 'IFG' }).click({ modifiers: ['Meta'] });
      await expectCategoriesParams(page, categories);
      await expectSortFieldsParams(page, ['PHG', 'IFG']);
      await expectSortOrdersParams(page, [1, -1]);
    });
  });
});
