export * from './dataversion.service';
import { DataversionService } from './dataversion.service';
export * from './team.service';
import { TeamService } from './team.service';
export * from './teamMember.service';
import { TeamMemberService } from './teamMember.service';
export const APIS = [DataversionService, TeamService, TeamMemberService];
