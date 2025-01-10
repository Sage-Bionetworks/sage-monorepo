import { expect, test } from '@playwright/test';

test('can search for gene', async ({ page }) => {
  const geneName = 'PTEN';
  const geneHgnc = 'ENSG00000171862';

  await page.goto('/');

  expect(await page.locator('h1').innerText()).toContain("Discover Alzheimer's Disease Genes");

  // open hamburger menu
  await page.locator('#header').getByRole('button').click();

  const responsePromise = page.waitForResponse(`**/genes/search?id=${geneName}`);
  const input = page.getByPlaceholder('Search genes');
  await input.pressSequentially(geneName);
  await responsePromise;

  const searchList = page.getByRole('list').filter({ hasText: geneName });
  const searchListItems = searchList.getByRole('listitem');
  await expect(searchListItems).toHaveCount(3);

  const searchListItem = searchListItems.first();
  await expect(searchListItem).toHaveText(geneName);

  await searchListItem.click();

  await page.waitForURL(`/genes/${geneHgnc}`);
  await expect(page.getByRole('heading', { level: 1 })).toHaveText(geneName);
});
