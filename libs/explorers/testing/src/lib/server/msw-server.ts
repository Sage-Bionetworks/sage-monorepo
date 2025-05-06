import { setupServer } from 'msw/node';
import { synapseHandlers } from '../handlers/synapse-handlers';

export const server = setupServer(...synapseHandlers);
