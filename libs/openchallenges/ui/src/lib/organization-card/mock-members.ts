export interface OrganizationMember {
  orgId: number;
  name: string;
  avatarUrl?: string;
}

export const MOCK_MEMBERS: OrganizationMember[] = [
  {
    orgId: 1,
    name: 'Awesome User',
  },
  {
    orgId: 1,
    name: 'Jane Doe',
  },
  {
    orgId: 1,
    name: 'John Smith',
  },
  {
    orgId: 1,
    name: 'Ash Ketchum',
  },
  {
    orgId: 1,
    name: 'Misty',
  },
  {
    orgId: 1,
    name: 'Brock',
  },
];
