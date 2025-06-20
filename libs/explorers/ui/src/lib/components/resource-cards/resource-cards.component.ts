
import { Component, input } from '@angular/core';
import { ResourceCardComponent } from '../resource-card/resource-card.component';

@Component({
  selector: 'explorers-resource-cards',
  imports: [ResourceCardComponent],
  templateUrl: './resource-cards.component.html',
  styleUrls: ['./resource-cards.component.scss'],
})
export class ResourceCardsComponent {
  cards = input.required<Partial<ResourceCardComponent>[]>();
}
