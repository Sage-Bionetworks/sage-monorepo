import { Component, computed, input } from '@angular/core';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { Model } from '@sagebionetworks/model-ad/api-client-angular';

@Component({
  selector: 'model-ad-model-details-resources',
  imports: [ResourceCardsComponent],
  templateUrl: './model-details-resources.component.html',
  styleUrls: ['./model-details-resources.component.scss'],
})
export class ModelDetailsResourcesComponent {
  model = input.required<Model>();

  modelSpecificResourceCards = computed(() => {
    const cards = [
      {
        imagePath: '/model-ad-assets/images/ad-knowledge-portal-logo.svg',
        description:
          "Explore all of the data and metadata that's available for this model in the AD Knowledge Portal.",
        link: `https://adknowledgeportal.synapse.org/Explore/Studies/DetailsPage/StudyDetails?Study=${this.model().study_synid}`,
      },
      {
        imagePath: '/model-ad-assets/images/alzforum-logo.svg',
        description: 'Visit Alzforum to find more information about this model.',
        link: `https://www.alzforum.org/research-models/${this.model().alzforum_id}`,
      },
      {
        imagePath: '/model-ad-assets/images/jax-logo.svg',
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

  additionalResourceCards = [
    {
      imagePath: '/model-ad-assets/images/agora-logo.svg',
      description: 'View evidence about the role of human genes in AD.',
      link: 'https://agora.adknowledgeportal.org/',
    },
    {
      imagePath: '/model-ad-assets/images/allen-institute-logo.svg',
      description: 'Explore mouse brain resources in the Allen Brain Atlas.',
      link: 'https://mouse.brain-map.org/ ',
    },
    {
      imagePath: '/model-ad-assets/images/model-ad-logo.svg',
      description: 'Learn about the MODEL-AD program.',
      link: 'https://www.model-ad.org/',
    },
    {
      imagePath: '/model-ad-assets/images/mgi-logo.svg',
      description:
        'Search Mouse Genome Informatics for detailed information about mouse genes, alleles, and more.',
      link: 'https://www.informatics.jax.org/',
    },
    {
      imagePath: '/model-ad-assets/images/stop-ad-compound-portal-logo.svg',
      description:
        'Nominate a test compound for preclinical screening by the MODEL-AD Preclinical Testing Core.',
      link: 'https://stopadportal.synapse.org/',
    },
  ];
}
