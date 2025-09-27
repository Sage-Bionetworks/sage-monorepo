import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Team, TeamMember } from '@sagebionetworks/agora/api-client';
import { TeamMemberListComponent } from './team-member-list.component';
import { provideHttpClient } from '@angular/common/http';

describe('TeamMemberListComponent', () => {
  let component: TeamMemberListComponent;
  let fixture: ComponentFixture<TeamMemberListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamMemberListComponent],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(TeamMemberListComponent);
    component = fixture.componentInstance;
  });

  describe('sort function', () => {
    it('should sort team members correctly', () => {
      const member1: TeamMember = { name: 'John Doe', isprimaryinvestigator: true };
      const member2: TeamMember = { name: 'Jane Smith', isprimaryinvestigator: false };
      const member3: TeamMember = { name: 'Alice Johnson', isprimaryinvestigator: true };

      const mockTeam: Team = {
        team: 'Test Team',
        team_full: 'Test Team Full',
        program: 'Test Program',
        description: 'Test Description',
        members: [member1, member2, member3],
      };

      component.sort(mockTeam.members);

      expect(mockTeam.members[0]).toEqual(member3);
      expect(mockTeam.members[1]).toEqual(member1);
      expect(mockTeam.members[2]).toEqual(member2);

      // Test case where primary investigators are both true
      const member4: TeamMember = { name: 'Bob Brown', isprimaryinvestigator: true };
      const member5: TeamMember = { name: 'Andy Anderson', isprimaryinvestigator: true };

      mockTeam.members = [member4, member5];

      component.sort(mockTeam.members);
      expect(mockTeam.members[0]).toEqual(member5);
      expect(mockTeam.members[1]).toEqual(member4);
    });
  });
});
