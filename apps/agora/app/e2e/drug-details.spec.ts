import { expect, test } from '@playwright/test';

const chemblId = 'CHEMBL2105759';
const drugBankId = 'DB11817';
const drugName = 'Baricitinib';

test.describe('drug details', () => {
  const drugPath = `/drugs/${chemblId}`;

  test('valid drug displays drug name', async ({ page }) => {
    await page.goto(drugPath);
    await expect(page.getByRole('heading', { level: 1, name: drugName })).toBeVisible();
  });

  test('default tab is summary', async ({ page }) => {
    await page.goto(drugPath);
    await expect(page.getByText('Modality')).toBeVisible();
  });

  test('correct tab is loaded from url', async ({ page }) => {
    await page.goto(`${drugPath}/resources`);
    await expect(page.getByRole('heading', { level: 2, name: 'Related Resources' })).toBeVisible();
  });

  test('invalid tab in url defaults to first available tab', async ({ page }) => {
    await page.goto(`${drugPath}/does-not-exist`);
    await page.waitForURL(drugPath);
    await expect(page.getByText('Modality')).toBeVisible();
  });
});

test.describe('drug details - summary', () => {
  const summaryUrl = `/drugs/${chemblId}`;

  test('displays modality', async ({ page }) => {
    await page.goto(summaryUrl);
    await expect(page.getByText('Modality')).toBeVisible();
    await expect(page.getByText('Small molecule', { exact: true })).toBeVisible();
  });

  test('displays max clinical trial phase', async ({ page }) => {
    await page.goto(summaryUrl);
    await expect(page.getByText('Max Clinical Trial Phase')).toBeVisible();
    await expect(page.getByText('Preclinical', { exact: true })).toBeVisible();
  });

  test('displays year of first approval', async ({ page }) => {
    await page.goto(summaryUrl);
    await expect(page.getByText('Year of first approval')).toBeVisible();
    await expect(page.getByText('2017', { exact: true })).toBeVisible();
  });

  test('displays mechanisms of action', async ({ page }) => {
    await page.goto(summaryUrl);
    await expect(page.getByText('Mechanisms of Action')).toBeVisible();
    await expect(
      page.getByText('Tyrosine-protein kinase JAK1 inhibitor', { exact: true }),
    ).toBeVisible();
    await expect(
      page.getByText('Tyrosine-protein kinase JAK2 inhibitor', { exact: true }),
    ).toBeVisible();
  });

  test('displays linked targets with links', async ({ page }) => {
    await page.goto(summaryUrl);
    await expect(page.getByText('Linked Targets')).toBeVisible();
    const jak1Link = page.getByRole('link', { name: 'JAK1' });
    await expect(jak1Link).toBeVisible();
    await expect(jak1Link).toHaveAttribute('href', '/genes/ENSG00000162434');
    const jak2Link = page.getByRole('link', { name: 'JAK2' });
    await expect(jak2Link).toBeVisible();
    await expect(jak2Link).toHaveAttribute('href', '/genes/ENSG00000096968');
  });

  test('linked target navigates to gene details page', async ({ page }) => {
    await page.goto(summaryUrl);
    await page.getByRole('link', { name: 'JAK1' }).click();
    await expect(page).toHaveURL(/\/genes\/ENSG00000162434/);
    await expect(page.getByRole('heading', { level: 1, name: 'JAK1' })).toBeVisible();
  });
});

test.describe('drug details - resources', () => {
  const resourcesUrl = `/drugs/${chemblId}/resources`;

  test('AlzGPS card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /alzgps/i,
    });
    await expect(card).toHaveAttribute(
      'href',
      `https://alzgps.lerner.ccf.org/e/?action=true&type=DRUG&key=${drugBankId}`,
    );
  });

  test('AlzPED card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /ad therapeutics/i,
    });
    await expect(card).toHaveAttribute('href', 'https://alzped.nia.nih.gov/');
  });

  test('FDA card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /fda/i,
    });
    await expect(card).toHaveAttribute('href', 'https://www.fda.gov/');
  });

  test('Open Targets card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /open targets/i,
    });
    await expect(card).toHaveAttribute('href', `https://platform.opentargets.org/drug/${chemblId}`);
  });

  test('PubMed card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /pubmed/i,
    });
    await expect(card).toHaveAttribute('href', `https://pubmed.ncbi.nlm.nih.gov/?term=${drugName}`);
  });

  test('Pharos card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /druggable genome/i,
    });
    await expect(card).toHaveAttribute('href', `https://pharos.nih.gov/ligands/${drugName}`);
  });

  test('TACA card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /taca/i,
    });
    await expect(card).toHaveAttribute(
      'href',
      `https://taca.lerner.ccf.org/e/?action=true&type=DRUG&key=${drugBankId}`,
    );
  });
});
