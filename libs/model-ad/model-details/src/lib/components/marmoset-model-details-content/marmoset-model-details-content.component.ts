import { Component, input, output } from '@angular/core';
import { Panel, ResourceCardData, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { MarmosetModel } from '@sagebionetworks/model-ad/api-client';
import { MarmosetModelDetailsBoxplotsSelectorComponent } from '../marmoset-model-details-boxplots-selector/marmoset-model-details-boxplots-selector.component';
import { ModelDetailsResourcesComponent } from '../model-details-resources/model-details-resources.component';

@Component({
  selector: 'model-ad-marmoset-model-details-content',
  imports: [
    PanelNavigationComponent,
    MarmosetModelDetailsBoxplotsSelectorComponent,
    ModelDetailsResourcesComponent,
  ],
  templateUrl: './marmoset-model-details-content.component.html',
  styleUrls: ['./marmoset-model-details-content.component.scss'],
})
export class MarmosetModelDetailsContentComponent {
  model = input.required<MarmosetModel>();
  panels = input.required<Panel[]>();
  activePanel = input.required<string>();
  activeParent = input.required<string>();
  scrollToPanelNavElementOnInitialLoad = input.required<boolean>();
  panelChange = output<Panel>();

  plasmaBiomarkersWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '641870' };

  additionalResourceCards: ResourceCardData[] = [
    {
      imagePath: 'explorers-assets/images/ad-knowledge-portal-logo.svg',
      description:
        "Explore all of the data and metadata that's available for this model in the AD Knowledge Portal.",
      link: 'https://adknowledgeportal.synapse.org/Explore/Studies/DetailsPage/StudyData?Study=syn61849889',
    },
    {
      imagePath: 'model-ad-assets/images/agora-logo.svg',
      description: 'View evidence about the role of human genes in AD.',
      link: 'https://agora.adknowledgeportal.org/',
    },
    {
      imagePath: 'explorers-assets/images/alzforum-logo.svg',
      description: 'Visit Alzforum to find more information about this model.',
      link: 'https://www.alzforum.org/',
    },
    {
      imagePath: 'model-ad-assets/images/marmo-ad-program-logo.svg',
      description: 'Learn about the MARMO-AD program.',
      link: 'https://www.marmo-ad.org/',
    },
    {
      imagePath: 'model-ad-assets/images/marmo-ad-tissue-biobank-logo.svg',
      description: 'Request samples from the MARMO-AD tissue biobank.',
      link: 'https://docs.google.com/forms/d/e/1FAIpQLScIWCeArdycKuPBwNXcv12Z_83rHUTqYzFeFZc7kSC3Hsp0ZQ/viewform',
    },
    {
      imagePath: 'model-ad-assets/images/marmo-hub-logo.svg',
      description: 'Visit Marmohub for to find marmoset resources and events.',
      link: 'https://www.marmohub.org/',
    },
    {
      imagePath: 'model-ad-assets/images/marmoset-coordinating-center-logo.svg',
      description: 'Find information about using marmosets in neuroscience research.',
      link: 'https://mcc.ohsu.edu/index.html',
    },
    {
      imagePath: 'model-ad-assets/images/pad-logo.svg',
      description: 'Explore the marmoset genome using the UCSC genome browser.',
      link: 'https://primatedatabase.org/',
    },
  ];
}
