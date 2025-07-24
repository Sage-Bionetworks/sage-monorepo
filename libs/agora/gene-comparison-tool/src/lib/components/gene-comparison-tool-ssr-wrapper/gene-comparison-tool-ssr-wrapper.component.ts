import { Component } from '@angular/core';
import { GeneComparisonToolComponent } from '../../gene-comparison-tool.component';

@Component({
  selector: 'agora-gene-comparison-tool-ssr-wrapper',
  imports: [GeneComparisonToolComponent],
  templateUrl: './gene-comparison-tool-ssr-wrapper.component.html',
  styleUrls: ['./gene-comparison-tool-ssr-wrapper.component.scss'],
})
export class GeneComparisonToolSsrWrapperComponent {}
