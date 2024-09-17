// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { TeamService } from '.';
import { teamsResponseMock } from '@sagebionetworks/agora/testing';
import { TeamList } from '@sagebionetworks/agora/api-client-angular';
import { provideHttpClient } from '@angular/common/http';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('TeamService', () => {
  let teamService: TeamService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [TeamService, provideHttpClient(), provideHttpClientTesting()],
    });

    teamService = TestBed.inject(TeamService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(teamService).toBeDefined();
  });

  it('should get all teams', () => {
    const mockResponse: TeamList = teamsResponseMock;
    let teams: TeamList | undefined;
    teamService.getTeams().subscribe((response) => {
      teams = response;
    });

    const req = httpMock.expectOne('http://localhost:3333/v1/teams');

    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
    expect(teams).toEqual(teamsResponseMock);
  });
});
