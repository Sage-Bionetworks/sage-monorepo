import { Component, Input } from '@angular/core';
import { FilterValue } from './filter-value.model';

@Component({
  selector: 'challenge-registry-checkbox-filter',
  templateUrl: './checkbox-filter.component.html',
  styleUrls: ['./checkbox-filter.component.scss'],
})
export class CheckboxFilterComponent {
  @Input() values!: FilterValue[];
  @Input() selectedValues!: string[];
}
