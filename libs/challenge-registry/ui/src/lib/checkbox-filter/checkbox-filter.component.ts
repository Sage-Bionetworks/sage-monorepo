import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FilterValue } from './filter-value.model';

@Component({
  selector: 'challenge-registry-checkbox-filter',
  templateUrl: './checkbox-filter.component.html',
  styleUrls: ['./checkbox-filter.component.scss'],
})
export class CheckboxFilterComponent {
  @Input() values!: FilterValue[];
  @Input() selectedValues!: string[];
  @Input() inputId!: string;
  @Output() checkboxChange = new EventEmitter<string[]>();

  onChange(selected: string[]): void {
    this.checkboxChange.emit(selected);
  }
}
