import { ExperimentalValidation, Team } from '@sagebionetworks/agora/api-client';

export interface ExperimentalValidationWithTeamData extends ExperimentalValidation {
  team_data?: Team;
}
