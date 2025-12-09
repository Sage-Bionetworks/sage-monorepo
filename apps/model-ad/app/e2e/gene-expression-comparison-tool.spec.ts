import { test } from '@playwright/test';
import {
  getQueryParamFromValues,
  testFullCaseInsensitiveMatch,
  testPartialCaseInsensitiveSearch,
  testSearchExcludesPinnedItems,
} from '@sagebionetworks/explorers/testing/e2e';
import { navigateToComparison } from './helpers/comparison-tool';

const CT_PAGE = 'Gene Expression';

// TODO: remove test.fail when MG-602 is resolved
const testFailDescription =
  'Test will fail until data is updated to remove duplicate objects with the same row id (MG-602)';
test.describe('gene expression', () => {
  test.fail(
    'filterbox search without comma returns partial case-insensitive matches',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      await testPartialCaseInsensitiveSearch(page, 'acul', [
        'ENSMUSG00000033417~5xFAD (IU/Jax/Pitt)',
        'ENSMUSG00000033417~LOAD1',
      ]); // Cacul1
    },
  );

  test.fail(
    'filterbox search excludes pinned items from results',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      const pinnedCorrelations = [
        'ENSMUSG00000033417~5xFAD (IU/Jax/Pitt)',
        'ENSMUSG00000033417~LOAD1',
      ]; // Cacul1

      await navigateToComparison(
        page,
        CT_PAGE,
        true,
        'url',
        getQueryParamFromValues(pinnedCorrelations, 'pinned'),
      );

      await testSearchExcludesPinnedItems(page, pinnedCorrelations, 'acul', 'cacul1,');
    },
  );

  test.fail(
    'filterbox search with commas returns full, case-insensitive matches',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      await testFullCaseInsensitiveMatch(
        page,
        'fer,',
        ['ENSMUSG00000000127~5xFAD (IU/Jax/Pitt)', 'ENSMUSG00000000127~LOAD1'], // Fer
        'ENSMUSG00000024965~5xFAD (IU/Jax/Pitt)', // Fermt3
      );
    },
  );

  test.fail(
    'filterbox search partial case-insensitive matches with special characters',
    { annotation: { type: 'fail', description: testFailDescription } },
    async ({ page }) => {
      await navigateToComparison(page, CT_PAGE, true);

      await testPartialCaseInsensitiveSearch(page, '(r', [
        'ENSMUSG00000086429~5xFAD (IU/Jax/Pitt)',
        'ENSMUSG00000086429~LOAD1',
      ]); // Gt(ROSA)26Sor
    },
  );
});
