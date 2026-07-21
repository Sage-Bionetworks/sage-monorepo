import { Page } from '@playwright/test';

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
