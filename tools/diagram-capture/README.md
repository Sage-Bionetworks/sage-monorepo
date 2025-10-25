# Diagram Capture Tool

A Node.js tool for capturing Mermaid diagrams as animated WebM videos or GIF files using Playwright and FFmpeg.

## Features

- 📹 Record Mermaid diagram animations as WebM video
- 🎞️ Convert to high-quality GIF with optimized palette
- 📐 Automatically detects diagram dimensions for perfect framing
- ⚡ Smooth animations with automatic recording
- 🎨 Advanced color palette generation and dithering

## Prerequisites

### Node.js Dependencies

The required packages are already included in the monorepo's `package.json`:

- `@playwright/test` - Headless browser automation and video recording

### Playwright Browsers

Playwright browsers (Chromium) are automatically installed on first run using `pnpx playwright install chromium`.

If you prefer to install manually beforehand:

```bash
playwright install chromium --with-deps
```

Or install all Playwright browsers:

```bash
playwright install --with-deps
```

### FFmpeg (for GIF conversion)

Install FFmpeg for video-to-GIF conversion:

```bash
sudo apt-get install -y ffmpeg
```

## Usage

### Generate WebM Video

```bash
node tools/diagram-capture/capture-diagram.mjs <path-to-html-file>
```

Example:

```bash
node tools/diagram-capture/capture-diagram.mjs docs/architecture/bixarena-architecture.html
```

Output: Creates `<diagram-name>.webm` in the same directory as the HTML file.

### Generate GIF

```bash
node tools/diagram-capture/capture-diagram.mjs <path-to-html-file> --gif
```

Example:

```bash
node tools/diagram-capture/capture-diagram.mjs docs/architecture/bixarena-architecture.html --gif
```

Output: Creates `<diagram-name>.gif` in the same directory as the HTML file.

## How It Works

1. **Launch headless Chromium** using Playwright
2. **Load the HTML file** containing the Mermaid diagram
3. **Wait for rendering** (8 seconds for icons and animations to load)
4. **Measure diagram dimensions** by querying the rendered SVG
5. **Relaunch browser with video recording** enabled
6. **Set viewport** to match diagram size perfectly (avoiding distortion)
7. **Record the animation** with automatic WebM capture
8. **For GIF**: Convert WebM to GIF using FFmpeg with optimized palette

## Quality Settings

### WebM Recording

- **Format**: VP8/VP9 codec (Playwright default)
- **Resolution**: Auto-detected from diagram
- **Device Scale**: 2x (retina quality)
- **Recording**: Native Playwright video recording

### GIF Conversion

- **FPS**: 24 (near cinema quality)
- **Colors**: 256 (full palette)
- **Dithering**: Bayer algorithm with scale 5 (smooth gradients)
- **Palette Generation**: Diff mode (optimized for animated content)
- **Loop**: Infinite

## File Sizes

Typical file sizes for a ~700x800px diagram:

- **WebM**: ~100-200 KB (5 seconds)
- **GIF**: ~500-800 KB (5 seconds, 24fps)

GIF files are larger due to format limitations but are widely compatible.

## Troubleshooting

### "Executable doesn't exist" error

You need to install Playwright browsers:

```bash
npx playwright install chromium --with-deps
```

### Diagram appears cut off or distorted

The script automatically measures the diagram, but if you still see issues:

- Increase the padding in the dimension calculation (edit the script around line 65)
- Check that the diagram has fully rendered before measurement

### Animation not captured

Increase the recording duration by editing the script around line 110:

```javascript
await recordingPage.waitForTimeout(10000); // 10 seconds
```

## Advanced Usage

### Custom Recording Duration

Edit `capture-diagram.mjs` around line 110:

```javascript
await recordingPage.waitForTimeout(10000); // 10 seconds
```

### Custom GIF Framerate

Edit `capture-diagram.mjs` around line 140, change the `fps=24` value:

```javascript
const ffmpegCmd = `ffmpeg -i "${webmFile}" -vf "fps=15,scale=...`;
```

Lower values = smaller file size, less smooth animation.

## License

Part of the Sage Bionetworks monorepo. See root LICENSE.txt for details.
