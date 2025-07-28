import { TestBed } from '@angular/core/testing';
import { BreakpointConfigService } from './breakpoint-config.service';

describe('BreakpointConfigService', () => {
  let service: BreakpointConfigService;
  let mockGetPropertyValue: jest.Mock;
  let mockGetComputedStyle: jest.Mock;

  beforeEach(() => {
    mockGetPropertyValue = jest.fn();
    mockGetComputedStyle = jest.fn().mockReturnValue({
      getPropertyValue: mockGetPropertyValue,
    });

    jest.spyOn(window, 'getComputedStyle').mockImplementation(mockGetComputedStyle);

    TestBed.configureTestingModule({});
    service = TestBed.inject(BreakpointConfigService);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
    jest.restoreAllMocks();
  });

  it('should return default breakpoint when CSS custom property is not defined', () => {
    mockGetPropertyValue.mockReturnValue('');

    const breakpoint = service.largeBreakpoint;

    expect(breakpoint).toBe('992px');
    expect(mockGetComputedStyle).toHaveBeenCalledWith(document.documentElement);
    expect(mockGetPropertyValue).toHaveBeenCalledWith('--lg-breakpoint');
  });

  it('should return CSS custom property value when defined', () => {
    mockGetPropertyValue.mockReturnValue('1024px');

    const breakpoint = service.largeBreakpoint;

    expect(breakpoint).toBe('1024px');
    expect(mockGetComputedStyle).toHaveBeenCalledWith(document.documentElement);
    expect(mockGetPropertyValue).toHaveBeenCalledWith('--lg-breakpoint');
  });

  it('should cache the computed value and not call getComputedStyle multiple times', () => {
    mockGetPropertyValue.mockReturnValue('1024px');

    const firstCall = service.largeBreakpoint;
    const secondCall = service.largeBreakpoint;

    expect(firstCall).toBe('1024px');
    expect(secondCall).toBe('1024px');
    expect(mockGetComputedStyle).toHaveBeenCalledTimes(1);
    expect(mockGetPropertyValue).toHaveBeenCalledTimes(1);
  });
});
