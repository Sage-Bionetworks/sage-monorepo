import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ResourceCardComponent } from '../resource-card/resource-card.component';

@Component({
  selector: 'explorers-resource-cards',
  imports: [CommonModule, ResourceCardComponent],
  templateUrl: './resource-cards.component.html',
  styleUrls: ['./resource-cards.component.scss'],
})
export class ResourceCardsComponent {
  @Input({ required: true }) cards: Partial<ResourceCardComponent>[] = [];
}
