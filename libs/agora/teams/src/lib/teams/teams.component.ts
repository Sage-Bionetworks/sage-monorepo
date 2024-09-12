import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Team, TeamsResponse } from '@sagebionetworks/agora/models';
import { TeamService } from '../../services';
import { HelperService } from '@sagebionetworks/agora/services';
import { TeamListComponent } from '../team-list/team-list.component';

@Component({
  selector: 'agora-teams',
  standalone: true,
  imports: [CommonModule, TeamListComponent],
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss'],
  providers: [HelperService, TeamService],
})
export class TeamsComponent implements OnInit {
  teams: Team[] = [];

  constructor(
    private helperService: HelperService,
    private teamService: TeamService,
  ) {}

  ngOnInit() {
    this.helperService.setLoading(true);
    this.teamService.getTeams().subscribe(
      (res: TeamsResponse) => {
        this.teams = res.items;
      },
      (err: Error) => {
        console.log('Error loading teams: ' + err.message);
      },
      () => {
        this.helperService.setLoading(false);
      },
    );
  }
}
