import { Component, inject, Input } from '@angular/core';

import { Gene, Team, TeamsService } from '@sagebionetworks/agora/api-client-angular';
import { TargetNominationWithTeamData } from '../../models/TargetNominationWithTeamData';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'agora-gene-nominations',
  standalone: true,
  imports: [CommonModule],
  providers: [TeamsService],
  templateUrl: './gene-nominations.component.html',
  styleUrls: ['./gene-nominations.component.scss'],
})
export class GeneNominationsComponent {
  teamService = inject(TeamsService);

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }

  nominations: TargetNominationWithTeamData[] = [];
  loading = true;

  reset() {
    this.nominations = [];
  }

  init() {
    this.reset();

    if (!this._gene?.target_nominations?.length) {
      return;
    }

    this.teamService.listTeams().subscribe((response) => {
      if (response.items) {
        this.nominations = this.sortNominations(response.items);
      }
    });
  }

  sortNominations(teams: Team[]) {
    const result: TargetNominationWithTeamData[] = [];
    if (!this.gene || !this.gene.target_nominations) return result;

    // add team_data to nominations
    this.nominations = this.gene.target_nominations.map((targetNomination) => {
      const extendedTargetNomination: TargetNominationWithTeamData = { ...targetNomination };
      extendedTargetNomination.team_data = teams.find((t) => t.team === targetNomination.team);
      return extendedTargetNomination;
    });

    return this.gene.target_nominations.sort((a, b) => {
      //primary sort on displayed team name
      const teamA = this.getFullDisplayName(a);
      const teamB = this.getFullDisplayName(b);

      const nameComparison = teamA.localeCompare(teamB, 'en');
      if (nameComparison !== 0) return nameComparison;

      //secondary sort on initial nomination year (descending)
      return b.initial_nomination - a.initial_nomination;
    }) as TargetNominationWithTeamData[];
  }

  getFullDisplayName(nomination: TargetNominationWithTeamData): string {
    const team = nomination.team_data;
    if (!team) return '';

    return (team.program ? team.program + ': ' : '') + team.team_full;
  }
}
