// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { provideHttpClient } from '@angular/common/http';
import { TeamService } from '@sagebionetworks/agora/api-client';
import { geneMock1, teamsResponseMock } from '@sagebionetworks/agora/testing';
import { of } from 'rxjs';
import { GeneNominationsComponent } from './gene-nominations.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Nominations', () => {
  let fixture: ComponentFixture<GeneNominationsComponent>;
  let component: GeneNominationsComponent;

  const mockTeamsService = {
    listTeams: jest.fn(() => of(teamsResponseMock)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: TeamService, useValue: mockTeamsService }, provideHttpClient()],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneNominationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call mock TeamService', () => {
    component.gene = geneMock1;
    component.init();
    fixture.detectChanges();

    expect(mockTeamsService.listTeams).toHaveBeenCalled();
  });

  it('should get full display name', () => {
    component.gene = geneMock1;
    component.init();
    fixture.detectChanges();

    const nomination = component.nominations[0];
    const result = component.getFullDisplayName(nomination);
    expect(result).toBe('AMP-AD: Emory University');
  });

  it('should sort nominations alphabetically then by date desc', () => {
    component.gene = geneMock1;
    component.init();
    fixture.detectChanges();

    const teamsList = teamsResponseMock.items ?? [];
    const result = component.sortNominations(teamsList);

    expect(result.length).toBe(5);

    expect(component.getFullDisplayName(result[0])).toBe('AMP-AD: Emory University');
    expect(component.getFullDisplayName(result[1])).toBe(
      'AMP-AD: Icahn School of Medicine at Mount Sinai',
    );
    expect(component.getFullDisplayName(result[2])).toBe(
      'AMP-AD: Icahn School of Medicine at Mount Sinai',
    );
    expect(component.getFullDisplayName(result[3])).toBe(
      'Community Contributed: The Chang Lab at the University of Arizona',
    );
    expect(component.getFullDisplayName(result[4])).toBe(
      'TREAT-AD: Emory University - Sage Bionetworks - Structural Genomics Consortium',
    );
    expect(result[1].initial_nomination).toBe(2020);
    expect(result[2].initial_nomination).toBe(2018);
  });
});
