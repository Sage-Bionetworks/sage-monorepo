import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import { SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { BehaviorSubject } from 'rxjs';
import { HomeComponent } from './home.component';

const upperArcDesktop = 'model-ad-assets/images/home-arc-bg-upper.svg';
const upperArcMobile = 'model-ad-assets/images/home-arc-bg-upper-mobile.svg';
const lowerArcDesktop = 'model-ad-assets/images/home-arc-bg-lower.svg';
const lowerArcMobile = 'model-ad-assets/images/home-arc-bg-lower-mobile.svg';

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

  describe('species toggle', () => {
    it('should show mouse tiles by default', async () => {
      await setup();
      expect(screen.getByText('Model Overview')).toBeInTheDocument();
      expect(screen.getByText('Differential Expression')).toBeInTheDocument();
      expect(screen.getByText('Disease Correlation')).toBeInTheDocument();
    });

    it('should swap to the marmoset tiles when marmoset is selected', async () => {
      const user = userEvent.setup();
      await setup();

      await user.click(screen.getByText('Marmoset Models'));

      expect(screen.getByText('Marmoset Model Overview')).toBeInTheDocument();
      expect(screen.getByText('Marmoset Model Search')).toBeInTheDocument();
      expect(screen.getByAltText('marmoset icon')).toBeInTheDocument();
      expect(screen.queryByText('Model Overview')).not.toBeInTheDocument();
    });
  });

  describe('organism-specific content', () => {
    it('should show the mouse methodology description and stats by default', async () => {
      await setup();
      expect(screen.getByText(/MODEL-AD comprises two research centers/i)).toBeInTheDocument();
      expect(screen.getByText('20K+')).toBeInTheDocument();
      expect(screen.getByText('15+')).toBeInTheDocument();
    });

    it('should show the marmoset methodology description and stats when marmoset is selected', async () => {
      const user = userEvent.setup();
      await setup();

      await user.click(screen.getByText('Marmoset Models'));

      expect(
        screen.getByText(/MARMO-AD is establishing marmoset models of Alzheimer's disease/i),
      ).toBeInTheDocument();
      expect(screen.getByText('30K+')).toBeInTheDocument();
      expect(screen.getByText('1+')).toBeInTheDocument();
      expect(screen.queryByText('20K+')).not.toBeInTheDocument();
      expect(screen.queryByText(/MODEL-AD comprises/i)).not.toBeInTheDocument();
    });
  });

  describe('hero arc images', () => {
    it('should use the desktop upper arc when the viewport is wide', async () => {
      const { fixture } = await setup(false);
      expect(fixture.componentInstance.upperArcImage()).toBe(upperArcDesktop);
    });

    it('should use the mobile arcs when the viewport is narrow', async () => {
      const { fixture } = await setup(true);
      expect(fixture.componentInstance.upperArcImage()).toBe(upperArcMobile);
      expect(fixture.componentInstance.lowerArcImage()).toBe(lowerArcMobile);
    });

    it('should swap both arcs when the breakpoint changes', async () => {
      const { fixture, mockBreakpointObserver } = await setup(false);

      expect(fixture.componentInstance.upperArcImage()).toBe(upperArcDesktop);
      expect(fixture.componentInstance.lowerArcImage()).toBe(lowerArcDesktop);

      mockBreakpointObserver.subject.next({
        matches: true,
        breakpoints: { '(width < 850px)': true },
      });

      expect(fixture.componentInstance.upperArcImage()).toBe(upperArcMobile);
      expect(fixture.componentInstance.lowerArcImage()).toBe(lowerArcMobile);
    });
  });
});
