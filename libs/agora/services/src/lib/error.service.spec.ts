// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { MessageService } from 'primeng/api';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { ErrorService } from './error.service';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Service: Error', () => {
  let errorService: ErrorService;

  beforeEach(async () => {
    errorService = new ErrorService(new MessageService());
  });

  it('should create', () => {
    expect(errorService).toBeDefined();
  });

  it('should handle error', () => {
    const errorMock = new Error('Error');

    expect(function () {
      errorService.handleError(errorMock);
    }).toThrow();
  });
});
