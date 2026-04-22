import { expect, test } from '@playwright/test';

test.describe('drug details', () => {
  const drugPath = '/drugs/CHEMBL2105759';

  test('valid drug displays drug name', async ({ page }) => {
    await page.goto(drugPath);
    await expect(page.getByRole('heading', { level: 1, name: 'Baricitinib' })).toBeVisible();
  });

  test('default tab is resources', async ({ page }) => {
    await page.goto(drugPath);
    await expect(page.getByRole('heading', { level: 2, name: 'Related Resources' })).toBeVisible();
  });

  test('correct tab is loaded from url', async ({ page }) => {
    await page.goto(`${drugPath}/resources`);
    await expect(page.getByRole('heading', { level: 2, name: 'Related Resources' })).toBeVisible();
  });

  test('invalid tab in url defaults to first available tab', async ({ page }) => {
    await page.goto(`${drugPath}/does-not-exist`);
    await page.waitForURL(drugPath);
    await expect(page.getByRole('heading', { level: 2, name: 'Related Resources' })).toBeVisible();
  });
});

test.describe('drug details - resources', () => {
  const resourcesUrl = '/drugs/CHEMBL2105759/resources';

  test('Open Targets card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const card = page.getByRole('link', {
      name: /open targets|TODO/i,
    });
    await expect(card).toHaveAttribute(
      'href',
      'https://platform.opentargets.org/drug/CHEMBL2105759',
    );
  });

  test('Pharos card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const cards = page.getByRole('link');
    const pharosCard = cards.filter({
      has: page.locator('[href*="pharos.nih.gov"]'),
    });
    await expect(pharosCard).toHaveAttribute('href', 'https://pharos.nih.gov/ligands/Baricitinib');
  });

  test('AlzGPS card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const cards = page.getByRole('link');
    const alzgpsCard = cards.filter({
      has: page.locator('[href*="alzgps.lerner.ccf.org"]'),
    });
    await expect(alzgpsCard).toHaveAttribute(
      'href',
      'https://alzgps.lerner.ccf.org/e/?action=true&type=DRUG&key=DB11817',
    );
  });

  test('TACA card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    const cards = page.getByRole('link');
    const tacaCard = cards.filter({
      has: page.locator('[href*="taca.lerner.ccf.org"]'),
    });
    await expect(tacaCard).toHaveAttribute(
      'href',
      'https://taca.lerner.ccf.org/e/?action=true&type=DRUG&key=DB11817',
    );
  });
});
