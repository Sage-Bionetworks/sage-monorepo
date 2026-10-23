import { Location } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QUERY_PARAMS } from '@sagebionetworks/agora/config';
import { HelperService } from '@sagebionetworks/explorers/services';
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
  private readonly helperService = inject(HelperService);
  private readonly location = inject(Location);

  _options: ModelOption[] = [];
  get options(): ModelOption[] {
    return this._options;
  }
  @Input() set options(options: string[] | undefined) {
    this.selected = {} as ModelOption;
    this._options = options?.map((option) => ({ name: option })) || [];
  }

  get defaultOption(): ModelOption {
    return this._options[0];
  }

  selected: ModelOption = { name: '' };

  // emits undefined when there are no options to select
  @Output() changeEvent = new EventEmitter<ModelOption | undefined>();

  ngOnInit() {
    // read from the browser URL rather than ActivatedRoute, which the replaceState calls on
    // gene details (tab switches, model changes) leave stale
    const modelFromURL = this.helperService.getUrlParam(QUERY_PARAMS.MODEL);
    const exactMatch = this._options.find((o) => o.name === modelFromURL);
    // links shared while the URL serializer stripped parentheses carry the stripped name
    const strippedMatch = this._options.find((o) => removeParentheses(o.name) === modelFromURL);
    this.selected = exactMatch ?? strippedMatch ?? this.defaultOption;
    this._onChange();
  }

  onSelectionChange() {
    const currentUrl = `${window.location.pathname}${window.location.search}${window.location.hash}`;
    const model = this.selected === this.defaultOption ? null : this.selected.name;
    this.location.replaceState(
      this.helperService.setQueryParams(currentUrl, { [QUERY_PARAMS.MODEL]: model }),
    );
    this._onChange();
  }

  _onChange() {
    this.changeEvent.emit(this.selected);
  }
}
