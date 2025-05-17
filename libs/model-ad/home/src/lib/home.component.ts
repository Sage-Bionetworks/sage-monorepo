import { Component } from '@angular/core';
import { CardComponent, SvgImageComponent } from '@sagebionetworks/explorers/ui';
import {
  checkQueryForErrors,
  getSearchResultsList,
  navigateToResult,
} from '@sagebionetworks/model-ad/util';

interface Stat {
  label: string;
  value: string;
}

@Component({
  selector: 'model-ad-home',
  imports: [CardComponent, SvgImageComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  navigateToResult = navigateToResult;
  getSearchResultsList = getSearchResultsList;
  checkQueryForErrors = checkQueryForErrors;

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
