import { Component, DestroyRef, inject, Input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Team, TeamMember, TeamService } from '@sagebionetworks/agora/api-client';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { map, Observable } from 'rxjs';
import { TeamCardComponent } from '../team-card/team-card.component';

@Component({
  selector: 'agora-team-list',
  imports: [TeamCardComponent],
  templateUrl: './team-list.component.html',
  styleUrls: ['./team-list.component.scss'],
})
export class TeamListComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly logger = inject(LoggerService);
  private readonly teamService = inject(TeamService);

  _teams: Team[] = [];
  imagesByTeam: Partial<Record<string, Record<string, string>>> = {};

  @Input() set teams(teams: Team[]) {
    this._teams = teams;
    this.imagesByTeam = {};
    teams.forEach((team) => {
      (team.members ?? []).forEach((member: TeamMember) => {
        const name = member.name.toLowerCase().replace(/[- ]/g, '-');
        this.logger.log(`TeamListComponent: Loading image for ${name}`);

        this.getTeamMemberImageUrl(name)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe((url) => {
            if (!url) return;
            const prev = this.imagesByTeam[team.team] ?? {};
            this.imagesByTeam = {
              ...this.imagesByTeam,
              [team.team]: { ...prev, [member.name]: url },
            };
          });
      });
    });
  }

  private getTeamMemberImageUrl(name: string): Observable<string | undefined> {
    return this.teamService.getTeamMemberImage(name).pipe(
      map((buffer) => {
        if (!buffer || buffer.size <= 0) return undefined;
        return URL.createObjectURL(
          new Blob([buffer], { type: 'image/jpg, image/png, image/jpeg' }),
        );
      }),
    );
  }
}
