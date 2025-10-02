import { expect, test } from '@playwright/test';

test('can search for gene', async ({ page }) => {
  const geneHgnc = 'PTEN';
  const geneEnsembl = 'ENSG00000171862';

  await page.goto('/');

  expect(await page.locator('h1').innerText()).toContain("Discover Alzheimer's Disease Genes");

  const responsePromise = page.waitForResponse(`**/genes/search/enhanced?q=${geneHgnc}`);
  const input = page.getByPlaceholder('Search genes');
  await input.pressSequentially(geneHgnc);
  await responsePromise;

  const searchList = page.getByRole('list').filter({ hasText: geneHgnc });
  const searchListItems = searchList.getByRole('listitem');
  await expect(searchListItems).toHaveCount(3);

  const searchListItem = searchListItems.first();
  await expect(searchListItem).toHaveText(geneHgnc);

  await searchListItem.click();

  await page.waitForURL(`/genes/${geneEnsembl}`);
  await expect(page.getByRole('heading', { level: 1 })).toHaveText(geneHgnc);
});
