import { Component, computed, input } from '@angular/core';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'model-ad-model-details-omics',
  imports: [ResourceCardsComponent],
  templateUrl: './model-details-omics.component.html',
  styleUrls: ['./model-details-omics.component.scss'],
})
export class ModelDetailsOmicsComponent {
  modelName = input.required<string>();

  cards = computed(() => [
    {
      imagePath: '/model-ad-assets/images/gene-expression.svg',
      description: 'View Gene Expression results for this model in the comparison tool.',
      title: 'Gene Expression',
      link: `/comparison/expression?model=${this.modelName()}`,
    },
    {
      imagePath: '/model-ad-assets/images/disease-correlation.svg',
      description: 'View Disease Correlation results for this model in the comparison tool.',
      title: 'Disease Correlation',
      link: `/comparison/correlation?model=${this.modelName()}`,
    },
  ]);
}
