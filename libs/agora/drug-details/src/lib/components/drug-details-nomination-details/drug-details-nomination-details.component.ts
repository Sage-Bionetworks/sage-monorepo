import { Component, input } from '@angular/core';
import { Drug, NominatedDrugEvidence } from '@sagebionetworks/agora/api-client';
import { CapitalizeFirstLetterPipe } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'agora-drug-details-nomination-details',
  imports: [CapitalizeFirstLetterPipe],
  templateUrl: './drug-details-nomination-details.component.html',
  styleUrls: ['./drug-details-nomination-details.component.scss'],
})
export class DrugDetailsNominationDetailsComponent {
  drug = input.required<Drug>();

  getNominationLabel(nomination: NominatedDrugEvidence): string {
    const partners = (nomination.combined_with ?? []).map(
      (partner) => partner.common_name || partner.chembl_id,
    );
    return [this.drug().common_name, ...partners].join(' + ');
  }
}
