import { Component } from '@angular/core';
import { HomeCardComponent, SvgImageComponent } from '@sagebionetworks/explorers/ui';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SearchInputComponent } from '@sagebionetworks/model-ad/ui';

interface Stat {
  label: string;
  value: string;
}

@Component({
  selector: 'model-ad-home',
  imports: [HomeCardComponent, SvgImageComponent, SearchInputComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  ROUTE_PATHS = ROUTE_PATHS;

  stats: Stat[] = [
    {
      label: 'Institutions',
      value: '5+',
    },
    {
      label: 'Genes',
      value: '20K+',
    },
    {
      label: 'Models',
      value: '15+',
    },
  ];
}
