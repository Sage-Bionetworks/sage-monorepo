import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import {
  LeaderboardEntry,
  LeaderboardEntryPage,
  LeaderboardListInner,
  LeaderboardService,
} from '@sagebionetworks/bixarena/api-client';
import { LeaderboardSectionComponent } from './leaderboard-section.component';

function listInner(id: string, name: string, entryCount: number): Partial<LeaderboardListInner> {
  return {
    id,
    name,
    description: '',
    updatedAt: '2026-04-29T00:00:00Z',
    latestSnapshot: entryCount
      ? ({ entryCount } as LeaderboardListInner['latestSnapshot'])
      : undefined,
  };
}

function entry(id: string, modelName: string, rank: number): Partial<LeaderboardEntry> {
  return {
    id,
    modelId: `m-${id}`,
    modelName,
    modelOrganization: 'OpenAI',
    modelUrl: '',
    license: 'MIT',
    btScore: 1000 - rank * 10,
    voteCount: 100,
    rank,
    rankDelta: rank === 1 ? 2 : rank === 2 ? -1 : 0,
    bootstrapQ025: 950,
    bootstrapQ975: 1050,
    createdAt: '2026-04-29T00:00:00Z',
  };
}

function page(slug: string, voteCount: number, entries: Partial<LeaderboardEntry>[]) {
  return {
    number: 0,
    size: 3,
    totalElements: entries.length,
    totalPages: 1,
    hasNext: false,
    hasPrevious: false,
    updatedAt: '2026-04-29T00:00:00Z',
    snapshotId: `snap-${slug}`,
    entryCount: entries.length,
    voteCount,
    entries: entries as LeaderboardEntry[],
  } as LeaderboardEntryPage;
}

describe('LeaderboardSectionComponent', () => {
  let fixture: ComponentFixture<LeaderboardSectionComponent>;
  let component: LeaderboardSectionComponent;
  let listSpy: jest.Mock;
  let getSpy: jest.Mock;

  async function setup(opts: {
    list: Partial<LeaderboardListInner>[];
    pages?: Record<string, LeaderboardEntryPage>;
    listError?: boolean;
    getError?: boolean;
  }) {
    listSpy = jest
      .fn()
      .mockReturnValue(opts.listError ? throwError(() => new Error()) : of(opts.list));
    getSpy = jest.fn((slug: string) => {
      if (opts.getError) return throwError(() => new Error());
      const p = opts.pages?.[slug];
      return p ? of(p) : throwError(() => new Error('no page for ' + slug));
    });

    await TestBed.configureTestingModule({
      imports: [LeaderboardSectionComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: PLATFORM_ID, useValue: 'browser' },
        {
          provide: LeaderboardService,
          useValue: { listLeaderboards: listSpy, getLeaderboard: getSpy },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LeaderboardSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('hides when fewer than 3 leaderboards have snapshots', async () => {
    await setup({
      list: [listInner('overall', 'Overall', 10), listInner('cancer-biology', 'Cancer Biology', 5)],
    });
    expect(component.visible()).toBe(false);
    expect(fixture.nativeElement.querySelector('.leaderboard-section')).toBeNull();
  });

  it('hides when listLeaderboards fails', async () => {
    await setup({ list: [], listError: true });
    expect(component.visible()).toBe(false);
  });

  it('renders 3 columns sorted by snapshot voteCount desc, including overall', async () => {
    await setup({
      list: [
        listInner('overall', 'Overall', 10),
        listInner('cancer-biology', 'Cancer Biology', 5),
        listInner('neuroscience', 'Neuroscience', 5),
      ],
      pages: {
        overall: page('overall', 200, [
          entry('a', 'GPT-4o', 1),
          entry('b', 'Claude', 2),
          entry('c', 'Gemini', 3),
        ]),
        'cancer-biology': page('cancer-biology', 80, [entry('d', 'GPT-4o', 1)]),
        neuroscience: page('neuroscience', 50, [entry('e', 'Claude', 1)]),
      },
    });
    const cols = component.columns();
    expect(cols.map((c) => c.slug)).toEqual(['overall', 'cancer-biology', 'neuroscience']);
    expect(cols[0].voteCount).toBe(200);
  });

  it('caps render at 3 columns even when more qualify', async () => {
    const list = ['a', 'b', 'c', 'd'].map((id) => listInner(id, id.toUpperCase(), 5));
    const pages: Record<string, LeaderboardEntryPage> = {};
    list.forEach((l, i) => {
      const id = l.id ?? '';
      pages[id] = page(id, 100 - i, [entry(`${id}-1`, 'M', 1)]);
    });
    await setup({ list, pages });
    expect(component.columns().length).toBe(3);
  });

  it('hides when partial fetch failures leave fewer than 3 columns', async () => {
    await setup({
      list: [
        listInner('overall', 'Overall', 10),
        listInner('cancer-biology', 'Cancer Biology', 5),
        listInner('neuroscience', 'Neuroscience', 5),
      ],
      pages: {
        overall: page('overall', 200, [entry('a', 'GPT-4o', 1)]),
        'cancer-biology': page('cancer-biology', 80, [entry('d', 'GPT-4o', 1)]),
        // neuroscience absent → throws → catchError → null → filtered out
      },
    });
    expect(component.visible()).toBe(false);
  });

  it('hides when all per-column fetches fail', async () => {
    await setup({
      list: [
        listInner('overall', 'Overall', 10),
        listInner('cancer-biology', 'Cancer Biology', 5),
        listInner('neuroscience', 'Neuroscience', 5),
      ],
      getError: true,
    });
    expect(component.visible()).toBe(false);
  });
});
