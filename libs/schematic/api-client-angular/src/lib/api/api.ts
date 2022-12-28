export * from './organization.service';
import { OrganizationService } from './organization.service';
export * from './user.service';
import { UserService } from './user.service';
export const APIS = [OrganizationService, UserService];
