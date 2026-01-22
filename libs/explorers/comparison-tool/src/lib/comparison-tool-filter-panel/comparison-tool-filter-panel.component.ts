import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
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
  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  filters = this.comparisonToolFilterService.filters;
  isOpen = this.comparisonToolService.isFilterPanelOpen;

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
    this.comparisonToolService.openFilterPanel();
  }

  close() {
    this.closePanes();
    this.comparisonToolService.closeFilterPanel();
  }

  toggle() {
    this.comparisonToolService.toggleFilterPanel();
  }
}
