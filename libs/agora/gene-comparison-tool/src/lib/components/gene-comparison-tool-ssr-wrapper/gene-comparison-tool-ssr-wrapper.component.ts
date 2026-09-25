import { Component, inject } from '@angular/core';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { GeneComparisonToolComponent } from '../../gene-comparison-tool.component';

@Component({
  selector: 'agora-gene-comparison-tool-ssr-wrapper',
  imports: [GeneComparisonToolComponent],
  templateUrl: './gene-comparison-tool-ssr-wrapper.component.html',
  styleUrls: ['./gene-comparison-tool-ssr-wrapper.component.scss'],
})
export class GeneComparisonToolSsrWrapperComponent {
  readonly platformService = inject(PlatformService);
}
