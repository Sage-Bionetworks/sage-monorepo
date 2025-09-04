export * from './bio-domain.service';
import { BioDomainService } from './bio-domain.service';
export * from './data-version.service';
import { DataVersionService } from './data-version.service';
export * from './distribution.service';
import { DistributionService } from './distribution.service';
export * from './gene.service';
import { GeneService } from './gene.service';
export * from './team.service';
import { TeamService } from './team.service';
export const APIS = [
  BioDomainService,
  DataVersionService,
  DistributionService,
  GeneService,
  TeamService,
];
