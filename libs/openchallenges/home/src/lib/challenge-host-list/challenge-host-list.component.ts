import { Component } from '@angular/core';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-host-list',
  templateUrl: './challenge-host-list.component.html',
  styleUrls: ['./challenge-host-list.component.scss'],
})
export class ChallengeHostListComponent {
  // constructor() {}
  // ngOnInit(): void {}
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;
}
