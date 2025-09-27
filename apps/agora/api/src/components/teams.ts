// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { mongo, connection } from 'mongoose';
import { Request, Response, NextFunction } from 'express';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { setHeaders, cache } from '../helpers';
import { TeamCollection } from '../models';
import { Team } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// GridFs
// -------------------------------------------------------------------------- //

let fsBucket: any;

connection.once('open', function () {
  const db = connection.getClient().db();
  fsBucket = new mongo.GridFSBucket(db);
});

// -------------------------------------------------------------------------- //
// Teams
// -------------------------------------------------------------------------- //

export async function getTeams() {
  let teams: Team[] | undefined = cache.get('teams');

  if (teams) {
    return teams;
  }

  teams = await TeamCollection.find().lean().exec();
  // sort null programs last
  sortNullProgramsForTeamsLast(teams);

  cache.set('teams', teams);
  return teams;
}

export function sortNullProgramsForTeamsLast(teams: Team[]): void {
  // default sort has null entries first so we will
  // sort nulls last since Sage is null and should be last
  // as per business requirements
  if (!teams) return teams;
  teams.sort((a, b) => {
    if (!a.program) {
      return 1;
    } else if (!b.program) {
      return -1;
    } else {
      return (a.program + a.team_full).localeCompare(b.program + b.team_full);
    }
  });
}

export async function teamsRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const teams = await getTeams();
    setHeaders(res);
    res.json({ items: teams });
  } catch (err) {
    next(err);
  }
}

// -------------------------------------------------------------------------- //
// Team member images
// -------------------------------------------------------------------------- //

export async function getTeamMemberImage(name: string) {
  name = name.toLowerCase().replace(/[- ]/g, '_');

  let files = await fsBucket.find({ filename: name + '.jpg' }).toArray();
  if (!files.length) {
    files = await fsBucket.find({ filename: name + '.jpeg' }).toArray();
    if (!files.length) {
      files = await fsBucket.find({ filename: name + '.png' }).toArray();
    }
  }

  return files[0] || undefined;
}

export async function teamMemberImageRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.params || !req.params.name) {
    res.status(404).send('Not found');
    return;
  }

  try {
    const name = req.params.name.trim();
    const file = await getTeamMemberImage(name);

    if (file?._id) {
      const stream = fsBucket.openDownloadStream(file._id);

      stream.on('data', (chunk: Buffer) => {
        res.write(chunk);
      });

      stream.on('error', () => {
        res.sendStatus(404);
      });

      stream.on('end', () => {
        res.end();
      });
    } else {
      res.end();
    }
  } catch (err) {
    next(err);
  }
}
