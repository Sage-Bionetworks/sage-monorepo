import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Team } from '@sagebionetworks/agora/models';
import { TeamMemberListComponent } from '../team-member-list/team-member-list.component';

@Component({
  selector: 'agora-team-list',
  standalone: true,
  imports: [CommonModule, TeamMemberListComponent],
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
    return team.description ? team.description.replace('‚Äô', '&quot;') : '';
  }
}
