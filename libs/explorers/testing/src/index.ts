// Export only mocks from this entry point - no MSW imports to avoid parsing issues in browser environments.
export * from './lib/mocks';

// If you need MSW functionality, import it explicitly from:
// import { server } from '@sagebionetworks/explorers/testing/msw';
