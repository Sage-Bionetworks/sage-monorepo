/**
 * The account status of a user
 */
export type UserStatus = 'pending' | 'approved' | 'disabled' | 'blacklist';

/**
 * A simple user
 */
export interface User {
  /**
   * The unique identifier of an account
   */
  id?: number;
  login: string;
  /**
   * An email address.
   */
  email: string;
  name?: string | null;
  status?: UserStatus;
  avatarUrl?: string | null;
  createdAt: string;
  updatedAt: string;
  type: string;
  bio?: string | null;
}
