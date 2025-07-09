import { CommonModule } from '@angular/common';
import { footerLinks, headerLinks } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { SvgImageComponent } from '../svg-image/svg-image.component';
import { HeaderComponent } from './header.component';

function changeWindowSize(width: number) {
  Object.defineProperty(window, 'innerWidth', {
    writable: true,
    configurable: true,
    value: width,
  });
}

const MOBILE_WIDTH = 500;
const DESKTOP_WIDTH = 1400;

async function setup() {
  const user = userEvent.setup();
  const { fixture } = await render(HeaderComponent, {
    componentInputs: {
      headerLogoPath: 'path/to/logo.svg',
      headerLinks: headerLinks,
      footerLinks: footerLinks,
    },
    imports: [CommonModule, SvgImageComponent],
  });

  const component = fixture.componentInstance;
  return { user, component };
}

describe('HeaderComponent', () => {
  afterEach(() => {
    changeWindowSize(DESKTOP_WIDTH); // Reset window size after each test
  });

  it('should create', async () => {
    await setup();
    expect(screen.getByAltText('header logo')).toBeTruthy();
  });

  it('should combine header and footer links in the popup menu when in mobile view', async () => {
    // Mock window width for mobile view
    changeWindowSize(MOBILE_WIDTH);
    const { user } = await setup();
    const toggleButton = screen.getByRole('button', { name: 'Toggle navigation' });

    await user.click(toggleButton);

    // Verify header links are present in the popup menu
    verifyHeaderLinks();

    // Verify footer links are present in the popup menu
    verifyFooterLinks();

    // Verify the links are visible
    expect(screen.getByRole('navigation', { name: 'Header navigation' })).toBeVisible();
  });

  it('should only show header links in desktop view', async () => {
    // Mock window width for desktop view
    changeWindowSize(DESKTOP_WIDTH);
    await setup();

    // Verify header links are present
    verifyHeaderLinks();

    // Verify footer links are not present in the header
    expect(screen.queryByRole('link', { name: 'FooterLinkInternal1' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'FooterLinkExternal' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'FooterLinkInternal2' })).not.toBeInTheDocument();
  });

  it('should toggle navigation visibility', async () => {
    // Mock window width for mobile view
    changeWindowSize(MOBILE_WIDTH);
    const { component, user } = await setup();

    const toggleButton = screen.getByRole('button', { name: 'Toggle navigation' });

    expect(component.isShown).toBe(false);

    await user.click(toggleButton);

    expect(component.isShown).toBe(true);
  });

  function verifyHeaderLinks() {
    expect(screen.getByRole('link', { name: 'HeaderLink1' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'HeaderLink2' })).toBeInTheDocument();
  }

  function verifyFooterLinks() {
    expect(screen.getByRole('link', { name: 'FooterLinkInternal1' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'FooterLinkExternal' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'FooterLinkInternal2' })).toBeInTheDocument();
  }
});
