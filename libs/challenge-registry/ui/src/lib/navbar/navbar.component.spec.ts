import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonGithubComponent } from '../button-github/button-github.component';
import { USER_MENU_ITEMS } from '../user-button/user-menu-items';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { NavbarComponent } from './navbar.component';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NavbarComponent, ButtonGithubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get sections', () => {
    const MOCK_SECTIONS = {
      sss: {
        name: 'awesome org',
        summary: 'awesome summary',
      },
    };
    component.sections = MOCK_SECTIONS;
    expect(component.sections).toEqual(MOCK_SECTIONS);
  });

  it('should emit', () => {
    jest.spyOn(component.userMenuItemSelected, 'emit');
    component.selectUserMenuItem(USER_MENU_ITEMS[0]);
    expect(component.userMenuItemSelected.emit).toHaveBeenCalledWith(
      USER_MENU_ITEMS[0]
    );
  });
});
