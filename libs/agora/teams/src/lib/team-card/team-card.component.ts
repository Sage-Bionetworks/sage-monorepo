import { Component, computed, input } from '@angular/core';
import { Team, TeamMember } from '@sagebionetworks/agora/api-client';

@Component({
  selector: 'agora-team-card',
  imports: [],
  templateUrl: './team-card.component.html',
  styleUrl: './team-card.component.scss',
})
export class TeamCardComponent {
  readonly team = input.required<Team>();
  readonly images = input<Record<string, string>>({});
  readonly placeholderImagePath = 'agora-assets/images/team-member.svg';

  readonly title = computed(() => {
    const team = this.team();
    return team.program ? `${team.program}: ${team.team_full}` : team.team_full;
  });

  readonly description = computed(() => {
    const desc = this.team().description;
    return desc ? desc.replaceAll('‚Äô', '&quot;') : '';
  });

  readonly sortedMembers = computed(() => {
    const members = [...(this.team().members ?? [])];
    members.sort((a: TeamMember, b: TeamMember) => {
      if (a.isprimaryinvestigator !== b.isprimaryinvestigator) {
        return a.isprimaryinvestigator ? -1 : 1; // true values come first
      }
      return a.name.localeCompare(b.name);
    });
    return members;
  });
}
