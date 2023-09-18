/**
 * State of an org membership
 */
export type OrgMembershipState = 'active' | 'pending';

/**
 * Role of an org membership
 */
export type OrgMembershipRole = 'admin' | 'member';

/**
 * Information about an org membership
 */
export interface OrgMembership {
  /**
   * The unique identifier of the org membership
   */
  id: string;
  /**
   * The name of the org membership
   */
  name: string;
  /**
   * The avatar url of the org membership
   */
  avatarUrl?: string | null;
  /**
   * The state of the member in the organization. The `pending` state indicates the user has not yet accepted an invitation.
   */
  state: OrgMembershipState;
  /**
   * The user\'s membership type in the organization.
   */
  roles: Array<OrgMembershipRole>;
  /**
   * The unique identifier of an organization
   */
  organizationId: string;
  /**
   * The unique identifier of a user
   */
  userId: string;
}
