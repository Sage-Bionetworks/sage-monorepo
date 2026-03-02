import { Component, Input, Output, ViewEncapsulation, EventEmitter } from '@angular/core';

import { CommonModule } from '@angular/common';
import { GCTFilter } from '@sagebionetworks/agora/models';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule } from '@angular/forms';
import { TooltipModule } from 'primeng/tooltip';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'agora-gene-comparison-tool-filter-panel',
  imports: [CommonModule, FormsModule, TooltipModule, CheckboxModule, SvgIconComponent],
  templateUrl: './gene-comparison-tool-filter-panel.component.html',
  styleUrls: ['./gene-comparison-tool-filter-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolFilterPanelComponent {
  @Input() filters: GCTFilter[] = [] as GCTFilter[];
  isOpen = false;
  activePane = -1;
  @Output() changeEvent: EventEmitter<object> = new EventEmitter<object>();
  handleChange(option: any) {
    if (option.preset) {
      this.filters.forEach((filter) => {
        filter.options.forEach((o) => {
          if (option.preset[filter.name] && option.preset[filter.name].includes(o.value)) {
            o.selected = true;
          } else {
            o.selected = false;
          }
        });
      });
    }
    this.changeEvent.emit(this.filters);
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
