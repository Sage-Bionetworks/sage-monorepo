import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvatarModule } from '../avatar/avatar.module';

import { OrgCardComponent } from './org-card.component';

describe('OrgCardComponent', () => {
  let component: OrgCardComponent;
  let fixture: ComponentFixture<OrgCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrgCardComponent],
      imports: [AvatarModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrgCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
