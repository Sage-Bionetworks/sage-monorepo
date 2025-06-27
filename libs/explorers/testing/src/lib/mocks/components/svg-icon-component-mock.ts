import { Component, Input } from '@angular/core';

@Component({
  selector: 'explorers-svg-icon',
  template: '<div></div>',
})
export class MockSvgIconComponent {
  @Input() imagePath!: string;
  @Input() altText = '';
  @Input() width = 14;
  @Input() height = 14;
  @Input() color = 'inherit'; // Default to parent color
  @Input() enableHoverEffects = true;

  className = 'svg-icon';
}
