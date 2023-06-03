import { AccountConfig } from './account-config';
import { ZoneConfig } from './zone-config';

export type DnsConfig = {
  accounts: AccountConfig[];
  zones: ZoneConfig[];
};
