import { Schema } from 'mongoose';
import { Account, AccountModel, AccountType } from './account';
import validator from 'validator';

export interface User extends Account {
  email: string;
  name: string;
  avatarUrl?: string;
  bio?: string;
  passwordHash: string;
}

export const UserSchema = new Schema<User>({
  email: {
    type: String,
    required: true,
    validate: [validator.isEmail, 'invalid email'],
  },
  name: { type: String, required: true },
  avatarUrl: { type: String, validate: [validator.isURL, 'invalid avatarUrl'] },
  bio: { type: String },
  passwordHash: {
    type: String,
    required: true,
    default:
      'pbkdf2:sha256:150000$L0qjkMps$37a3151f2bfe7d95f2fd990d9d8696c40354afc8e1bc713797ed45a191c33ce0',
  },
});

UserSchema.pre<User>('validate', function (next) {
  this.type = AccountType.User;
  next();
});

export const UserModel = AccountModel.discriminator<User>(
  AccountType.User,
  UserSchema,
  'Account.User'
);
