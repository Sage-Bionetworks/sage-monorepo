import { expect, test } from '@playwright/test';
import { waitForSpinnerNotVisible } from './helpers/utils';

const ensemblId = 'ENSG00000178209';
const hgncSymbol = 'PLEC';
const resourcesUrl = `/genes/${ensemblId}/resources`;

test.describe('gene resources', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('has title', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page);
    await expect(page).toHaveTitle('Agora');
  });

  test('displays Drug Development Resources section', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    await expect(page.getByRole('heading', { name: 'Drug Development Resources' })).toBeVisible();
  });

  test('displays Additional Resources section', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    await expect(page.getByRole('heading', { name: 'Additional Resources' })).toBeVisible();
  });
});

test.describe('gene resources - drug development cards', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('Chemical Probes card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /chemical probes that are available/i });
    await expect(card).toHaveAttribute('href', `https://www.chemicalprobes.org/?q=${hgncSymbol}`);
  });

  test('Open Targets card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /genome-scale experiments/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://platform.opentargets.org/target/${ensemblId}`,
    );
  });

  test('Pharos card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /Druggable Genome/i });
    await expect(card).toHaveAttribute('href', `https://pharos.nih.gov/targets?q=${ensemblId}`);
  });

  test('Probe Miner card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /medicinal chemistry data/i });
    await expect(card).toHaveAttribute('href', 'https://probeminer.icr.ac.uk/#/');
  });

  test('Protein Data Bank card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /3D protein structure/i });
    await expect(card).toHaveAttribute('href', 'https://www.rcsb.org');
  });
});

test.describe('gene resources - additional resource cards', () => {
  test.slow();
  test.use({ viewport: { width: 1600, height: 1200 } });

  test('AD Atlas card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /network and enrichment analyses/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://adatlas.org/?type=geneEnsembl&ids=${ensemblId}`,
    );
  });

  test('Alzforum card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /news and information resources about AD/i });
    await expect(card).toHaveAttribute('href', 'https://www.alzforum.org');
  });

  test('AlzPED card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /preclinical efficacy studies/i });
    await expect(card).toHaveAttribute('href', 'https://alzped.nia.nih.gov');
  });

  test('AMP-PD card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /Parkinson's Disease/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://target-explorer.amp-pd.org/genes/target-search?gene=${ensemblId}`,
    );
  });

  test('Brain Knowledge Platform card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /SEA-AD Comparative Viewer/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://knowledge.brain-map.org/data/5IU4U8BP711TR6KZ843/2CD0HDC5PS6A58T0P6E/compare?geneOption=${hgncSymbol}`,
    );
  });

  test('Gene Ontology card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /GO terms/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://amigo.geneontology.org/amigo/search/annotation?q=${hgncSymbol}`,
    );
  });

  test('GeneCards card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /comprehensive collection of public sources/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://www.genecards.org/cgi-bin/carddisp.pl?gene=${hgncSymbol}`,
    );
  });

  test('Genomics DB card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /NIAGADS/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://www.niagads.org/genomics/app/record/gene/${ensemblId}`,
    );
  });

  test('Pub AD card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /dementia-related publication/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://adexplorer.medicine.iu.edu/pubad/external/${hgncSymbol}`,
    );
  });

  test('PubMed card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /publications related to this target on PubMed/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://pubmed.ncbi.nlm.nih.gov/?term=${hgncSymbol}`,
    );
  });

  test('Ensembl card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /reactome pathway/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://www.ensembl.org/Homo_sapiens/Gene/Pathway?g=${ensemblId}`,
    );
  });

  test('SEA-AD card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /Brain Cell Atlas/i });
    await expect(card).toHaveAttribute(
      'href',
      'https://portal.brain-map.org/explore/seattle-alzheimers-disease',
    );
  });

  test('UniProtKB card links to correct page', async ({ page }) => {
    await page.goto(resourcesUrl);
    await waitForSpinnerNotVisible(page, 150000);
    const card = page.getByRole('link', { name: /protein sequence and functional information/i });
    await expect(card).toHaveAttribute(
      'href',
      `https://www.uniprot.org/uniprotkb?query=${ensemblId}`,
    );
  });
});
