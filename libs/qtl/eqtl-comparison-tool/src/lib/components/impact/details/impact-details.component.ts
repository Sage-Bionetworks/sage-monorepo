import { Component, computed, input, ViewEncapsulation } from '@angular/core';
import { SimpleTableCell } from '@sagebionetworks/explorers/models';
import { SimpleTableComponent } from '@sagebionetworks/explorers/ui';

export interface ImpactDetails {
  beta: string;
  pValue: string;
  variantLocation: string;
  geneLocation: string;
  cellClass: string;
}

@Component({
  selector: 'qtl-eqtl-comparison-tool-impact-details',
  imports: [SimpleTableComponent],
  templateUrl: './impact-details.component.html',
  styleUrls: ['./impact-details.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ImpactDetailsComponent {
  details = input.required<ImpactDetails>();

  protected readonly rows = computed<SimpleTableCell[][]>(() => {
    const { beta, pValue, variantLocation, geneLocation, cellClass } = this.details();
    return [
      ['Beta', beta],
      ['P-Value', pValue],
      ['Variant Loc.', variantLocation],
      ['Gene Loc.', geneLocation],
      ['Cell Class', cellClass],
    ].map(([label, value]) => [
      { type: 'label', text: label },
      { type: 'text', value },
    ]);
  });
}
