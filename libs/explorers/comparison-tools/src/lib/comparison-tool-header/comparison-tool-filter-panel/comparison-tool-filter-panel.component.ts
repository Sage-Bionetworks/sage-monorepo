import { CommonModule } from '@angular/common';
import { Component, computed, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ComparisonToolFilter,
  ComparisonToolFilterConfig,
} from '@sagebionetworks/explorers/models';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { CheckboxModule } from 'primeng/checkbox';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-comparison-tool-filter-panel',
  imports: [CommonModule, FormsModule, TooltipModule, CheckboxModule, SvgIconComponent],
  templateUrl: './comparison-tool-filter-panel.component.html',
  styleUrls: ['./comparison-tool-filter-panel.component.scss'],
})
export class ComparisonToolFilterPanelComponent {
  filterConfigs = input<ComparisonToolFilterConfig[]>([]);
  filtersChanged = output<ComparisonToolFilter[]>();

  filters = computed(() => {
    return this.filterConfigs().map((config) => {
      const filter: ComparisonToolFilter = {
        name: config.name,
        field: config.field,
        options: config.values.map((value) => ({
          label: value,
          value,
          selected: false,
        })),
      };
      return filter;
    });
  });

  isOpen = false;
  activePane = -1;

  handleChange() {
    this.filtersChanged.emit(this.filters());
  }

  openPane(index: number) {
    this.activePane = index;
  }

  closePanes() {
    this.activePane = -1;
  }

  open() {
    this.isOpen = true;
  }

  close() {
    this.closePanes();
    this.isOpen = false;
  }

  toggle() {
    if (this.isOpen) {
      this.close();
    } else {
      this.open();
    }
  }
}
