import { Component, Input } from '@angular/core';
import { Gene } from '@sagebionetworks/agora/api-client';
import { GeneBioDomainsComponent } from '../gene-biodomains/gene-biodomains.component';
import { GeneSoeChartsComponent } from '../gene-soe-charts/gene-soe-charts.component';
import { GeneSoeListComponent } from '../gene-soe-list/gene-soe-list.component';

@Component({
  selector: 'agora-gene-soe',
  imports: [GeneBioDomainsComponent, GeneSoeChartsComponent, GeneSoeListComponent],
  templateUrl: './gene-soe.component.html',
  styleUrls: ['./gene-soe.component.scss'],
})
export class GeneSoeComponent {
  @Input() gene: Gene | undefined;
}
