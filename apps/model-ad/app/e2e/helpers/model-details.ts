import { expect, Page, test } from '@playwright/test';

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
