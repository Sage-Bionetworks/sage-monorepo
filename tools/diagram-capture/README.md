# Diagram Capture Tool

A Node.js tool for capturing Mermaid diagrams as animated WebM videos or GIF files using Puppeteer and FFmpeg.

## Features

- 📹 Record Mermaid diagram animations as WebM video
- 🎞️ Convert to high-quality GIF with optimized palette
- 📐 Automatically detects diagram dimensions for perfect framing
- ⚡ High framerate (30fps WebM, 24fps GIF) for smooth animations
- 🎨 Advanced color palette generation and dithering

## Prerequisites

### Node.js Dependencies

The required packages are already included in the monorepo's `package.json`:

- `puppeteer` - Headless Chrome automation
- `puppeteer-screen-recorder` - Screen recording for Puppeteer

### Chrome Browser

Install Chrome for Puppeteer (headless):

```bash
npx puppeteer browsers install chrome
```

### System Dependencies (Linux/Ubuntu)

For headless Chrome to run, you need several system libraries:

```bash
sudo apt-get update
sudo apt-get install -y \
  libatk-bridge2.0-0t64 \
  libxkbcommon0 \
  libxcomposite1 \
  libxdamage1 \
  libxfixes3 \
  libxrandr2 \
  libgbm1 \
  libasound2t64 \
  libpango-1.0-0 \
  libcairo2 \
  libatspi2.0-0t64 \
  libcups2t64 \
  libdbus-1-3 \
  libdrm2 \
  libgtk-3-0t64 \
  libnss3 \
  libxss1
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

1. **Launch headless Chrome** using Puppeteer
2. **Load the HTML file** containing the Mermaid diagram
3. **Wait for rendering** (8 seconds for icons and animations to load)
4. **Measure diagram dimensions** by querying the rendered SVG
5. **Set viewport** to match diagram size perfectly (avoiding distortion)
6. **Reload and record** with correct dimensions at 30fps
7. **For GIF**: Convert WebM to GIF using FFmpeg with optimized palette

## Quality Settings

### WebM Recording

- **FPS**: 30 (smooth animation)
- **Bitrate**: 10 Mbps (high quality)
- **Resolution**: Auto-detected from diagram
- **Device Scale**: 2x (retina quality)

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

### "Chrome missing shared libraries" error

You need to install the system dependencies listed above. On Ubuntu 24.04, make sure to use the `t64` variants of packages (e.g., `libasound2t64` instead of `libasound2`).

### "File format is not supported" error

The library only supports WebM recording. For GIF, the script automatically converts WebM to GIF using FFmpeg.

### Diagram appears cut off or distorted

The script automatically measures the diagram, but if you still see issues:

- Increase the padding in the dimension calculation (line ~45)
- Check that the diagram has fully rendered before measurement

### Animation not captured

Increase the recording duration:

- Edit line ~88: Change `setTimeout(resolve, 5000)` to a higher value (e.g., 10000 for 10 seconds)

## Advanced Usage

### Custom Recording Duration

Edit `capture-diagram.mjs` line ~88:

```javascript
await new Promise((resolve) => setTimeout(resolve, 10000)); // 10 seconds
```

### Custom GIF Framerate

Edit `capture-diagram.mjs` line ~102, change the `fps=24` value:

```javascript
const ffmpegCmd = `ffmpeg -i "${webmFile}" -vf "fps=15,scale=...`;
```

Lower values = smaller file size, less smooth animation.

## License

Part of the Sage Bionetworks monorepo. See root LICENSE.txt for details.
