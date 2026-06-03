import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Team, TeamMember } from '@sagebionetworks/agora/api-client';
import { TeamCardComponent } from './team-card.component';

const mockTeam: Team = {
  team: 'test-team',
  team_full: 'Test Team Full',
  program: 'Test Program',
  description: '‚ÄôHello, World!‚Äô',
  members: [],
};

describe('TeamCardComponent', () => {
  let component: TeamCardComponent;
  let fixture: ComponentFixture<TeamCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TeamCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('team', mockTeam);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('title', () => {
    it('should combine program and team_full when program exists', () => {
      expect(component.title()).toBe('Test Program: Test Team Full');
    });

    it('should return only team_full when program is empty', () => {
      fixture.componentRef.setInput('team', { ...mockTeam, program: '' });
      expect(component.title()).toBe('Test Team Full');
    });
  });

  describe('description', () => {
    it('should replace mojibake characters', () => {
      expect(component.description()).toBe('&quot;Hello, World!&quot;');
    });

    it('should return empty string when description is empty', () => {
      fixture.componentRef.setInput('team', { ...mockTeam, description: '' });
      expect(component.description()).toBe('');
    });
  });

  describe('images', () => {
    it('should render an img element when an image is provided for a member', async () => {
      const member: TeamMember = { name: 'Alice Smith', isprimaryinvestigator: false };
      fixture.componentRef.setInput('team', { ...mockTeam, members: [member] });
      fixture.componentRef.setInput('images', { 'Alice Smith': 'blob:mock-url' });
      fixture.detectChanges();

      const img = fixture.nativeElement.querySelector('img');
      expect(img).toBeTruthy();
      expect(img.src).toContain('blob:mock-url');
      expect(img.alt).toBe('Alice Smith');
    });

    it('should not render an img element when no image is provided for a member', async () => {
      const member: TeamMember = { name: 'Alice Smith', isprimaryinvestigator: false };
      fixture.componentRef.setInput('team', { ...mockTeam, members: [member] });
      fixture.componentRef.setInput('images', {});
      fixture.detectChanges();

      const img = fixture.nativeElement.querySelector('img');
      expect(img).toBeNull();
    });
  });

  describe('sortedMembers', () => {
    it('should sort primary investigators before other members', () => {
      const pi: TeamMember = { name: 'John Doe', isprimaryinvestigator: true };
      const member: TeamMember = { name: 'Alice Smith', isprimaryinvestigator: false };
      fixture.componentRef.setInput('team', { ...mockTeam, members: [member, pi] });
      expect(component.sortedMembers()[0]).toEqual(pi);
      expect(component.sortedMembers()[1]).toEqual(member);
    });

    it('should sort alphabetically within the same investigator status', () => {
      const pi1: TeamMember = { name: 'Zara Jones', isprimaryinvestigator: true };
      const pi2: TeamMember = { name: 'Alice Brown', isprimaryinvestigator: true };
      fixture.componentRef.setInput('team', { ...mockTeam, members: [pi1, pi2] });
      expect(component.sortedMembers()[0]).toEqual(pi2);
      expect(component.sortedMembers()[1]).toEqual(pi1);
    });

    it('should return empty array when members is empty', () => {
      expect(component.sortedMembers()).toEqual([]);
    });
  });
});
