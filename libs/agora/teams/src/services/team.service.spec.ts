import { TestBed } from '@angular/core/testing';
import { TeamService } from '.';
import { provideHttpClient } from '@angular/common/http';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('TeamService', () => {
  let service: TeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [TeamService, provideHttpClient()],
    });

    service = TestBed.inject(TeamService);
  });

  it('should create', () => {
    expect(service).toBeDefined();
  });
});
