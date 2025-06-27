import { Component } from '@angular/core';
import { HomeCardComponent, SvgImageComponent } from '@sagebionetworks/explorers/ui';
import {
  checkQueryForErrors,
  getSearchResultsList,
  navigateToResult,
  ROUTE_PATHS,
} from '@sagebionetworks/model-ad/util';

interface Stat {
  label: string;
  value: string;
}

@Component({
  selector: 'model-ad-home',
  imports: [HomeCardComponent, SvgImageComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  navigateToResult = navigateToResult;
  getSearchResultsList = getSearchResultsList;
  checkQueryForErrors = checkQueryForErrors;

  ROUTE_PATHS = ROUTE_PATHS;

  stats: Stat[] = [
    {
      label: 'Institutions',
      value: '5+',
    },
    {
      label: 'Genes',
      value: '30K+',
    },
    {
      label: 'Models',
      value: '15+',
    },
  ];
}
