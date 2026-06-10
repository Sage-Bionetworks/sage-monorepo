import { Component, computed, input } from '@angular/core';
import { StatCardData } from '@sagebionetworks/explorers/models';
import { StatCardComponent } from '../stat-card/stat-card.component';

@Component({
  selector: 'explorers-stat-cards',
  imports: [StatCardComponent],
  templateUrl: './stat-cards.component.html',
  styleUrls: ['./stat-cards.component.scss'],
  host: {
    '[style.--stat-card-count]': 'cardCount()',
  },
})
export class StatCardsComponent {
  cards = input.required<StatCardData[]>();
  animateOnLoad = input<boolean>(false);
  cardCount = computed(() => this.cards().length);
}
