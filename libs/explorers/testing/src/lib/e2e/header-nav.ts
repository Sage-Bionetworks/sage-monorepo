import { Page } from '@playwright/test';

/**
 * How to reach a page through the header navigation.
 * - `dropdown` — top-level trigger that must be opened before the link is clickable. Desktop
 *   renders it as a lazily-populated popup menu; mobile renders the children inline, so there is
 *   no trigger to click.
 * - `link` — the link that performs the navigation.
 */
export type HeaderNavTrail = {
  dropdown?: string;
  link: string;
};

/**
 * Clicks through the header navigation to reach a page, opening the hamburger menu and any
 * dropdown trigger along the way. Safe at both mobile and desktop breakpoints.
 */
export const navigateViaHeaderNav = async (page: Page, trail: HeaderNavTrail) => {
  // Open the hamburger menu if the button is visible (mobile breakpoint)
  const menuButton = page.getByRole('button', { name: 'Toggle navigation' });
  if (await menuButton.isVisible().catch(() => false)) {
    await menuButton.click();
  }

  if (trail.dropdown) {
    const dropdownTrigger = page.getByRole('button', { name: trail.dropdown, exact: true });
    if (await dropdownTrigger.isVisible().catch(() => false)) {
      await dropdownTrigger.click();
    }
  }

  await page.getByRole('link', { name: trail.link, exact: true }).click();
};
