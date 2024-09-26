export type NavigationLink = {
  label: string;
  url?: string;
  target?: '_blank' | '_self' | '_parent' | '_top';
  routerLink?: string[];
};
