import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'agora-svg-icon',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
})
export class SvgIconComponent {
  @Input() name = '';
  @Input() customClass = '';

  getClasses() {
    const classes = ['svg-icon', 'svg-icon-' + this.name];

    if (this.customClass) {
      classes.push(this.customClass);
    }

    return classes.join(' ');
  }
}
