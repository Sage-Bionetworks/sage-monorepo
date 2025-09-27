import { TeamListComponent } from './team-list.component';
import { Team } from '@sagebionetworks/agora/api-client';

describe('TeamListComponent', () => {
  let component: TeamListComponent;

  const mockTeam: Team = {
    team: 'Test Team',
    team_full: 'Test Team Full',
    program: 'Test Program',
    description: '‚ÄôHello, World!‚Äô',
    members: [],
  };

  beforeEach(() => {
    component = new TeamListComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('getFullName method', () => {
    it('should return program and team_full when program exists', () => {
      expect(component.getFullName(mockTeam)).toBe('Test Program: Test Team Full');
    });

    it('should return only team_full when program does not exist', () => {
      const mockTeamEmptyProgram: Team = {
        team: 'Test Team',
        team_full: 'Test Team Full',
        program: '',
        description: 'Test Description',
        members: [],
      };

      expect(component.getFullName(mockTeamEmptyProgram)).toBe('Test Team Full');
    });
  });

  describe('getDescription method', () => {
    it('should return description with replaced characters', () => {
      expect(component.getDescription(mockTeam)).toBe('&quot;Hello, World!&quot;');
    });

    it('should return empty string when description does not exist', () => {
      const mockTeamNoDescription: Team = {
        team: 'Test Team',
        team_full: 'Test Team Full',
        program: 'Test Program',
        description: '',
        members: [],
      };

      expect(component.getDescription(mockTeamNoDescription)).toBe('');
    });
  });
});
