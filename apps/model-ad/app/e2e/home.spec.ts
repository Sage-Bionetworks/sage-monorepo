import { expect, test } from '@playwright/test';

test.describe('home', () => {
  test('model overview card links to model overview CT', async ({ page }) => {
    await page.goto('');

    const modelOverviewCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Model Overview' }) });
    await expect(modelOverviewCard).toBeVisible();

    await modelOverviewCard.click();

    await expect(page).toHaveURL('/comparison/model');
    await expect(page.getByRole('heading', { level: 1, name: 'Model Overview' })).toBeVisible();
  });

  test('gene expression card links to gene expression CT', async ({ page }) => {
    await page.goto('');

    const geneExpressionCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Gene Expression' }) });
    await expect(geneExpressionCard).toBeVisible();

    await geneExpressionCard.click();

    await expect(page).toHaveURL('/comparison/expression');
    await expect(page.getByRole('heading', { level: 1, name: 'Gene Expression' })).toBeVisible();
  });

  test('disease correlation card links to disease correlation CT', async ({ page }) => {
    await page.goto('');

    const diseaseCorrelationCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Disease Correlation' }) });
    await expect(diseaseCorrelationCard).toBeVisible();

    await diseaseCorrelationCard.click();

    await expect(page).toHaveURL('/comparison/correlation');
    await expect(
      page.getByRole('heading', { level: 1, name: 'Disease Correlation' }),
    ).toBeVisible();
  });
});
