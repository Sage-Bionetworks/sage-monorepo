import { Component, Input } from '@angular/core';
import {
  ChallengeOrganizer,
  Organization,
} from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
// import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'challenge-registry-search-dropdown-filter',
  templateUrl: './search-dropdown-filter.component.html',
  styleUrls: ['./search-dropdown-filter.component.scss'],
})
export class SearchDropdownFilterComponent {
  @Input() values!: Organization[] | ChallengeOrganizer[];

  selectedValues!: Organization | ChallengeOrganizer;

  constructor() {
    this.values = [];
  }
}
