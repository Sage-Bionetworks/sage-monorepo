import { Component, Input } from '@angular/core';
import { TeamMemberListComponent } from '../team-member-list/team-member-list.component';
import { Team } from '@sagebionetworks/agora/api-client';

@Component({
  selector: 'agora-team-list',
  imports: [TeamMemberListComponent],
  templateUrl: './team-list.component.html',
  styleUrls: ['./team-list.component.scss'],
  providers: [],
})
export class TeamListComponent {
  _teams: Team[] = [];
  get teams(): Team[] {
    return this._teams;
  }

  @Input() set teams(teams: Team[]) {
    this._teams = teams;
  }

  getFullName(team: Team): string {
    return team.program ? team.program + ': ' + team.team_full : team.team_full;
  }

  getDescription(team: Team): string {
    return team.description ? team.description.replace(/‚Äô/g, '&quot;') : '';
  }
}
