import { Component, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { removeParenthesis } from '@sagebionetworks/agora/util';
import { DropdownModule } from 'primeng/dropdown';

interface Option {
  name: string;
  value: string;
}

@Component({
  selector: 'agora-gene-model-selector',
  imports: [FormsModule, DropdownModule],
  standalone: true,
  templateUrl: './gene-model-selector.component.html',
  styleUrls: ['./gene-model-selector.component.scss'],
})
export class GeneModelSelectorComponent implements OnInit {
  route = inject(ActivatedRoute);

  _options: Option[] = [];
  get options(): Option[] {
    return this._options;
  }
  @Input() set options(options: any) {
    this.selected = {} as Option;
    this._options =
      options?.map((option: any) => {
        const newValue = removeParenthesis(option);
        return {
          name: option,
          value: newValue,
        } as Option;
      }) || [];
  }

  selected: Option = { name: '', value: '' };

  @Output() changeEvent: EventEmitter<object> = new EventEmitter<object>();

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      const modelFromURL = params['model'];
      let index = this._options.findIndex((o) => o.value === modelFromURL);
      if (index === -1) {
        // default to first option if page is loaded without a model parameter
        index = 0;
      }
      this.selected = this._options[index];
      this._onChange();
    });
  }

  _onChange() {
    this.changeEvent.emit(this.selected);
  }
}
