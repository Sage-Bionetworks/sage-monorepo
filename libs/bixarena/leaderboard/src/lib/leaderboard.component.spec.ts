import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { LeaderboardService as LeaderboardApiService } from '@sagebionetworks/bixarena/api-client';
import { of } from 'rxjs';
import { LeaderboardComponent } from './leaderboard.component';

describe('LeaderboardComponent', () => {
  let component: LeaderboardComponent;
  let fixture: ComponentFixture<LeaderboardComponent>;

  beforeEach(async () => {
    const apiStub = {
      getLeaderboard: jest.fn().mockReturnValue(
        of({
          number: 0,
          size: 0,
          totalElements: 0,
          totalPages: 0,
          hasNext: false,
          hasPrevious: false,
          updatedAt: new Date().toISOString(),
          snapshotId: 'snap-1',
          entries: [],
        }),
      ),
      listLeaderboards: jest.fn().mockReturnValue(of([])),
    };

    await TestBed.configureTestingModule({
      imports: [LeaderboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: LeaderboardApiService, useValue: apiStub },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the hero title', () => {
    const title = (fixture.nativeElement as HTMLElement).querySelector('.title');
    expect(title?.textContent).toContain('Leader');
  });
});
