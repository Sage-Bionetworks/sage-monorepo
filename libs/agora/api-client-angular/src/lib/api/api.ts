export * from './dataversion.service';
import { DataversionService } from './dataversion.service';
export * from './teamMemberImage.service';
import { TeamMemberImageService } from './teamMemberImage.service';
export * from './teams.service';
import { TeamsService } from './teams.service';
export const APIS = [DataversionService, TeamMemberImageService, TeamsService];
