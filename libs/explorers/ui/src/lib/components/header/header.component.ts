import { CommonModule } from '@angular/common';
import { Component, input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-header',
  imports: [CommonModule, SvgImageComponent, RouterModule],
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

  ngOnInit() {
    this.onResize();
  }

  refreshNavItems() {
    // mobile will combine header and footer links
    // desktop will only show header links
    this.links = this.isMobile
      ? [...this.headerLinks(), ...this.footerLinks()]
      : [...this.headerLinks()];
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
}
