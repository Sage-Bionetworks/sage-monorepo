import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { OrganizationCardComponent } from './organization-card.component';
import { MOCK_ORGANIZATIONS } from './mock-organizations';
import { HttpClientModule } from '@angular/common/http';
import { AvatarComponent } from '../avatar/avatar.component';

describe('OrganizationCardComponent', () => {
  let component: OrganizationCardComponent;
  let fixture: ComponentFixture<OrganizationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrganizationCardComponent],
      imports: [
        HttpClientModule,
        AvatarComponent,
        MatCardModule,
        MatDividerModule,
        MatIconModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationCardComponent);
    component = fixture.componentInstance;
    component.organization = MOCK_ORGANIZATIONS[0];
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('orgAvatar name and avatar should be defined', () => {
    fixture.detectChanges();
    expect(component.organizationAvatar).toEqual({
      name: MOCK_ORGANIZATIONS[0].name,
      src: MOCK_ORGANIZATIONS[0].avatarUrl,
      size: 140,
    });
  });

  it('src property of orgAvatar should be empty string', () => {
    component.organization.avatarUrl = null;
    fixture.detectChanges();
    expect(component.organizationAvatar).toEqual({
      name: MOCK_ORGANIZATIONS[0].name,
      src: '',
      size: 140,
    });
  });

  it('login property of org should be used for orgAvatar name', () => {
    component.organization.name = '';
    fixture.detectChanges();
    expect(component.organizationAvatar).toEqual({
      name: MOCK_ORGANIZATIONS[0].login.replace(/-/g, ' '),
      src: '',
      size: 140,
    });
  });
});
