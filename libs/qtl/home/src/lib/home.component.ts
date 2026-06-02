import { Component, input } from '@angular/core';
import { Chiclet, StatCardData } from '@sagebionetworks/explorers/models';
import {
  ChicletCardComponent,
  LinkBarComponent,
  StatCardsComponent,
} from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'qtl-home',
  imports: [LinkBarComponent, ChicletCardComponent, StatCardsComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  chiclets: Chiclet[] = [
    { text: 'PAK1', backgroundColor: '#9D67D6' },
    { text: 'chr1:109.8Mb', backgroundColor: '#3f51b5' },
    { text: 'rs1801133', backgroundColor: '#1AA582' },
    { text: 'rs6265', backgroundColor: '#1AA582' },
    { text: 'APOE', backgroundColor: '#9D67D6' },
    { text: 'chrX:73.5Mb', backgroundColor: '#1AA582' },
    { text: 'GATA3, FADS1, SLC30A8', backgroundColor: '#9D67D6' },
    { text: 'chr2:164.3Mb', backgroundColor: '#3f51b5' },
    { text: 'chr2:164.3Mb', backgroundColor: '#3f51b5' },
    { text: 'rs38090111, rs48102218', backgroundColor: '#1AA582' },
    { text: 'DRD2', backgroundColor: '#9D67D6' },
  ];

  statCards = input<StatCardData[]>([]);
}
