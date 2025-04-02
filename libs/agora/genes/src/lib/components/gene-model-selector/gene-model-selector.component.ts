/* eslint-disable @angular-eslint/no-input-rename */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { removeParenthesis } from '@sagebionetworks/agora/util';
import { SelectModule } from 'primeng/select';

interface Option {
  name: string;
  value: string;
}

@Component({
  selector: 'agora-gene-model-selector',
  imports: [FormsModule, SelectModule],
  templateUrl: './gene-model-selector.component.html',
  styleUrls: ['./gene-model-selector.component.scss'],
})
export class GeneModelSelectorComponent {
  _model = '';
  get model(): string {
    return this._model;
  }
  @Input() set model(model: string | undefined) {
    this._model = model ?? '';
    this.initSelected();
  }

  _options: Option[] = [];
  get options(): Option[] {
    return this._options;
  }
  @Input() set options(options: any) {
    this._options =
      options?.map((option: any) => {
        const newValue = removeParenthesis(option);
        return {
          name: option,
          value: newValue,
        } as Option;
      }) || [];
    this.initSelected();
  }

  selected: Option = { name: '', value: '' };

  @Output() changeEvent: EventEmitter<object> = new EventEmitter<object>();

  initSelected() {
    this.selected = {} as Option;
    let index = this._options.findIndex((o) => o.value === this._model);
    if (index === -1) {
      // default to first option if model is not defined
      index = 0;
    }
    this.selected = this._options[index];
    this._onChange();
  }

  _onChange() {
    this.changeEvent.emit(this.selected);
  }
}
