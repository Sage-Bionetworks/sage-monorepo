import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';

interface Option {
  name: string;
  value: string;
}

@Component({
  selector: 'agora-gene-protein-selector',
  imports: [FormsModule, SelectModule],
  templateUrl: './gene-protein-selector.component.html',
  styleUrls: ['./gene-protein-selector.component.scss'],
})
export class GeneProteinSelectorComponent {
  _options: Option[] = [];
  get options(): Option[] {
    return this._options;
  }
  @Input() set options(options: any) {
    this.selected = {} as Option;
    this._options =
      options?.map((option: any) => {
        return {
          name: option,
          value: option,
        } as Option;
      }) || [];

    if (this._options.length) {
      this.selected = this._options[0];
    }
  }

  @Input() selected: Option = { name: '', value: '' };

  @Output() changeEvent: EventEmitter<object> = new EventEmitter<object>();

  _onChange() {
    this.changeEvent.emit(this.selected);
  }
}
