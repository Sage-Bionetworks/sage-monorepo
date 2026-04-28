import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LeaderboardEntry } from '@sagebionetworks/bixarena/api-client';
import { LEADERBOARD_BAR_CHART_TOP_N } from '../leaderboard.constants';
import { LeaderboardBarChartComponent } from './leaderboard-bar-chart.component';

function buildEntry(overrides: Partial<LeaderboardEntry> = {}): LeaderboardEntry {
  return {
    id: 'entry-1',
    modelId: 'test-model',
    modelName: 'Test Model',
    modelOrganization: null,
    modelUrl: 'https://example.com',
    license: 'open-source',
    btScore: 1000,
    voteCount: 100,
    rank: 1,
    bootstrapQ025: 950,
    bootstrapQ975: 1050,
    createdAt: '2026-01-01T00:00:00Z',
    ...overrides,
  };
}

function buildEntries(count: number): LeaderboardEntry[] {
  return Array.from({ length: count }, (_, i) =>
    buildEntry({
      id: `entry-${i + 1}`,
      modelId: `model-${i + 1}`,
      modelName: `Model ${i + 1}`,
      rank: i + 1,
      btScore: 1500 - i * 10,
    }),
  );
}

describe('LeaderboardBarChartComponent', () => {
  let fixture: ComponentFixture<LeaderboardBarChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaderboardBarChartComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(LeaderboardBarChartComponent);
  });

  it(`renders at most ${LEADERBOARD_BAR_CHART_TOP_N} bars when given more entries`, () => {
    fixture.componentRef.setInput('entries', buildEntries(25));
    fixture.detectChanges();
    const bars = (fixture.nativeElement as HTMLElement).querySelectorAll('.chart-bar');
    expect(bars.length).toBe(LEADERBOARD_BAR_CHART_TOP_N);
  });

  it('renders one bar per entry when fewer than top-N entries are given', () => {
    fixture.componentRef.setInput('entries', buildEntries(5));
    fixture.detectChanges();
    const bars = (fixture.nativeElement as HTMLElement).querySelectorAll('.chart-bar');
    expect(bars.length).toBe(5);
  });

  it('maps height linearly so the lowest bar sits at the floor and others scale proportionally', () => {
    fixture.componentRef.setInput('entries', buildEntries(3));
    fixture.detectChanges();
    const bars = fixture.componentInstance.bars();
    expect(bars[0].heightPct).toBe(100);
    expect(bars[bars.length - 1].heightPct).toBe(32);
    expect(bars[1].heightPct).toBeGreaterThan(32);
    expect(bars[1].heightPct).toBeLessThan(100);
  });

  it('uses brand gradient for top three and silver gradient for the rest', () => {
    fixture.componentRef.setInput('entries', buildEntries(5));
    fixture.detectChanges();
    const bars = fixture.componentInstance.bars();
    expect(bars[0].barGradient).toContain('--p-primary-300');
    expect(bars[2].barGradient).toContain('--p-primary-500');
    expect(bars[3].barGradient).toContain('--p-slate-300');
    expect(bars[4].barGradient).toContain('--p-slate-500');
  });

  it('exposes rank, model name, and score via aria-label', () => {
    fixture.componentRef.setInput('entries', [
      buildEntry({ rank: 3, modelName: 'GPT-X', btScore: 1234.6 }),
    ]);
    fixture.detectChanges();
    const bar = (fixture.nativeElement as HTMLElement).querySelector('.chart-bar');
    expect(bar?.getAttribute('aria-label')).toBe('Rank 3: GPT-X, score 1235');
  });
});
