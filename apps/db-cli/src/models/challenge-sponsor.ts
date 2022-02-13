import { model, ObjectId, Schema } from 'mongoose';

/* eslint-disable no-unused-vars */
export enum ChallengeSponsorRole {
  ChallengeOrganizer = 'ChallengeOrganizer',
  ComputeProvider = 'ComputeProvider',
  DataProvider = 'DataProvider',
  Funder = 'Funder',
  Other = 'Other'
}
/* eslint-enable no-unused-vars */

export interface ChallengeSponsor {
  _id: ObjectId;
  name: string;
  login?: string;
  roles?: ChallengeSponsorRole[];
  challengeId: ObjectId;
  createdAt: Date;
  updatedAt: Date;
}

const options = {
  collection: 'challenge_sponsor',
  timestamps: true,
};

export const ChallengeSponsorSchema = new Schema<ChallengeSponsor>(
  {
    _id: { type: Schema.Types.ObjectId, required: true },
    name: { type: String, required: true },
    login: { type: String },
    roles: { type: [String], enum: ChallengeSponsorRole, default: [] },
    challengeId: {
      type: Schema.Types.ObjectId,
      ref: 'Challenge',
      required: true,
    },
  },
  options
);

export const ChallengeSponsorModel = model(
  'ChallengeSponsor',
  ChallengeSponsorSchema
);
