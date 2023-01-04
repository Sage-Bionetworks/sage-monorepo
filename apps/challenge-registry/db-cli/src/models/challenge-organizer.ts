import { model, ObjectId, Schema } from 'mongoose';

/* eslint-disable no-unused-vars */
export enum ChallengeOrganizerRole {
  ChallengeLead = 'ChallengeLead',
  InfrastructureLead = 'InfrastructureLead',
}
/* eslint-enable no-unused-vars */

export interface ChallengeOrganizer {
  _id: ObjectId;
  name: string;
  login?: string;
  roles?: ChallengeOrganizerRole[];
  challengeId: ObjectId;
  createdAt: Date;
  updatedAt: Date;
}

const options = {
  collection: 'challenge_organizer',
  timestamps: true,
};

export const ChallengeOrganizerSchema = new Schema<ChallengeOrganizer>(
  {
    _id: { type: Schema.Types.ObjectId, required: true },
    name: { type: String, required: true },
    login: { type: String },
    roles: { type: [String], enum: ChallengeOrganizerRole, default: [] },
    challengeId: {
      type: Schema.Types.ObjectId,
      ref: 'Challenge',
      required: true,
    },
  },
  options
);

export const ChallengeOrganizerModel = model(
  'ChallengeOrganizer',
  ChallengeOrganizerSchema
);
