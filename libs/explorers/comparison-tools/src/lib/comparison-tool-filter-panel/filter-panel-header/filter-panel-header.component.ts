import { Component, input, output } from '@angular/core';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-filter-panel-header',
  imports: [SvgIconComponent],
  templateUrl: './filter-panel-header.component.html',
  styleUrls: ['./filter-panel-header.component.scss'],
})
export class FilterPanelHeaderComponent {
  heading = input<string>('');
  hideCloseButton = input<boolean>(false);
  closeClick = output<void>();
}
