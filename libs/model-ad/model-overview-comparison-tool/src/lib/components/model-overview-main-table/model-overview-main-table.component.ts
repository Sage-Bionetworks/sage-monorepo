import { Component, ViewChild, input, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client-angular';
import { Table, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolTableLinkComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-model-overview-main-table',
  imports: [TableModule, TooltipModule, ComparisonToolTableLinkComponent],
  templateUrl: './model-overview-main-table.component.html',
  styleUrls: ['./model-overview-main-table.component.scss'],
})
export class ModelOverviewMainTableComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  data = input.required<ModelOverview[]>();

  @ViewChild('headerTable') headerTable!: Table;
}
