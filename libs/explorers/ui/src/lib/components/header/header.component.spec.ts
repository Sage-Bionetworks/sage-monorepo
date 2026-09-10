import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router, provideRouter } from '@angular/router';
import { NavigationLink } from '@sagebionetworks/explorers/models';
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

@Component({ template: '', standalone: true })
class DummyComponent {}

async function setup() {
  const user = userEvent.setup();
  const { fixture } = await render(HeaderComponent, {
    componentInputs: {
      headerLogoPath: 'path/to/logo.svg',
      headerLinks: headerLinks,
      footerLinks: footerLinks,
    },
    imports: [CommonModule, SvgImageComponent],
    providers: [
      provideNoopAnimations(),
      provideRouter([
        { path: 'header-link-1', component: DummyComponent },
        { path: 'header-link-2', component: DummyComponent },
        { path: 'child-link-1', component: DummyComponent },
        { path: 'child-link-2', component: DummyComponent },
        { path: 'sub-child-link-1', component: DummyComponent },
        { path: 'sub-child-link-2', component: DummyComponent },
        { path: 'footer-link-internal-1', component: DummyComponent },
        { path: 'footer-link-internal-2', component: DummyComponent },
      ]),
    ],
  });

  const component = fixture.componentInstance;
  return { user, component, fixture };
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

    // Verify header links are present
    verifyHeaderLinks();

    // Verify dropdown parent is shown
    expect(screen.getByText('DropdownLink')).toBeInTheDocument();

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

  it('should render a collapsed menu button for a link with children in desktop mode', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    await setup();

    const trigger = screen.getByRole('button', { name: 'DropdownLink' });
    expect(trigger).toHaveAttribute('aria-haspopup', 'menu');
    expect(trigger).toHaveAttribute('aria-expanded', 'false');
    expect(screen.queryByRole('menu', { name: 'DropdownLink' })).not.toBeInTheDocument();
  });

  it('should open the dropdown when the trigger is clicked', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { user } = await setup();

    const trigger = screen.getByRole('button', { name: 'DropdownLink' });
    await user.click(trigger);

    const menu = screen.getByRole('menu', { name: 'DropdownLink' });
    expect(trigger).toHaveAttribute('aria-expanded', 'true');
    expect(trigger).toHaveAttribute('aria-controls', menu.id);
    expect(screen.getByRole('link', { name: 'ChildLink1' })).toBeInTheDocument();
  });

  it('should focus the dropdown trigger and open it with the keyboard in desktop mode', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { user } = await setup();

    const trigger = screen.getByRole('button', { name: 'DropdownLink' });
    trigger.focus();
    expect(trigger).toHaveFocus();

    await user.keyboard('{Enter}');

    const menu = screen.getByRole('menu', { name: 'DropdownLink' });
    expect(trigger).toHaveAttribute('aria-expanded', 'true');
    expect(trigger).toHaveAttribute('aria-controls', menu.id);
    expect(screen.getByRole('link', { name: 'ChildLink1' })).toBeInTheDocument();
  });

  it('should show dropdown label and children in mobile mode', async () => {
    changeWindowSize(MOBILE_WIDTH);
    const { user } = await setup();
    const toggleButton = screen.getByRole('button', { name: 'Toggle navigation' });

    await user.click(toggleButton);

    // Parent label should be visible
    expect(screen.getByText('DropdownLink')).toBeInTheDocument();

    // Children should be visible by default
    expect(screen.getByRole('link', { name: 'ChildLink1' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'ChildLink2' })).toBeInTheDocument();
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

  it('should not toggle isShown when clicking a link in desktop mode', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { component, user } = await setup();

    expect(component.isShown).toBe(false);

    const headerLink = screen.getByRole('link', { name: 'HeaderLink1' });
    await user.click(headerLink);

    expect(component.isShown).toBe(false);
  });

  it('should reset isShown to false when transitioning from mobile to desktop', async () => {
    changeWindowSize(MOBILE_WIDTH);
    const { component, user } = await setup();

    const toggleButton = screen.getByRole('button', { name: 'Toggle navigation' });
    await user.click(toggleButton);
    expect(component.isShown).toBe(true);

    changeWindowSize(DESKTOP_WIDTH);
    component.onResize();

    expect(component.isShown).toBe(false);
  });

  it('should not open menu when resizing to mobile after clicking a link in desktop mode', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { component, user } = await setup();

    expect(component.isShown).toBe(false);

    const headerLink = screen.getByRole('link', { name: 'HeaderLink1' });
    await user.click(headerLink);

    expect(component.isShown).toBe(false);

    changeWindowSize(MOBILE_WIDTH);
    component.onResize();

    expect(component.isShown).toBe(false);
    expect(component.isMobile).toBe(true);
  });

  it('should throw when a dropdown mixes subheader and flat children regardless of screen size', async () => {
    changeWindowSize(MOBILE_WIDTH);

    await expect(
      render(HeaderComponent, {
        componentInputs: {
          headerLogoPath: 'path/to/logo.svg',
          headerLinks: [
            {
              label: 'MixedDropdown',
              children: [
                { label: 'Flat Item', routerLink: ['/flat'] },
                {
                  label: 'SubheaderLabel',
                  isSubheader: true,
                  children: [{ label: 'Child', routerLink: ['/child'] }],
                },
              ],
            },
          ],
        },
        imports: [CommonModule, SvgImageComponent],
        providers: [provideRouter([])],
      }),
    ).rejects.toThrow('mixing subheader and flat');
  });

  it('should build grouped MenuItem structure for dropdown with subheaders', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { component } = await setup();

    const items = component.dropdownMenuItems.get('DropdownWithSubheader');
    expect(items).toHaveLength(1);

    const subheaderItem = items?.[0];
    expect(subheaderItem?.label).toBe('SubheaderLabel');
    expect(subheaderItem?.items).toHaveLength(2);
    expect(subheaderItem?.items?.[0].label).toBe('SubChildLink1');
    expect(subheaderItem?.items?.[1].label).toBe('SubChildLink2');
  });

  it('should forward routerLink and queryParams to dropdown MenuItems', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { component } = await setup();

    const flatItems = component.dropdownMenuItems.get('DropdownLink');
    expect(flatItems?.[1].routerLink).toEqual(['/child-link-2']);
    expect(flatItems?.[1].queryParams).toEqual({ tab: 'overview' });

    const grandchildren = component.dropdownMenuItems.get('DropdownWithSubheader')?.[0].items;
    expect(grandchildren?.[1].routerLink).toEqual(['/sub-child-link-2']);
    expect(grandchildren?.[1].queryParams).toEqual({ tab: 'details' });
  });

  it('should include queryParams in dropdown link hrefs in mobile mode', async () => {
    changeWindowSize(MOBILE_WIDTH);
    const { user } = await setup();
    await user.click(screen.getByRole('button', { name: 'Toggle navigation' }));

    expect(screen.getByRole('link', { name: 'ChildLink2' })).toHaveAttribute(
      'href',
      '/child-link-2?tab=overview',
    );
    expect(screen.getByRole('link', { name: 'SubChildLink2' })).toHaveAttribute(
      'href',
      '/sub-child-link-2?tab=details',
    );
  });

  it('should include queryParams in a top-level link href', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    await render(HeaderComponent, {
      componentInputs: {
        headerLogoPath: 'path/to/logo.svg',
        headerLinks: [
          {
            label: 'TopLevelWithParams',
            routerLink: ['/header-link-1'],
            queryParams: { tab: 'overview' },
          },
        ],
      },
      imports: [CommonModule, SvgImageComponent],
      providers: [
        provideNoopAnimations(),
        provideRouter([{ path: 'header-link-1', component: DummyComponent }]),
      ],
    });

    expect(screen.getByRole('link', { name: 'TopLevelWithParams' })).toHaveAttribute(
      'href',
      '/header-link-1?tab=overview',
    );
  });

  it('should mark dropdown as active when a subheader grandchild route is active', async () => {
    changeWindowSize(DESKTOP_WIDTH);
    const { component, fixture } = await setup();

    const router = fixture.debugElement.injector.get(Router);
    await router.navigate(['/sub-child-link-1']);

    const subheaderLink = headerLinks.find((l) => l.label === 'DropdownWithSubheader');
    expect(subheaderLink).toBeDefined();
    expect(component.isDropdownActive(subheaderLink ?? { label: '' })).toBe(true);
  });

  it('should render subheader as non-link text and its children as links in mobile mode', async () => {
    changeWindowSize(MOBILE_WIDTH);
    const { user } = await setup();
    await user.click(screen.getByRole('button', { name: 'Toggle navigation' }));

    expect(screen.getByText('SubheaderLabel')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'SubheaderLabel' })).not.toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'SubChildLink1' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'SubChildLink2' })).toBeInTheDocument();
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

