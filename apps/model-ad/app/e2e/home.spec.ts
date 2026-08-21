import { expect, test } from '@playwright/test';

test.describe('home', () => {
  test('mouse model overview card links to mouse model overview CT', async ({ page }) => {
    await page.goto('');

    const mouseModelOverviewCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Mouse Model Overview' }) });
    await expect(mouseModelOverviewCard).toBeVisible();

    await mouseModelOverviewCard.click();

    await page.waitForURL('/comparison/model/mouse');
    await expect(
      page.getByRole('heading', { level: 1, name: 'Mouse Model Overview' }),
    ).toBeVisible();
  });

  test('differential expression card links to differential expression CT', async ({ page }) => {
    await page.goto('');

    const differentialExpressionCard = page
      .getByRole('button')
      .filter({ has: page.getByRole('heading', { level: 2, name: 'Differential Expression' }) });
    await expect(differentialExpressionCard).toBeVisible();

    await differentialExpressionCard.click();

    await page.waitForURL('/comparison/expression');
    await expect(
      page.getByRole('heading', { level: 1, name: 'Differential Expression' }),
    ).toBeVisible();
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
