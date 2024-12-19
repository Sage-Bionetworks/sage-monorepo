import { TargetNomination, Team } from '@sagebionetworks/agora/api-client-angular';

export interface TargetNominationWithTeamData extends TargetNomination {
  team_data?: Team;
}
