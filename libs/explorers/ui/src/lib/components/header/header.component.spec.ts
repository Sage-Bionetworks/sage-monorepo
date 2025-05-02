import { render } from '@testing-library/angular';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './header.component';
import { CommonModule } from '@angular/common';
import { SvgImageComponent } from '@sagebionetworks/explorers/ui';
import { NavigationLink } from '@sagebionetworks/explorers/models';

function changeWindowSize(width: number) {
  Object.defineProperty(window, 'innerWidth', {
    writable: true,
    configurable: true,
    value: width,
  });
}

const mockDefaultNavItems: NavigationLink[] = [
  {
    label: 'Home',
    routerLink: [''],
    activeOptions: { exact: true },
  },
  {
    label: 'Model Overview',
    routerLink: ['model-overview'],
  },
  {
    label: 'Gene Expression',
    routerLink: ['gene-expression'],
  },
  {
    label: 'Disease Correlation',
    routerLink: ['disease-correlation'],
  },
];
const mockMobileNavItems: NavigationLink[] = [
  {
    label: 'About',
    routerLink: ['about'],
  },
  {
    label: 'Help',
    url: 'https://help.adknowledgeportal.org/apd/Agora-Help.2663088129.html',
    target: '_blank',
  },
  {
    label: 'News',
    routerLink: ['news'],
  },
  {
    label: 'Terms of Service',
    url: 'https://s3.amazonaws.com/static.synapse.org/governance/SageBionetworksSynapseTermsandConditionsofUse.pdf?v=5',
    target: '_blank',
  },
];

describe('HeaderComponent', () => {
  const setup = async () => {
    return render(HeaderComponent, {
      imports: [CommonModule, RouterModule, SvgImageComponent],
    });
  };

  it('should create', async () => {
    const { container } = await setup();
    expect(container).toBeTruthy();
  });

  it('should combine default and mobile nav items when in mobile view', async () => {
    const { fixture } = await setup();
    const component = fixture.componentInstance;
    component.defaultNavItems = mockDefaultNavItems;
    component.mobileNavItems = mockMobileNavItems;

    // Mock window width for mobile view
    changeWindowSize(1000);

    component.onResize();

    const totalItems = component.defaultNavItems.length + component.mobileNavItems.length;
    expect(component.navItems.length).toBe(totalItems);
    expect(component.navItems).toEqual([...component.defaultNavItems, ...component.mobileNavItems]);

    // Verify mobile-specific items are present
    expect(component.navItems.find((item) => item.label === 'About')).toBeTruthy();
    expect(component.navItems.find((item) => item.label === 'Help')).toBeTruthy();
    expect(component.navItems.find((item) => item.label === 'News')).toBeTruthy();
    expect(component.navItems.find((item) => item.label === 'Terms of Service')).toBeTruthy();
  });

  it('should show only default nav items in desktop view', async () => {
    const { fixture } = await setup();
    const component = fixture.componentInstance;
    component.defaultNavItems = mockDefaultNavItems;
    component.mobileNavItems = mockMobileNavItems;

    // Mock window width for desktop view
    changeWindowSize(1400);

    component.onResize();

    expect(component.navItems.length).toBe(component.defaultNavItems.length);
    expect(component.navItems).toEqual(component.defaultNavItems);
    expect(component.navItems.length).toBeGreaterThan(0);

    // Verify mobile items are not present
    expect(component.navItems.find((item) => item.label === 'About')).toBeFalsy();
    expect(component.navItems.find((item) => item.label === 'Help')).toBeFalsy();
    expect(component.navItems.find((item) => item.label === 'News')).toBeFalsy();
    expect(component.navItems.find((item) => item.label === 'Terms of Service')).toBeFalsy();
  });

  it('should toggle navigation visibility', async () => {
    const { fixture } = await setup();
    const component = fixture.componentInstance;
    component.defaultNavItems = mockDefaultNavItems;
    component.mobileNavItems = mockMobileNavItems;

    expect(component.isShown).toBe(false);

    component.toggleNav();
    expect(component.isShown).toBe(true);

    component.toggleNav();
    expect(component.isShown).toBe(false);
  });

  it('should handle resize events and update navigation accordingly', async () => {
    const { fixture } = await setup();
    const component = fixture.componentInstance;
    component.defaultNavItems = mockDefaultNavItems;
    component.mobileNavItems = mockMobileNavItems;

    // Start with desktop view
    changeWindowSize(1400);

    component.onResize();
    expect(component.isMobile).toBe(false);
    expect(component.navItems.length).toBe(component.defaultNavItems.length);

    // Switch to mobile view
    changeWindowSize(1000);

    component.onResize();
    expect(component.isMobile).toBe(true);
    expect(component.navItems.length).toBe(
      component.defaultNavItems.length + component.mobileNavItems.length,
    );
  });
});
