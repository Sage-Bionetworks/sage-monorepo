import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'bixarena-stats-section',
  templateUrl: './stats-section.component.html',
  styleUrl: './stats-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatsSectionComponent {}
