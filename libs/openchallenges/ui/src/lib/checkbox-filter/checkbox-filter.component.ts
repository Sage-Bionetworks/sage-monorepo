import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FilterValue } from './filter-value.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';

@Component({
  selector: 'openchallenges-checkbox-filter',
  standalone: true,
  imports: [CommonModule, CheckboxModule, FormsModule],
  templateUrl: './checkbox-filter.component.html',
  styleUrls: ['./checkbox-filter.component.scss'],
})
export class CheckboxFilterComponent {
  @Input({ required: true }) values!: FilterValue[];
  @Input({ required: true }) selectedValues!: string[];
  @Input({ required: true }) inputId!: string;
  @Output() selectionChange = new EventEmitter<string[]>();

  onChange(selected: string[]): void {
    this.selectionChange.emit(selected);
  }

  formatId(str: string): string {
    return str.replace(/\s+/g, '-');
  }
}
