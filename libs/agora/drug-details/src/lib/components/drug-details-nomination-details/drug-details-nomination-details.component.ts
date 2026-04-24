import { Component, input } from '@angular/core';
import { Drug } from '@sagebionetworks/agora/api-client';

@Component({
  selector: 'agora-drug-details-nomination-details',
  imports: [],
  templateUrl: './drug-details-nomination-details.component.html',
  styleUrls: ['./drug-details-nomination-details.component.scss'],
})
export class DrugDetailsNominationDetailsComponent {
  drug = input.required<Drug>();
}
