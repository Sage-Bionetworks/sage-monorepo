import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NominationFormComponent } from './nomination-form.component';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('NominationFormComponent', () => {
  let component: NominationFormComponent;
  let fixture: ComponentFixture<NominationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [provideHttpClient(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(NominationFormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
