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

const EXACT_PATH_MATCH_OPTIONS: IsActiveMatchOptions = {
  ...PATH_MATCH_OPTIONS,
  paths: 'exact',
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
   * A link is active when its route path matches and, for every query param the link declares, the
   * URL's values for that param begin with the link's values. This keeps a link active when the page
   * appends extra values -- e.g. link `categories=RNA` matches URL `categories=RNA,Tissue`, but not
   * `categories=Protein`.
   *
   * A link with no query params is intentionally active whenever its path matches, ignoring the URL's
   * query string -- e.g. the param-less Home link highlights on the home route regardless of any
   * query string. A consequence: if two `navigation-links.ts` entries share the same `routerLink` and
   * one declares query params while the other does not, then whenever the param-bearing entry matches,
   * the param-less entry matches too -- so this method returns true for both, and both are
   * highlighted. No two entries share a routerLink this way today, but keep it in mind if a param-less
   * entry is ever added next to a param-bearing one.
   */
  isLinkActive(link: NavigationLink): boolean {
    if (!link.routerLink) return false;
    if (!this.router.isActive(link.routerLink.join('/'), this.resolvePathMatchOptions(link)))
      return false;

    // What the link declares, e.g. { categories: 'RNA - DIFFERENTIAL EXPRESSION' }.
    const linkParams = link.queryParams;
    if (!linkParams) return true;

    // What the current URL carries, e.g. { categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION,Tissue%20-%20Hemibrain' }.
    const urlParams = this.router.parseUrl(this.router.url).queryParams;
    return Object.keys(linkParams).every((key) =>
      this.urlValuesStartWith(urlParams[key], linkParams[key]),
    );
  }

  /**
   * Whether the URL's values for a query param begin with the values a link declares. The link stays
   * active when the page adds more values than the link lists (e.g. link ['RNA'] matches URL
   * ['RNA', 'Tissue']), but not when they differ or the link lists more values than the URL has.
   *
   * The link value is raw literal text, so we only wrap it in an array -- never split or decode it.
   * The URL value is comma-joined and percent-encoded, so we parse it back into its list of values.
   */
  private urlValuesStartWith(urlValue: unknown, linkValue: unknown): boolean {
    const linkValues = Array.isArray(linkValue) ? linkValue.map(String) : [String(linkValue)];
    const urlValues = parseCommaSeparatedQueryParam(
      urlValue as string | string[] | null | undefined,
    );

    // Every value the link declares must equal the URL value at the same position:
    //   link ['RNA']          vs url ['RNA', 'Tissue'] -> true  (matches, extra url value ignored)
    //   link ['RNA']          vs url ['Protein']       -> false (position 0 differs)
    //   link ['RNA', 'Tissue'] vs url ['RNA']          -> false (url[1] is undefined, so it fails)
    return linkValues.every((value, index) => value === urlValues[index]);
  }

  // Query params are matched manually via urlValuesStartWith, so path matching always ignores them
  private resolvePathMatchOptions(link: NavigationLink): IsActiveMatchOptions {
    const activeOptions = link.activeOptions;
    if (!activeOptions) return PATH_MATCH_OPTIONS;
    if ('exact' in activeOptions) {
      return activeOptions.exact ? EXACT_PATH_MATCH_OPTIONS : PATH_MATCH_OPTIONS;
    }
    return { ...activeOptions, queryParams: 'ignored' };
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
