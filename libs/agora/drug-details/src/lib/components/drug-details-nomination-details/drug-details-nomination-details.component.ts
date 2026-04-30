import { Component, input } from '@angular/core';
import { Drug } from '@sagebionetworks/agora/api-client';
import { CapitalizeFirstLetterPipe } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'agora-drug-details-nomination-details',
  imports: [CapitalizeFirstLetterPipe],
  templateUrl: './drug-details-nomination-details.component.html',
  styleUrls: ['./drug-details-nomination-details.component.scss'],
})
export class DrugDetailsNominationDetailsComponent {
  drug = input.required<Drug>();
}
