import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { HeaderComponent } from './header.component';
import { CommonModule } from '@angular/common';
import { SvgImageComponent } from '@sagebionetworks/explorers/ui';
import {
  footerLinks as modelADFooterLinks,
  headerLinks as modelADHeaderLinks,
} from '@sagebionetworks/model-ad/util';

function changeWindowSize(width: number) {
  Object.defineProperty(window, 'innerWidth', {
    writable: true,
    configurable: true,
    value: width,
  });
}

const MOBILE_WIDTH = 1000;
const DESKTOP_WIDTH = 1400;

async function setup() {
  const user = userEvent.setup();
  const { fixture } = await render(HeaderComponent, {
    componentInputs: {
      headerLogoPath: 'path/to/logo.svg',
      headerLinks: modelADHeaderLinks,
      footerLinks: modelADFooterLinks,
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
    expect(screen.getByTestId('nav-links')).toBeVisible();
  });

  it('should show header and footer links in desktop view', async () => {
    // Mock window width for desktop view
    changeWindowSize(DESKTOP_WIDTH);
    await setup();

    // Verify header links are present
    verifyHeaderLinks();

    // Verify footer links are not present in the header
    expect(screen.queryByTestId('nav-link-about')).not.toBeInTheDocument();
    expect(screen.queryByTestId('nav-link-help')).not.toBeInTheDocument();
    expect(screen.queryByTestId('nav-link-news')).not.toBeInTheDocument();
    expect(screen.queryByTestId('nav-link-terms-of-service')).not.toBeInTheDocument();
  });

  it('should toggle navigation visibility', async () => {
    // Mock window width for mobile view
    changeWindowSize(MOBILE_WIDTH);
    const { component, user } = await setup();

    const toggleButton = screen.getByTestId('hamburger-menu-button');

    expect(component.isShown).toBe(false);

    await user.click(toggleButton);

    expect(component.isShown).toBe(true);
  });

  function verifyHeaderLinks() {
    expect(screen.getByTestId('nav-link-home')).toBeInTheDocument();
    expect(screen.getByTestId('nav-link-model-overview')).toBeInTheDocument();
    expect(screen.getByTestId('nav-link-gene-expression')).toBeInTheDocument();
    expect(screen.getByTestId('nav-link-disease-correlation')).toBeInTheDocument();
  }

  function verifyFooterLinks() {
    expect(screen.getByTestId('nav-link-about')).toBeInTheDocument();
    expect(screen.getByTestId('nav-link-help')).toBeInTheDocument();
    expect(screen.getByTestId('nav-link-news')).toBeInTheDocument();
    expect(screen.getByTestId('nav-link-terms-of-service')).toBeInTheDocument();
  }
});
