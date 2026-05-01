import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

interface Dot {
  x: number;
  y: number;
}

interface Line {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

interface Seat {
  x: number;
  y: number;
  r: number;
  o: number;
}

const CX = 600;
const CY = 350;

function buildVomitoriums(): Dot[] {
  const out: Dot[] = [];
  for (let i = 0; i < 36; i++) {
    const a = (i / 36) * Math.PI * 2;
    out.push({ x: CX + Math.cos(a) * 680, y: CY + Math.sin(a) * 349 });
  }
  return out;
}

function buildRadials(): Line[] {
  const out: Line[] = [];
  for (let i = 0; i < 48; i++) {
    const a = (i / 48) * Math.PI * 2;
    out.push({
      x1: CX + Math.cos(a) * 380,
      y1: CY + Math.sin(a) * 200,
      x2: CX + Math.cos(a) * 700,
      y2: CY + Math.sin(a) * 360,
    });
  }
  return out;
}

function buildSeats(): Seat[] {
  const out: Seat[] = [];
  for (let i = 0; i < 240; i++) {
    const a = (i / 240) * Math.PI * 2 + (i % 3) * 0.018;
    const ringT = (i % 4) / 4;
    const r = 410 + ringT * 270;
    const ry = 215 + ringT * 145;
    out.push({
      x: CX + Math.cos(a) * r,
      y: CY + Math.sin(a) * ry,
      r: 0.6 + ((i * 7) % 10) / 18,
      o: 0.2 + ((i * 13) % 9) / 18,
    });
  }
  return out;
}

/**
 * Decorative amphitheatre-floorplan backdrop. Drop inside a wrapper that uses
 * `position: relative` (anchors the absolute host) and `isolation: isolate`
 * (creates a stacking context so the host's z-index: -1 sits cleanly behind
 * siblings, no explicit z-index needed on each foreground element).
 *
 * Usage:
 *   .wrapper { position: relative; isolation: isolate; }
 *
 *   <div class="wrapper">
 *     <bixarena-blueprint-bg />
 *     <!-- foreground content paints above in DOM order -->
 *   </div>
 *
 * Inputs:
 *   - `scale` — geometry size relative to the SVG viewBox. Default 0.85.
 *
 * CSS hooks (set on a `bixarena-blueprint-bg` selector in the parent):
 *   - `opacity` — overall dimming.
 *   - `mask-image` — gradient masks for edge fades.
 *   - `height` / `inset` / `bottom` — restrict vertical coverage.
 *   - `--blueprint-stroke` — stroke/fill color (theme-aware via tokens).
 */
@Component({
  selector: 'bixarena-blueprint-bg',
  imports: [],
  templateUrl: './blueprint-bg.component.html',
  styleUrl: './blueprint-bg.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BlueprintBgComponent {
  readonly scale = input(0.85);

  readonly vomitoriums = buildVomitoriums();
  readonly radials = buildRadials();
  readonly seats = buildSeats();

  readonly transform = computed(
    () => `translate(${CX} ${CY}) scale(${this.scale()}) translate(${-CX} ${-CY})`,
  );
}
