import { Component, Input } from '@angular/core';

@Component({
  selector: 'explorers-hero',
  imports: [],
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.scss'],
})
export class HeroComponent {
  @Input() title = '';
  @Input() backgroundImagePath = '';
}
