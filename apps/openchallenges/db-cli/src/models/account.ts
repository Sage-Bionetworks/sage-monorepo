import { model, ObjectId, Schema } from 'mongoose';

/* eslint-disable no-unused-vars */
export enum AccountType {
  User = 'User',
  Organization = 'Organization',
}
/* eslint-enable no-unused-vars */

export interface Account {
  _id: ObjectId;
  login: string;
  type: AccountType;
  createdAt: Date;
  updatedAt: Date;
}

const options = {
  discriminatorKey: '_cls',
  collection: 'account',
  timestamps: true,
};

export const AccountSchema = new Schema<Account>(
  {
    _id: { type: Schema.Types.ObjectId, required: true },
    login: { type: String, required: true },
    type: { type: String, enum: AccountType, required: true },
  },
  options
);

export const AccountModel = model('Account', AccountSchema);
