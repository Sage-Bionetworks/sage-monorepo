import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { Team, TeamMember, TeamMemberService } from '@sagebionetworks/agora/api-client-angular';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'agora-team-member-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './team-member-list.component.html',
  styleUrls: ['./team-member-list.component.scss'],
  providers: [TeamMemberService],
})
export class TeamMemberListComponent {
  teamMemberService = inject(TeamMemberService);

  _team: Team = {} as Team;
  get team(): Team {
    return this._team;
  }
  @Input() set team(team: Team) {
    this.init(team);
  }

  images: { [key: string]: string } = {};

  init(team: Team) {
    if (!team.members) {
      return;
    }

    this.images = {};

    team.members.forEach((member: TeamMember) => {
      const name = member.name.toLowerCase().replace(/[- ]/g, '-');
      this.getTeamMemberImageUrl(name).subscribe((url) => {
        if (!url) {
          return;
        }
        this.images[member.name] = url;
      });
    });

    this.sort(team.members);
    this._team = team;
  }

  getTeamMemberImageUrl(name: string): Observable<string | undefined> {
    return this.teamMemberService.getTeamMemberImage(name).pipe(
      map((buffer) => {
        if (!buffer || buffer.size <= 0) {
          return;
        }
        return URL.createObjectURL(
          new Blob([buffer], {
            type: 'image/jpg, image/png, image/jpeg',
          }),
        );
      }),
    );
  }

  sort(members: TeamMember[]) {
    members.sort((a, b) => {
      if (a.isPrimaryInvestigator === b.isPrimaryInvestigator) {
        return a.name > b.name ? 1 : -1;
      } else if (a.isPrimaryInvestigator > b.isPrimaryInvestigator) {
        return -1;
      }
      return 1;
    });
  }

  getBackgroundImage(name: string) {
    return this.images[name];
  }
}
