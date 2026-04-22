// DNA helix geometry.
// Each strand is a chain of S-cubics — one cubic per full wavelength with
// control points on OPPOSITE sides of the midline. Y(t) peaks at
// t = 0.5 ∓ √3/6 and reaches MID ± 0.2887·CTRL_AMP, so the visible amplitude
// is VIS_AMP when CTRL_AMP = VIS_AMP / 0.2887.

export interface RungSpec {
  x: number;
  y1: number;
  y2: number;
}

export const VB_W = 900;
export const WAVELEN = 600;

const MID_Y = 90;
const VIS_AMP = 65;
const CTRL_AMP = VIS_AMP / 0.2887;
// RUNG_STEP must divide WAVELEN evenly so the rung lattice is also
// WAVELEN-periodic — otherwise rungs shift relative to the strands on spin
// reset and flash at the viewBox edges.
const RUNG_STEP = 20;
const LEAD_WAVES = 1;
const TRAIL_WAVES = 3;
// Quarter-wavelength phase shift places midline X-crossings inside the
// viewBox (at 150 / 450 / 750) instead of at the edges, so the leftmost
// and rightmost ovals read symmetric with the middle one.
const PHASE_OFFSET = WAVELEN / 4;

function cubicFactor(t: number): number {
  // 3·t·(1-t)·(1-2t) ∈ [-0.2887, +0.2887]
  return 3 * t * (1 - t) * (1 - 2 * t);
}

export function buildStrandPath(firstUp: boolean): string {
  const startX = -LEAD_WAVES * WAVELEN - PHASE_OFFSET;
  const endX = VB_W + TRAIL_WAVES * WAVELEN;
  const count = Math.ceil((endX - startX) / WAVELEN);
  let d = `M ${startX} ${MID_Y}`;
  for (let i = 0; i < count; i++) {
    const x0 = startX + i * WAVELEN;
    const x1 = x0 + WAVELEN;
    const c1x = x0 + WAVELEN / 3;
    const c2x = x0 + (2 * WAVELEN) / 3;
    const c1y = firstUp ? MID_Y - CTRL_AMP : MID_Y + CTRL_AMP;
    const c2y = firstUp ? MID_Y + CTRL_AMP : MID_Y - CTRL_AMP;
    d += ` C ${c1x} ${c1y} ${c2x} ${c2y} ${x1} ${MID_Y}`;
  }
  return d;
}

export function buildRungs(): RungSpec[] {
  const strandStartX = -LEAD_WAVES * WAVELEN - PHASE_OFFSET;
  const rungEnd = VB_W + TRAIL_WAVES * WAVELEN;
  const out: RungSpec[] = [];
  for (let x = strandStartX; x <= rungEnd; x += RUNG_STEP) {
    const offset = x - strandStartX;
    const local = ((offset % WAVELEN) + WAVELEN) % WAVELEN;
    const factor = cubicFactor(local / WAVELEN);
    const yF = MID_Y - CTRL_AMP * factor;
    const yB = MID_Y + CTRL_AMP * factor;
    if (Math.abs(yF - yB) < 14) continue;
    out.push({ x, y1: yF, y2: yB });
  }
  return out;
}
