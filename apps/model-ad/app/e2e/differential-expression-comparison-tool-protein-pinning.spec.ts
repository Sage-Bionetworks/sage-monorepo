import { expect, Page, test } from '@playwright/test';
import { MAX_PIN_LIMIT } from '@sagebionetworks/explorers/constants';
import {
  closeTutorialDialog,
  expectCategoriesParams,
  expectPinnedParams,
  expectPinnedResultsCount,
  expectPinnedRows,
  expectToastDetail,
  expectUnpinnedTableOnly,
  expectViewNounLabels,
  getPinAllButton,
  getPinnedTable,
  getPinToggleButtonByName,
  getQueryParamsFromRecords,
  getUnpinnedTable,
  goToLastPage,
  pinAll,
  pinByName,
  searchViaFilterbox,
  selectCategoryOption,
  testPinsRemovedFromUrlOnClearAllPins,
  unPinByName,
  waitForTableLoadingComplete,
} from '@sagebionetworks/explorers/testing/e2e';
import {
  DIFFERENTIAL_EXPRESSION_CT_PAGE as CT_PAGE,
  DIFFERENTIAL_EXPRESSION_DROPDOWN_INDEX as DROPDOWN_INDEX,
  DIFFERENTIAL_EXPRESSION_PROTEIN_CATEGORY as PROTEIN_MAIN_CATEGORY,
  DIFFERENTIAL_EXPRESSION_PROTEIN_HEMIBRAIN_CATEGORIES as proteinCategories,
  DIFFERENTIAL_EXPRESSION_RNA_CATEGORY as RNA_MAIN_CATEGORY,
  DIFFERENTIAL_EXPRESSION_RNA_HEMIBRAIN_CATEGORIES as rnaHemibrainCategories,
} from './constants';
import {
  fetchProteinRows,
  fetchProteomics,
  fetchRnaRows,
  getEnsemblGeneId,
  getProteinIds,
  getRowIds,
  navigateToComparison,
} from './helpers/comparison-tool';

// Protein rows are children of the RNA row keyed by their rna_composite_id, so each gene below is
// an RNA composite_id.
const fourProteinGene = 'ENSMUSG00000019961~LOAD2~Male'; // Tmpo
const fiveProteinMaleGene = 'ENSMUSG00000032826~LOAD2~Male'; // Ank2
const fiveProteinFemaleGene = 'ENSMUSG00000032826~LOAD2~Female'; // Ank2
const proteinOnlyGene = 'ENSMUSG00000005681~LOAD2~Female'; // Apoa2
const rnaOnlyGene = 'ENSMUSG00000086714~LOAD2~Male'; // has RNA rows but no Protein rows
const PINNED_RESULTS_HEADING = 'Pinned Results';
const PROTEIN_PIN_LIMIT_TOOLTIP = `You have already pinned the maximum number of results (proteins for ${MAX_PIN_LIMIT} genes). You must unpin all proteins for a gene before you can pin proteins for a new gene.`;

// pinnedProteins - the count and verb the toast opens with, e.g. '1 protein was' or '9 proteins were'
const getProteinPinLimitToast = (pinnedProteins: string) =>
  `Only ${pinnedProteins} pinned, because you reached the maximum of ${MAX_PIN_LIMIT} pinned genes. Some proteins were skipped because they belong to a gene not already in your pinned list.`;

// Every Protein row of the first geneCount genes in the table's default sort order. The tests
// assume fiveProteinMaleGene and fiveProteinFemaleGene are outside this set, so this checks neither
// is in it.
const fetchLeadingGeneRows = async (page: Page, geneCount: number) => {
  const rows = await fetchProteomics(
    page,
    proteinCategories,
    {},
    {
      remainingBudget: geneCount,
    },
  );
  const genes = [...new Set(rows.map((row) => row.rna_composite_id))];
  expect(genes).toHaveLength(geneCount);
  expect(genes).not.toContain(fiveProteinMaleGene);
  expect(genes).not.toContain(fiveProteinFemaleGene);
  return { rowIds: getRowIds(rows), genes };
};

