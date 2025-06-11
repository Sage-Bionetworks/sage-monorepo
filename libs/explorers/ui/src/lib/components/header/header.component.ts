import { CommonModule } from '@angular/common';
import { Component, input, OnInit } from '@angular/core';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import { RouterModule } from '@angular/router';
import { SvgImageComponent } from '../svg-image/svg-image.component';
import { MIN_DESKTOP_WIDTH } from '@sagebionetworks/model-ad/util';

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
    if (typeof window !== 'undefined') this.isMobile = window.innerWidth < MIN_DESKTOP_WIDTH;
    this.refreshNavItems();
  }

  toggleNav() {
    this.isShown = !this.isShown;
  }
}
