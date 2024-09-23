import { setupServer } from 'msw/node';
import { synapseHandlers } from './synapse-handlers';

export const server = setupServer(...synapseHandlers);
