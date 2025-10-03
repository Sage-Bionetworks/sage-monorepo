import { CommonModule } from '@angular/common';
import { Component, computed, input, model, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ComparisonToolFilter,
  ComparisonToolFilterConfig,
} from '@sagebionetworks/explorers/models';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { CheckboxModule } from 'primeng/checkbox';
import { TooltipModule } from 'primeng/tooltip';
import { FilterPanelCloseButtonComponent } from './filter-panel-close-button/filter-panel-close-button.component';

@Component({
  selector: 'explorers-comparison-tool-filter-panel',
  imports: [
    CommonModule,
    FormsModule,
    TooltipModule,
    CheckboxModule,
    SvgIconComponent,
    FilterPanelCloseButtonComponent,
  ],
  templateUrl: './comparison-tool-filter-panel.component.html',
  styleUrls: ['./comparison-tool-filter-panel.component.scss'],
})
export class ComparisonToolFilterPanelComponent {
  filterConfigs = input<ComparisonToolFilterConfig[]>([]);
  filtersChanged = output<ComparisonToolFilter[]>();
  isOpen = model<boolean>(false);

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
    this.isOpen.set(true);
  }

  close() {
    this.closePanes();
    this.isOpen.set(false);
  }

  toggle() {
    if (this.isOpen()) {
      this.close();
    } else {
      this.open();
    }
  }
}
