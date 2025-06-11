import { expect, test } from '@playwright/test';

test.describe('model details', () => {
  test('invalid model results in a 404 redirect', async ({ page }) => {
    await page.goto('/models/DOES-NOT-EXIST');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

  test('valid model displays model name', async ({ page }) => {
    await page.goto('/models/APOE4');
    await expect(page.getByRole('heading', { level: 1, name: 'APOE4' })).toBeVisible();
  });

  test('default tab is omics', async ({ page }) => {
    await page.goto('/models/APOE4');
    await expect(page.getByRole('heading', { level: 2, name: 'Available Data' })).toBeVisible();
  });

  test('correct tab is loaded from url', async ({ page }) => {
    await page.goto('/models/3xTg-AD/biomarkers');
    await expect(page.getByRole('heading', { level: 2, name: 'Biomarkers' })).toBeVisible();
  });

  test('clicking on a tab updates url and displays correct content', async ({ page }) => {
    const modelPath = '/models/3xTg-AD';
    await page.goto(modelPath);

    const biomarkersTab = page.getByRole('button', { name: 'Biomarkers' });
    await biomarkersTab.click();
    await page.waitForURL(`${modelPath}/biomarkers`);
    await expect(page.getByRole('heading', { level: 2, name: 'Biomarkers' })).toBeVisible();

    const pathologyTab = page.getByRole('button', { name: 'Pathology' });
    await pathologyTab.click();
    await page.waitForURL(`${modelPath}/pathology`);
    await expect(page.getByRole('heading', { level: 2, name: 'Pathology' })).toBeVisible();

    const resourcesTab = page.getByRole('button', { name: 'Resources' });
    await resourcesTab.click();
    await page.waitForURL(`${modelPath}/resources`);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Model-Specific Resources' }),
    ).toBeVisible();

    const omicsTab = page.getByRole('button', { name: 'Omics' });
    await omicsTab.click();
    await page.waitForURL(`${modelPath}/omics`);
    await expect(page.getByRole('heading', { level: 2, name: 'Available Data' })).toBeVisible();
  });
});
