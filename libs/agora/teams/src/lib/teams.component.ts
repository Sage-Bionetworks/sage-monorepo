import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Team, TeamsList, TeamsService } from '@sagebionetworks/agora/api-client-angular';
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
  helperService = inject(HelperService);
  teamsService = inject(TeamsService);

  teams$!: Observable<Team[]>;

  ngOnInit() {
    this.loadTeams();
  }

  loadTeams() {
    this.helperService.setLoading(true);

    this.teams$ = this.teamsService.listTeams().pipe(
      map((res: TeamsList) => res.items || []),
      catchError((error: Error) => {
        console.error('Error loading teams:', error.message);
        return of([]);
      }),
      finalize(() => this.helperService.setLoading(false)),
    );
  }
}
