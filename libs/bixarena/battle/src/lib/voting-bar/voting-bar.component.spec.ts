import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VotingBarComponent } from './voting-bar.component';

describe('VotingBarComponent', () => {
  let component: VotingBarComponent;
  let fixture: ComponentFixture<VotingBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VotingBarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VotingBarComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('phase', 'voting');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit model1 vote', () => {
    const spy = jest.fn();
    component.vote.subscribe(spy);
    component.voteModel1();
    expect(spy).toHaveBeenCalledWith('model1');
  });

  it('should emit tie vote', () => {
    const spy = jest.fn();
    component.vote.subscribe(spy);
    component.voteTie();
    expect(spy).toHaveBeenCalledWith('tie');
  });

  it('should emit model2 vote', () => {
    const spy = jest.fn();
    component.vote.subscribe(spy);
    component.voteModel2();
    expect(spy).toHaveBeenCalledWith('model2');
  });
});
