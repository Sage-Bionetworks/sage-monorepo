import { Component, ViewEncapsulation, input } from '@angular/core';

@Component({
  selector: 'bixarena-hero',
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class HeroComponent {
  readonly titleHtml = input.required<string>();
  readonly subtitleHtml = input.required<string>();
}
