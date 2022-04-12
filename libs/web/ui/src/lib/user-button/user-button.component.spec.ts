import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AvatarModule } from '../avatar/avatar.module';

import { UserButtonComponent } from './user-button.component';

describe('UserButtonComponent', () => {
  let component: UserButtonComponent;
  let fixture: ComponentFixture<UserButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserButtonComponent],
      imports: [
        HttpClientModule,
        MatIconModule,
        MatMenuModule,
        MatDividerModule,
        AvatarModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
