import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { SortEvent } from 'primeng/api';
import { LeaderboardEntry } from '@sagebionetworks/bixarena/api-client';
import { KebabToTitlePipe } from '@sagebionetworks/bixarena/services';
import {
  DEFAULT_SORT_FIELD,
  DEFAULT_SORT_ORDER,
  WHISKER_PADDING_PCT,
} from '../leaderboard.constants';

export type LeaderboardSortField = keyof Pick<
  LeaderboardEntry,
  'rank' | 'modelName' | 'btScore' | 'voteCount' | 'license'
>;

export interface LeaderboardSortChange {
  field: LeaderboardSortField;
  order: 1 | -1;
}

interface RenderedEntry extends LeaderboardEntry {
  readonly whiskerLeft: number;
  readonly whiskerWidth: number;
  readonly scorePosition: number;
  readonly scoreRounded: number;
  readonly q025Rounded: number;
  readonly q975Rounded: number;
  readonly isTopThree: boolean;
}

@Component({
  selector: 'bixarena-leaderboard-table',
  imports: [TableModule, DecimalPipe, KebabToTitlePipe],
  templateUrl: './leaderboard-table.component.html',
  styleUrl: './leaderboard-table.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeaderboardTableComponent {
  readonly entries = input.required<LeaderboardEntry[]>();
  readonly sortField = input<LeaderboardSortField>(DEFAULT_SORT_FIELD);
  readonly sortOrder = input<1 | -1>(DEFAULT_SORT_ORDER);
  readonly sortChange = output<LeaderboardSortChange>();

  onSort(event: SortEvent): void {
    if (!event.field) return;
    this.sortChange.emit({
      field: event.field as LeaderboardSortField,
      order: (event.order === -1 ? -1 : 1) as 1 | -1,
    });
  }

  private readonly bounds = computed(() => {
    const entries = this.entries();
    if (entries.length === 0) return { min: 0, max: 1 };
    let min = Infinity;
    let max = -Infinity;
    for (const e of entries) {
      if (e.bootstrapQ025 < min) min = e.bootstrapQ025;
      if (e.bootstrapQ975 > max) max = e.bootstrapQ975;
    }
    return min === max ? { min: min - 1, max: max + 1 } : { min, max };
  });

  readonly rows = computed<RenderedEntry[]>(() => {
    const entries = this.entries();
    const { min, max } = this.bounds();
    const range = max - min || 1;

    return entries.map((entry) => {
      const left = clampPct(((entry.bootstrapQ025 - min) / range) * 100);
      const right = clampPct(((entry.bootstrapQ975 - min) / range) * 100);
      const scorePosition = clampPct(((entry.btScore - min) / range) * 100);

      return {
        ...entry,
        whiskerLeft: left,
        whiskerWidth: Math.max(right - left, 0.5),
        scorePosition,
        scoreRounded: Math.round(entry.btScore),
        q025Rounded: Math.round(entry.bootstrapQ025),
        q975Rounded: Math.round(entry.bootstrapQ975),
        isTopThree: entry.rank <= 3,
      };
    });
  });
}

function clampPct(value: number): number {
  if (value < WHISKER_PADDING_PCT) return WHISKER_PADDING_PCT;
  if (value > 100 - WHISKER_PADDING_PCT) return 100 - WHISKER_PADDING_PCT;
  return value;
}
