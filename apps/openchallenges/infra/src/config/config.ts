import { AccountConfig } from './account-config';
import { ZoneConfig } from './zone-config';

export type Config = {
  accounts: AccountConfig[];
  zones: ZoneConfig[];
};
