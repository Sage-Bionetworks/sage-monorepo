import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'agora-svg-icon',
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgIconComponent {
  @Input() imagePath = '';
  @Input() altText = '';
  @Input() customClasses = '';

  getClasses() {
    const classes = ['svg-icon'];

    if (this.customClasses) {
      classes.push(this.customClasses);
    }

    return classes.join(' ');
  }
}
