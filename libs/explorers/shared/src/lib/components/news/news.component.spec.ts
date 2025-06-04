import { render } from '@testing-library/angular';
import { NewsComponent } from './news.component';
import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

class MockSynapseApiService {
  getWikiContent = jest
    .fn()
    .mockReturnValue(new BehaviorSubject({ markdown: 'Test wiki content' }));
}

@Component({
  selector: 'explorers-wiki-driven',
  template: `<div>Wiki Content for ID: {{ wikiId }}</div>`,
})
class MockWikiDrivenComponent {
  @Input() wikiId = '';
  @Input() title = '';
  @Input() className = '';
}

describe('NewsComponent', () => {
  const mockActivatedRoute = {
    data: new BehaviorSubject<{ wikiId?: string }>({ wikiId: 'test-wiki-id' }),
  };

  async function setup(routeData = { wikiId: 'test-wiki-id' }) {
    mockActivatedRoute.data.next(routeData);

    const result = await render(NewsComponent, {
      imports: [MockWikiDrivenComponent],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        { provide: MockSynapseApiService, useClass: MockSynapseApiService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    });

    const fixture = result.fixture;
    const component = fixture.componentInstance;
    return { fixture, component };
  }

  afterAll(() => {
    mockActivatedRoute.data.complete();
  });

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should set wikiId from route data on init', async () => {
    const { component, fixture } = await setup();

    // Wait for ngOnInit to complete
    await fixture.whenStable();

    expect(component.wikiId).toBe('test-wiki-id');
  });
});
