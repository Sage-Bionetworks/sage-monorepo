import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import { RouterModule } from '@angular/router';
import { SvgImageComponent } from '../svg-image/svg-image.component';
import { toKebabCase } from '@sagebionetworks/explorers/util';
@Component({
  selector: 'explorers-header',
  imports: [CommonModule, SvgImageComponent, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  @Input() headerLogoPath = '';
  @Input() headerLinks: NavigationLink[] = [];
  @Input() footerLinks: NavigationLink[] = [];

  isMobile = false;
  isShown = false;

  links: NavigationLink[] = [];

  ngOnInit() {
    this.onResize();
  }

  refreshNavItems() {
    // mobile will combine header and footer links
    // desktop will only show header links
    this.links = this.isMobile ? [...this.headerLinks, ...this.footerLinks] : [...this.headerLinks];
  }

  onResize() {
    if (typeof window !== 'undefined') this.isMobile = window.innerWidth < 1320;
    this.refreshNavItems();
  }

  toggleNav() {
    this.isShown = !this.isShown;
  }

  formatTestId(s: string) {
    return toKebabCase(s);
  }
}
