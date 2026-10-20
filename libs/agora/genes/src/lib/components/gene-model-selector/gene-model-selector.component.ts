import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { QUERY_PARAMS } from '@sagebionetworks/agora/config';
import { removeParentheses } from '@sagebionetworks/shared/util';
import { SelectModule } from 'primeng/select';

interface Option {
  name: string;
}

@Component({
  selector: 'agora-gene-model-selector',
  imports: [FormsModule, SelectModule],
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
    this._options = options?.map((option: any) => ({ name: option }) as Option) || [];
  }

  selected: Option = { name: '' };

  @Output() changeEvent: EventEmitter<object> = new EventEmitter<object>();

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      const modelFromURL = params[QUERY_PARAMS.MODEL];
      const exactMatch = this._options.find((o) => o.name === modelFromURL);
      // links shared while the URL serializer stripped parentheses carry the stripped name
      const strippedMatch = this._options.find((o) => removeParentheses(o.name) === modelFromURL);
      const defaultOption = this._options[0];
      this.selected = exactMatch ?? strippedMatch ?? defaultOption;
      this._onChange();
    });
  }

  _onChange() {
    this.changeEvent.emit(this.selected);
  }
}
