import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { HomeComponent } from './home.component';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8111' } },
    battle: { promptLengthLimit: 5000 },
    links: { termsOfService: '' },
  },
};

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideHttpClient(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(HomeComponent);
    fixture.detectChanges();
  });

  it('renders the hero header and four section components in order', () => {
    const root = fixture.nativeElement as HTMLElement;
    const children = Array.from(root.querySelectorAll('.page-content > *')).map((el) =>
      el.tagName.toLowerCase(),
    );
    expect(children).toEqual([
      'header',
      'bixarena-composer-section',
      'bixarena-stats-section',
      'bixarena-trending-section',
      'bixarena-community-quest-section',
      'bixarena-leaderboard-section',
    ]);
  });

  it('renders the hero title with biomedical highlighted', () => {
    const root = fixture.nativeElement as HTMLElement;
    const title = root.querySelector('.hero .title');
    expect(title?.textContent?.replace(/\s+/g, ' ').trim()).toBe(
      'Your vote shapes the future ofbiomedical AI',
    );
    expect(title?.querySelector('em')?.textContent).toBe('biomedical');
  });
});