const fetchFiveProteinGeneRows = async (page: Page) => {
  const rows = await fetchProteinRows(page, [fiveProteinMaleGene, fiveProteinFemaleGene]);
  const maleProteins = rows.filter((row) => row.rna_composite_id === fiveProteinMaleGene);
  expect(maleProteins).toHaveLength(5);
  const femaleProteinIds = getProteinIds(rows, fiveProteinFemaleGene);
  expect(femaleProteinIds.length).toBeGreaterThan(0);
  return { maleProteins, femaleProteinIds };
};

const navigateByUrlWithPins = async (page: Page, categories: string[], pinnedItems: string[]) => {
  const queryParameters = getQueryParamsFromRecords({ categories, pinned: pinnedItems });
  await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
};

const switchToProteinView = async (page: Page) => {
  await selectCategoryOption(page, DROPDOWN_INDEX.MAIN_CATEGORY, PROTEIN_MAIN_CATEGORY);
  await expectCategoriesParams(page, proteinCategories);
};

const switchToRnaView = async (page: Page) => {
  await selectCategoryOption(page, DROPDOWN_INDEX.MAIN_CATEGORY, RNA_MAIN_CATEGORY);
  await expectCategoriesParams(page, rnaHemibrainCategories);
};

const getOverwritePinsDialog = (page: Page) =>
  page.getByRole('dialog', { name: 'Overwrite hidden pins?' });

// Pins Tmpo's and Apoa2's proteins in Protein, then switches to RNA, where Apoa2 has no gene row,
// so its proteins are hidden
const hideProteinOnlyGeneInRna = async (page: Page) => {
  const proteinRows = await fetchProteinRows(page, [fourProteinGene, proteinOnlyGene]);
  const hiddenProteinIds = getProteinIds(proteinRows, proteinOnlyGene);
  expect(hiddenProteinIds.length).toBeGreaterThan(0);
  expect(await fetchRnaRows(page, [proteinOnlyGene])).toHaveLength(0);
  const proteinIds = getRowIds(proteinRows);

  const queryParameters = getQueryParamsFromRecords({
    categories: proteinCategories,
    pinned: proteinIds,
  });
  await navigateToComparison(page, CT_PAGE, true, 'url', queryParameters);
  await expectPinnedParams(page, proteinIds);
  await switchToRnaView(page);
  await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene 1 hidden']);

  return { proteinIds, hiddenProteinIds };
};

const pinFiveProteinGeneInRna = async (page: Page) => {
  await searchViaFilterbox(page, getEnsemblGeneId(fiveProteinMaleGene));
  await pinByName(getUnpinnedTable(page), page, fiveProteinMaleGene);
};

