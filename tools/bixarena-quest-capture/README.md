# Quest Capture Tool

A Node.js tool for capturing the BioArena Community Quest section as animated WebM videos or GIF files using Playwright and FFmpeg.

## Features

- 📹 Record quest section with carousel animation as WebM video
- 🎞️ Convert to high-quality GIF with optimized palette
- 📐 Automatically detects quest section dimensions for perfect framing
- 🎯 Captures only the quest section from the running app (no need for standalone HTML)
- ⚡ Smooth carousel animations with configurable recording duration
- 🎨 Advanced color palette generation and dithering for high-quality GIFs

## Prerequisites

### BioArena App

The BioArena web app must be running locally:

```bash
# From monorepo root
nx serve bixarena-app
# App will be available at http://localhost:8100
```

Make sure `APP_COMMUNITY_QUEST_ENABLED=true` is set in your environment.

### Node.js Dependencies

The required packages are already included in the monorepo's `package.json`:

- `@playwright/test` - Headless browser automation and video recording

### Playwright Browsers

Playwright browsers (Chromium) are automatically installed on first run using `pnpx playwright install chromium`.

If you prefer to install manually beforehand:

```bash
pnpm dlx playwright install chromium --with-deps
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
node tools/quest-capture/capture-quest.mjs
```

Output: Creates `bixarena-quest.webm` in `tools/quest-capture/` directory.

### Generate GIF

```bash
node tools/quest-capture/capture-quest.mjs --gif
```

Output: Creates `bixarena-quest.gif` in `tools/quest-capture/` directory.

### Custom Options

```bash
# Custom FPS (lower = smaller file, less smooth)
node tools/quest-capture/capture-quest.mjs --gif --fps=10

# Custom recording duration (in milliseconds)
# Default is 12000ms = 2 carousel rotations at 6s each
node tools/quest-capture/capture-quest.mjs --gif --record-ms=18000

# Custom app URL (if running on different port)
node tools/quest-capture/capture-quest.mjs --url=http://localhost:7860

# Combine options
node tools/quest-capture/capture-quest.mjs --gif --fps=15 --record-ms=18000
```

## How It Works

1. **Connect to running app** at http://localhost:8100
2. **Wait for page load** and quest section to render
3. **Measure quest section dimensions** by querying DOM elements
4. **Launch recording browser** with viewport sized to quest section
5. **Navigate and scroll** to quest section
6. **Record carousel animation** for specified duration (default: 12 seconds)
7. **For GIF**: Convert WebM to GIF using FFmpeg with optimized palette

## Quality Settings

### WebM Recording

- **Format**: VP8/VP9 codec (Playwright default)
- **Resolution**: Auto-detected from quest section
- **Device Scale**: 2x (retina quality)
- **Recording**: Native Playwright video recording

### GIF Conversion

- **FPS**: 15 (default, configurable)
- **Colors**: 256 (full palette)
- **Dithering**: Bayer algorithm with scale 5 (smooth gradients)
- **Palette Generation**: Diff mode (optimized for animated content)
- **Loop**: Infinite

## File Sizes

Typical file sizes for a ~1200x800px quest section:

- **WebM**: ~200-400 KB (12 seconds)
- **GIF (15fps)**: ~1-2 MB (12 seconds)
- **GIF (10fps)**: ~700 KB - 1.2 MB (12 seconds)

Lower FPS reduces file size but makes animation less smooth. 15 fps is a good balance.

## Carousel Timing

The quest carousel rotates images every 6 seconds. Recommended recording durations:

- **6 seconds**: Shows 1 complete rotation (minimal)
- **12 seconds**: Shows 2 complete rotations (default, recommended)
- **18 seconds**: Shows 3 complete rotations (comprehensive)

## Troubleshooting

### "Could not connect" error

Make sure the BioArena app is running:

```bash
nx serve bixarena-app
```

Check that it's accessible at http://localhost:8100

### "Quest section not found" error

Make sure the Community Quest feature is enabled:

```bash
export APP_COMMUNITY_QUEST_ENABLED=true
nx serve bixarena-app
```

### "Executable doesn't exist" error

You need to install Playwright browsers:

```bash
pnpm dlx playwright install chromium --with-deps
```

### Quest section appears cut off

The script automatically measures the section, but if you see issues:

- Increase the padding in the dimension calculation (edit the script around line 110)
- Try increasing `PRE_WAIT_MS` to allow more time for rendering

### Carousel not animating

Increase the recording duration:

```bash
node tools/quest-capture/capture-quest.mjs --gif --record-ms=18000
```

## Advanced Usage

### For LinkedIn Posts

For optimal LinkedIn compatibility:

```bash
# Generate GIF with moderate FPS for smaller file size
node tools/quest-capture/capture-quest.mjs --gif --fps=12 --record-ms=12000
```

LinkedIn's file size limits:

- Images/GIFs: Up to 5 MB
- If your GIF exceeds this, reduce FPS or duration

### For Documentation

For high-quality documentation:

```bash
# Use higher FPS for smoother animation
node tools/quest-capture/capture-quest.mjs --gif --fps=20 --record-ms=12000
```

## Example Output

The tool will capture the entire Community Quest section including:

- Quest header and badge
- Title and description
- Image carousel (with rotation animation)
- Progress bar and statistics
- Time remaining
- "Contribute a Block Now" button
- Builders list with tier badges
- Contributor tier legend
- Credits section

## License

Part of the Sage Bionetworks monorepo. See root LICENSE.txt for details.
