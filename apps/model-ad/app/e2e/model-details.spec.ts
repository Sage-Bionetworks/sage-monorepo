import { expect, test } from '@playwright/test';
import { searchAndGetSearchListItems } from './helpers';

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

  test('correct tab is loaded when navigating to new model via search', async ({ page }) => {
    const initialModelPath = '/models/3xTg-AD';
    const nextModel = 'LOAD1';

    await page.goto('/models/3xTg-AD');
    await expect(page.getByRole('heading', { level: 2, name: 'Available Data' })).toBeVisible();

    const pathologyTab = page.getByRole('button', { name: 'Pathology' });
    await pathologyTab.click();
    await page.waitForURL(`${initialModelPath}/pathology`);
    await expect(page.getByRole('heading', { level: 2, name: 'Pathology' })).toBeVisible();

    const { searchListItems } = await searchAndGetSearchListItems(nextModel, page);
    await searchListItems.first().click();

    await page.waitForURL(`/models/${nextModel}`);
    await expect(page.getByRole('heading', { level: 1, name: nextModel })).toBeVisible();
    await expect(page.getByRole('heading', { level: 2, name: 'Available Data' })).toBeVisible();
  });

  test('scrolls to panel content on initial load when tab specified in url and no hash fragment is present', async ({
    page,
  }) => {
    const model = '3xTg-AD';
    await page.goto(`/models/${model}/omics`);
    await expect(page.getByRole('heading', { level: 2, name: 'Available Data' })).toBeInViewport();
    await expect(page.getByRole('heading', { level: 1, name: model })).not.toBeInViewport();
    await page.evaluate(() => window.pageYOffset !== 0);
  });

  test('does not scroll to panel content on initial load when tab not specified in url', async ({
    page,
  }) => {
    const model = '3xTg-AD';
    await page.goto(`/models/${model}`);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeInViewport();
    await expect(
      page.getByRole('heading', { level: 2, name: 'Available Data' }),
    ).not.toBeInViewport();
    await page.evaluate(() => window.pageYOffset === 0);
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
    const tocLinks = toc.getByRole('button').filter({ hasNotText: /download all/i });
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

  test('appropriate section is shown when url includes an evidence type fragment', async ({
    page,
  }) => {
    const model = 'Abca7*V1599M';
    const fragment = 'soluble-a-beta-40';
    const section = 'Soluble Aβ40';
    await page.goto(`/models/${model}/biomarkers#${fragment}`);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();
    await expect(
      page.getByRole('heading', { level: 2, name: section, exact: true }),
    ).toBeInViewport();
  });

  test('clicking on a section adds fragment to url', async ({ page }) => {
    const model = 'Abca7*V1599M';
    const basePath = `/models/${model}/biomarkers`;
    const section = 'NfL';
    const fragment = 'nfl';

    await page.goto(basePath);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();

    const nflButton = page.getByRole('button', { name: section, exact: true });
    await nflButton.click();

    await expect(page.getByRole('heading', { level: 2, name: section })).toBeInViewport();
    await page.waitForURL(`${basePath}#${fragment}`);
  });

  test('clicking on a section updates fragment in url', async ({ page }) => {
    const model = 'Abca7*V1599M';
    const basePath = `/models/${model}/biomarkers`;
    const section = 'NfL';
    const fragment = 'nfl';

    await page.goto(`${basePath}#soluble-a-beta-40`);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();

    const nflButton = page.getByRole('button', { name: section, exact: true });
    await nflButton.click();

    await expect(page.getByRole('heading', { level: 2, name: section })).toBeInViewport();
    await page.waitForURL(`${basePath}#${fragment}`);
  });
});

