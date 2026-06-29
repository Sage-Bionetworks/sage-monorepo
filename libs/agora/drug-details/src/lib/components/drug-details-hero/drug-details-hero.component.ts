import { Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Drug } from '@sagebionetworks/agora/api-client';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH, ROUTE_PATHS } from '@sagebionetworks/agora/config';

export interface DrugBadge {
  label: string;
  link?: string;
  linkText?: string;
}

@Component({
  selector: 'agora-drug-details-hero',
  imports: [RouterLink],
  templateUrl: './drug-details-hero.component.html',
  styleUrls: ['./drug-details-hero.component.scss'],
})
export class DrugDetailsHeroComponent {
  readonly backgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;

  drug = input.required<Drug>();

  badges = computed<DrugBadge[]>(() => {
    const badges = new Map<string, DrugBadge>();
    for (const evidence of this.drug().drug_nominations) {
      if (evidence.combined_with.length === 0) {
        badges.set('nominated', { label: 'Nominated Drug' });
      } else {
        for (const partner of evidence.combined_with) {
          const link = `/${ROUTE_PATHS.DRUG_DETAILS}/${partner.chembl_id}`;
          const linkText = partner.common_name || partner.chembl_id;
          badges.set(link, { label: 'Nominated Combination Therapy with', link, linkText });
        }
      }
    }
    return Array.from(badges.values());
  });
}
