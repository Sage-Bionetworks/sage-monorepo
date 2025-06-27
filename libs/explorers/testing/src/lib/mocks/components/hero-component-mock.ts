import { Component, input } from '@angular/core';

@Component({
  selector: 'explorers-hero',
  template: '<div>Mock Hero Component</div>',
})
export class MockHeroComponent {
  title = input('');
  backgroundImagePath = input('');
}
