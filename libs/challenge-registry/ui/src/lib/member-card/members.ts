// TODO: To remove once this interface is available from the client library.
import {
  ChallengeSponsor,
  OrgMembership,
} from '@sagebionetworks/api-client-angular-deprecated';

// Mock up OrgMember and ChallengeMember
// Replace role with roles
export interface OrgMember extends Omit<OrgMembership, 'role'> {
  name: string;
  avatarUrl?: string | null;
  roles: Array<OrgMembership.RoleEnum>;
}

export interface ChallengeMember extends ChallengeSponsor {
  avatarUrl?: string | null;
}
