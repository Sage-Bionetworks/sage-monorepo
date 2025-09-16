import { Component, computed, input } from '@angular/core';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { Model } from '@sagebionetworks/model-ad/api-client-angular';

@Component({
  selector: 'model-ad-model-details-omics',
  imports: [ResourceCardsComponent],
  templateUrl: './model-details-omics.component.html',
  styleUrls: ['./model-details-omics.component.scss'],
})
export class ModelDetailsOmicsComponent {
  model = input.required<Model>();

  cards = computed(() => {
    const cards = [];
    if (this.model().gene_expression) {
      cards.push({
        imagePath: '/model-ad-assets/images/gene-expression.svg',
        description: 'View Gene Expression results for this model in the comparison tool.',
        title: 'Gene Expression',
        link: `/${this.model().gene_expression}`,
      });
    }
    if (this.model().disease_correlation) {
      cards.push({
        imagePath: '/model-ad-assets/images/disease-correlation.svg',
        description: 'View Disease Correlation results for this model in the comparison tool.',
        title: 'Disease Correlation',
        link: `/${this.model().disease_correlation}`,
      });
    }
    return cards;
  });
}
