import { expect, test } from '@playwright/test';

test.describe('home', () => {
  test('model overview card links to model overview CT', async ({ page }) => {
    await page.goto('');

    const modelOverviewCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Model Overview' }) });
    await expect(modelOverviewCard).toBeVisible();

    await modelOverviewCard.click();

    await page.waitForURL('/comparison/model');
    await expect(page.getByRole('heading', { level: 1, name: 'Model Overview' })).toBeVisible();
  });

  test('differential expression card links to gene expression CT', async ({ page }) => {
    await page.goto('');

    const differentialExpressionCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Differential Expression' }) });
    await expect(differentialExpressionCard).toBeVisible();

    await differentialExpressionCard.click();

    await page.waitForURL('/comparison/expression');
    await expect(page.getByRole('heading', { level: 1, name: 'Gene Expression' })).toBeVisible();
  });

  test('disease correlation card links to disease correlation CT', async ({ page }) => {
    await page.goto('');

    const diseaseCorrelationCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Disease Correlation' }) });
    await expect(diseaseCorrelationCard).toBeVisible();

    await diseaseCorrelationCard.click();

    await page.waitForURL('/comparison/correlation');
    await expect(
      page.getByRole('heading', { level: 1, name: 'Disease Correlation' }),
    ).toBeVisible();
  });
});
