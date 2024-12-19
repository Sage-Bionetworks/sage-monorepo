import * as d3 from 'd3';

const a = Math.pow(3, 0.25);
// Given an area, compute the side length of a hexagon with that area.
function sideLength(area: number) {
  return a * Math.sqrt(2 * (area / 9));
}

// Generate the 6 vertices of a unit hexagon.
const basePoints = d3
  .range(6)
  .map((p) => (Math.PI / 3) * p)
  .map((p) => ({
    x: Math.cos(p),
    y: Math.sin(p),
  }));

export const hexagonSymbol = {
  draw: function (context: any, size: number) {
    // Scale the unit hexagon's vertices by the desired size of the hexagon.
    const len = sideLength(size);
    const points = basePoints.map(({ x, y }) => ({
      x: x * len,
      y: y * len,
    }));

    // Move to the first vertex of the hexagon.
    const { x, y } = points[0];
    context.moveTo(x, y);
    // Line-to the remaining vertices of the hexagon.
    for (let p = 1; p < points.length; p++) {
      const { x, y } = points[p];
      context.lineTo(x, y);
    }
    // Close the path to connect the last vertex back to the first.
    context.closePath();
  },
};
