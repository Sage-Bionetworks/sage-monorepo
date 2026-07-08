import { ChangeDetectionStrategy, Component, computed, effect, input, signal } from '@angular/core';

@Component({
  selector: 'bixarena-avatar',
  imports: [],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    '[class.size-sm]': "size() === 'sm'",
    '[class.size-md]': "size() === 'md'",
    '[class.size-lg]': "size() === 'lg'",
    '[class.shape-circle]': "shape() === 'circle'",
    '[class.shape-bare]': "shape() === 'bare'",
  },
})
export class AvatarComponent {
  readonly imageUrl = input<string | null>(null);
  readonly name = input<string>('');
  readonly size = input<'sm' | 'md' | 'lg'>('md');
  readonly shape = input<'circle' | 'bare'>('circle');

  readonly imageError = signal(false);
  readonly initials = computed(() => this.name().slice(0, 2).toUpperCase());

  constructor() {
    effect(() => {
      this.imageUrl();
      this.imageError.set(false);
    });
  }

  onImageError() {
    this.imageError.set(true);
  }
}
