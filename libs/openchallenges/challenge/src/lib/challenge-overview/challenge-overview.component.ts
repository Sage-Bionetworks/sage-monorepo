import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input() challenge!: Challenge;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;
  // mockTopics = ['breast', 'cancer'];

  use_default(str: string) {
    return str === '' ? 'Not available' : str;
  }

  prettify(camel: string | undefined) {
    return camel
      ? camel.charAt(0).toUpperCase() +
          camel.slice(1).replace(/_/g, ' ').toLowerCase()
      : undefined;
  }
}
