import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { Drug } from '@sagebionetworks/agora/api-client';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH, ROUTE_PATHS } from '@sagebionetworks/agora/config';

export interface DrugBadge {
  label: string;
  link?: string;
  linkText?: string;
}

@Component({
  selector: 'agora-drug-details-hero',
  imports: [CommonModule],
  templateUrl: './drug-details-hero.component.html',
  styleUrls: ['./drug-details-hero.component.scss'],
})
export class DrugDetailsHeroComponent {
  readonly backgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;

  drug = input.required<Drug>();

  badges = computed<DrugBadge[]>(() => {
    const badges = new Map<string, DrugBadge>();
    for (const evidence of this.drug().drug_nominations) {
      if (evidence.combined_with_chembl_id === null) {
        badges.set('nominated', { label: 'Nominated Drug' });
      } else {
        const link = `${ROUTE_PATHS.DRUG_DETAILS}/${evidence.combined_with_chembl_id}`;
        const linkText = evidence.combined_with_common_name || evidence.combined_with_chembl_id;
        badges.set(link, { label: 'Nominated Combination Therapy, with', link, linkText });
      }
    }
    return Array.from(badges.values());
  });
}
