import { expect, Page, test } from '@playwright/test';
import { waitForScrollToStop } from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../playwright.config';
import { GCT_RNA_SUBCATEGORIES } from './helpers/constants';
import { waitForSpinnerNotVisible } from './helpers/utils';

const geneWithAllTabs = { name: 'PLEC', id: 'ENSG00000178209' };
const geneWithoutDataDependentTabs = { name: 'A2M', id: 'ENSG00000175899' };

function getTab(page: Page, label: string) {
  return page.locator('.gene-details-nav').getByText(label, { exact: true });
}

// Retries the click because a click on server-rendered tabs before hydration finishes is dropped
async function clickTab(page: Page, label: string, expectedPathname: string) {
  await expect(async () => {
    await getTab(page, label).click();
    expect(new URL(page.url()).pathname).toBe(expectedPathname);
  }).toPass();
}

async function goToGene(page: Page, path: string) {
  await page.goto(path);
  await waitForSpinnerNotVisible(page);
}

function getModelQueryParam(page: Page) {
  return new URL(page.url()).searchParams.get('model');
}

test.describe('gene details', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('invalid gene results in a 404 redirect', async ({ page }) => {
    // go to invalid ENSG page
    await page.goto('/genes/ENSG00000000000');
    await waitForSpinnerNotVisible(page);

    // expect a title "to contain" a substring.
    await expect(page).toHaveTitle('Agora | Page Not Found');

    // expect div for page not found content to be visible
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(
      ` This page isn't available right now. `,
    );
  });

  test('consistency of change section heading is visible when using anchor link', async ({
    page,
  }) => {
    await page.goto('/genes/ENSG00000178209/evidence/rna#consistency-of-change');

    await waitForSpinnerNotVisible(page);
    await waitForScrollToStop(page);

    const header = page.getByRole('heading', { name: 'Consistency of Change in Expression' });
    await expect(header).toBeInViewport();
  });

  test('can navigate to new gene via search bar', async ({ page }) => {
    const gene1 = { name: 'PLEC', id: 'ENSG00000178209' };
    const gene2 = { name: 'PTEN', id: 'ENSG00000171862' };

    await page.goto(`/genes/${gene1.id}`);
    await waitForSpinnerNotVisible(page);

    await page.waitForURL(`/genes/${gene1.id}`);
    await expect(page.getByRole('heading', { name: gene1.name, exact: true })).toBeVisible();

    const responsePromise = page.waitForResponse(`**/genes/search/enhanced?q=${gene2.id}`);
    const searchInput = page.getByRole('textbox', { name: 'Search genes' });
    await expect(searchInput).toBeEnabled();
    await searchInput.pressSequentially(gene2.id);
    await expect(searchInput).toHaveValue(gene2.id);
    await responsePromise;

    const searchList = page.getByRole('list').filter({ hasText: gene2.name });
    const searchListItem = searchList.getByRole('listitem');
    await expect(searchListItem).toHaveCount(1, { timeout: 10_000 });
    await searchListItem.click();

    await expect(page).toHaveURL(`${baseURL}/genes/${gene2.id}`);
    await waitForSpinnerNotVisible(page);

    await expect(page.getByRole('heading', { name: gene2.name, exact: true })).toBeVisible();
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

test.describe('gene details - tabs', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  const genePath = `/genes/${geneWithAllTabs.id}`;

  test('default tab is summary', async ({ page }) => {
    await goToGene(page, genePath);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Summary of Evidence' }),
    ).toBeVisible();
  });

  test('top-level tab is loaded from url', async ({ page }) => {
    await goToGene(page, `${genePath}/resources`);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Drug Development Resources' }),
    ).toBeVisible();
  });

  test('subtab is loaded from url', async ({ page }) => {
    await goToGene(page, `${genePath}/evidence/protein`);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Proteomics', exact: true }),
    ).toBeVisible();
  });

  test('parent tab in url opens its first subtab', async ({ page }) => {
    await goToGene(page, `${genePath}/evidence`);
    await expect(page.getByRole('heading', { level: 2, name: 'RNA Expression' })).toBeVisible();
  });

  test('clicking tabs and subtabs updates url and displays correct content', async ({ page }) => {
    await goToGene(page, genePath);

    await clickTab(page, 'Resources', `${genePath}/resources`);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Drug Development Resources' }),
    ).toBeVisible();

    await clickTab(page, 'Evidence', `${genePath}/evidence/rna`);
    await expect(page.getByRole('heading', { level: 2, name: 'RNA Expression' })).toBeVisible();

    await clickTab(page, 'Metabolomics', `${genePath}/evidence/metabolomics`);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Metabolomics', exact: true }),
    ).toBeVisible();

    await clickTab(page, 'Summary', `${genePath}/summary`);
    await expect(
      page.getByRole('heading', { level: 2, name: 'Summary of Evidence' }),
    ).toBeVisible();
  });

  test('model query parameter is kept when switching tabs', async ({ page }) => {
    const model = GCT_RNA_SUBCATEGORIES.AD_SEX_F;
    // Agora's CustomUrlSerializer strips parentheses from the whole URL, query string included
    // TODO(AG-2217): expect the model with parentheses once the serializer leaves query values alone
    const modelInUrl = 'AD Diagnosis x Sex females only';
    await goToGene(page, `${genePath}/evidence/rna?model=${model}`);
    await expect(page.getByRole('combobox', { name: model })).toBeVisible();
    await expect.poll(() => getModelQueryParam(page)).toBe(modelInUrl);

    await clickTab(page, 'Protein', `${genePath}/evidence/protein`);
    expect(getModelQueryParam(page)).toBe(modelInUrl);

    await clickTab(page, 'RNA', `${genePath}/evidence/rna`);
    expect(getModelQueryParam(page)).toBe(modelInUrl);
    await expect(page.getByRole('combobox', { name: model })).toBeVisible();
  });

  test('data-dependent tabs are shown for a gene with nominations and experimental validation', async ({
    page,
  }) => {
    await goToGene(page, genePath);
    await expect(getTab(page, 'Nomination Details')).toBeVisible();
    await expect(getTab(page, 'Experimental Validation')).toBeVisible();

    await clickTab(page, 'Nomination Details', `${genePath}/nominations`);
    await expect(
      page.getByRole('heading', {
        level: 2,
        name: `Evidence Supporting the Nomination of ${geneWithAllTabs.name}`,
      }),
    ).toBeVisible();

    await clickTab(page, 'Experimental Validation', `${genePath}/experimental-validation`);
    await expect(
      page.getByRole('heading', {
        level: 2,
        name: `Experimental Validation of ${geneWithAllTabs.name}`,
      }),
    ).toBeVisible();
  });

  test('data-dependent tabs are hidden for a gene without nominations or experimental validation', async ({
    page,
  }) => {
    await goToGene(page, `/genes/${geneWithoutDataDependentTabs.id}`);
    await expect(
      page.getByRole('heading', { level: 1, name: geneWithoutDataDependentTabs.name }),
    ).toBeVisible();
    await expect(getTab(page, 'Resources')).toBeVisible();
    await expect(getTab(page, 'Nomination Details')).toHaveCount(0);
    await expect(getTab(page, 'Experimental Validation')).toHaveCount(0);
  });
});
