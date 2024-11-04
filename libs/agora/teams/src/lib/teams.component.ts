import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Team, TeamsList, TeamsService } from '@sagebionetworks/agora/api-client-angular';
import { HelperService } from '@sagebionetworks/agora/services';
import { TeamListComponent } from './team-list/team-list.component';
import { catchError, finalize, map, Observable, of } from 'rxjs';

@Component({
  selector: 'agora-teams',
  standalone: true,
  imports: [CommonModule, TeamListComponent],
  providers: [HelperService, TeamsService],
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss'],
})
export class TeamsComponent implements OnInit {
  teams$!: Observable<Team[]>;

  constructor(
    private helperService: HelperService,
    private teamsService: TeamsService,
  ) {}

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
