import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-challenge-overview',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input({ required: true }) challenge!: Challenge;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;
  // mockTopics = ['breast', 'cancer'];

  use_default(str: string | null | undefined) {
    return str === '' || str == null ? 'Not available' : str;
  }

  prettify(camel: string | undefined) {
    return camel
      ? camel.charAt(0).toUpperCase() +
          camel.slice(1).replace(/_/g, ' ').toLowerCase()
      : undefined;
  }
}
