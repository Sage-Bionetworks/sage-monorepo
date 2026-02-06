# Quest Capture Tool

A Node.js tool for capturing the BioArena Community Quest section as animated WebM videos or GIF files using Playwright and FFmpeg.

## Features

- 📹 Record quest section with carousel animation as WebM video
- 🎞️ Convert to high-quality GIF with optimized palette
- 🎬 Convert to MP4 video (H.264) for much smaller file sizes (recommended for LinkedIn)
- 📐 Automatically detects quest section dimensions for perfect framing
- 🎯 Captures only the quest section from the running app (no need for standalone HTML)
- ⚡ Smooth carousel animations with configurable recording duration
- ✂️ Automatic trimming to remove loading artifacts and page transitions
- 🎨 Dark theme support for professional appearance
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
node tools/bixarena-quest-capture/capture-quest.mjs
```

Output: Creates `bixarena-quest.webm` in `tools/bixarena-quest-capture/` directory.

### Generate MP4 Video (Recommended for LinkedIn)

```bash
node tools/bixarena-quest-capture/capture-quest.mjs --mp4 --record-ms=30000 --trim-start=6
```

Output: Creates `bixarena-quest.mp4` (~500KB-2MB for 30s video, well under 5MB limit).

### Generate GIF

```bash
node tools/bixarena-quest-capture/capture-quest.mjs --gif
```

Output: Creates `bixarena-quest.gif` (Note: GIFs are much larger than MP4 for same content).

### Custom Options

```bash
# Custom FPS (lower = smaller file, less smooth)
node tools/quest-capture/capture-quest.mjs --gif --fps=10

# Custom recording duration (in milliseconds)
# Default is 12000ms = 2 carousel rotations at 6s each
node tools/quest-capture/capture-quest.mjs --gif --record-ms=18000

# Custom trim duration (removes N seconds from start to eliminate loading)
# Default is 5 seconds
node tools/quest-capture/capture-quest.mjs --gif --trim-start=6

# Scale output size (percentage, default: 100)
# 75 = 75% of original dimensions
node tools/quest-capture/capture-quest.mjs --gif --scale=75

# Custom app URL (if running on different port)
node tools/quest-capture/capture-quest.mjs --url=http://localhost:7860

# Combine options for smaller file size
node tools/quest-capture/capture-quest.mjs --gif --fps=8 --scale=75 --trim-start=6
```

## How It Works

1. **Connect to running app** at http://localhost:8100
2. **Wait for page load** and quest section to render in dark theme
3. **Measure quest section dimensions** by querying DOM elements
4. **Launch recording browser** with viewport sized to quest section
5. **Navigate and position** to quest section instantly
6. **Record carousel animation** for specified duration (default: 12 seconds + 5s extra)
7. **Trim video** to remove first 5 seconds (loading artifacts and transitions)
8. **For GIF**: Convert trimmed WebM to GIF using FFmpeg with optimized palette

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

### MP4 (H.264) - RECOMMENDED

- **30 seconds, 15fps, 100% scale**: ~800KB - 1.5MB ✅
- **30 seconds, 15fps, 90% scale**: ~600KB - 1MB ✅
- **12 seconds, 15fps, 100% scale**: ~300KB - 600KB ✅

### GIF (Not Recommended for Large Videos)

- **12 seconds, 15fps, 100% scale**: ~1-2 MB
- **12 seconds, 10fps, 100% scale**: ~700 KB - 1.2 MB
- **12 seconds, 8fps, 75% scale**: ~400-800 KB
- **30 seconds, 8fps, 75% scale**: ~2-5 MB (often exceeds LinkedIn limit)

### WebM

- **12 seconds**: ~200-400 KB
- **30 seconds**: ~500-800 KB

**Why MP4 is better than GIF:**

- MP4 uses modern video compression (10-20x smaller for same quality)
- GIF uses 256-color palette (poor for photos/screenshots)
- LinkedIn, Twitter, and all social platforms support MP4
- MP4 maintains higher quality at smaller file sizes

**For LinkedIn (< 5 MB limit):** Use `--mp4` format. Even 30-second videos at full quality stay under 2 MB.

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

### Loading animation or page transitions visible in output

If you still see the Gradio loading indicator or page scrolling in the final output:

1. Increase the trim duration to remove more from the start:

```bash
node tools/quest-capture/capture-quest.mjs --gif --trim-start=6
```

2. Or if that removes too much of the actual carousel, increase the recording duration:

```bash
node tools/quest-capture/capture-quest.mjs --gif --record-ms=18000 --trim-start=6
```

## Advanced Usage

### For LinkedIn Posts

For optimal LinkedIn compatibility (< 5 MB limit):

```bash
# RECOMMENDED: Use MP4 format - shows all 5 carousel images, ~1-2 MB
node tools/bixarena-quest-capture/capture-quest.mjs --mp4 --record-ms=30000 --trim-start=6

# If you need smaller file size:
node tools/bixarena-quest-capture/capture-quest.mjs --mp4 --record-ms=30000 --trim-start=6 --scale=90

# For highest quality at full resolution:
node tools/bixarena-quest-capture/capture-quest.mjs --mp4 --record-ms=30000 --trim-start=6 --fps=20
```

**Why MP4 instead of GIF?**

- MP4 is 10-20x smaller than GIF for the same content
- LinkedIn fully supports MP4 videos in posts
- Better quality, smaller file size, smoother playback
- GIFs struggle with photographic content and large dimensions
- **LinkedIn auto-loops videos in the feed** (just like GIFs)

**Video Looping:**

- LinkedIn, Twitter, and most social platforms automatically loop videos in feeds
- No special looping flag needed - platforms handle this automatically
- Videos behave just like GIFs when posted to social media

**LinkedIn file size limits:**

- Videos/GIFs: Up to 5 MB
- MP4 format easily stays under 2 MB for 30-second videos

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
