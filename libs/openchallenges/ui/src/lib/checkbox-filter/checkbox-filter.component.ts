import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Filter } from './filter.model';

import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';

@Component({
  selector: 'openchallenges-checkbox-filter',
  imports: [CheckboxModule, FormsModule],
  templateUrl: './checkbox-filter.component.html',
  styleUrls: ['./checkbox-filter.component.scss'],
})
export class CheckboxFilterComponent {
  @Input({ required: true }) options!: Filter[];
  @Input({ required: true }) selectedOptions!: string[];
  @Input({ required: true }) inputId!: string;
  @Output() selectionChange = new EventEmitter<string[]>();

  onChange(selected: string[]): void {
    this.selectionChange.emit(selected);
  }

  formatId(str: string): string {
    return str.replace(/\s+/g, '-');
  }
}
