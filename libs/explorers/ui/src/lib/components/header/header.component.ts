import { CommonModule } from '@angular/common';
import { Component, inject, input, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import { MenuItem } from 'primeng/api';
import { MenuModule } from 'primeng/menu';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-header',
  imports: [CommonModule, SvgImageComponent, RouterModule, MenuModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  headerLogoPath = input('');
  headerLinks = input<NavigationLink[]>([]);
  footerLinks = input<NavigationLink[]>([]);
  minDesktopWidth = input(1300);

  isMobile = false;
  isShown = false;

  links: NavigationLink[] = [];
  dropdownMenuItems: Map<string, MenuItem[]> = new Map();

  private router = inject(Router);

  ngOnInit() {
    this.onResize();
  }

  refreshNavItems() {
    const headerLinks = this.headerLinks();
    const footerLinks = this.footerLinks();

    this.validateHeaderLinks(headerLinks);

    if (this.isMobile) {
      this.links = [...headerLinks, ...footerLinks];
    } else {
      this.links = [...headerLinks];
      this.buildDropdownMenuItems(headerLinks);
    }
  }

  onResize() {
    if (typeof window !== 'undefined') this.isMobile = window.innerWidth < this.minDesktopWidth();
    // Reset menu state when in desktop mode to prevent state leaking across mode transitions
    if (!this.isMobile) {
      this.isShown = false;
    }
    this.refreshNavItems();
  }

  toggleNav() {
    // Menu only visible to be toggled in mobile mode
    if (this.isMobile) {
      this.isShown = !this.isShown;
    }
  }

  getDropdownItems(link: NavigationLink): MenuItem[] {
    return this.dropdownMenuItems.get(link.label) || [];
  }

  isDropdownActive(link: NavigationLink): boolean {
    if (!link.children) return false;
    return link.children.some((child) => {
      const candidates = child.isSubheader && child.children ? child.children : [child];
      return candidates.some((c) =>
        c.routerLink
          ? this.router.isActive(c.routerLink.join('/'), {
              paths: 'subset',
              queryParams: 'ignored',
              fragment: 'ignored',
              matrixParams: 'ignored',
            })
          : false,
      );
    });
  }

  private validateHeaderLinks(links: NavigationLink[]) {
    for (const link of links) {
      if (!link.children) continue;
      const hasSubheaders = link.children.some((c) => c.isSubheader);
      const hasFlat = link.children.some((c) => !c.isSubheader);
      // PrimeNG renders all top-level MenuItem entries as group headers when any entry has nested
      // `items`, making flat siblings non-clickable. Mixed layouts are therefore not supported.
      if (hasSubheaders && hasFlat) {
        throw new Error(
          'HeaderComponent: mixing subheader and flat children in the same dropdown is not supported. All children should be either subheaders or flat links.',
        );
      }
    }
  }

  private buildDropdownMenuItems(links: NavigationLink[]) {
    this.dropdownMenuItems.clear();
    for (const link of links) {
      if (link.children) {
        this.dropdownMenuItems.set(link.label, this.toMenuItems(link.children));
      }
    }
  }

  private toMenuItems(children: NavigationLink[]): MenuItem[] {
    const items: MenuItem[] = [];
    for (const child of children) {
      if (child.isSubheader) {
        if (child.children?.length) {
          items.push({
            label: child.label,
            items: child.children.map((grandchild) => ({
              label: grandchild.label,
              routerLink: grandchild.routerLink,
              styleClass: 'header-dropdown-subheader-child',
            })),
          });
        }
        // isSubheader with no children is a no-op
      } else {
        items.push({ label: child.label, routerLink: child.routerLink });
      }
    }
    return items;
  }
}
