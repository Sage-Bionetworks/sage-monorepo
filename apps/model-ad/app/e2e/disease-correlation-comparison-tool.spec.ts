import { expect, test } from '@playwright/test';
import {
  expectCategories,
  expectCategoriesParams,
  expectPinnedParams,
  expectPinnedRows,
  expectUnpinnedTableOnly,
  getPinnedTable,
  getQueryParamFromValues,
  getRowByName,
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
  test('pins are cached for dropdown selections and can be unpinned', async ({ page }) => {
    const correlations = await fetchDiseaseCorrelations(page);
    const [firstCorrelation] = correlations;

    await navigateToComparison(
      page,
      CT_PAGE,
      true,
      'url',
      `pinned=${firstCorrelation.composite_id}`,
    );

    await expectPinnedParams(page, [firstCorrelation.composite_id]);
    await expectPinnedRows(page, [firstCorrelation.name]);

    const dropdown = page.getByRole('combobox');
    await dropdown.click();
    const options = page.getByRole('option');
    const secondOption = options.nth(1);
    await secondOption.click();

    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await dropdown.click();
    await options.first().click();

    await expectPinnedParams(page, [firstCorrelation.composite_id]);
    await expectPinnedRows(page, [firstCorrelation.name]);

    const pinnedTable = getPinnedTable(page);
    await unPinByName(pinnedTable, page, firstCorrelation.name);
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
    await expect(getRowByName(getPinnedTable(page), page, firstCorrelation.name)).toHaveCount(1);
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
});
