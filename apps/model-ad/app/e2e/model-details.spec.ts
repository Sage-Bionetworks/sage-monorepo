import { expect, test } from '@playwright/test';

test.describe('model details', () => {
  test('invalid model results in a 404 redirect', async ({ page }) => {
    await page.goto('/models/DOES-NOT-EXIST');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

  test('invalid model with special characters results in a 404 redirect', async ({ page }) => {
    await page.goto('/models/does (not/exist)?modelOrganism=mouse');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

  test('valid model displays model name', async ({ page }) => {
    await page.goto('/models/APOE4?modelOrganism=mouse');
    await expect(page.getByRole('heading', { level: 1, name: 'APOE4' })).toBeVisible();
  });

  test('falls back to mouse data when no modelOrganism query param is provided', async ({
    page,
  }) => {
    await page.goto('/models/APOE4');
    await page.waitForURL('/models/APOE4?modelOrganism=mouse');
    await expect(page).toHaveTitle('Mouse Model Details | APOE4 AD model');
    await expect(page.locator('meta[name="description"]')).toHaveAttribute(
      'content',
      "Explore information and results for the APOE4 Alzheimer's Disease mouse model.",
    );
  });

  test('loads mouse data when modelOrganism=mouse query param is provided', async ({ page }) => {
    await page.goto('/models/APOE4?modelOrganism=mouse');
    await expect(page).toHaveTitle('Mouse Model Details | APOE4 AD model');
    await expect(page.locator('meta[name="description"]')).toHaveAttribute(
      'content',
      "Explore information and results for the APOE4 Alzheimer's Disease mouse model.",
    );
  });

  test('loads marmoset data when modelOrganism=marmoset query param is provided', async ({
    page,
  }) => {
    const model = 'Presenilin 1';
    await page.goto(`/models/${encodeURIComponent(model)}?modelOrganism=marmoset`);
    await expect(page).toHaveTitle(`Marmoset Model Details | ${model} AD model`);
    await expect(page.locator('meta[name="description"]')).toHaveAttribute(
      'content',
      `Explore information and results for the ${model} Alzheimer's Disease marmoset model.`,
    );
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();
  });

  test('loads marmoset data when modelOrganism query param has wrong casing', async ({ page }) => {
    const model = 'Presenilin 1';
    const modelPath = `/models/${encodeURIComponent(model)}`;
    await page.goto(`${modelPath}?modelOrganism=Marmoset`);
    await page.waitForURL(`${modelPath}?modelOrganism=marmoset`);
    await expect(page).toHaveTitle(`Marmoset Model Details | ${model} AD model`);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();
  });
});
