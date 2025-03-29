import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { Team, TeamMember, TeamsService } from '@sagebionetworks/agora/api-client-angular';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'agora-team-member-list',
  imports: [CommonModule],
  templateUrl: './team-member-list.component.html',
  styleUrls: ['./team-member-list.component.scss'],
})
export class TeamMemberListComponent {
  teamsService = inject(TeamsService);

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
    return this.teamsService.getTeamMemberImage(name).pipe(
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
      if (a.isprimaryinvestigator !== b.isprimaryinvestigator) {
        return a.isprimaryinvestigator ? -1 : 1; // true values come first
      }

      return a.name.localeCompare(b.name);
    });
  }

  getBackgroundImage(name: string) {
    return this.images[name];
  }
}
