/**
 * An organization
 */
export interface Organization {
  /**
   * The unique identifier of an account
   */
  id: string;
  login: string;
  /**
   * An email address
   */
  email: string;
  name?: string | null;
  avatarUrl?: string | null;
  websiteUrl?: string | null;
  description?: string | null;
  createdAt: string;
  updatedAt: string;
  type: string;
}
