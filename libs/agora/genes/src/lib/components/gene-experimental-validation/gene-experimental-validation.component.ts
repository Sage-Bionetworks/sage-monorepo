import { Component, DestroyRef, inject, Input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { Gene, TeamService } from '@sagebionetworks/agora/api-client';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { ExperimentalValidationWithTeamData } from '../../models';

@Component({
  selector: 'agora-gene-experimental-validation',
  imports: [],
  templateUrl: './gene-experimental-validation.component.html',
  styleUrls: ['./gene-experimental-validation.component.scss'],
})
export class ExperimentalValidationComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly logger = inject(LoggerService);

  teamService = inject(TeamService);

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }

  experimentalValidationWithTeamData: ExperimentalValidationWithTeamData[] = [];

  init() {
    this.logger.log('ExperimentalValidationComponent: Loading teams for validation data');

    this.teamService
      .listTeams()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        if (
          !this.gene ||
          !this.gene.experimental_validation ||
          !this.gene.experimental_validation.length
        ) {
          return;
        }

        const teams = response.items;
        if (teams) {
          this.experimentalValidationWithTeamData = this.gene.experimental_validation.map(
            (item) => {
              const extendedItem: ExperimentalValidationWithTeamData = { ...item };
              extendedItem.team_data = teams.find((t) => t.team === item.team);
              return extendedItem;
            },
          );
        }
      });
  }
}
