import { CommonModule } from '@angular/common';
import { Component, inject, ViewEncapsulation } from '@angular/core';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';

@Component({
  selector: 'explorers-loading-icon',
  imports: [CommonModule],
  templateUrl: './loading-icon.component.html',
  styleUrls: ['./loading-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LoadingIconComponent {
  private readonly colors = inject(LOADING_ICON_COLORS);

  get customColors() {
    return {
      '--hex-outermost-color': this.colors.colorOutermost,
      '--hex-central-color': this.colors.colorCentral,
      '--hex-innermost-color': this.colors.colorInnermost,
    };
  }
}
