import { Schema } from 'mongoose';
import { Account, AccountModel, AccountType } from './account';
import validator from 'validator';

export interface Organization extends Account {
  email: string;
  name: string;
  avatarUrl?: string;
  websiteUrl?: string;
  description?: string;
}

export const OrganizationSchema = new Schema<Organization>({
  email: {
    type: String,
    required: true,
    validate: [validator.isEmail, 'invalid email'],
  },
  name: { type: String, required: true },
  avatarUrl: { type: String, validate: [validator.isURL, 'invalid avatarUrl'] },
  websiteUrl: {
    type: String,
    validate: [validator.isURL, 'invalid websiteUrl'],
  },
  description: { type: String },
});

OrganizationSchema.pre<Organization>('validate', function(next) {
  this.type = AccountType.Organization;
  next();
});

export const OrganizationModel = AccountModel.discriminator<Organization>(
  AccountType.Organization,
  OrganizationSchema,
  'Account.Organization'
);
