import { expect, Page, test } from '@playwright/test';
import { baseURL } from '../playwright.config';
import { GCT_RNA_SUBCATEGORIES } from './helpers/constants';
import { waitForSpinnerNotVisible } from './helpers/utils';

test.describe('specific viewport block', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('invalid gene results in a 404 redirect', async ({ page }) => {
    // go to invalid ENSG page
    await page.goto('/genes/ENSG00000000000');
    await waitForSpinnerNotVisible(page);

    // expect a title "to contain" a substring.
    await expect(page).toHaveTitle('Page not found');

    // expect div for page not found content to be visible
    await expect(page.getByRole('heading', { name: 'Page not found.' })).toBeVisible();
  });

  test('consistency of change section heading is visible when using anchor link', async ({
    page,
  }) => {
    await page.goto('/genes/ENSG00000178209/evidence/rna#consistency-of-change');

    await waitForSpinnerNotVisible(page);

    const header = page.getByRole('heading', { name: 'Consistency of Change in Expression' });
    await expect(header).toBeInViewport();
  });
});

test.describe('legacy url redirects', () => {
  test('redirects to gene details', async ({ page }) => {
    await page.goto('/genes/(genes-router:gene-details/ENSG00000135940)');
    await expect(page).toHaveURL(`${baseURL}/genes/ENSG00000135940`);
  });
});

test.describe('gene details - query parameter sets initial selected model', () => {
  const path = '/genes/ENSG00000178209/evidence/rna';
  function buildUrlWithModelQueryParam(model: string) {
    return `${path}?model=${model}`;
  }
  async function confirmDropdown(page: Page, model: string) {
    await expect(page.getByRole('combobox', { name: model })).toBeVisible();
  }

  test('default model is used when there is no query parameter', async ({ page }) => {
    await page.goto(path);
    await waitForSpinnerNotVisible(page);
    await confirmDropdown(page, GCT_RNA_SUBCATEGORIES.AD);
  });

  for (const model of Object.values(GCT_RNA_SUBCATEGORIES)) {
    test(`model ${model} is used when query parameter is set`, async ({ page }) => {
      await page.goto(buildUrlWithModelQueryParam(model));
      await waitForSpinnerNotVisible(page);
      await confirmDropdown(page, model);
    });
  }
});
