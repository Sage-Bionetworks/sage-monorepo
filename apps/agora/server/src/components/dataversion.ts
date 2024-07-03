import { NextFunction, Request, Response } from 'express';
import { DataVersionCollection } from '../models/dataversion';

export async function getDataVersion() {
  return await DataVersionCollection.findOne();
}

export async function dataVersionRoute(
  req: Request,
  res: Response,
  next: NextFunction
) {
  try {
    const result = await getDataVersion();
    res.json(result);
  } catch (err) {
    next(err);
  }
}
