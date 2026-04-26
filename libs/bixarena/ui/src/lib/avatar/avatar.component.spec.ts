import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { AvatarComponent } from './avatar.component';

describe('AvatarComponent', () => {
  let fixture: ComponentFixture<AvatarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [AvatarComponent] }).compileComponents();
    fixture = TestBed.createComponent(AvatarComponent);
  });

  function setInputs(values: Record<string, unknown>) {
    for (const [key, value] of Object.entries(values)) {
      fixture.componentRef.setInput(key, value);
    }
    fixture.detectChanges();
  }

  it('renders an <img> when imageUrl is provided', () => {
    setInputs({ imageUrl: 'https://example.com/me.png', name: 'Jane Doe' });
    const img = fixture.debugElement.query(By.css('img.avatar-img'));
    expect(img).toBeTruthy();
    expect(img.nativeElement.getAttribute('src')).toBe('https://example.com/me.png');
    expect(img.nativeElement.getAttribute('alt')).toBe('Jane Doe');
    expect(fixture.debugElement.query(By.css('.avatar-initials'))).toBeNull();
  });

  it('falls back to initials from name when imageUrl is null', () => {
    setInputs({ imageUrl: null, name: 'Jane Doe' });
    expect(fixture.debugElement.query(By.css('img.avatar-img'))).toBeNull();
    const initials = fixture.debugElement.query(By.css('.avatar-initials'));
    expect(initials.nativeElement.textContent.trim()).toBe('JA');
    expect(initials.nativeElement.getAttribute('aria-label')).toBe('Jane Doe');
  });

  it('uppercases initials regardless of input casing', () => {
    setInputs({ imageUrl: null, name: 'acme corp' });
    const initials = fixture.debugElement.query(By.css('.avatar-initials'));
    expect(initials.nativeElement.textContent.trim()).toBe('AC');
  });

  it('reactively swaps from initials to image when imageUrl is set', () => {
    setInputs({ imageUrl: null, name: 'Claude' });
    expect(fixture.debugElement.query(By.css('.avatar-initials'))).toBeTruthy();

    setInputs({ imageUrl: 'https://example.com/anthropic.svg' });
    expect(fixture.debugElement.query(By.css('.avatar-initials'))).toBeNull();
    expect(fixture.debugElement.query(By.css('img.avatar-img'))).toBeTruthy();
  });

  it('applies size and shape classes on the host', () => {
    setInputs({ size: 'sm', shape: 'bare', name: 'X' });
    const host = fixture.debugElement.nativeElement as HTMLElement;
    expect(host.classList.contains('size-sm')).toBe(true);
    expect(host.classList.contains('shape-bare')).toBe(true);
  });
});
