import { WikiHeroComponent } from './wiki-hero.component';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { render, screen } from '@testing-library/angular';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

// Mock components
@Component({
  selector: 'explorers-hero',
  template: '<div>Mock Hero Component</div>',
})
class MockHeroComponent {}

@Component({
  selector: 'explorers-wiki',
  template: '<div>Mock Wiki Component with ID: {{wikiId}}</div>',
})
class MockWikiComponent {
  wikiId = '';
}

const TITLE = 'Test Title';
const WIKI_ID = '0';

const mockRouteDataSubject = new BehaviorSubject<{ wikiId: string; heroTitle: string }>({
  wikiId: 'test-wiki-id',
  heroTitle: 'test-hero-title',
});

async function setup() {
  mockRouteDataSubject.next({
    wikiId: WIKI_ID || '',
    heroTitle: TITLE,
  });

  const mockActivatedRoute = {
    data: mockRouteDataSubject.asObservable(),
  };

  // const user = userEvent.setup();
  const { fixture } = await render(WikiHeroComponent, {
    imports: [CommonModule, MockHeroComponent, MockWikiComponent],
    providers: [provideHttpClient(), { provide: ActivatedRoute, useValue: mockActivatedRoute }],
  });

  const component = fixture.componentInstance;
  return { fixture, component };
}

describe('WikiHeroComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should display the title', async () => {
    await setup();
    expect(screen.getByText(TITLE)).toBeInTheDocument();
  });

  it('should render the hero component', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('explorers-hero')).toBeTruthy();
  });
});
