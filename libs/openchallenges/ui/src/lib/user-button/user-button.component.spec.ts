import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { USER_MENU_ITEMS } from './user-menu-items';

import { UserButtonComponent } from './user-button.component';
import { AvatarComponent } from '../avatar/avatar.component';

describe('UserButtonComponent', () => {
  let component: UserButtonComponent;
  let fixture: ComponentFixture<UserButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        MatIconModule,
        MatMenuModule,
        MatDividerModule,
        AvatarComponent,
        UserButtonComponent,
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

  it('should emit', () => {
    jest.spyOn(component.menuItemSelected, 'emit');
    component.selectMenuItem(USER_MENU_ITEMS[0]);
    expect(component.menuItemSelected.emit).toHaveBeenCalledWith(
      USER_MENU_ITEMS[0],
    );
  });
});
