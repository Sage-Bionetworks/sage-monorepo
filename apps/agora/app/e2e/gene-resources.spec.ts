import { expect, test } from '@playwright/test';
import { waitForSpinnerNotVisible } from './helpers/utils';

test.describe('specific viewport block', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto('/genes/ENSG00000178209/resources');

    // wait for page to load (i.e. spinner to disappear)
    await waitForSpinnerNotVisible(page);

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle('Agora');
  });

  test('AMP-PD explorer link to go to gene', async ({ page }) => {
    test.slow();
    await page.goto('/genes/ENSG00000178209/resources');

    // wait for page to load (i.e. spinner to disappear)
    await waitForSpinnerNotVisible(page, 150000);

    // expect link named 'Visit AMP-PD'
    const link = page.getByRole('link', { name: 'Visit AMP-PD' });
    await expect(link).toHaveText('Visit AMP-PD');

    // expect url to have ensembleid
    await expect(link).toHaveAttribute(
      'href',
      'https://target-explorer.amp-pd.org/genes/target-search?gene=ENSG00000178209',
    );
  });
});
