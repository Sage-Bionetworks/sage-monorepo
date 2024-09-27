import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Dataversion, DataversionService } from '@sagebionetworks/agora/api-client-angular';
import { Observable } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { PathSanitizer } from '@sagebionetworks/agora/util';
import { ConfigService } from '@sagebionetworks/agora/config';
import { NavigationLink } from '../../models/navigation-link';
import { inject } from '@angular/core';

@Component({
  selector: 'agora-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  configService = inject(ConfigService);
  dataVersionService = inject(DataversionService);
  sanitizer = inject(PathSanitizer);

  headerLogoPath!: SafeUrl;
  dataVersion$!: Observable<Dataversion>;

  isMobile = false;
  isShown = false;

  navItems: Array<NavigationLink> = [];
  defaultNavItems: Array<NavigationLink> = [
    {
      label: 'Home',
      routerLink: [''],
      activeOptions: { exact: true },
    },
    {
      label: 'Gene Comparison',
      routerLink: ['genes/comparison'],
    },
    {
      label: 'Nominated Targets',
      routerLink: ['genes/nominated-targets'],
    },
    {
      label: 'Teams',
      routerLink: ['teams'],
    },
    {
      label: 'News',
      routerLink: ['news'],
    },
  ];
  mobileNavItems: Array<NavigationLink> = [
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
      label: 'Terms of Service',
      url: 'https://s3.amazonaws.com/static.synapse.org/governance/SageBionetworksSynapseTermsandConditionsofUse.pdf?v=5',
      target: '_blank',
    },
  ];

  constructor() {
    this.headerLogoPath = this.sanitizer.sanitize('/agora-assets/images/header-logo.svg');
  }

  ngOnInit() {
    this.onResize();
  }

  refreshNavItems() {
    this.navItems = this.isMobile
      ? [...this.defaultNavItems, ...this.mobileNavItems]
      : [...this.defaultNavItems];
  }

  onResize() {
    this.isMobile = window.innerWidth < 1320;
    this.refreshNavItems();
  }

  toggleNav() {
    this.isShown = !this.isShown;
  }
}
