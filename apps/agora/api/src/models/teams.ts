// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { Team, TeamMember } from '@sagebionetworks/agora/api-client-angular';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const TeamMemberSchema = new Schema<TeamMember>({
  name: { type: String, required: true },
  isPrimaryInvestigator: { type: Boolean, required: true },
  url: String,
});

const TeamSchema = new Schema<Team>(
  {
    team: { type: String, required: true },
    team_full: { type: String, required: true },
    program: { type: String, required: true },
    description: { type: String, required: true },
    members: { type: [TeamMemberSchema], required: true },
  },
  { collection: 'teaminfo' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const TeamCollection = model<Team>('TeamCollection', TeamSchema);
