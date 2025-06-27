import { CommonModule } from '@angular/common';
import { render, screen } from '@testing-library/angular';
import { DialogModule } from 'primeng/dialog';
import { ModalLinkComponent } from './modal-link.component';

// Mocks for child components
import { MockSvgIconComponent, MockWikiComponent } from '@sagebionetworks/explorers/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('ModalLinkComponent', () => {
  async function setup({ text = 'Open Modal', title = 'Modal Title', textColor = '#000' }) {
    const result = await render(ModalLinkComponent, {
      componentInputs: { text: text, title: title, textColor: textColor },
      imports: [CommonModule, DialogModule, MockSvgIconComponent, MockWikiComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    const component = result.fixture.componentInstance;
    return { result, component };
  }

  it('should render the link text', async () => {
    const expectedText = 'Open Modal';
    await setup({ text: expectedText });
    expect(screen.getByText(expectedText)).toBeInTheDocument();
  });
});
