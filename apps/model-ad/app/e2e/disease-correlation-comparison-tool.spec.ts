import { test } from '@playwright/test';
import {
  expectPinnedParams,
  expectPinnedRows,
  expectUnpinnedTableOnly,
  getPinnedTable,
  unPinByName,
} from '@sagebionetworks/explorers/testing/e2e';
import { fetchDiseaseCorrelations, navigateToComparison } from './helpers/comparison-tool';

const CT_PAGE = 'Disease Correlation';

test.describe('disease correlation', () => {
  test('pins are cached for dropdown selections and can be unpinned', async ({ page }) => {
    const correlations = await fetchDiseaseCorrelations(page);
    const [firstCorrelation] = correlations;

    await navigateToComparison(page, CT_PAGE, true, 'url', `pinned=${firstCorrelation._id}`);

    await expectPinnedParams(page, [firstCorrelation._id]);
    await expectPinnedRows(page, [firstCorrelation.name]);

    const dropdown = page.getByRole('combobox');
    await dropdown.click();
    const options = page.getByRole('option');
    const secondOption = options.nth(1);
    await secondOption.click();

    await expectPinnedParams(page, []);
    await expectUnpinnedTableOnly(page);

    await dropdown.click();
    await options.first().click();

    await expectPinnedParams(page, [firstCorrelation._id]);
    await expectPinnedRows(page, [firstCorrelation.name]);

    const pinnedTable = getPinnedTable(page);
    await unPinByName(pinnedTable, page, firstCorrelation.name);
    await expectPinnedParams(page, []);
  });
});
