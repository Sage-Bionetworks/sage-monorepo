import { TargetNomination, Team } from '@sagebionetworks/agora/api-client';

export interface TargetNominationWithTeamData extends TargetNomination {
  team_data?: Team;
}