describe('HeaderComponent active dropdown links in mobile mode', () => {
  const COMPARISON_ROUTE = '/comparison-tool';
  const ROUTE_WITHOUT_LINKS = '/other-page';
  const RNA_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';
  const PROTEIN_CATEGORY = 'PROTEIN - DIFFERENTIAL EXPRESSION';
  const TISSUE = 'Tissue - Hemibrain';

  // Every link shares one route, mirroring the RNA and Protein differential expression header links:
  // query params are the only thing that distinguishes them. The two dropdowns are separate because
  // subheader and flat children render through different template bindings (and validateHeaderLinks
  // rejects mixing them), so both need coverage.
  const linksWithQueryParams: NavigationLink[] = [
    {
      label: 'Differential Expression',
      children: [
        {
          label: 'Mouse Models',
          isSubheader: true,
          children: [
            {
              label: 'RNA',
              routerLink: [COMPARISON_ROUTE],
              queryParams: { categories: RNA_CATEGORY },
            },
            {
              label: 'Protein',
              routerLink: [COMPARISON_ROUTE],
              queryParams: { categories: PROTEIN_CATEGORY },
            },
          ],
        },
      ],
    },
    {
      label: 'Tabs',
      children: [
        { label: 'Overview', routerLink: [COMPARISON_ROUTE], queryParams: { tab: 'overview' } },
        { label: 'NoParams', routerLink: [COMPARISON_ROUTE] },
      ],
    },
  ];

  /** Mirrors how the comparison tool writes multi-value params: encode each value, join with commas. */
  function serializeCategories(...categories: string[]) {
    return categories.map((category) => encodeURIComponent(category)).join(',');
  }

  async function setupMobile() {
    changeWindowSize(MOBILE_WIDTH);
    const { fixture } = await render(HeaderComponent, {
      componentInputs: {
        headerLogoPath: 'path/to/logo.svg',
        headerLinks: linksWithQueryParams,
      },
      imports: [CommonModule, SvgImageComponent],
      providers: [
        provideNoopAnimations(),
        provideRouter([
          { path: 'comparison-tool', component: DummyComponent },
          { path: 'other-page', component: DummyComponent },
        ]),
      ],
    });

    const router = fixture.debugElement.injector.get(Router);

    async function navigate(path: string, queryParams: Record<string, string>) {
      await router.navigate([path], { queryParams });
      fixture.detectChanges();
    }

    return { navigate };
  }

  function link(name: string) {
    return screen.getByRole('link', { name });
  }

  afterEach(() => {
    changeWindowSize(DESKTOP_WIDTH);
  });

  it('should mark a link as active when its categories are a prefix of the encoded URL value', async () => {
    const { navigate } = await setupMobile();

    await navigate(COMPARISON_ROUTE, { categories: serializeCategories(RNA_CATEGORY, TISSUE) });

    expect(link('RNA')).toHaveClass('active');
  });

  it('should not mark a sibling category link as active', async () => {
    const { navigate } = await setupMobile();

    await navigate(COMPARISON_ROUTE, { categories: serializeCategories(RNA_CATEGORY, TISSUE) });

    expect(link('Protein')).not.toHaveClass('active');
  });

  it('should keep the link active after the page appends more category levels', async () => {
    const { navigate } = await setupMobile();

    await navigate(COMPARISON_ROUTE, { categories: serializeCategories(PROTEIN_CATEGORY) });
    expect(link('Protein')).toHaveClass('active');

    await navigate(COMPARISON_ROUTE, { categories: serializeCategories(PROTEIN_CATEGORY, TISSUE) });
    expect(link('Protein')).toHaveClass('active');
  });

  it('should match single-value query params exactly', async () => {
    const { navigate } = await setupMobile();

    await navigate(COMPARISON_ROUTE, { tab: 'overview' });
    expect(link('Overview')).toHaveClass('active');

    await navigate(COMPARISON_ROUTE, { tab: 'details' });
    expect(link('Overview')).not.toHaveClass('active');
  });

  it('should mark a link without query params as active whenever its path is active', async () => {
    const { navigate } = await setupMobile();

    await navigate(COMPARISON_ROUTE, { tab: 'details' });

    expect(link('NoParams')).toHaveClass('active');
  });

  it('should not mark any link as active when a different route is active', async () => {
    const { navigate } = await setupMobile();

    await navigate(ROUTE_WITHOUT_LINKS, { categories: serializeCategories(RNA_CATEGORY) });

    expect(link('RNA')).not.toHaveClass('active');
    expect(link('Protein')).not.toHaveClass('active');
    expect(link('Overview')).not.toHaveClass('active');
    expect(link('NoParams')).not.toHaveClass('active');
  });
});
