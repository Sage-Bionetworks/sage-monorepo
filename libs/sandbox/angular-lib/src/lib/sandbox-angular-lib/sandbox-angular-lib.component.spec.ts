import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SandboxAngularLibComponent } from './sandbox-angular-lib.component';

describe('SandboxAngularLibComponent', () => {
  let component: SandboxAngularLibComponent;
  let fixture: ComponentFixture<SandboxAngularLibComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SandboxAngularLibComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SandboxAngularLibComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
