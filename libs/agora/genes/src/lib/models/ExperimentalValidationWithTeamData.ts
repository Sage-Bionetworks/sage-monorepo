import { ExperimentalValidation, Team } from '@sagebionetworks/agora/api-client-angular';

export interface ExperimentalValidationWithTeamData extends ExperimentalValidation {
  team_data?: Team;
}
