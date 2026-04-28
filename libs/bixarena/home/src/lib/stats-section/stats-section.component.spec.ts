import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { PublicStats, StatsService } from '@sagebionetworks/bixarena/api-client';
import { StatsSectionComponent } from './stats-section.component';

describe('StatsSectionComponent', () => {
  let fixture: ComponentFixture<StatsSectionComponent>;
  let getSpy: jest.Mock;

  async function setup(stats: PublicStats | 'error') {
    getSpy = jest
      .fn()
      .mockReturnValue(stats === 'error' ? throwError(() => new Error('boom')) : of(stats));
    await TestBed.configureTestingModule({
      imports: [StatsSectionComponent],
      providers: [{ provide: StatsService, useValue: { getPublicStats: getSpy } }],
    }).compileComponents();
    fixture = TestBed.createComponent(StatsSectionComponent);
    fixture.detectChanges();
  }

  it('renders three stat tiles with formatted numbers when the API resolves', async () => {
    await setup({ completedBattles: 2847, modelsEvaluated: 24, totalUsers: 312 });
    const root = fixture.nativeElement as HTMLElement;
    const tiles = Array.from(root.querySelectorAll('.stat'));
    expect(tiles).toHaveLength(3);
    const values = tiles.map((t) => t.querySelector('.stat-val')?.textContent?.trim());
    const labels = tiles.map((t) => t.querySelector('.stat-lbl')?.textContent?.trim());
    expect(values).toEqual(['24', '2,847', '312']);
    expect(labels).toEqual(['Models Evaluated', 'Total Battles', 'Total Users']);
  });

  it('renders the disclaimer line', async () => {
    await setup({ completedBattles: 1, modelsEvaluated: 1, totalUsers: 1 });
    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('.disclaimer')?.textContent?.trim()).toBe(
      'Only battles on biomedical topics count toward stats and rankings.',
    );
  });

  it('omits the stats grid but keeps the disclaimer when the API fails', async () => {
    await setup('error');
    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('.stats')).toBeNull();
    expect(root.querySelector('.disclaimer')).toBeTruthy();
  });
});
