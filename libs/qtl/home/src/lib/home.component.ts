import { Component, input } from '@angular/core';
import { StatCardData } from '@sagebionetworks/explorers/models';
import { LinkBarComponent, StatCardsComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'qtl-home',
  imports: [LinkBarComponent, StatCardsComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  statCards = input<StatCardData[]>([]);
}
