import test, { expect, Page } from '@playwright/test';
import { getGeneRowButtons } from './gct';
import { convertToQueryParam } from './utils';

export const closePinnedGeneWarningModal = async (page: Page) => {
  await test.step('click through warning modal', async () => {
    const dialog = page.getByRole('dialog').filter({
      hasText: /you will lose some of your pins if you proceed/i,
    });
    const proceedBtn = dialog.getByRole('button', { name: 'Proceed' });
    await proceedBtn.click();
    await expect(dialog).toBeHidden();
  });
};

export const formatPinnedGenesQueryParam = (pinnedGenes: string[]) => {
  return convertToQueryParam(pinnedGenes, 'pinned');
};

export const getGenesTableCount = async (page: Page) => {
  const paginator = (await page.getByText('1-10').textContent()) ?? '';
  return Number(paginator.split(' ')[2]);
};

export const expectDisplayedGenesCountText = async (page: Page, nGenes: number) => {
  await expect(page.getByTestId('gene-count')).toHaveText(nGenes.toString());
};

export const expectPinnedGenesCountText = async (page: Page, nGenes: number) => {
  await expect(page.getByText(`Pinned Genes (${nGenes}/50)`)).toBeVisible();
};

export const expectPinnedProteinsCountText = async (page: Page, nProteins: number) => {
  await expect(
    page.getByText(`${nProteins} Protein${nProteins > 1 ? 's' : ''}`, { exact: true }),
  ).toBeVisible();
};

export const expectTooManyPinnedGenesToast = async (page: Page) => {
  const alert = page.getByRole('alert');
  await expect(alert).toHaveText(
    'Only 50 rows were pinned, because you reached the maximum of 50 pinned genes.',
  );
};

export const getPinnedItemsFromUrl = (url: string) => {
  const queryString = url.split('?');
  if (queryString.length !== 2) return null;

  const urlParams = new URLSearchParams(queryString[1]);
  const genesString = urlParams.get('pinned');
  return genesString?.split(/%2C|,/);
};

export const confirmPinnedItemsCount = async (page: Page, nPinned: number) => {
  await test.step(`confirm pinned items count is ${nPinned}`, async () => {
    const rows = page.locator('tr.pinned');
    const rowsCount = await rows.count();
    expect(rowsCount).toBe(nPinned);
  });
};

export const confirmPinnedItemsByGeneName = async (
  page: Page,
  geneName: string,
  nItems: number,
) => {
  await test.step(`confirm ${nItems} pinned items for ${geneName}`, async () => {
    const rows = page.getByRole('row').filter({ hasText: geneName });
    const nRows = await rows.count();
    expect(nRows).toEqual(nItems);

    for (let i = 0; i < nRows; ++i) {
      await expect(rows.nth(i)).toHaveClass(/pinned/i);
    }
  });
};

export const confirmPinnedItemsByItemName = async (page: Page, itemNames: string[]) => {
  for (const itemName of itemNames) {
    await confirmPinnedItemsByGeneName(page, itemName, 1);
  }
};

export const confirmPinnedProteins = async (page: Page, geneName: string, proteins: string[]) => {
  const geneProteins = proteins.map((protein) => `${geneName} (${protein})`);
  await confirmPinnedItemsByItemName(page, geneProteins);
};

// assumes that geneName will resolve to a single search result
export const pinGeneViaSearch = async (page: Page, geneName: string) => {
  await test.step(`search for a gene named ${geneName}`, async () => {
    const search = page.getByPlaceholder('Search', { exact: true });
    await search.fill(geneName);
  });

  await test.step('pin the gene', async () => {
    const btns = await getGeneRowButtons(page, geneName);
    await btns.pin.click();
  });
};

// assumes that each geneName will resolve to a single search result
export const pinMultipleGenesViaSearch = async (page: Page, geneNames: string[]) => {
  await test.step('pin genes', async () => {
    let pinnedCount = 0;
    for (const gene of geneNames) {
      await pinGeneViaSearch(page, gene);
      pinnedCount++;
      await expectPinnedGenesCountText(page, pinnedCount);
      await confirmPinnedItemsByGeneName(page, gene, 1);
    }
  });
};

export const pinAllItemsViaSearchByGene = async (page: Page, geneName: string) => {
  await test.step(`search for items named ${geneName}`, async () => {
    const search = page.getByPlaceholder('Search', { exact: true });
    await search.fill(geneName);
  });

  await test.step('pin the gene', async () => {
    const pinAllBtn = page.getByRole('button', { name: 'Pin All' });
    await pinAllBtn.click();
  });
};
