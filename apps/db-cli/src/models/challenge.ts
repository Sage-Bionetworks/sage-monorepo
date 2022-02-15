import { model, ObjectId, Schema } from 'mongoose';
import validator from 'validator';

/* eslint-disable no-unused-vars */
export enum ChallengeStatus {
  Active = 'active',
  Upcoming = 'upcoming',
  Completed = 'completed',
}

export enum ChallengeDifficulty {
  GoodForBeginners = 'GoodForBeginners',
  Intermediate = 'Intermediate',
  Advanced = 'Advanced',
}

export enum ChallengeSubmissionType {
  DockerImage = 'DockerImage',
  PredictionFile = 'PredictionFile',
  Other = 'Other',
}

export enum ChallengeIncentiveType {
  Monetary = 'Monetary',
  Publication = 'Publication',
  SpeakingEngagement = 'SpeakingEngagement',
  Other = 'Other',
}
/* eslint-enable no-unused-vars */

export interface Challenge {
  _id: ObjectId;
  name: string;
  displayName: string;
  description: string;
  fullName?: string; // TODO make required after fixing production JSON
  ownerId: ObjectId;
  websiteUrl?: string;
  status: ChallengeStatus;
  startDate?: Date;
  endDate?: Date;
  platformId: ObjectId;
  readmeId: ObjectId;
  topics?: string[];
  doi?: string;
  createdAt: Date;
  updatedAt: Date;
  featured: boolean;
  participantCount: number;
  viewCount: number;
  starredCount: number;
  inputDataTypes: string[];
  difficulty: ChallengeDifficulty;
  submissionTypes: ChallengeSubmissionType[];
  incentiveTypes: ChallengeIncentiveType[];
}

const options = {
  collection: 'challenge',
  timestamps: true,
};

export const ChallengeSchema = new Schema<Challenge>(
  {
    _id: { type: Schema.Types.ObjectId, required: true },
    name: { type: String, required: true },
    displayName: { type: String, minlength: 3, maxlength: 60, required: true },
    description: { type: String, minlength: 3, maxlength: 280, required: true },
    fullName: { type: String }, // TODO make required and unique after fixing production JSON
    ownerId: {
      type: Schema.Types.ObjectId,
      ref: 'Account',
      required: true,
    },
    websiteUrl: {
      type: String,
      validate: [validator.isURL, 'invalid websiteUrl'],
    },
    status: { type: String, enum: ChallengeStatus, required: true },
    startDate: { type: Date },
    endDate: { type: Date },
    platformId: {
      type: Schema.Types.ObjectId,
      ref: 'ChallengePlatform',
      required: true,
    },
    readmeId: {
      type: Schema.Types.ObjectId,
      ref: 'ChallengeReadme',
      required: true,
    },
    topics: { type: [String], default: [] },
    doi: { type: String },
    featured: { type: Boolean, default: false },
    participantCount: {
      type: Number,
      minimum: 0,
      default: 0,
      validate: [Number.isInteger, 'invalid participantCount'],
    },
    viewCount: {
      type: Number,
      minimum: 0,
      default: 0,
      validate: [Number.isInteger, 'invalid viewCount'],
    },
    starredCount: {
      type: Number,
      minimum: 0,
      default: 0,
      validate: [Number.isInteger, 'invalid starredCount'],
    },
    inputDataTypes: { type: [String], default: [] },
    difficulty: { type: String, enum: ChallengeDifficulty, required: false },
    submissionTypes: {
      type: [String],
      enum: ChallengeSubmissionType,
      required: false,
    },
    incentiveTypes: {
      type: [String],
      enum: ChallengeIncentiveType,
      required: false,
    },
  },
  options
);

export const ChallengeModel = model('Challenge', ChallengeSchema);
