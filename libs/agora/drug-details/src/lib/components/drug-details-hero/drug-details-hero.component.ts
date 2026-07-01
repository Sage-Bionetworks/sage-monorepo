import { Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Drug } from '@sagebionetworks/agora/api-client';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH, ROUTE_PATHS } from '@sagebionetworks/agora/config';

export interface DrugBadge {
  label: string;
  partners?: { link: string; linkText: string; separator: string }[];
}

// Human-readable list separator: "A", "A & B", "A, B & C".
function partnerSeparator(index: number, count: number): string {
  if (index === count - 1) return '';
  if (index === count - 2) return ' & ';
  return ', ';
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
      const combinedWith = evidence.combined_with ?? [];
      if (combinedWith.length === 0) {
        badges.set('nominated', { label: 'Nominated Drug' });
      } else {
        const partners = combinedWith.map((partner, index) => ({
          link: `/${ROUTE_PATHS.DRUG_DETAILS}/${partner.chembl_id}`,
          linkText: partner.common_name || partner.chembl_id,
          separator: partnerSeparator(index, combinedWith.length),
        }));
        const key = partners.map((partner) => partner.link).join('|');
        badges.set(key, { label: 'Nominated Combination Therapy with', partners });
      }
    }
    return Array.from(badges.values());
  });
}
