import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TeamsComponent } from './teams.component';
import { HttpErrorResponse, HttpStatusCode, provideHttpClient } from '@angular/common/http';
import { TeamService } from '@sagebionetworks/agora/api-client';
import {
  HttpRequestError,
  PlatformService,
  SourceLogger,
} from '@sagebionetworks/explorers/services';
import { firstValueFrom, throwError } from 'rxjs';

describe('TeamListComponent', () => {
  let component: TeamsComponent;
  let fixture: ComponentFixture<TeamsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(TeamsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

describe('TeamsComponent when the teams request fails', () => {
  const teamsRequestError = new HttpRequestError(
    new HttpErrorResponse({
      status: HttpStatusCode.ServiceUnavailable,
      url: 'https://agora.adknowledgeportal.org/api/v1/teams',
    }),
    'GET',
  );

  afterEach(() => jest.restoreAllMocks());

  it('shows no teams and reports the failure', async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: TeamService,
          useValue: { listTeams: () => throwError(() => teamsRequestError) },
        },
        { provide: PlatformService, useValue: { isBrowser: true, isServer: false } },
      ],
    });
    const loggerError = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
    const component = TestBed.createComponent(TeamsComponent).componentInstance;

    component.loadTeams();

    await expect(firstValueFrom(component.teams$)).resolves.toEqual([]);
    expect(loggerError).toHaveBeenCalledWith(
      expect.stringContaining(teamsRequestError.message),
      expect.objectContaining({ error: teamsRequestError }),
    );
  });
});
