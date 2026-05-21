import { Component, inject } from '@angular/core';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { SidebarComponent } from './components/sidebar/sidebar.component';

@Component({
  selector: 'qtl-eqtl-comparison-tool',
  imports: [ComparisonToolComponent, SidebarComponent],
  providers: [
    MessageService,
    ...provideComparisonToolService(),
    ...provideComparisonToolFilterService(),
  ],
  templateUrl: './eqtl-comparison-tool.component.html',
  styleUrls: ['./eqtl-comparison-tool.component.scss'],
})
export class EqtlComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  constructor() {
    this.comparisonToolService.connect({
      config$: of([]),
      queryParams$: of({}),
    });
  }
}
