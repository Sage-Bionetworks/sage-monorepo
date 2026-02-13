import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import { BehaviorSubject } from 'rxjs';
import { HomeComponent } from './home.component';

const mobileBgImage = 'model-ad-assets/images/home-arc-bg-mobile.svg';
const desktopBgImage = 'model-ad-assets/images/home-arc-bg.svg';

function createMockBreakpointObserver(matches: boolean) {
  const breakpointSubject = new BehaviorSubject<BreakpointState>({
    matches,
    breakpoints: { '(width < 850px)': matches },
  });

  return {
    observe: jest.fn().mockReturnValue(breakpointSubject.asObservable()),
    subject: breakpointSubject,
  };
}

async function setup(breakpointMatches = false) {
  const mockBreakpointObserver = createMockBreakpointObserver(breakpointMatches);

  const { fixture } = await render(HomeComponent, {
    providers: [
      provideHttpClient(),
      provideRouter([]),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      { provide: BreakpointObserver, useValue: mockBreakpointObserver },
    ],
  });

  return { fixture, mockBreakpointObserver };
}

describe('HomeComponent', () => {
  it('should render heading', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { name: /find the right model for your research/i }),
    ).toBeInTheDocument();
  });

  describe('background image breakpoint logic', () => {
    it('should initialize with desktop background image when viewport is wide', async () => {
      const { fixture } = await setup(false);
      expect(fixture.componentInstance.backgroundImage()).toBe(desktopBgImage);
    });

    it('should initialize with mobile background image when viewport is narrow', async () => {
      const { fixture } = await setup(true);
      expect(fixture.componentInstance.backgroundImage()).toBe(mobileBgImage);
    });

    it('should update background image when breakpoint changes', async () => {
      const { fixture, mockBreakpointObserver } = await setup(false);

      expect(fixture.componentInstance.backgroundImage()).toBe(desktopBgImage);

      mockBreakpointObserver.subject.next({
        matches: true,
        breakpoints: { '(width < 850px)': true },
      });

      expect(fixture.componentInstance.backgroundImage()).toBe(mobileBgImage);
    });
  });
});
