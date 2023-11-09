import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { OrganizationCardComponent } from './organization-card.component';
import { MOCK_ORGANIZATION_CARDS } from './mock-organization-card';
import { HttpClientModule } from '@angular/common/http';
import { AvatarComponent } from '../avatar/avatar.component';

describe('OrganizationCardComponent', () => {
  let component: OrganizationCardComponent;
  let fixture: ComponentFixture<OrganizationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        RouterTestingModule,
        AvatarComponent,
        MatCardModule,
        MatDividerModule,
        MatIconModule,
        OrganizationCardComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationCardComponent);
    component = fixture.componentInstance;
    component.organizationCard = MOCK_ORGANIZATION_CARDS[0];
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('orgAvatar name and avatar should be defined', () => {
    fixture.detectChanges();
    expect(component.organizationAvatar).toEqual({
      name: MOCK_ORGANIZATION_CARDS[0].name,
      src: MOCK_ORGANIZATION_CARDS[0].avatarUrl,
      size: 160,
    });
  });

  it('src property of orgAvatar should be undefined', () => {
    component.organizationCard.avatarUrl = undefined;
    fixture.detectChanges();
    expect(component.organizationAvatar).toEqual({
      name: MOCK_ORGANIZATION_CARDS[0].name,
      src: undefined,
      size: 160,
    });
  });
});
