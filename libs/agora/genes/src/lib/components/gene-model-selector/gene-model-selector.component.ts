import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { QUERY_PARAMS } from '@sagebionetworks/agora/config';
import { removeParentheses } from '@sagebionetworks/shared/util';
import { SelectModule } from 'primeng/select';

export interface ModelOption {
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

  _options: ModelOption[] = [];
  get options(): ModelOption[] {
    return this._options;
  }
  @Input() set options(options: string[] | undefined) {
    this.selected = {} as ModelOption;
    this._options = options?.map((option) => ({ name: option })) || [];
  }

  selected: ModelOption = { name: '' };

  // emits undefined when there are no options to select
  @Output() changeEvent = new EventEmitter<ModelOption | undefined>();

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
