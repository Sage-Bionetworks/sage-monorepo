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

test.describe('model details - omics', () => {
  test('gene expression card links to gene expression CT', async ({ page }) => {
    await page.goto('/models/APOE4');
    const card = page.getByRole('button', {
      name: /view gene expression results.*comparison tool/i,
    });
    await card.click();
    await page.waitForURL('/comparison/expression?model=APOE4');
  });

  test('disease correlation card links to disease correlation CT', async ({ page }) => {
    await page.goto('/models/APOE4');
    const card = page.getByRole('button', {
      name: /view disease correlation results.*comparison tool/i,
    });
    await card.click();
    await page.waitForURL('/comparison/correlation?model=APOE4');
  });
});

test.describe('model details - resources', () => {
  const resourcesUrl = '/models/3xTg-AD/resources';

  test('AD knowledge portal card links to ADKP study page for this model', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('button', {
      name: /ad knowledge portal/i,
    });

    const popupPromise = page.waitForEvent('popup');
    await card.click();
    const popup = await popupPromise;

    await popup.waitForURL(
      'https://adknowledgeportal.synapse.org/Explore/Studies/DetailsPage/StudyDetails?Study=**',
    );
    await expect.poll(() => page.getByText(/3xtg-ad/i).count()).toBeGreaterThan(0);
  });

  test('alzforum card links to alzforum page for this model', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('button', {
      name: /alzforum/i,
    });

    const popupPromise = page.waitForEvent('popup');
    await card.click();
    const popup = await popupPromise;

    await popup.waitForURL('https://www.alzforum.org/research-models/**');
    await expect.poll(() => page.getByText(/3xtg-ad/i).count()).toBeGreaterThan(0);
  });

  test('JAX card links to JAX page for this model', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('button', {
      name: /jax/i,
    });

    const popupPromise = page.waitForEvent('popup');
    await card.click();
    const popup = await popupPromise;

    await popup.waitForURL('https://www.jax.org/strain/**');
    await expect.poll(() => page.getByText(/3xtg-ad/i).count()).toBeGreaterThan(0);
  });
});
