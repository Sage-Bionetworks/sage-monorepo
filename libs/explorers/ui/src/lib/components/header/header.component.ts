import { CommonModule } from '@angular/common';
import { Component, inject, input, OnInit } from '@angular/core';
import { IsActiveMatchOptions, Router, RouterModule } from '@angular/router';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import { WidestLineWidthDirective } from '@sagebionetworks/explorers/util';
import { parseCommaSeparatedQueryParam } from '@sagebionetworks/shared/util';
import { MenuItem } from 'primeng/api';
import { MenuModule } from 'primeng/menu';
import { SvgImageComponent } from '../svg-image/svg-image.component';

const PATH_MATCH_OPTIONS: IsActiveMatchOptions = {
  paths: 'subset',
  queryParams: 'ignored',
  fragment: 'ignored',
  matrixParams: 'ignored',
};

@Component({
  selector: 'explorers-header',
  imports: [CommonModule, SvgImageComponent, RouterModule, MenuModule, WidestLineWidthDirective],
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
        c.routerLink ? this.router.isActive(c.routerLink.join('/'), PATH_MATCH_OPTIONS) : false,
      );
    });
  }

  /**
   * A link is active when its path matches and every query param it declares is a leading subset of
   * the same param in the URL. `routerLinkActive` can't express this: it compares whole raw param
   * strings, so a link declaring one value never matches a URL carrying that value plus the ones
   * the page filled in, and its comparison is also blind to percent-encoding differences.
   */
  isLinkActive(link: NavigationLink): boolean {
    if (!link.routerLink) return false;
    if (!this.router.isActive(link.routerLink.join('/'), PATH_MATCH_OPTIONS)) return false;

    const linkParams = link.queryParams;
    if (!linkParams) return true;

    const urlParams = this.router.parseUrl(this.router.url).queryParams;
    return Object.keys(linkParams).every((key) =>
      this.isValuePrefix(
        parseCommaSeparatedQueryParam(linkParams[key]),
        parseCommaSeparatedQueryParam(urlParams[key]),
      ),
    );
  }

  private isValuePrefix(linkValues: string[], urlValues: string[]): boolean {
    return linkValues.length <= urlValues.length && linkValues.every((v, i) => urlValues[i] === v);
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
              queryParams: grandchild.queryParams,
              styleClass: 'header-dropdown-subheader-child',
            })),
          });
        }
        // isSubheader with no children is a no-op
      } else {
        items.push({
          label: child.label,
          routerLink: child.routerLink,
          queryParams: child.queryParams,
        });
      }
    }
    return items;
  }
}
