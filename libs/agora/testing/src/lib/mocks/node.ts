import { setupServer } from 'msw/node';
import { synapseHandlers as synapseHandlers } from './synapse-handlers';

export const server = setupServer(...synapseHandlers);
