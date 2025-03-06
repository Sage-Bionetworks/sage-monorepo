// team-service.mock.ts
import { Injectable } from '@angular/core';
import { Team, TeamsService } from '@sagebionetworks/agora/api-client-angular';

@Injectable()
export class MockTeamService extends TeamsService {
  getMembers(): Promise<Team> {
    return Promise.resolve({
      team: 'Test Team',
      team_full: 'Test Team Full',
      program: 'Test Program',
      description: 'Test Description',
      members: [
        { name: 'John Doe', isprimaryinvestigator: true },
        { name: 'Jane Smith', isprimaryinvestigator: false },
        { name: 'Alice Johnson', isprimaryinvestigator: true },
      ],
    });
  }
}
