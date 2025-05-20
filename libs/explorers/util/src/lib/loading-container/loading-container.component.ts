import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation } from '@angular/core';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';

@Component({
  selector: 'explorers-loading-container',
  imports: [CommonModule, LoadingIconComponent],
  templateUrl: './loading-container.component.html',
  styleUrls: ['./loading-container.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LoadingContainerComponent {
  @Input() colorOutermost = '#8B8AD1';
  @Input() colorCentral = '#8B8AD1';
  @Input() colorInnermost = '#8B8AD1';
  @Input() count = 0;
}
