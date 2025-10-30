import { CommonModule } from '@angular/common';
import { Component, input, ViewEncapsulation } from '@angular/core';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';

@Component({
  selector: 'explorers-loading-container',
  imports: [CommonModule, LoadingIconComponent],
  templateUrl: './loading-container.component.html',
  styleUrls: ['./loading-container.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LoadingContainerComponent {
  count = input<string>();
}
