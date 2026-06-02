import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Team, TeamService } from '@sagebionetworks/agora/api-client';
import { of } from 'rxjs';
import { TeamListComponent } from './team-list.component';

const memberA = { name: 'Alice Smith', isprimaryinvestigator: true };
const memberB = { name: 'Bob Jones', isprimaryinvestigator: false };

const team1: Team = {
  team: 'team-1',
  team_full: 'Team One',
  program: 'Prog',
  description: 'Desc',
  members: [memberA],
};

const team2: Team = {
  team: 'team-2',
  team_full: 'Team Two',
  program: '',
  description: '',
  members: [memberB],
};

describe('TeamListComponent', () => {
  let component: TeamListComponent;
  let fixture: ComponentFixture<TeamListComponent>;
  let getTeamMemberImageSpy: jest.SpyInstance;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamListComponent],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(TeamListComponent);
    component = fixture.componentInstance;

    const teamService = TestBed.inject(TeamService);
    getTeamMemberImageSpy = jest
      .spyOn(teamService, 'getTeamMemberImage')
      .mockReturnValue(of(new Blob()) as never);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('teams input', () => {
    it('should set _teams', () => {
      component.teams = [team1, team2];
      expect(component._teams).toEqual([team1, team2]);
    });

    it('should reset images when a new teams value arrives', () => {
      component.teams = [team1];
      component.imagesByTeam = { 'team-1': { 'Alice Smith': 'blob:old' } };

      component.teams = [team2];

      expect(component.imagesByTeam['team-1']).toBeUndefined();
    });

    it('should store images per team so members with the same name across teams do not collide', () => {
      const sharedName = { name: 'Alice Smith', isprimaryinvestigator: false };
      const teamA: Team = {
        team: 'team-a',
        team_full: 'Team A',
        program: '',
        description: '',
        members: [sharedName],
      };
      const teamB: Team = {
        team: 'team-b',
        team_full: 'Team B',
        program: '',
        description: '',
        members: [sharedName],
      };

      const blobA = new Blob(['a'], { type: 'image/png' });
      const blobB = new Blob(['b'], { type: 'image/png' });
      getTeamMemberImageSpy
        .mockReturnValueOnce(of(blobA) as never)
        .mockReturnValueOnce(of(blobB) as never);

      let callCount = 0;
      URL.createObjectURL = jest.fn(() => `blob:mock-${++callCount}`);

      component.teams = [teamA, teamB];

      expect(component.imagesByTeam['team-a']?.['Alice Smith']).toBe('blob:mock-1');
      expect(component.imagesByTeam['team-b']?.['Alice Smith']).toBe('blob:mock-2');
    });

    it('should call getTeamMemberImage once per member across all teams', () => {
      component.teams = [team1, team2];

      expect(getTeamMemberImageSpy).toHaveBeenCalledTimes(2);
      expect(getTeamMemberImageSpy).toHaveBeenCalledWith('alice-smith');
      expect(getTeamMemberImageSpy).toHaveBeenCalledWith('bob-jones');
    });
  });
});
