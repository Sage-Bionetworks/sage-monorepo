import { signal } from '@angular/core';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { render, screen } from '@testing-library/angular';
import { userEvent } from '@testing-library/user-event';
import { SignificanceControlsComponent } from './significance-controls.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

const mockComparisonToolFilterService = {
  significanceThreshold: signal(0.05),
  significanceThresholdActive: signal(false),
  setSignificanceThreshold: jest.fn(),
  setSignificanceThresholdActive: jest.fn(),
};

async function setup(threshold = 0.05, active = false) {
  mockComparisonToolFilterService.significanceThreshold.set(threshold);
  mockComparisonToolFilterService.significanceThresholdActive.set(active);

  const { fixture } = await render(SignificanceControlsComponent, {
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      {
        provide: ComparisonToolFilterService,
        useValue: mockComparisonToolFilterService,
      },
      ComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('SignificanceControlsComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockComparisonToolFilterService.significanceThreshold.set(0.05);
    mockComparisonToolFilterService.significanceThresholdActive.set(false);
  });

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should display the current significance threshold', async () => {
    const { component } = await setup(0.01);
    expect(component.significanceThreshold).toBe(0.01);
  });

  it('should display the current significance threshold active state', async () => {
    const { component } = await setup(0.05, true);
    expect(component.significanceThresholdActive).toBe(true);
  });

  it('should update significance threshold when updateSignificanceThreshold is called', async () => {
    const { component } = await setup();

    component.updateSignificanceThreshold(0.01);

    expect(mockComparisonToolFilterService.setSignificanceThreshold).toHaveBeenCalledWith(0.01);
  });

  it('should update significance threshold active state when updateSignificanceThresholdActive is called', async () => {
    const { component } = await setup();

    component.updateSignificanceThresholdActive(true);

    expect(mockComparisonToolFilterService.setSignificanceThresholdActive).toHaveBeenCalledWith(
      true,
    );
  });

  it('should toggle significance threshold active state', async () => {
    const user = userEvent.setup();
    await setup(0.05, false);

    const toggleSwitch = screen.getByRole('switch');
    await user.click(toggleSwitch);

    expect(mockComparisonToolFilterService.setSignificanceThresholdActive).toHaveBeenCalledWith(
      true,
    );
  });
});
