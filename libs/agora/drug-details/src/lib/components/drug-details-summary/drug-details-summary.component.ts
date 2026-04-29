import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Drug } from '@sagebionetworks/agora/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/agora/config';

@Component({
  selector: 'agora-drug-details-summary',
  imports: [RouterLink],
  templateUrl: './drug-details-summary.component.html',
  styleUrls: ['./drug-details-summary.component.scss'],
})
export class DrugDetailsSummaryComponent {
  ROUTE_PATHS = ROUTE_PATHS;

  drug = input.required<Drug>();
}
