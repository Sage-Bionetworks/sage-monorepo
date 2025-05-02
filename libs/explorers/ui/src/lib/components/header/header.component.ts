import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { NavigationLink } from '@sagebionetworks/explorers/models';
import { RouterModule } from '@angular/router';
import { SvgImageComponent } from '../svg-image/svg-image.component';
@Component({
  selector: 'explorers-header',
  imports: [CommonModule, SvgImageComponent, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  @Input() headerLogoPath = '';
  @Input() defaultNavItems: NavigationLink[] = [];
  @Input() mobileNavItems: NavigationLink[] = [];

  isMobile = false;
  isShown = false;

  navItems: NavigationLink[] = [];

  ngOnInit() {
    this.onResize();
  }

  refreshNavItems() {
    this.navItems = this.isMobile
      ? [...this.defaultNavItems, ...this.mobileNavItems]
      : [...this.defaultNavItems];
  }

  onResize() {
    if (typeof window !== 'undefined') this.isMobile = window.innerWidth < 1320;
    this.refreshNavItems();
  }

  toggleNav() {
    this.isShown = !this.isShown;
  }
}
