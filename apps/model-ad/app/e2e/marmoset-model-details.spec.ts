import { expect, test } from '@playwright/test';
import { expectPageNotAtTop } from '@sagebionetworks/explorers/testing/e2e';
import { baseURL } from '../playwright.config';
import {
  expectHighlightClears,
  expectTocLinksScrollToSections,
  getTocExpandButton,
  getTocLinks,
} from './helpers/model-details';

test.describe('marmoset model details - boxplots selector', () => {
  const model = 'Presenilin 1';
  const biomarkersPath = '/models/Presenilin%201/biomarkers?modelOrganism=marmoset';
  const noTabPath = '/models/Presenilin%201?modelOrganism=marmoset';
  const measurementDefault = 'Soluble Aβ40';
  const measurementOther = 'Insoluble Aβ42';
  const ageSection = '0-1 year';
  const ageFragment = '0-1-year';

  test('clicking on table of contents link scrolls to appropriate age group', async ({ page }) => {
    await expectTocLinksScrollToSections(page, biomarkersPath, 3);
  });

  test('age group section is shown when url includes an age fragment', async ({ page }) => {
    await page.goto(`${biomarkersPath}#${ageFragment}`);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();
    await expect(
      page.getByRole('heading', { level: 3, name: ageSection, exact: true }),
    ).toBeInViewport();
    await expectPageNotAtTop(page);
  });

  test('age group section is shown when a url with no tab segment includes an age fragment', async ({
    page,
  }) => {
    await page.goto(`${noTabPath}#${ageFragment}`);
    await expect(
      page.getByRole('heading', { level: 3, name: ageSection, exact: true }),
    ).toBeInViewport();
    await expectPageNotAtTop(page);
    await page.waitForURL(`${noTabPath}#${ageFragment}`);
  });

  test('filters are set from query parameters when the url has no tab segment', async ({
    page,
  }) => {
    const sexFilter = 'Female';
    await page.goto(`${noTabPath}&sex=${sexFilter}`);
    await expect(page.getByRole('combobox', { name: sexFilter })).toBeVisible();
    await page.waitForURL(`${noTabPath}&sex=${sexFilter}`);
  });

  test('clicking on an age group adds fragment to url', async ({ page }) => {
    await page.goto(biomarkersPath);
    await expect(page.getByRole('heading', { level: 1, name: model })).toBeVisible();

    await getTocExpandButton(page).click();
    await getTocLinks(page).filter({ hasText: ageSection }).click();

    await expect(page.getByRole('heading', { level: 3, name: ageSection })).toBeInViewport();
    await page.waitForURL(`${biomarkersPath}#${ageFragment}`);
  });

  test('loading a page with an age fragment highlights the age group heading', async ({ page }) => {
    await page.goto(`${biomarkersPath}#${ageFragment}`);

    await expectHighlightClears(
      page.getByRole('heading', { level: 3, name: ageSection, exact: true }),
    );
  });

  test('default measurement filter is set when there is no query parameter', async ({ page }) => {
    await page.goto(biomarkersPath);
    await expect(page.getByRole('combobox', { name: measurementDefault })).toBeVisible();
    await expect(page.getByRole('combobox', { name: 'Female & Male' })).toBeVisible();
  });

  test('measurement filter can be set from query parameter', async ({ page }) => {
    await page.goto(`${biomarkersPath}&measurement=${encodeURIComponent(measurementOther)}`);
    await expect(page.getByRole('combobox', { name: measurementOther })).toBeVisible();
  });

  test('measurement query parameter is updated when filter changes', async ({ page }) => {
    await page.goto(biomarkersPath);

    await page.getByRole('combobox', { name: measurementDefault }).click();
    await page.getByRole('option', { name: measurementOther }).click();

    await page.waitForURL((url) => url.searchParams.get('measurement') === measurementOther);
  });

  test('evidence type heading has no share link or download button', async ({ page }) => {
    await page.goto(biomarkersPath);

    const evidenceTypeHeading = page.getByRole('heading', {
      level: 2,
      name: measurementDefault,
      exact: true,
    });
    await evidenceTypeHeading.hover();

    await expect(
      page.getByRole('button', { name: `Copy link to ${measurementDefault}` }),
    ).toHaveCount(0);
    await expect(page.getByRole('button', { name: 'Download', exact: true })).toHaveCount(0);
    await expect(page.getByRole('button', { name: 'Download All' })).toBeVisible();
  });

  test('hovering an age group reveals a share link that copies the url to that section', async ({
    page,
    context,
  }) => {
    await context.grantPermissions(['clipboard-read']);
    await page.goto(biomarkersPath);

    // hover on heading, so share link appears
    const heading = page.getByRole('heading', { level: 3, name: ageSection, exact: true });
    await heading.hover();

    const button = page.getByRole('button', { name: `Copy link to ${ageSection}` });
    await button.click();

    const clipboardContent = await page.evaluate(() => navigator.clipboard.readText());
    expect(clipboardContent).toEqual(`${baseURL}${biomarkersPath}#${ageFragment}`);
  });
});
