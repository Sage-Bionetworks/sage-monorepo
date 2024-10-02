import { TestBed, ComponentFixture } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { ModalLinkComponent } from './modal-link.component';

describe('Component: Modal Link', () => {
  let fixture: ComponentFixture<ModalLinkComponent>;
  let component: ModalLinkComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [BrowserAnimationsModule],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(ModalLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have modal', () => {
    expect(element.querySelector('p-dialog.p-element')).toBeTruthy();
  });

  it('should open modal on click', () => {
    const toggle = element.querySelector('.modal-link-inner') as HTMLElement;
    const modal = element.querySelector('p-dialog.p-element') as HTMLElement;

    expect(toggle).toBeTruthy();
    expect(modal).toBeTruthy();

    toggle.click();
    fixture.detectChanges();

    expect(modal.getAttribute('ng-reflect-visible')).toEqual('true');
  });
});
