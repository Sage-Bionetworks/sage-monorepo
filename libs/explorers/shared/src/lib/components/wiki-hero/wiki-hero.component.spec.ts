import { WikiHeroComponent } from './wiki-hero.component';
import { CommonModule } from '@angular/common';
import { render, screen } from '@testing-library/angular';
import { provideHttpClient } from '@angular/common/http';
import { LOADING_ICON_COLORS_PROVIDER } from '@sagebionetworks/model-ad/testing';
import { MockHeroComponent, MockWikiComponent } from '@sagebionetworks/explorers/testing';

const TITLE = 'Test Title';
const WIKI_ID = '0';

async function setup() {
  const { fixture } = await render(WikiHeroComponent, {
    componentInputs: {
      wikiParams: { ownerId: 'syn123456', wikiId: WIKI_ID },
      heroTitle: TITLE,
    },
    imports: [CommonModule, MockHeroComponent, MockWikiComponent],
    providers: [provideHttpClient(), LOADING_ICON_COLORS_PROVIDER],
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
