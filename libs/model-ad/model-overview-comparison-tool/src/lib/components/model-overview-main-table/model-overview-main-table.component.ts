import { Component, ViewChild, effect, inject, input, viewChild } from '@angular/core';
import {
  ComparisonToolTableLinkComponent,
  FilterPanelService,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { CommaSeparatePipe } from '@sagebionetworks/explorers/util';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client';
import {
  intersectFilterCallback,
  urlLinkCallback,
} from '@sagebionetworks/explorers/comparison-tools';
import { FilterService } from 'primeng/api';
import { Table, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'model-ad-model-overview-main-table',
  imports: [TableModule, TooltipModule, ComparisonToolTableLinkComponent, CommaSeparatePipe],
  templateUrl: './model-overview-main-table.component.html',
  styleUrls: ['./model-overview-main-table.component.scss'],
})
export class ModelOverviewMainTableComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly filterPanelService = inject(FilterPanelService);

  private readonly filterService = inject(FilterService);

  data = input.required<ModelOverview[]>();

  @ViewChild('headerTable') headerTable!: Table;

  mainTable = viewChild.required<Table>('mainTable');
  objectFields = [
    'Gene Expression',
    'Disease Correlation',
    'Biomarkers',
    'Pathology',
    'Study Data',
    'Jax Strain',
    'Center',
  ];

  constructor() {
    effect(() => {
      this.filter();
    });

    this.filterService.register('intersect', intersectFilterCallback);
    this.filterService.register('urlLink', urlLinkCallback);
  }

  filter() {
    const newfilters: { [key: string]: any } = {};
    this.filterPanelService.filters().forEach((filter) => {
      if (!filter.field) {
        return;
      }

      // Get selected options
      const selectedOptions = filter.options.filter((option) => option.selected);

      if (selectedOptions.length) {
        selectedOptions.forEach(() => {
          // Extract values (labels) from selected options
          const values = selectedOptions.map((selected) => selected.label);

          // Check if ANY of the selected options are object fields
          const hasObjectFields = selectedOptions.some((option) =>
            this.objectFields.includes(option.label),
          );

          // Create filter metadata with proper structure
          if (hasObjectFields) {
            newfilters[filter.field] = [
              {
                value: values,
                matchMode: 'urlLink',
                operator: 'and', // Use 'and' or 'or' depending on your needs
              },
            ];
          } else {
            newfilters[filter.field] = [
              {
                value: values,
                matchMode: filter.matchMode || 'in', // Use 'in' for array of values
                operator: 'or',
              },
            ];
          }
        });
      }
    });
    this.mainTable().filters = newfilters;
    this.mainTable()._filter();
  }
}
