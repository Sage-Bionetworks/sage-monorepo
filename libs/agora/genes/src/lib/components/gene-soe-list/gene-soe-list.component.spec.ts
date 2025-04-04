import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GeneSoeListComponent } from './gene-soe-list.component';

describe('Component: Gene SOE List', () => {
  let fixture: ComponentFixture<GeneSoeListComponent>;
  let component: GeneSoeListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneSoeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
