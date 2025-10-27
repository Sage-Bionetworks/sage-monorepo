import { Component, ViewChild, inject, input } from '@angular/core';
import { ComparisonToolTableLinkComponent } from '@sagebionetworks/explorers/comparison-tools';
import { CommaSeparatePipe } from '@sagebionetworks/explorers/util';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client';
import { Table, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ModelOverviewComparisonToolService } from '../../services/model-overview-comparison-tool.service';

@Component({
  selector: 'model-ad-model-overview-main-table',
  imports: [TableModule, TooltipModule, ComparisonToolTableLinkComponent, CommaSeparatePipe],
  templateUrl: './model-overview-main-table.component.html',
  styleUrls: ['./model-overview-main-table.component.scss'],
})
export class ModelOverviewMainTableComponent {
  private readonly comparisonToolService = inject(ModelOverviewComparisonToolService);

  data = input.required<ModelOverview[]>();

  @ViewChild('headerTable') headerTable!: Table;
}
