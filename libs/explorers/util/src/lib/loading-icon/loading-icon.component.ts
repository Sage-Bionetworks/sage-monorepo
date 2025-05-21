import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'explorers-loading-icon',
  imports: [CommonModule],
  templateUrl: './loading-icon.component.html',
  styleUrls: ['./loading-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LoadingIconComponent {
  @Input() colorOutermost = '#8B8AD1';
  @Input() colorCentral = '#8B8AD1';
  @Input() colorInnermost = '#8B8AD1';

  get customColors() {
    return {
      '--hex-outermost-color': this.colorOutermost,
      '--hex-central-color': this.colorCentral,
      '--hex-innermost-color': this.colorInnermost,
    };
  }
}
