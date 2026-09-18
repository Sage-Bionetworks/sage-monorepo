import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { BaseDownloadDomImageComponent } from './base-download-dom-image.component';

describe('BaseDownloadDomImageComponent', () => {
  let fixture: ComponentFixture<BaseDownloadDomImageComponent>;
  let component: BaseDownloadDomImageComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(BaseDownloadDomImageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have overlay', () => {
    expect(element.querySelector('p-popover')).toBeTruthy();
  });

  it('should open overlay on click', () => {
    const button = element.querySelector('button') as HTMLElement;

    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();

    expect(document.querySelector('.base-download-dom-image-panel')).toBeTruthy();
  });

  it('should have a radiobox for each types', () => {
    const button = element.querySelector('button') as HTMLElement;

    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();

    const overlayPanel = document.querySelector('.base-download-dom-image-panel') as HTMLElement;

    expect(overlayPanel).toBeTruthy();
    expect(overlayPanel.querySelectorAll('p-radiobutton>.p-radiobutton-input')?.length).toEqual(
      component.types().length,
    );
  });

  it('should not render a note by default', () => {
    const button = element.querySelector('button') as HTMLElement;
    button.click();
    fixture.detectChanges();

    expect(document.querySelector('.base-download-dom-image-note')).toBeFalsy();
  });

  it('should render the note with a link when provided', () => {
    fixture.componentRef.setInput('note', {
      textBefore: 'See the ',
      linkText: 'Model AD Explorer documentation',
      linkUrl: 'https://help.adknowledgeportal.org/',
      textAfter: ' for links to the study-specific data files.',
    });
    fixture.detectChanges();

    const button = element.querySelector('button') as HTMLElement;
    button.click();
    fixture.detectChanges();

    const note = document.querySelector('.base-download-dom-image-note') as HTMLElement;
    expect(note).toBeTruthy();
    expect(note.textContent).toContain(
      'See the Model AD Explorer documentation for links to the study-specific data files.',
    );

    const link = note.querySelector('a') as HTMLAnchorElement;
    expect(link).toBeTruthy();
    expect(link.textContent).toBe('Model AD Explorer documentation');
    expect(link.getAttribute('href')).toBe('https://help.adknowledgeportal.org/');
    expect(link.getAttribute('target')).toBe('_blank');
  });
});