test.describe('model details - boxplots selector - share links - initial load', () => {
  const basePath = '/models/Abca7*V1599M/biomarkers';
  const tissueDefault = 'Cerebral Cortex';
  const sexDefault = 'Female & Male';

  test('default filters are set when there are no query parameters', async ({ page }) => {
    await page.goto(basePath);
    await expect(page.getByRole('combobox', { name: tissueDefault })).toBeVisible();
    await expect(page.getByRole('combobox', { name: sexDefault })).toBeVisible();
  });

  test('sex filter can be set from query parameter', async ({ page }) => {
    const sexFilter = 'Male';
    await page.goto(`${basePath}?sex=${sexFilter}`);
    await expect(page.getByRole('combobox', { name: tissueDefault })).toBeVisible();
    await expect(page.getByRole('combobox', { name: sexFilter })).toBeVisible();
  });

  test('tissue filter can be set from query parameter', async ({ page }) => {
    const tissueFilter = 'Hippocampus';
    await page.goto(`${basePath}?tissue=${tissueFilter}`);
    await expect(page.getByRole('combobox', { name: tissueFilter })).toBeVisible();
    await expect(page.getByRole('combobox', { name: sexDefault })).toBeVisible();
  });

  test('sex and tissue filters can be set from query parameters', async ({ page }) => {
    const tissueFilter = 'Hippocampus';
    const sexFilter = 'Male';
    await page.goto(`${basePath}?tissue=${tissueFilter}&sex=${sexFilter}`);
    await expect(page.getByRole('combobox', { name: tissueFilter })).toBeVisible();
    await expect(page.getByRole('combobox', { name: sexFilter })).toBeVisible();
  });

  test('filters are set from query parameters and page scrolls to appropriate section based on fragment', async ({
    page,
  }) => {
    const tissueFilter = 'Hippocampus';
    const sexFilter = 'Male';
    await page.goto(`${basePath}?tissue=${tissueFilter}&sex=${sexFilter}#soluble-a-beta-42`);
    await expect(page.getByRole('combobox', { name: tissueFilter })).toBeVisible();
    await expect(page.getByRole('combobox', { name: sexFilter })).toBeVisible();
    await expect(
      page.getByRole('heading', { level: 2, name: 'Soluble Aβ42', exact: true }),
    ).toBeInViewport();
  });

  test('falls back to default filters when query parameters are invalid', async ({ page }) => {
    const invalidTissue = 'InvalidTissue';
    const invalidSex = 'InvalidSex';
    const fragment = '#soluble-a-beta-42';
    await page.goto(`${basePath}?tissue=${invalidTissue}&sex=${invalidSex}${fragment}`);
    await expect(page.getByRole('combobox', { name: tissueDefault })).toBeVisible();
    await expect(page.getByRole('combobox', { name: sexDefault })).toBeVisible();
    await page.waitForURL(`${basePath}${fragment}`);
  });

  test('does not scroll and removes invalid fragment from url', async ({ page }) => {
    const invalidFragment = 'invalid-section';
    const queryParam = '?sex=Male';
    await page.goto(`${basePath}${queryParam}#${invalidFragment}`);
    await expect(page.getByRole('heading', { level: 1, name: 'Abca7*V1599M' })).toBeInViewport();
    await page.waitForURL(`${basePath}${queryParam}`);
  });
});

test.describe('model details - boxplots selector - share links - updates', () => {
  const basePath = '/models/Abca7*V1599M/biomarkers';
  const tissueDefault = 'Cerebral Cortex';

  test('query parameters are updated when filters change', async ({ page }) => {
    const tissueInitial = 'Hippocampus';
    const tissueChosen = 'Plasma';
    const sexInitial = 'Male';
    const sexChosen = 'Female';
    await page.goto(`${basePath}?tissue=${tissueInitial}&sex=${sexInitial}`);

    await page.getByRole('combobox', { name: tissueInitial }).click();
    await page.getByRole('option', { name: tissueChosen }).click();

    await page.waitForURL(`${basePath}?tissue=${tissueChosen}&sex=${sexInitial}`);

    await page.getByRole('combobox', { name: sexInitial }).click();
    await page.getByRole('option', { name: sexChosen, exact: true }).click();

    await page.waitForURL(`${basePath}?tissue=${tissueChosen}&sex=${sexChosen}`);
  });

  test('query parameters are maintained when fragment is updated', async ({ page }) => {
    const pathWithParams = `${basePath}?tissue=Hippocampus&sex=Male`;
    await page.goto(pathWithParams);
    await page.getByRole('button', { name: 'Soluble Aβ42', exact: true }).click();
    await page.waitForURL(`${pathWithParams}#soluble-a-beta-42`);
  });

  test('preserves fragment that remains valid when query parameters are updated', async ({
    page,
  }) => {
    const tissueChosen = 'Hippocampus';
    const fragment = '#soluble-a-beta-42';
    await page.goto(`${basePath}${fragment}`);
    await page.getByRole('combobox', { name: tissueDefault }).click();
    await page.getByRole('option', { name: tissueChosen }).click();
    await page.waitForURL(`${basePath}?tissue=${tissueChosen}${fragment}`);
  });

  test('removes fragment that becomes invalid when query parameters are updated', async ({
    page,
  }) => {
    const tissueChosen = 'Hippocampus';
    const fragment = '#nfl';
    await page.goto(`${basePath}${fragment}`);
    await page.getByRole('combobox', { name: tissueDefault }).click();
    await page.getByRole('option', { name: tissueChosen }).click();
    await page.waitForURL(`${basePath}?tissue=${tissueChosen}`);
  });
});

test.describe('model details - boxplots selector - share links - link button', () => {
  const basePath = '/models/Abca7*V1599M/biomarkers';
  test('clicking share link button updates fragment and copies link to clipboard', async ({
    page,
    context,
  }) => {
    await context.grantPermissions(['clipboard-read']);

    await page.goto(`${basePath}#soluble-a-beta-42`);

    // hover on heading, so share link appears
    const heading = page.getByRole('heading', { level: 2, name: 'Nfl' });
    await heading.hover();

    const button = page.getByRole('button', { name: 'Copy link to Nfl' });
    await button.click();

    await page.waitForURL(`${basePath}#nfl`);

    const clipboardContent = await page.evaluate(() => navigator.clipboard.readText());
    expect(clipboardContent).toEqual(page.url());
  });
});
