export interface TeamMember {
  name: string;
  isprimaryinvestigator: boolean;
  url?: string;
}

export type TeamMemberImage = {
  _id: string;
  length: number;
  chunkSize: number;
  uploadDate: Date;
  filename: string;
  metadata: object;
};

export interface Team {
  team: string;
  team_full: string;
  program: string;
  description: string;
  members: TeamMember[];
}

export type TeamsResponse = {
  items: Team[];
};
