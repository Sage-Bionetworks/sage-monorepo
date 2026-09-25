import { expect, Page, test } from '@playwright/test';
import { getPinLimitWarning, MAX_PIN_LIMIT } from '@sagebionetworks/explorers/constants';
import {
  expectCategoriesParams,
  expectPinnedParams,
  expectPinnedResultsCount,
  expectPinnedRows,
  expectToastDetail,
  expectUnpinnedTableOnly,
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
import { Proteomics } from '@sagebionetworks/model-ad/api-client';
import {
  DIFFERENTIAL_EXPRESSION_CT_PAGE as CT_PAGE,
  DIFFERENTIAL_EXPRESSION_DROPDOWN_INDEX as DROPDOWN_INDEX,
  DIFFERENTIAL_EXPRESSION_PROTEIN_CATEGORY as PROTEIN_MAIN_CATEGORY,
} from './constants';
import {
  fetchProteomics,
  fetchTranscriptomics,
  navigateToComparison,
} from './helpers/comparison-tool';

// Protein rows are children of the RNA row keyed by their rna_composite_id, so each gene below is
// an RNA composite_id. Protein only offers the Hemibrain tissue.
const RNA_MAIN_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';
const HEMIBRAIN_TISSUE = 'Tissue - Hemibrain';
const rnaHemibrainCategories = [RNA_MAIN_CATEGORY, HEMIBRAIN_TISSUE];
const proteinCategories = [PROTEIN_MAIN_CATEGORY, HEMIBRAIN_TISSUE];
const fourProteinGene = 'ENSMUSG00000019961~LOAD2~Male'; // Tmpo
const fiveProteinMaleGene = 'ENSMUSG00000032826~LOAD2~Male'; // Ank2
const fiveProteinFemaleGene = 'ENSMUSG00000032826~LOAD2~Female'; // Ank2
const proteinOnlyGene = 'ENSMUSG00000005681~LOAD2~Female'; // Apoa2

const getEnsemblGeneId = (gene: string) => gene.split('~')[0];

const getProteinIds = (rows: Proteomics[], gene: string) =>
  rows.filter((row) => row.rna_composite_id === gene).map((row) => row.composite_id);

const getRowIds = (rows: Proteomics[]) => rows.map((row) => row.composite_id);

// The Protein rows of the given genes, in the table's default sort order
const fetchProteinRows = async (page: Page, genes: string[]): Promise<Proteomics[]> => {
  const ensemblGeneIds = [...new Set(genes.map(getEnsemblGeneId))];
  const rows = await fetchProteomics(
    page,
    proteinCategories,
    {},
    {
      search: ensemblGeneIds.join(','),
    },
  );
  return rows.filter((row) => genes.includes(row.rna_composite_id));
};

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

test.describe('differential expression protein pinning', () => {
  test('RNA pins fan out to every protein of their genes in the Protein view', async ({ page }) => {
    const pinnedGenes = [fourProteinGene, fiveProteinMaleGene];
    const proteinRows = await fetchProteinRows(page, pinnedGenes);
    expect(getProteinIds(proteinRows, fourProteinGene)).toHaveLength(4);
    expect(getProteinIds(proteinRows, fiveProteinMaleGene)).toHaveLength(5);
    const proteinIds = getRowIds(proteinRows);

    await navigateByUrlWithPins(page, rnaHemibrainCategories, pinnedGenes);
    await expectPinnedRows(page, pinnedGenes);
    await expectPinnedResultsCount(page, pinnedGenes.length);

    await switchToProteinView(page);
    await expectPinnedParams(page, proteinIds);
    // TODO(MG-1084): the header counts rows, so it reads the protein count, not the gene count
    await expectPinnedResultsCount(page, proteinIds.length);
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
    await expectPinnedResultsCount(page, proteinIds.length);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);
    await expectPinnedResultsCount(page, 1);
    await expectPinnedRows(page, [fourProteinGene]);
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
    await expectPinnedResultsCount(page, remainingProteinIds.length);
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
    await expectPinnedResultsCount(page, pinnedProteinIds.length);
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
    await expectPinnedResultsCount(page, proteinRows.length);
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
    await expectPinnedResultsCount(page, pinnedProteinIds.length);
  });

  test('a Protein pin with no RNA gene row is hidden in RNA and returns in Protein', async ({
    page,
  }) => {
    const proteinRows = await fetchProteinRows(page, [fourProteinGene, proteinOnlyGene]);
    expect(getProteinIds(proteinRows, fourProteinGene)).toHaveLength(4);
    expect(getProteinIds(proteinRows, proteinOnlyGene).length).toBeGreaterThan(0);
    const rnaRows = await fetchTranscriptomics(
      page,
      rnaHemibrainCategories,
      {},
      {
        search: getEnsemblGeneId(proteinOnlyGene),
      },
    );
    expect(rnaRows.map((row) => row.composite_id)).not.toContain(proteinOnlyGene);
    const proteinIds = getRowIds(proteinRows);

    await navigateByUrlWithPins(page, proteinCategories, proteinIds);
    await expectPinnedParams(page, proteinIds);

    await switchToRnaView(page);
    await expectPinnedParams(page, [fourProteinGene]);
    await expectPinnedResultsCount(page, 1);

    await switchToProteinView(page);
    await expectPinnedParams(page, proteinIds);
    await expectPinnedResultsCount(page, proteinIds.length);
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
    await expectPinnedResultsCount(page, 1);
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
    await expectPinnedResultsCount(page, 1);
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
    await expectPinnedResultsCount(page, pinnedProteinIds.length);
    await searchViaFilterbox(page, searchTerm);
    await pinAll(page);

    await expectToastDetail(page, getPinLimitWarning(newRowIds.length, MAX_PIN_LIMIT));
    await expectPinnedResultsCount(page, pinnedProteinIds.length + newRowIds.length);
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
      await expectPinnedResultsCount(page, pinnedIds.length);
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
      // TODO(MG-1084): the warning reports the rows kept against a limit that counts genes
      await expectToastDetail(page, getPinLimitWarning(leadingRowIds.length, MAX_PIN_LIMIT));
      await expectPinnedParams(page, leadingRowIds);
      await expectPinnedResultsCount(page, leadingRowIds.length);
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
        await expectPinnedResultsCount(page, pinnedIds.length);
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
        await expectPinnedResultsCount(page, pinnedIds.length + 1);
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
        await expectToastDetail(page, getPinLimitWarning(1, MAX_PIN_LIMIT));
        await expectPinnedResultsCount(page, pinnedIds.length + 1);
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
      await expectPinnedResultsCount(page, MAX_PIN_LIMIT);

      await switchToProteinView(page);
      await expectPinnedParams(page, leadingRowIds);
      await expectPinnedResultsCount(page, leadingRowIds.length);

      // The search matches rows of genes outside the pinned ones only
      await searchViaFilterbox(page, getEnsemblGeneId(fiveProteinMaleGene));
      const unpinnedTable = getUnpinnedTable(page);
      for (const id of [...getRowIds(maleProteins), ...femaleProteinIds]) {
        await expect(getPinToggleButtonByName(unpinnedTable, page, id, 'pin')).toBeDisabled();
      }
      await expect(getPinAllButton(page)).toBeDisabled();
    });

    test('Protein pins collapsed into RNA leave only pinned genes pinnable', async ({ page }) => {
      const { rowIds: leadingRowIds, genes } = await fetchLeadingGeneRows(page, MAX_PIN_LIMIT);

      await navigateByUrlWithPins(page, proteinCategories, leadingRowIds);
      await expectPinnedParams(page, leadingRowIds);

      await switchToRnaView(page);
      await expectPinnedResultsCount(page, MAX_PIN_LIMIT);
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
