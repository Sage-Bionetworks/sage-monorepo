import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Team, TeamMember } from '@sagebionetworks/agora/models';
import { TeamService } from '../../services';

@Component({
  selector: 'agora-team-member-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './team-member-list.component.html',
  styleUrls: ['./team-member-list.component.scss'],
  providers: [],
})
export class TeamMemberListComponent {
  _team: Team = {} as Team;
  get team(): Team {
    return this._team;
  }
  @Input() set team(team: Team) {
    this.init(team);
  }

  images: { [key: string]: string } = {};

  constructor(private teamService: TeamService) {}

  init(team: Team) {
    if (!team.members) {
      return;
    }

    this.images = {};

    team.members.forEach((member: TeamMember) => {
      this.teamService.getTeamMemberImageUrl(member.name).subscribe((url: string | undefined) => {
        if (!url) {
          return;
        }
        this.images[member.name] = url;
      });
    });

    this.sort(team.members);
    this._team = team;
  }

  sort(members: TeamMember[]) {
    members.sort((a, b) => {
      if (a.isprimaryinvestigator === b.isprimaryinvestigator) {
        return a.name > b.name ? 1 : -1;
      } else if (a.isprimaryinvestigator > b.isprimaryinvestigator) {
        return -1;
      }
      return 1;
    });
  }

  getBackgroundImage(name: string) {
    return this.images[name];
  }
}
