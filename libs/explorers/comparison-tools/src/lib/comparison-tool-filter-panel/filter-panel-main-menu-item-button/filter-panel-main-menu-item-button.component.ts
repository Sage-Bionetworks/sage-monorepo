import { Component, input, output } from '@angular/core';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-filter-panel-main-menu-item-button',
  imports: [SvgIconComponent],
  templateUrl: './filter-panel-main-menu-item-button.component.html',
  styleUrls: ['./filter-panel-main-menu-item-button.component.scss'],
})
export class FilterPanelMainMenuItemButtonComponent {
  filter = input.required<ComparisonToolFilter>();
  buttonClick = output<void>();
}
