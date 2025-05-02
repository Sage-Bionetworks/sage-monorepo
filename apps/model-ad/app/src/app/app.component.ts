import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/model-ad/config';
import { HeaderComponent } from '@sagebionetworks/explorers/ui';
import {
  CONFIG_SERVICE_TOKEN,
  GoogleTagManagerComponent,
  createGoogleTagManagerIdProvider,
  isGoogleTagManagerIdSet,
} from '@sagebionetworks/shared/google-tag-manager';
import { NavigationLink } from '@sagebionetworks/explorers/models';

@Component({
  imports: [RouterModule, HeaderComponent, GoogleTagManagerComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: CONFIG_SERVICE_TOKEN,
      useFactory: () => inject(ConfigService),
    },
    createGoogleTagManagerIdProvider(),
  ],
})
export class AppComponent {
  configService = inject(ConfigService);

  readonly useGoogleTagManager: boolean;

  defaultNavItems: Array<NavigationLink> = [
    {
      label: 'Home',
      routerLink: [''],
      activeOptions: { exact: true },
    },
    {
      label: 'Model Overview',
      routerLink: ['model-overview'],
    },
    {
      label: 'Gene Expression',
      routerLink: ['gene-expression'],
    },
    {
      label: 'Disease Correlation',
      routerLink: ['disease-correlation'],
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
      label: 'News',
      routerLink: ['news'],
    },
    {
      label: 'Terms of Service',
      url: 'https://s3.amazonaws.com/static.synapse.org/governance/SageBionetworksSynapseTermsandConditionsofUse.pdf?v=5',
      target: '_blank',
    },
  ];

  constructor() {
    this.useGoogleTagManager = isGoogleTagManagerIdSet(
      this.configService.config.googleTagManagerId,
    );
  }
}
