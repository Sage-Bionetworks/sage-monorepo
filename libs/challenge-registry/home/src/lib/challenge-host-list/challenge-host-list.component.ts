import { Component } from '@angular/core';
import { Organization } from '@sagebionetworks/challenge-registry/api-client-angular';
import { MOCK_ORGANIZATIONS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-host-list',
  templateUrl: './challenge-host-list.component.html',
  styleUrls: ['./challenge-host-list.component.scss'],
})
export class ChallengeHostListComponent {
  // constructor() {}
  // ngOnInit(): void {}
  organizations: Organization[] = MOCK_ORGANIZATIONS;
}
