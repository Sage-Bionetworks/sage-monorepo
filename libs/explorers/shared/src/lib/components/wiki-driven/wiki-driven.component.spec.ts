import { WikiDrivenComponent } from './wiki-driven.component';
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
const CLASS_NAME = 'class-name';

const mockRouteDataSubject = new BehaviorSubject<{ wikiId: string }>({ wikiId: 'test-wiki-id' });

async function setup(options?: {
  componentInputs?: Record<string, any>;
  imports?: any[];
  providers?: any[];
}) {
  mockRouteDataSubject.next({ wikiId: WIKI_ID || '' });

  const mockActivatedRoute = {
    data: mockRouteDataSubject.asObservable(),
  };

  // const user = userEvent.setup();
  const { fixture } = await render(WikiDrivenComponent, {
    componentInputs: {
      title: TITLE,
      wikiId: WIKI_ID,
      className: CLASS_NAME,
      ...options?.componentInputs,
    },
    imports: [CommonModule, MockHeroComponent, MockWikiComponent, ...(options?.imports || [])],
    providers: [provideHttpClient(), { provide: ActivatedRoute, useValue: mockActivatedRoute }],
  });

  const component = fixture.componentInstance;
  return { fixture, component };
}

describe('WikiDrivenComponent', () => {
  it('should create the component', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should display the title', async () => {
    await setup({ componentInputs: { title: TITLE } });
    expect(screen.getByText(TITLE)).toBeInTheDocument();
  });

  it('should render the hero component', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('explorers-hero')).toBeTruthy();
  });
});
