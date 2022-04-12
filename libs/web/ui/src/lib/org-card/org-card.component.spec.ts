import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvatarModule } from '../avatar/avatar.module';
import { MatCardModule } from '@angular/material/card';
import { OrgCardComponent } from './org-card.component';
import { MOCK_ORG } from './mock-org';
import { HttpClientModule } from '@angular/common/http';

describe('OrgCardComponent', () => {
  let component: OrgCardComponent;
  let fixture: ComponentFixture<OrgCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrgCardComponent],
      imports: [HttpClientModule, AvatarModule, MatCardModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrgCardComponent);
    component = fixture.componentInstance;
    component.org = MOCK_ORG;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('orgAvatar name and avatar should be defined', () => {
    fixture.detectChanges();
    expect(component.orgAvatar).toEqual({
      name: MOCK_ORG.name,
      src: MOCK_ORG.avatarUrl,
      size: 100,
    });
  });

  it('src property of orgAvatar should be empty string', () => {
    component.org.avatarUrl = null;
    fixture.detectChanges();
    expect(component.orgAvatar).toEqual({
      name: MOCK_ORG.name,
      src: '',
      size: 100,
    });
  });

  it('login property of org should be used for orgAvatar name', () => {
    component.org.name = null;
    fixture.detectChanges();
    expect(component.orgAvatar).toEqual({
      name: MOCK_ORG.login.replace(/-/g, ' '),
      src: '',
      size: 100,
    });
  });
});
