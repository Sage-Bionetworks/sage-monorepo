import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GeneSearchComponent } from '@sagebionetworks/agora/genes';
import { NavigationLink } from '@sagebionetworks/agora/models';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'agora-header',
  imports: [CommonModule, RouterModule, GeneSearchComponent, SvgImageComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
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
      url: 'https://help.adknowledgeportal.org/apd/Agora-Resources.2646671361.html',
      target: '_blank',
    },
    {
      label: 'Terms of Service',
      url: 'https://s3.amazonaws.com/static.synapse.org/governance/SageBionetworksSynapseTermsandConditionsofUse.pdf?v=5',
      target: '_blank',
    },
  ];

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
