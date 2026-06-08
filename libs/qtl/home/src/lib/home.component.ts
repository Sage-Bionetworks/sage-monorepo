import { Component } from '@angular/core';
import { Chiclet, StatCardData } from '@sagebionetworks/explorers/models';
import {
  ChicletCardComponent,
  LinkBarComponent,
  StatCardsComponent,
} from '@sagebionetworks/explorers/ui';
import { ROUTE_PATHS } from '@sagebionetworks/qtl/config';

@Component({
  selector: 'qtl-home',
  imports: [LinkBarComponent, ChicletCardComponent, StatCardsComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  chiclets: Chiclet[] = [
    { text: 'PAK1', backgroundColor: 'var(--color-chiclet-purple)' },
    { text: 'chr1:109.8Mb', backgroundColor: 'var(--color-chiclet-blue)' },
    { text: 'rs1801133', backgroundColor: 'var(--color-chiclet-green)' },
    { text: 'rs6265', backgroundColor: 'var(--color-chiclet-green)' },
    { text: 'APOE', backgroundColor: 'var(--color-chiclet-purple)' },
    { text: 'chrX:73.5Mb', backgroundColor: 'var(--color-chiclet-green)' },
    { text: 'GATA3, FADS1, SLC30A8', backgroundColor: 'var(--color-chiclet-purple)' },
    { text: 'chr2:164.3Mb', backgroundColor: 'var(--color-chiclet-blue)' },
    { text: 'chr11:58.4Mb', backgroundColor: 'var(--color-chiclet-blue)' },
    { text: 'rs38090111, rs48102218', backgroundColor: 'var(--color-chiclet-green)' },
    { text: 'DRD2', backgroundColor: 'var(--color-chiclet-purple)' },
  ];

  statCards: StatCardData[] = [
    {
      iconPath: 'explorers-assets/icons/people.svg',
      iconAltText: 'people',
      iconColor: 'white',
      iconBackgroundColor: 'var(--color-action-primary)',
      header: '4000',
      subHeader: 'Brain Donors',
      // TODO: update stat card link (QTL-110)
      link: `/${ROUTE_PATHS.NOT_FOUND}`,
    },
    {
      iconPath: 'explorers-assets/icons/cell.svg',
      iconAltText: 'cell',
      iconColor: 'white',
      iconBackgroundColor: 'var(--color-accent-green-600)',
      header: '81',
      subHeader: 'Cell Types',
      // TODO: update stat card link (QTL-110)
      link: `/${ROUTE_PATHS.NOT_FOUND}`,
    },
    {
      iconPath: 'explorers-assets/icons/gene-search.svg',
      iconAltText: 'gene search',
      header: '3',
      subHeader: 'Ancestries',
      iconBackgroundColor: 'var(--color-browser-exon)',
      iconColor: 'white',
      // TODO: update stat card link (QTL-110)
      link: `/${ROUTE_PATHS.NOT_FOUND}`,
    },
    {
      iconPath: 'explorers-assets/icons/erythrocytes.svg',
      iconAltText: 'erythrocytes',
      iconColor: 'white',
      iconBackgroundColor: 'var(--color-accent-lavender-600)',
      header: '2 million',
      subHeader: 'Single Cells',
      // TODO: update stat card link (QTL-110)
      link: `/${ROUTE_PATHS.NOT_FOUND}`,
    },
    {
      iconPath: 'explorers-assets/icons/eqtl.svg',
      iconAltText: 'eQTL',
      iconColor: 'white',
      iconBackgroundColor: 'var(--color-gold)',
      header: '252 million',
      subHeader: 'eQTLs',
      // TODO: update stat card link (QTL-110)
      link: `/${ROUTE_PATHS.ABOUT}`,
    },
  ];
}
