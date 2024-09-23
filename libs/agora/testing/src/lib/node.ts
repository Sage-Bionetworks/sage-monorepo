import { setupServer } from 'msw/node';
import { synapseHandlers } from './handlers/synapse-handlers';
import { teamHandlers } from './handlers/team-handlers';

export const server = setupServer(...teamHandlers, ...synapseHandlers);
