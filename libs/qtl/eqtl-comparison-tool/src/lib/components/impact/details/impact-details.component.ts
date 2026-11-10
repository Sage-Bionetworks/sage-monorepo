import { Component, computed, input } from '@angular/core';
import { DescriptionListItem } from '@sagebionetworks/explorers/models';
import { DescriptionListComponent } from '@sagebionetworks/explorers/ui';

export interface ImpactDetails {
  beta: string;
  pValue: string;
  variantLocation: string;
  geneLocation: string;
  cellClass: string;
}

export const IMPACT_DETAIL_FIELDS = [
  { key: 'beta', label: 'Beta' },
  { key: 'pValue', label: 'P-Value' },
  { key: 'variantLocation', label: 'Variant Loc.' },
  { key: 'geneLocation', label: 'Gene Loc.' },
  { key: 'cellClass', label: 'Cell Class' },
] as const satisfies readonly { key: keyof ImpactDetails; label: string }[];

@Component({
  selector: 'qtl-eqtl-comparison-tool-impact-details',
  imports: [DescriptionListComponent],
  templateUrl: './impact-details.component.html',
  styleUrls: ['./impact-details.component.scss'],
})
export class ImpactDetailsComponent {
  details = input.required<ImpactDetails>();

  protected readonly items = computed<DescriptionListItem[]>(() =>
    IMPACT_DETAIL_FIELDS.map(({ key, label }) => ({
      term: label,
      description: this.details()[key],
    })),
  );
}
