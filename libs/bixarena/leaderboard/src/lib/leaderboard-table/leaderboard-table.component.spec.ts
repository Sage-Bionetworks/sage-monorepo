import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LeaderboardEntry } from '@sagebionetworks/bixarena/api-client';
import { LeaderboardTableComponent } from './leaderboard-table.component';

function buildEntry(overrides: Partial<LeaderboardEntry> = {}): LeaderboardEntry {
  return {
    id: 'entry-1',
    modelId: 'test-model',
    modelName: 'Test Model',
    modelOrganization: null,
    modelUrl: 'https://example.com',
    license: 'mit',
    btScore: 1000,
    voteCount: 100,
    rank: 5,
    bootstrapQ025: 950,
    bootstrapQ975: 1050,
    createdAt: '2026-01-01T00:00:00Z',
    ...overrides,
  };
}

describe('LeaderboardTableComponent — Diff column', () => {
  let fixture: ComponentFixture<LeaderboardTableComponent>;

  function diffCellText(): string {
    const cell = (fixture.nativeElement as HTMLElement).querySelector('td.col-diff .diff');
    return cell?.textContent?.replace(/\s+/g, '').trim() ?? '';
  }

  function diffCellClass(): string {
    const cell = (fixture.nativeElement as HTMLElement).querySelector('td.col-diff .diff');
    return cell?.className ?? '';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaderboardTableComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(LeaderboardTableComponent);
  });

  it('renders ▲N for positive rankDelta', () => {
    fixture.componentRef.setInput('entries', [buildEntry({ rankDelta: 3 })]);
    fixture.detectChanges();
    expect(diffCellText()).toBe('▲3');
    expect(diffCellClass()).toContain('diff-up');
  });

  it('renders ▼N (positive magnitude) for negative rankDelta', () => {
    fixture.componentRef.setInput('entries', [buildEntry({ rankDelta: -2 })]);
    fixture.detectChanges();
    expect(diffCellText()).toBe('▼2');
    expect(diffCellClass()).toContain('diff-down');
  });

  it('renders dash for zero rankDelta', () => {
    fixture.componentRef.setInput('entries', [buildEntry({ rankDelta: 0 })]);
    fixture.detectChanges();
    expect(diffCellText()).toBe('—');
    expect(diffCellClass()).toContain('diff-flat');
  });

  it('renders dash when rankDelta is null (no comparison available)', () => {
    fixture.componentRef.setInput('entries', [buildEntry({ rankDelta: null })]);
    fixture.detectChanges();
    expect(diffCellText()).toBe('—');
    expect(diffCellClass()).toContain('diff-flat');
  });

  it('renders dash when rankDelta is omitted', () => {
    fixture.componentRef.setInput('entries', [buildEntry()]);
    fixture.detectChanges();
    expect(diffCellText()).toBe('—');
    expect(diffCellClass()).toContain('diff-flat');
  });
});
