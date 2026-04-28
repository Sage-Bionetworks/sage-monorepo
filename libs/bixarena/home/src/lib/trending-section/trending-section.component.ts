import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'bixarena-trending-section',
  templateUrl: './trending-section.component.html',
  styleUrl: './trending-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrendingSectionComponent {}
