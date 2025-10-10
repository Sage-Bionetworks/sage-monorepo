import { Component, inject, output } from '@angular/core';

import { CommonModule } from '@angular/common';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule } from '@angular/forms';
import { TooltipModule } from 'primeng/tooltip';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolFilterOption } from '@sagebionetworks/explorers/models';
import { FilterPanelService } from './filter-panel.service';

@Component({
  selector: 'explorers-filter-panel',
  imports: [CommonModule, FormsModule, TooltipModule, CheckboxModule, SvgIconComponent],
  templateUrl: './filter-panel.component.html',
  styleUrls: ['./filter-panel.component.scss'],
})
export class FilterPanelComponent {
  changeEvent = output();

  filterPanelService = inject(FilterPanelService);

  handleChange(option: ComparisonToolFilterOption) {
    this.filterPanelService.handleFilterChange(option);
    this.changeEvent.emit();
  }

  openPane(index: number) {
    this.filterPanelService.setActivePane(index);
  }

  close() {
    this.filterPanelService.close();
  }

  toggle() {
    this.filterPanelService.toggle();
  }
}
