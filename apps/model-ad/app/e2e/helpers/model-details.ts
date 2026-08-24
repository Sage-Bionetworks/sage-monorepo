import { expect, Locator, Page, test } from '@playwright/test';

export function getTocContainer(page: Page) {
  return page.locator('.table-of-contents-container');
}

export function getTocExpandButton(page: Page) {
  return getTocContainer(page).getByRole('button', { name: 'Expand' });
}

export function getTocCollapseButton(page: Page) {
  return getTocContainer(page).getByRole('button', { name: 'Collapse' });
}

export function getTocList(page: Page) {
  return page.locator('#toc-list');
}

export function getTocLinks(page: Page) {
  return getTocContainer(page).getByTestId('toc-item-link');
}

// Must exceed ANCHOR_HIGHLIGHT_HOLD_MS in model-details-boxplots-selector.component.ts.
const HIGHLIGHT_CLEAR_TIMEOUT_MS = 10000;

// All the highlights are asserted before any is waited out, since they share a single hold timer.
export async function expectHighlightClears(...locators: Locator[]) {
  for (const locator of locators) {
    await expect(locator).toHaveClass(/highlighted/);
  }
  for (const locator of locators) {
    await expect(locator).not.toHaveClass(/highlighted/, { timeout: HIGHLIGHT_CLEAR_TIMEOUT_MS });
  }
}

// Expands the table of contents, then clicks every link and asserts it scrolls to its section.
export async function expectTocLinksScrollToSections(
  page: Page,
  biomarkersPath: string,
  sectionHeadingLevel: 2 | 3,
) {
  await page.goto(biomarkersPath);
  await expect(page.getByRole('heading', { level: 2, name: 'Table of Contents' })).toBeVisible();

  await getTocExpandButton(page).click();
  const tocLinks = getTocLinks(page);
  const tocLinksCount = await tocLinks.count();

  for (let i = 0; i < tocLinksCount; i++) {
    await test.step(`validate link ${i}`, async () => {
      const tocLink = tocLinks.nth(i);
      const tocLinkName = (await tocLink.textContent()) ?? '';
      await tocLink.click();
      await expect(
        page.getByRole('heading', { level: sectionHeadingLevel, name: tocLinkName, exact: true }),
      ).toBeInViewport();
    });
  }
}
