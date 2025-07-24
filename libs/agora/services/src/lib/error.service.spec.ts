import { TestBed } from '@angular/core/testing';
import { MessageService } from 'primeng/api';
import { ErrorService } from './error.service';

// Mock MessageService
const mockMessageService = {
  clear: jest.fn(),
  add: jest.fn(),
};

describe('ErrorService', () => {
  let service: ErrorService;

  beforeEach(() => {
    // Clear all mocks before each test
    jest.clearAllMocks();

    // Mock console.error
    console.error = jest.fn();

    TestBed.configureTestingModule({
      providers: [ErrorService, { provide: MessageService, useValue: mockMessageService }],
    });

    service = TestBed.inject(ErrorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should handle error', () => {
    const errorMock = new Error('Error');

    expect(function () {
      service.handleError(errorMock);
    }).toThrow();
  });
});
