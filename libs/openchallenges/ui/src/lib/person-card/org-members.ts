// TODO: To remove once this interface is available from the client library.
import { OrgMembership } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';

export interface OrgMember extends Omit<OrgMembership, 'role'> {
  name: string;
  avatarUrl?: string | null;
  roles: Array<OrgMembership.RoleEnum>;
}
