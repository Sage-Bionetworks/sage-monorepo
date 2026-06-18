import { IsActiveMatchOptions } from '@angular/router';

export type NavigationLink = {
  label: string;
  url?: string;
  target?: '_blank' | '_self' | '_parent' | '_top';
  routerLink?: string[];
  activeOptions?: { exact: boolean } | IsActiveMatchOptions;
  isSubheader?: boolean;
  // Mixing subheader and flat children in the same dropdown is not supported.
  // All children should be either subheaders (each with their own children) or flat links.
  children?: NavigationLink[];
};
