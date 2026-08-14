import { Component, computed, input, output } from '@angular/core';
import { Panel, ResourceCardData, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { ModelDetailsOmicsComponent } from '../model-details-omics/model-details-omics.component';
import { ModelDetailsResourcesComponent } from '../model-details-resources/model-details-resources.component';
import { MouseModelDetailsBoxplotsSelectorComponent } from '../mouse-model-details-boxplots-selector/mouse-model-details-boxplots-selector.component';
import { MouseModelDetailsHeroComponent } from '../mouse-model-details-hero/mouse-model-details-hero.component';

@Component({
  selector: 'model-ad-mouse-model-details-content',
  imports: [
    PanelNavigationComponent,
    ModelDetailsOmicsComponent,
    ModelDetailsResourcesComponent,
    MouseModelDetailsBoxplotsSelectorComponent,
    MouseModelDetailsHeroComponent,
  ],
  templateUrl: './mouse-model-details-content.component.html',
  styleUrls: ['./mouse-model-details-content.component.scss'],
})
export class MouseModelDetailsContentComponent {
  model = input.required<MouseModel>();
  panels = input.required<Panel[]>();
  activePanel = input.required<string>();
  activeParent = input.required<string>();
  scrollToPanelNavElementOnInitialLoad = input.required<boolean>();
  panelChange = output<Panel>();

  biomarkersWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '632871' };
  pathologyWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '632872' };

  modelSpecificResourceCards = computed<ResourceCardData[]>(() => {
    const cards = [
      {
        imagePath: 'explorers-assets/images/ad-knowledge-portal-logo.svg',
        description:
          "Explore all of the data and metadata that's available for this model in the AD Knowledge Portal.",
        link: `https://adknowledgeportal.synapse.org/Explore/Studies/DetailsPage/StudyDetails?Study=${this.model().study_synid}`,
      },
      {
        imagePath: 'explorers-assets/images/alzforum-logo.svg',
        description: 'Visit Alzforum to find more information about this model.',
        link: `https://www.alzforum.org/research-models/${this.model().alzforum_id}`,
      },
      {
        imagePath: 'model-ad-assets/images/jax-logo.svg',
        description: 'View detailed information about this AD model on JAX.',
        link: `https://www.jax.org/strain/${this.model().jax_id}`,
      },
    ];

    return cards.filter((card) => {
      // hide alzforum resource card when alzforum_id is empty
      if (card.link.includes('www.alzforum.org')) {
        return this.model().alzforum_id !== '';
      }
      return true;
    });
  });

  additionalResourceCards: ResourceCardData[] = [
    {
      imagePath: 'model-ad-assets/images/agora-logo.svg',
      description: 'View evidence about the role of human genes in AD.',
      link: 'https://agora.adknowledgeportal.org/',
    },
    {
      imagePath: 'model-ad-assets/images/allen-institute-logo.svg',
      description: 'Explore mouse brain resources in the Allen Brain Atlas.',
      link: 'https://mouse.brain-map.org/',
    },
    {
      imagePath: 'model-ad-assets/images/model-ad-logo.svg',
      description: 'Learn about the MODEL-AD program.',
      link: 'https://www.model-ad.org/',
    },
    {
      imagePath: 'model-ad-assets/images/mgi-logo.svg',
      description:
        'Search Mouse Genome Informatics for detailed information about mouse genes, alleles, and more.',
      link: 'https://www.informatics.jax.org/',
    },
    {
      imagePath: 'model-ad-assets/images/stop-ad-compound-portal-logo.svg',
      description:
        'Nominate a test compound for preclinical screening by the MODEL-AD Preclinical Testing Core.',
      link: 'https://stopadportal.synapse.org/',
    },
  ];
}
