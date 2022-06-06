import { model, ObjectId, Schema } from 'mongoose';
import validator from 'validator';

export interface ChallengePlatform {
  _id: ObjectId;
  name: string;
  displayName: string;
  websiteUrl?: string;
  avatarUrl?: string;
  createdAt: Date;
  updatedAt: Date;
}

const options = {
  collection: 'challenge_platform',
  timestamps: true,
};

export const ChallengePlatformSchema = new Schema<ChallengePlatform>(
  {
    _id: { type: Schema.Types.ObjectId, required: true },
    name: { type: String, required: true, unique: true },
    displayName: { type: String, required: true, unique: true },
    websiteUrl: {
      type: String,
      validate: [validator.isURL, 'invalid websiteUrl'],
    },
    avatarUrl: {
      type: String,
      validate: [validator.isURL, 'invalid avatarUrl'],
    },
  },
  options
);

export const ChallengePlatformModel = model(
  'ChallengePlatform',
  ChallengePlatformSchema
);
