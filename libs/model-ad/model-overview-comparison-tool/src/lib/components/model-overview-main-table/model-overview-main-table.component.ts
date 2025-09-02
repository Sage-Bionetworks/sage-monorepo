import { Component, ViewChild, inject, input } from '@angular/core';
import { ComparisonToolTableLinkComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { CommaSeparatePipe } from '@sagebionetworks/explorers/util';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client-angular';
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

  data = input.required<ModelOverview[]>();

  @ViewChild('headerTable') headerTable!: Table;
}
