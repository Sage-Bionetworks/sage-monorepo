import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Team, TeamService, TeamsList } from '@sagebionetworks/agora/api-client-angular';
import { HelperService } from '@sagebionetworks/agora/services';
import { catchError, finalize, map, Observable, of } from 'rxjs';
import { TeamListComponent } from './team-list/team-list.component';

@Component({
  selector: 'agora-teams',
  imports: [CommonModule, TeamListComponent],
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss'],
})
export class TeamsComponent implements OnInit {
  private destroyRef = inject(DestroyRef);

  helperService = inject(HelperService);
  teamService = inject(TeamService);

  teams$!: Observable<Team[]>;

  ngOnInit() {
    this.loadTeams();
  }

  loadTeams() {
    this.helperService.setLoading(true);

    this.teams$ = this.teamService.listTeams().pipe(
      takeUntilDestroyed(this.destroyRef),
      map((res: TeamsList) => res.items || []),
      catchError((error: Error) => {
        console.error('Error loading teams:', error.message);
        return of([]);
      }),
      finalize(() => this.helperService.setLoading(false)),
    );
  }
}
