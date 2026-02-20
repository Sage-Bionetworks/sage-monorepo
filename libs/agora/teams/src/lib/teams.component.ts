import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, DestroyRef, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Team, TeamService, TeamsList } from '@sagebionetworks/agora/api-client';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH } from '@sagebionetworks/agora/config';
import { HelperService } from '@sagebionetworks/agora/services';
import { ErrorOverlayService, LoggerService } from '@sagebionetworks/explorers/services';
import { catchError, finalize, map, Observable, of } from 'rxjs';
import { TeamListComponent } from './team-list/team-list.component';

@Component({
  selector: 'agora-teams',
  imports: [CommonModule, TeamListComponent],
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss'],
})
export class TeamsComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly logger = inject(LoggerService);
  private readonly errorOverlayService = inject(ErrorOverlayService);

  helperService = inject(HelperService);
  teamService = inject(TeamService);

  teams$!: Observable<Team[]>;

  readonly heroBackgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;

  ngOnInit() {
    this.loadTeams();
  }

  loadTeams() {
    if (isPlatformBrowser(this.platformId)) {
      this.helperService.setLoading(true);
      this.logger.log('TeamsComponent: Loading teams');

      this.teams$ = this.teamService.listTeams().pipe(
        takeUntilDestroyed(this.destroyRef),
        map((res: TeamsList) => res.items || []),
        catchError(() => {
          this.errorOverlayService.showError('Failed to load teams. Please try again later.');
          return of([]);
        }),
        finalize(() => this.helperService.setLoading(false)),
      );
    }
  }
}
