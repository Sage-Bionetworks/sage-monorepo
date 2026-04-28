import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'bixarena-composer-section',
  templateUrl: './composer-section.component.html',
  styleUrl: './composer-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComposerSectionComponent {}
