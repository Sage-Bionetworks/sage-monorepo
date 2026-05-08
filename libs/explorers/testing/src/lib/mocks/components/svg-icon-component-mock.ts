import { Component, input } from '@angular/core';

@Component({
  selector: 'explorers-svg-icon',
  template: '<div></div>',
})
export class MockSvgIconComponent {
  imagePath = input.required<string>();
  altText = input('');
  width = input(14);
  height = input(14);
  color = input('inherit');
  enableHoverEffects = input(false);
}
