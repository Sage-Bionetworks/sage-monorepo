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

test.describe('model details - model with special characters', () => {
  const specialModel = '5xFAD (IU/Jax/Pitt)';
  const specialModelEncoded = '5xFAD%20%28IU%2FJax%2FPitt%29';

  test('valid model with special characters displays model name', async ({ page }) => {
    await page.goto(`/models/${specialModel}`);
    await page.waitForURL(`/models/${specialModelEncoded}`);
    await expect(page.getByRole('heading', { level: 1, name: specialModel })).toBeVisible();
  });

  test('url is encoded when changing tabs for model with special characters', async ({ page }) => {
    await page.goto(`/models/${specialModel}`);
    await expect(page.getByRole('heading', { level: 2, name: 'Available Data' })).toBeVisible();

    const resourcesTab = page.getByRole('button', { name: 'Resources' });
    await resourcesTab.click();
    await page.waitForURL(`/models/${specialModelEncoded}/resources`);

    await expect(page.getByRole('heading', { level: 1, name: specialModel })).toBeVisible();
    await expect(
      page.getByRole('heading', { level: 2, name: 'Model-Specific Resources' }),
    ).toBeVisible();
  });

  test('correct tab is loaded from url for model with special characters', async ({ page }) => {
    await page.goto(`/models/${specialModel}/resources`);
    await page.waitForURL(`/models/${specialModelEncoded}/resources`);

    await expect(page.getByRole('heading', { level: 1, name: specialModel })).toBeVisible();
    await expect(
      page.getByRole('heading', { level: 2, name: 'Model-Specific Resources' }),
    ).toBeVisible();
  });

  test('can reload page for model with special characters', async ({ page }) => {
    await page.goto(`/models/${specialModel}/resources`);
    await page.waitForURL(`/models/${specialModelEncoded}/resources`);
    await expect(page.getByRole('heading', { level: 1, name: specialModel })).toBeVisible();

    await page.reload();
    await page.waitForURL(`/models/${specialModelEncoded}/resources`);
    await expect(page.getByRole('heading', { level: 1, name: specialModel })).toBeVisible();
  });

  test('invalid model with special characters results in a 404 redirect', async ({ page }) => {
    await page.goto('/models/does (not/exist)');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });
});

test.describe('model details - boxplots selector - table of contents', () => {
  test('clicking on table of contents link scrolls to appropriate section', async ({ page }) => {
    const model = '3xTg-AD';
    await page.goto(`/models/${model}/biomarkers`);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();
    await expect(page.getByRole('heading', { level: 2, name: 'Table of Contents' })).toBeVisible();

    const toc = page.locator('.table-of-contents-container');
    const tocLinks = toc.getByRole('button');
    const tocLinksCount = await tocLinks.count();

    for (let i = 0; i < tocLinksCount; i++) {
      await test.step(`validate link ${i}`, async () => {
        const tocLink = tocLinks.nth(i);
        const tocLinkName = (await tocLink.textContent()) ?? '';
        await tocLink.click();
        await expect(
          page.getByRole('heading', { level: 2, name: tocLinkName, exact: true }),
        ).toBeInViewport();
      });
    }
  });
});
