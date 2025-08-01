import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { provideRouter } from '@angular/router';
import { DownloadDomImageComponent } from './download-dom-image.component';

describe('DownloadDomImageComponent', () => {
  let fixture: ComponentFixture<DownloadDomImageComponent>;
  let component: DownloadDomImageComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(DownloadDomImageComponent);
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

    expect(document.querySelector('.download-dom-image-panel')).toBeTruthy();
  });

  it('should have a radiobox for each types', () => {
    const button = element.querySelector('button') as HTMLElement;

    expect(button).toBeTruthy();
    button.click();
    fixture.detectChanges();

    const overlayPanel = document.querySelector('.download-dom-image-panel') as HTMLElement;

    expect(overlayPanel).toBeTruthy();
    expect(overlayPanel.querySelectorAll('p-radiobutton>.p-radiobutton')?.length).toEqual(
      component.types.length,
    );
  });
});
