import { Component, Input } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { FilterValue } from './filter-value.model';
import { FilterState } from './filter-state.model';

export const emptyFilterState: FilterState = {
  name: '',
  value: '',
};

@Component({
  template: '',
})
export abstract class FilterComponent {
  /* Filter name. */
  @Input() name = '';
  /* The available value that the query parameter can takes. */
  protected _values: FilterValue[] = [];
  /* Emits each time the selected filter value changes. */
  protected state = new BehaviorSubject<FilterState>(emptyFilterState);

  getStateAsObservable(): Observable<FilterState> {
    return this.state.asObservable();
  }

  get values(): FilterValue[] {
    return this._values;
  }

  @Input()
  set values(values: FilterValue[]) {
    this._values = values;
    this.emitState();
  }

  protected abstract getState(): FilterState;

  emitState(): void {
    this.state.next(this.getState());
  }
}
