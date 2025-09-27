// team-service.mock.ts
import { Injectable } from '@angular/core';
import { Team, TeamService } from '@sagebionetworks/agora/api-client';

@Injectable()
export class MockTeamService extends TeamService {
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
