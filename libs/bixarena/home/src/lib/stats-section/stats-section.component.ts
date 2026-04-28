import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { StatsService } from '@sagebionetworks/bixarena/api-client';

@Component({
  selector: 'bixarena-stats-section',
  imports: [DecimalPipe],
  templateUrl: './stats-section.component.html',
  styleUrl: './stats-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatsSectionComponent {
  readonly stats = toSignal(
    inject(StatsService)
      .getPublicStats()
      .pipe(catchError(() => of(null))),
    { initialValue: null },
  );
}
