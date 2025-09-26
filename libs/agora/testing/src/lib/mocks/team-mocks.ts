/* eslint quotes: off */

import { Team, TeamMember, TeamsList as TeamsResponse } from '@sagebionetworks/agora/api-client';

export const mockTeamMember: TeamMember = {
  isprimaryinvestigator: true,
  name: 'Philip De Jager',
  url: 'http://www.cumc.columbia.edu/dept/taub/faculty-dejager.html',
};

export const mockTeam1: Team = {
  team: 'Chang Lab',
  team_full: 'The Chang Lab at the University of Arizona',
  program: 'Community Contributed',
  description:
    'The Chang Team at the University of Arizona, led by Rui Chang, develops novel computational systems biology models to discover drug targets for in-silico precision medicine for AD. The target nominations are based on work performed by the Chang Team at the Icahn School of Medicine at Mt. Sinai.',
  members: [mockTeamMember],
};

export const mockTeam2: Team = {
  team: 'Emory',
  team_full: 'Emory University',
  program: 'AMP-AD',
  description:
    'The Emory AMP-AD team, led by Allan Levey, focuses on the generation and analysis of proteomic data to understand neurodegenerative disease. Targets nominated by the Emory team have been identified through the analysis of differential protein expression and co-expression network analysis.',
  members: [mockTeamMember],
};

export const mockTeam3: Team = {
  team: 'Emory-Sage-SGC',
  team_full: 'Emory University - Sage Bionetworks - Structural Genomics Consortium',
  program: 'TREAT-AD',
  description:
    "The mission of the Emory-Sage-Structural Genomics Consortium (SGC) TREAT-AD center is to diversify the portfolio of drug targets in Alzheimer's disease (AD). We aim to catalyze research into biological pathways that have been associated with disease from deep molecular profiling and bioinformatic evaluation of AD in the human brain within the National Institute on Aging's (NIA) Accelerating Medicines Partnership-Alzheimer's Disease (AMP-AD) consortium.  Many of these potential AD drug targets are predicted to reside among the thousands of human proteins that historically have received little attention and for which there are few reagents, such as quality-verified antibodies, cell lines, assays or chemical probes. To catalyze their investigation, we are developing and openly distributing experimental tools, including chemical probes, for broad use in the evaluation of a diverse set of novel AD targets.",
  members: [mockTeamMember],
};

export const mockTeam4: Team = {
  team: 'MSSM',
  team_full: 'Icahn School of Medicine at Mount Sinai',
  program: 'AMP-AD',
  description:
    "The Icahn School of Medicine at Mount Sinai AMP-AD team, led by Eric Schadt, Bin Zhang, Jun Zhu, Michelle Ehrlich, Vahram Haroutunian, Samuel Gandy, Koichi Iijima, and Scott Noggle focuses on developing a multiscale network approach to elucidating the complexity of Alzheimer's disease.",
  members: [mockTeamMember],
};

export const mockTeam5: Team = {
  team: 'Community',
  team_full: 'Icahn School of Medicine at Mount Sinai',
  program: 'AMP-AD',
  description: 'Arizona State University',
  members: [mockTeamMember],
};

export const teamsResponseMock: TeamsResponse = {
  items: [mockTeam1, mockTeam2, mockTeam3, mockTeam4, mockTeam5],
};
