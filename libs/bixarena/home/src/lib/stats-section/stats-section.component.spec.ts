import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import {
  PublicStats,
  StatsService,
  UserService,
  UserStats,
} from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService } from '@sagebionetworks/bixarena/services';
import { StatsSectionComponent } from './stats-section.component';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8111' } },
    battle: { promptLengthLimit: 5000 },
    links: { termsOfService: '' },
  },
};

describe('StatsSectionComponent', () => {
  let fixture: ComponentFixture<StatsSectionComponent>;
  let auth: AuthService;
  let getPublicSpy: jest.Mock;
  let getUserSpy: jest.Mock;

  async function setup(
    publicStats: PublicStats | 'error',
    userStats: UserStats | null = null,
  ): Promise<void> {
    getPublicSpy = jest
      .fn()
      .mockReturnValue(
        publicStats === 'error' ? throwError(() => new Error('boom')) : of(publicStats),
      );
    getUserSpy = jest.fn().mockReturnValue(of(userStats));

    await TestBed.configureTestingModule({
      imports: [StatsSectionComponent],
      providers: [
        provideHttpClient(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
        { provide: StatsService, useValue: { getPublicStats: getPublicSpy } },
        { provide: UserService, useValue: { getUserStats: getUserSpy } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StatsSectionComponent);
    auth = TestBed.inject(AuthService);
    fixture.detectChanges();
  }

  it('renders three public stat tiles when unauthenticated', async () => {
    await setup({ completedBattles: 2847, modelsEvaluated: 24, totalUsers: 312 });
    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelectorAll('.stat')).toHaveLength(3);
    expect(root.querySelector('.user-stat')).toBeNull();
  });

  it('does not call getUserStats when unauthenticated', async () => {
    await setup({ completedBattles: 1, modelsEvaluated: 1, totalUsers: 1 });
    expect(getUserSpy).not.toHaveBeenCalled();
  });

  it('renders five tiles (3 public + 2 authed) when authenticated', async () => {
    await setup(
      { completedBattles: 2847, modelsEvaluated: 24, totalUsers: 312 },
      { totalBattles: 12, completedBattles: 12, activeBattles: 0, rank: 47 },
    );
    auth.user.set({ sub: 'u1', preferred_username: 'tester' });
    fixture.detectChanges();

    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelectorAll('.stat')).toHaveLength(5);
    const userStats = Array.from(root.querySelectorAll<HTMLElement>('.user-stat'));
    expect(userStats).toHaveLength(2);
    expect(userStats[0].querySelector('.stat-val')?.textContent?.trim()).toBe('12');
    expect(userStats[0].querySelector('.stat-lbl')?.textContent?.trim()).toBe('Battles Completed');
    expect(userStats[1].querySelector('.stat-val')?.textContent?.trim()).toBe('#47');
    expect(userStats[1].querySelector('.stat-lbl')?.textContent?.trim()).toBe('Your Rank');
    expect(getUserSpy).toHaveBeenCalledTimes(1);
  });

  it('removes the authed tiles when the user logs out mid-session', async () => {
    await setup(
      { completedBattles: 1, modelsEvaluated: 1, totalUsers: 1 },
      { totalBattles: 1, completedBattles: 1, activeBattles: 0, rank: 1 },
    );
    auth.user.set({ sub: 'u1', preferred_username: 'tester' });
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('.user-stat')).toHaveLength(2);

    auth.user.set(null);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('.user-stat')).toBeNull();
  });

  it('omits the stats grid when the public API fails', async () => {
    await setup('error');
    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('.stats')).toBeNull();
  });
});
