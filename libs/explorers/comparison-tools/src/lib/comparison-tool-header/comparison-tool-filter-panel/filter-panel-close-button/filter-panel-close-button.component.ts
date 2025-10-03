import { Component, output } from '@angular/core';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-filter-panel-close-button',
  imports: [SvgIconComponent],
  templateUrl: './filter-panel-close-button.component.html',
  styleUrls: ['./filter-panel-close-button.component.scss'],
})
export class FilterPanelCloseButtonComponent {
  closeClick = output<void>();
}