test.describe('differential expression protein pinning', () => {
  test('RNA pins fan out to every protein of their genes in the Protein view', async ({ page }) => {
    const pinnedGenes = [fourProteinGene, fiveProteinMaleGene];
    const proteinRows = await fetchProteinRows(page, pinnedGenes);
    expect(getProteinIds(proteinRows, fourProteinGene)).toHaveLength(4);
    expect(getProteinIds(proteinRows, fiveProteinMaleGene)).toHaveLength(5);
    const proteinIds = getRowIds(proteinRows);

    await navigateByUrlWithPins(page, rnaHemibrainCategories, pinnedGenes);
    await expectPinnedRows(page, pinnedGenes);
    await expectPinnedResultsCount(page, pinnedGenes.length, [PINNED_RESULTS_HEADING, '2 Genes']);

    await switchToProteinView(page);
    await expectPinnedParams(page, proteinIds);
    await expectPinnedResultsCount(page, proteinIds.length, [
      PINNED_RESULTS_HEADING,
      '2 Genes',
      '9 Proteins',
    ]);
    await expectPinnedRows(page, proteinIds);
  });

  test('an RNA pin with no Protein rows is noted as a hidden gene in the Protein view', async ({
    page,
  }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);
    expect(await fetchRnaRows(page, [rnaOnlyGene])).toHaveLength(1);
    expect(await fetchProteinRows(page, [rnaOnlyGene])).toHaveLength(0);
    const pinnedGenes = [fourProteinGene, rnaOnlyGene];

    await navigateByUrlWithPins(page, rnaHemibrainCategories, pinnedGenes);
    await expectPinnedRows(page, pinnedGenes);

    await switchToProteinView(page);
    await expectPinnedResultsCount(page, proteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene 1 hidden',
      '4 Proteins',
    ]);
    await expectPinnedRows(page, proteinIds);
  });

  test('Protein pins collapse to their gene in the RNA view', async ({ page }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);

    await navigateByUrlWithPins(page, proteinCategories, proteinIds);
    await expectPinnedParams(page, proteinIds);
    await expectPinnedResultsCount(page, proteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene',
      '4 Proteins',
    ]);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);
    await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene']);
    await expectPinnedRows(page, [fourProteinGene]);
  });

  test('pinned header and result labels use the gene and protein nouns in the Protein view', async ({
    page,
  }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);

    await navigateByUrlWithPins(page, proteinCategories, proteinIds);
    await expectPinnedResultsCount(page, proteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene',
      '4 Proteins',
    ]);
    await expectViewNounLabels(page, 'Proteins', getEnsemblGeneId(fiveProteinMaleGene));
  });

  test('a protein fanned out from an RNA pin can be unpinned in the Protein view', async ({
    page,
  }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);
    const [proteinIdToUnpin, ...remainingProteinIds] = proteinIds;

    await navigateByUrlWithPins(page, rnaHemibrainCategories, [fourProteinGene]);
    await expectPinnedRows(page, [fourProteinGene]);

    await switchToProteinView(page);
    await expectPinnedParams(page, proteinIds);
    await unPinByName(getPinnedTable(page), page, proteinIdToUnpin);
    await expectPinnedParams(page, remainingProteinIds);
    await expectPinnedResultsCount(page, remainingProteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene',
      '3 Proteins',
    ]);
  });

  test('a gene collapsed from Protein pins can be unpinned in the RNA view', async ({ page }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);

    await navigateByUrlWithPins(page, proteinCategories, proteinIds);
    await expectPinnedParams(page, proteinIds);

    await switchToRnaView(page);
    await expectPinnedRows(page, [fourProteinGene]);
    await unPinByName(getPinnedTable(page), page, fourProteinGene);
    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);
  });

  test('Protein pins return unchanged after a round trip through the RNA view', async ({
    page,
  }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);
    const pinnedProteinIds = proteinIds.slice(0, 2);

    await navigateByUrlWithPins(page, proteinCategories, pinnedProteinIds);
    await expectPinnedParams(page, pinnedProteinIds);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);

    await switchToProteinView(page);
    await expectPinnedParams(page, pinnedProteinIds);
    await expectPinnedResultsCount(page, pinnedProteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene',
      '2 Proteins',
    ]);
  });

  test('an RNA pin edit replaces the Protein pins with every protein of the genes', async ({
    page,
  }) => {
    const proteinRows = await fetchProteinRows(page, [fourProteinGene, fiveProteinMaleGene]);
    const fourProteinIds = getProteinIds(proteinRows, fourProteinGene);
    expect(fourProteinIds).toHaveLength(4);
    expect(getProteinIds(proteinRows, fiveProteinMaleGene)).toHaveLength(5);
    const pinnedProteinIds = fourProteinIds.slice(0, 2);

    await navigateByUrlWithPins(page, proteinCategories, pinnedProteinIds);
    await expectPinnedParams(page, pinnedProteinIds);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);
    await searchViaFilterbox(page, getEnsemblGeneId(fiveProteinMaleGene));
    await pinByName(getUnpinnedTable(page), page, fiveProteinMaleGene);
    await expectPinnedRows(page, [fourProteinGene, fiveProteinMaleGene]);

    await switchToProteinView(page);
    await expectPinnedParams(page, getRowIds(proteinRows));
    await expectPinnedResultsCount(page, proteinRows.length, [
      PINNED_RESULTS_HEADING,
      '2 Genes',
      '9 Proteins',
    ]);
  });

  test('Protein pins return unchanged after a tissue change in the RNA view', async ({ page }) => {
    const otherRnaTissue = 'Tissue - Hippocampus';
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);
    const pinnedProteinIds = proteinIds.slice(0, 2);

    await navigateByUrlWithPins(page, proteinCategories, pinnedProteinIds);
    await expectPinnedParams(page, pinnedProteinIds);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);
    await selectCategoryOption(page, DROPDOWN_INDEX.TISSUE, otherRnaTissue);
    await expectCategoriesParams(page, [RNA_MAIN_CATEGORY, otherRnaTissue]);
    await waitForTableLoadingComplete(page);

    await switchToProteinView(page);
    await expectPinnedParams(page, pinnedProteinIds);
    await expectPinnedResultsCount(page, pinnedProteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene',
      '2 Proteins',
    ]);
  });

  test('a Protein pin with no RNA gene row is hidden in RNA and returns in Protein', async ({
    page,
  }) => {
    const proteinRows = await fetchProteinRows(page, [fourProteinGene, proteinOnlyGene]);
    expect(getProteinIds(proteinRows, fourProteinGene)).toHaveLength(4);
    expect(getProteinIds(proteinRows, proteinOnlyGene).length).toBeGreaterThan(0);
    expect(await fetchRnaRows(page, [proteinOnlyGene])).toHaveLength(0);
    const proteinIds = getRowIds(proteinRows);

    await navigateByUrlWithPins(page, proteinCategories, proteinIds);
    await expectPinnedParams(page, proteinIds);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);
    await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene 1 hidden']);

    await switchToProteinView(page);
    await expectPinnedParams(page, proteinIds);
    await expectPinnedResultsCount(page, proteinIds.length, [
      PINNED_RESULTS_HEADING,
      '2 Genes',
      `${proteinIds.length} Proteins`,
    ]);
  });

  test('Protein pins with no RNA gene row say that no genes match in the RNA view', async ({
    page,
  }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [proteinOnlyGene]),
      proteinOnlyGene,
    );
    expect(proteinIds.length).toBeGreaterThan(0);

    await navigateByUrlWithPins(page, proteinCategories, proteinIds);
    await expectPinnedParams(page, proteinIds);
    await switchToRnaView(page);

    await expectPinnedParams(page, []);
    await expectPinnedResultsCount(page, 0, [PINNED_RESULTS_HEADING, '0 Genes 1 hidden']);
    await expect(page.getByText('No matching gene exists in the current view.')).toBeVisible();
  });

  test('RNA pins with no Protein rows say that no proteins match in the Protein view', async ({
    page,
  }) => {
    expect(await fetchRnaRows(page, [rnaOnlyGene])).toHaveLength(1);
    expect(await fetchProteinRows(page, [rnaOnlyGene])).toHaveLength(0);

    await navigateByUrlWithPins(page, rnaHemibrainCategories, [rnaOnlyGene]);
    await expectPinnedRows(page, [rnaOnlyGene]);
    await switchToProteinView(page);

    await expectPinnedParams(page, []);
    await expectPinnedResultsCount(page, 0, [
      PINNED_RESULTS_HEADING,
      '0 Genes 1 hidden',
      '0 Proteins',
    ]);
    // Plural even for one gene, since the number of proteins a hidden gene has here isn't known
    await expect(page.getByText('No matching proteins exist in the current view.')).toBeVisible();
  });

  test.describe('overwriting hidden pins', () => {
    test('a pin edit with hidden pins asks to confirm, and Cancel keeps the hidden pins', async ({
      page,
    }) => {
      const { proteinIds, hiddenProteinIds } = await hideProteinOnlyGeneInRna(page);

      await pinFiveProteinGeneInRna(page);

      const dialog = getOverwritePinsDialog(page);
      await expect(dialog).toBeVisible();
      await expect(dialog).toContainText(
        `You pinned ${hiddenProteinIds.length} ${hiddenProteinIds.length === 1 ? 'result' : 'results'} for 1 gene that can't be shown in this view.`,
      );
      await expect(dialog.locator('i')).toHaveText([
        'PROTEIN - DIFFERENTIAL EXPRESSION',
        'Tissue - Hemibrain',
      ]);

      await dialog.getByRole('button', { name: 'Cancel' }).click();

      await expect(dialog).toBeHidden();
      await expectPinnedParams(page, [fourProteinGene]);
      await switchToProteinView(page);
      await expectPinnedParams(page, proteinIds);
    });

    test('Overwrite Pins applies the pin edit and drops the hidden pins', async ({ page }) => {
      await hideProteinOnlyGeneInRna(page);
      const keptProteinRows = await fetchProteinRows(page, [fourProteinGene, fiveProteinMaleGene]);

      await pinFiveProteinGeneInRna(page);
      await getOverwritePinsDialog(page).getByRole('button', { name: 'Overwrite Pins' }).click();

      await expect(getOverwritePinsDialog(page)).toBeHidden();
      await expectPinnedRows(page, [fourProteinGene, fiveProteinMaleGene]);
      await switchToProteinView(page);
      await expectPinnedResultsCount(page, keptProteinRows.length, [
        PINNED_RESULTS_HEADING,
        '2 Genes',
        `${keptProteinRows.length} Proteins`,
      ]);
    });

    test('leaving the CT with the confirmation open cancels the pin edit', async ({ page }) => {
      const { proteinIds } = await hideProteinOnlyGeneInRna(page);
      // hideProteinOnlyGeneInRna loads the CT as its own document, so Back from it would reload
      // the app and lose the service. Leave and return within the app first, so Back and Forward
      // stay in the app
      await page.getByRole('link', { name: 'Home' }).click();
      await navigateToComparison(page, CT_PAGE, true, 'link');
      await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene 1 hidden']);

      await pinFiveProteinGeneInRna(page);
      const dialog = getOverwritePinsDialog(page);
      await expect(dialog).toBeVisible();

      await page.goBack();
      await expect.poll(() => new URL(page.url()).pathname).toBe('/');
      await page.goForward();
      await closeTutorialDialog(page);

      await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene 1 hidden']);
      await expect(dialog).toBeHidden();
      await expectPinnedParams(page, [fourProteinGene]);
      await switchToProteinView(page);
      await expectPinnedParams(page, proteinIds);
    });

    test("Don't ask me this again stops the confirmation for later pin edits", async ({ page }) => {
      await hideProteinOnlyGeneInRna(page);
      await pinFiveProteinGeneInRna(page);
      const dialog = getOverwritePinsDialog(page);
      await dialog.getByText("Don't ask me this again.").click();
      await dialog.getByRole('button', { name: 'Overwrite Pins' }).click();
      await expect(dialog).toBeHidden();

      // The choice is stored in the browser, so it holds after the pins are hidden again
      await hideProteinOnlyGeneInRna(page);
      await pinFiveProteinGeneInRna(page);

      await expect(dialog).toBeHidden();
      await expectPinnedRows(page, [fourProteinGene, fiveProteinMaleGene]);
    });
  });

  test('Clear All Pins in the Protein view also clears the RNA pins', async ({ page }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);

    await navigateByUrlWithPins(page, rnaHemibrainCategories, [fourProteinGene]);
    await expectPinnedRows(page, [fourProteinGene]);

    await switchToProteinView(page);
    await testPinsRemovedFromUrlOnClearAllPins(page, proteinIds);

    await switchToRnaView(page);
    // The search waits for the RNA rows, and shows the gene is now unpinned
    await searchViaFilterbox(page, getEnsemblGeneId(fourProteinGene));
    await expect(
      getPinToggleButtonByName(getUnpinnedTable(page), page, fourProteinGene, 'pin'),
    ).toBeEnabled();
    await expectUnpinnedTableOnly(page);
    await expectPinnedParams(page, []);
  });

  test('a gene id pinned in the Protein view URL pins nothing', async ({ page }) => {
    const proteinRows = await fetchProteinRows(page, [fourProteinGene, fiveProteinMaleGene]);
    expect(getProteinIds(proteinRows, fourProteinGene)).toHaveLength(4);
    // A protein pinned alongside the gene id shows when the pinned rows have arrived
    const [pinnedProteinId] = getProteinIds(proteinRows, fiveProteinMaleGene);

    await navigateByUrlWithPins(page, proteinCategories, [pinnedProteinId, fourProteinGene]);
    await expectPinnedParams(page, [pinnedProteinId]);
    await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene', '1 Protein']);
  });

  test('protein ids pinned in the RNA view URL pin nothing', async ({ page }) => {
    const proteinIds = getProteinIds(
      await fetchProteinRows(page, [fourProteinGene]),
      fourProteinGene,
    );
    expect(proteinIds).toHaveLength(4);

    // The gene pinned alongside the protein ids shows when the pinned rows have arrived
    await navigateByUrlWithPins(page, rnaHemibrainCategories, [fourProteinGene, ...proteinIds]);
    await expectPinnedParams(page, [fourProteinGene]);
    await expectPinnedResultsCount(page, 1, [PINNED_RESULTS_HEADING, '1 Gene']);
  });

  test('Protein Pin All pins every row of new genes up to the gene limit', async ({ page }) => {
    const searchTerm = 't';
    const { maleProteins } = await fetchFiveProteinGeneRows(page);
    const pinnedProteinIds = getRowIds(maleProteins);
    // The pinned five-protein gene takes one gene of the budget but five rows, so a row cap
    // would stop short of the rows of the remaining genes
    const newRows = await fetchProteomics(
      page,
      proteinCategories,
      {},
      {
        search: searchTerm,
        remainingBudget: MAX_PIN_LIMIT - 1,
      },
    );
    const newGenes = [...new Set(newRows.map((row) => row.rna_composite_id))];
    expect(newGenes).toHaveLength(MAX_PIN_LIMIT - 1);
    expect(newGenes).not.toContain(fiveProteinMaleGene);
    const newRowIds = getRowIds(newRows);

    await navigateByUrlWithPins(page, proteinCategories, pinnedProteinIds);
    await expectPinnedResultsCount(page, pinnedProteinIds.length, [
      PINNED_RESULTS_HEADING,
      '1 Gene',
      '5 Proteins',
    ]);
    await searchViaFilterbox(page, searchTerm);
    await pinAll(page);

    await expectToastDetail(page, getProteinPinLimitToast(`${newRowIds.length} proteins were`));
    await expectPinnedResultsCount(page, pinnedProteinIds.length + newRowIds.length, [
      PINNED_RESULTS_HEADING,
      `${MAX_PIN_LIMIT} Genes`,
      `${pinnedProteinIds.length + newRowIds.length} Proteins`,
    ]);
    await expectPinnedRows(page, [...pinnedProteinIds, ...newRowIds]);
    await expect(getPinAllButton(page)).toBeDisabled();
  });

  test.describe('at the pin limit in the Protein view', () => {
    test('a Protein URL with every row of the limit of genes loads untrimmed', async ({ page }) => {
      const { rowIds: leadingRowIds } = await fetchLeadingGeneRows(page, MAX_PIN_LIMIT - 1);
      const { maleProteins } = await fetchFiveProteinGeneRows(page);
      const pinnedIds = [...leadingRowIds, ...getRowIds(maleProteins)];
      expect(pinnedIds.length).toBeGreaterThan(MAX_PIN_LIMIT);

      await navigateByUrlWithPins(page, proteinCategories, pinnedIds);
      await expectPinnedResultsCount(page, pinnedIds.length, [
        PINNED_RESULTS_HEADING,
        `${MAX_PIN_LIMIT} Genes`,
        `${pinnedIds.length} Proteins`,
      ]);
      await expectPinnedParams(page, pinnedIds);
      await expect(page.getByRole('alert')).toBeHidden();
    });

    test('a Protein URL with more genes than the limit keeps the leading genes whole', async ({
      page,
    }) => {
      const { rowIds: leadingRowIds } = await fetchLeadingGeneRows(page, MAX_PIN_LIMIT);
      const { maleProteins } = await fetchFiveProteinGeneRows(page);

      await navigateByUrlWithPins(page, proteinCategories, [
        ...leadingRowIds,
        ...getRowIds(maleProteins),
      ]);
      await expectToastDetail(
        page,
        getProteinPinLimitToast(`${leadingRowIds.length} proteins were`),
      );
      await expectPinnedParams(page, leadingRowIds);
      await expectPinnedResultsCount(page, leadingRowIds.length, [
        PINNED_RESULTS_HEADING,
        `${MAX_PIN_LIMIT} Genes`,
        `${leadingRowIds.length} Proteins`,
      ]);
    });

    test.describe('with one protein of a pinned gene unpinned', () => {
      // Pins every row of the MAX_PIN_LIMIT - 1 leading genes and all but one protein of the
      // five-protein male gene from a Protein URL, which pins MAX_PIN_LIMIT genes
      const loadPinLimitWithOneProteinUnpinned = async (page: Page) => {
        const { rowIds: leadingRowIds } = await fetchLeadingGeneRows(page, MAX_PIN_LIMIT - 1);
        const { maleProteins, femaleProteinIds } = await fetchFiveProteinGeneRows(page);
        const [unpinnedProtein, ...pinnedMaleProteins] = maleProteins;
        const pinnedIds = [...leadingRowIds, ...getRowIds(pinnedMaleProteins)];

        await navigateByUrlWithPins(page, proteinCategories, pinnedIds);
        await expectPinnedResultsCount(page, pinnedIds.length, [
          PINNED_RESULTS_HEADING,
          `${MAX_PIN_LIMIT} Genes`,
          `${pinnedIds.length} Proteins`,
        ]);
        await expectPinnedParams(page, pinnedIds);

        return { unpinnedProtein, newGeneProteinIds: femaleProteinIds, pinnedIds };
      };

      test('every pinned protein can still be unpinned', async ({ page }) => {
        const { pinnedIds } = await loadPinLimitWithOneProteinUnpinned(page);

        const unpinButtons = getPinnedTable(page).getByRole('button', { name: 'Unpin' });
        await expect(unpinButtons).toHaveCount(pinnedIds.length);
        for (const unpinButton of await unpinButtons.all()) {
          await expect(unpinButton).toBeEnabled();
        }
      });

      test('the unpinned protein of a pinned gene can be pinned but no protein of a new gene can', async ({
        page,
      }) => {
        const { unpinnedProtein, newGeneProteinIds, pinnedIds } =
          await loadPinLimitWithOneProteinUnpinned(page);
        const unpinnedTable = getUnpinnedTable(page);

        await searchViaFilterbox(page, getEnsemblGeneId(fiveProteinMaleGene));
        for (const id of newGeneProteinIds) {
          await expect(getPinToggleButtonByName(unpinnedTable, page, id, 'pin')).toBeDisabled();
        }
        await expect(
          getPinToggleButtonByName(unpinnedTable, page, unpinnedProtein.composite_id, 'pin'),
        ).toBeEnabled();
        await expect(getPinAllButton(page)).toBeEnabled();

        await pinByName(unpinnedTable, page, unpinnedProtein.composite_id);
        await expectPinnedResultsCount(page, pinnedIds.length + 1, [
          PINNED_RESULTS_HEADING,
          `${MAX_PIN_LIMIT} Genes`,
          `${pinnedIds.length + 1} Proteins`,
        ]);
      });

      test('Pin All pins the unpinned proteins of pinned genes from any page', async ({ page }) => {
        const { unpinnedProtein, pinnedIds } = await loadPinLimitWithOneProteinUnpinned(page);
        const pinAllButton = getPinAllButton(page);

        // A partial match on one letter spans multiple pages. Pin All stays enabled whether or not
        // the page shows the unpinned protein, so check it on two different pages
        await searchViaFilterbox(page, unpinnedProtein.gene_symbol.charAt(0));
        await expect(pinAllButton).toBeEnabled();
        await goToLastPage(page);
        await expect(pinAllButton).toBeEnabled();

        await pinAll(page);
        await expectToastDetail(page, getProteinPinLimitToast('1 protein was'));
        await expectPinnedResultsCount(page, pinnedIds.length + 1, [
          PINNED_RESULTS_HEADING,
          `${MAX_PIN_LIMIT} Genes`,
          `${pinnedIds.length + 1} Proteins`,
        ]);
        await expectPinnedRows(page, [unpinnedProtein.composite_id]);
        await expect(pinAllButton).toBeDisabled();
      });
    });

    test('RNA pins fanned out to every protein leave nothing to pin in Protein', async ({
      page,
    }) => {
      const { rowIds: leadingRowIds, genes } = await fetchLeadingGeneRows(page, MAX_PIN_LIMIT);
      const { maleProteins, femaleProteinIds } = await fetchFiveProteinGeneRows(page);

      await navigateByUrlWithPins(page, rnaHemibrainCategories, genes);
      await expectPinnedResultsCount(page, MAX_PIN_LIMIT, [
        PINNED_RESULTS_HEADING,
        `${MAX_PIN_LIMIT} Genes`,
      ]);

      await switchToProteinView(page);
      await expectPinnedParams(page, leadingRowIds);
      await expectPinnedResultsCount(page, leadingRowIds.length, [
        PINNED_RESULTS_HEADING,
        `${MAX_PIN_LIMIT} Genes`,
        `${leadingRowIds.length} Proteins`,
      ]);

      // The search matches rows of genes outside the pinned ones only
      await searchViaFilterbox(page, getEnsemblGeneId(fiveProteinMaleGene));
      const unpinnedTable = getUnpinnedTable(page);
      for (const id of [...getRowIds(maleProteins), ...femaleProteinIds]) {
        await expect(getPinToggleButtonByName(unpinnedTable, page, id, 'pin')).toBeDisabled();
      }
      const pinAllButton = getPinAllButton(page);
      await expect(pinAllButton).toBeDisabled();
      await pinAllButton.hover();
      await expect(page.getByRole('tooltip')).toHaveText(PROTEIN_PIN_LIMIT_TOOLTIP);
    });

    test('Protein pins collapsed into RNA leave only pinned genes pinnable', async ({ page }) => {
      const { rowIds: leadingRowIds, genes } = await fetchLeadingGeneRows(page, MAX_PIN_LIMIT);

      await navigateByUrlWithPins(page, proteinCategories, leadingRowIds);
      await expectPinnedParams(page, leadingRowIds);

      await switchToRnaView(page);
      await expectPinnedResultsCount(page, MAX_PIN_LIMIT, [
        PINNED_RESULTS_HEADING,
        `${MAX_PIN_LIMIT} Genes`,
      ]);
      await expectPinnedRows(page, genes);

      // RNA is self-parented, so no RNA row outside the pinned ones can be pinned
      await searchViaFilterbox(page, getEnsemblGeneId(fiveProteinMaleGene));
      await expect(
        getPinToggleButtonByName(getUnpinnedTable(page), page, fiveProteinMaleGene, 'pin'),
      ).toBeDisabled();
      await expect(getPinAllButton(page)).toBeDisabled();
    });
  });
});
