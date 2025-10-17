import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, model, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ComparisonToolConfigFilter,
  ComparisonToolFilter,
} from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { CheckboxModule } from 'primeng/checkbox';
import { TooltipModule } from 'primeng/tooltip';
import { FilterPanelHeaderComponent } from './filter-panel-header/filter-panel-header.component';
import { FilterPanelMainMenuItemButtonComponent } from './filter-panel-main-menu-item-button/filter-panel-main-menu-item-button.component';

@Component({
  selector: 'explorers-comparison-tool-filter-panel',
  imports: [
    CommonModule,
    FormsModule,
    TooltipModule,
    CheckboxModule,
    FilterPanelHeaderComponent,
    FilterPanelMainMenuItemButtonComponent,
  ],
  templateUrl: './comparison-tool-filter-panel.component.html',
  styleUrls: ['./comparison-tool-filter-panel.component.scss'],
})
export class ComparisonToolFilterPanelComponent {
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  filterConfigs = input<ComparisonToolConfigFilter[]>([]);
  isOpen = model<boolean>(false);

  filters = computed(() => {
    return this.filterConfigs().map((config) => {
      const filter: ComparisonToolFilter = {
        name: config.name,
        field: config.field,
        options: config.values.map((value) => ({
          label: value,
          selected: false,
        })),
      };
      return filter;
    });
  });

  activePane = signal(-1);
  hasActivePane = computed(() => this.activePane() !== -1);

  handleChange() {
    this.comparisonToolFilterService.setFilters(this.filters());
  }

  openPane(index: number) {
    this.activePane.set(index);
  }

  closePanes() {
    this.activePane.set(-1);
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
