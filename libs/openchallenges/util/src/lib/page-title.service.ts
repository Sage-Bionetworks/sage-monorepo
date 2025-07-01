import { inject, Injectable, OnDestroy } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { BehaviorSubject, combineLatest, Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PageTitleService implements OnDestroy {
  private readonly bodyTitle = inject(Title);

  private title: BehaviorSubject<string> = new BehaviorSubject<string>('');
  private numNotifications: BehaviorSubject<number> = new BehaviorSubject<number>(0);

  private subscription!: Subscription;

  constructor() {
    combineLatest([this.title, this.numNotifications]).subscribe(
      ([title, numNotifications]) => {
        title = title !== '' ? `${title}` : '';
        const notification = numNotifications > 0 ? `(${numNotifications}) ` : '';
        this.bodyTitle.setTitle(`${notification}${title}`);
      },
      (err) => console.error(err),
    );
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  setTitle(title: string): void {
    this.title.next(title);
  }

  setNumNotifications(numNotifications: number): void {
    this.numNotifications.next(numNotifications);
  }
}
