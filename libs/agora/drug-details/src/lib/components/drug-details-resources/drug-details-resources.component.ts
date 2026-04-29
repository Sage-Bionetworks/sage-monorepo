import { Component, computed, input } from '@angular/core';
import { Drug } from '@sagebionetworks/agora/api-client';
import { ALZPED_URL, FDA_URL } from '@sagebionetworks/agora/config';
import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'agora-drug-details-resources',
  imports: [ResourceCardsComponent],
  templateUrl: './drug-details-resources.component.html',
  styleUrls: ['./drug-details-resources.component.scss'],
})
export class DrugDetailsResourcesComponent {
  drug = input.required<Drug>();

  drugResourceCards = computed(() => {
    return [
      {
        imagePath: 'agora-assets/images/alzgps-logo.svg',
        description:
          'Explore information about this drug using the AlzGPS network-based multiomics analysis.',
        link: `https://alzgps.lerner.ccf.org/e/?action=true&type=DRUG&key=${this.drug().drug_bank_id || this.drug().common_name}`,
      },
      {
        imagePath: 'agora-assets/images/alzped-logo.svg',
        description:
          'Search for information on preclinical efficacy studies of candidate AD therapeutics.',
        link: ALZPED_URL,
      },
      {
        imagePath: 'agora-assets/images/fda-logo.svg',
        description: 'Search the FDA for information about this drug.',
        link: FDA_URL,
      },
      {
        imagePath: 'agora-assets/images/open-targets-logo.svg',
        description: 'Visit Open Targets to see more information about this drug.',
        link: `https://platform.opentargets.org/drug/${this.drug().chembl_id}`,
      },
      {
        imagePath: 'agora-assets/images/pharos-logo.svg',
        description:
          'Find information about this drug from the Illuminating the Druggable Genome program.',
        link: `https://pharos.nih.gov/ligands/${this.drug().common_name}`,
      },
      {
        imagePath: 'agora-assets/images/pubmed-logo.svg',
        description: 'Find publications related to this drug on PubMed.',
        link: `https://pubmed.ncbi.nlm.nih.gov/?term=${this.drug().common_name}`,
      },
      {
        imagePath: 'agora-assets/images/taca-logo.svg',
        description: 'View a drug-target network for this drug on TACA.',
        link: `https://taca.lerner.ccf.org/e/?action=true&type=DRUG&key=${this.drug().drug_bank_id || this.drug().common_name}`,
      },
    ];
  });
}
