export * from './bioDomains.service';
import { BioDomainsService } from './bioDomains.service';
export * from './dataversion.service';
import { DataversionService } from './dataversion.service';
export * from './distribution.service';
import { DistributionService } from './distribution.service';
export * from './genes.service';
import { GenesService } from './genes.service';
export * from './teams.service';
import { TeamsService } from './teams.service';
export const APIS = [
  BioDomainsService,
  DataversionService,
  DistributionService,
  GenesService,
  TeamsService,
];
