import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';
import { execSync, exec } from 'child_process';
import { promisify } from 'util';
import fs from 'fs/promises';

const execAsync = promisify(exec);

// Ensure Playwright browsers are installed
function ensurePlaywrightBrowsers() {
  console.log('Ensuring Playwright browsers are installed...');
  try {
    execSync('pnpx playwright install chromium --with-deps', { stdio: 'inherit' });
  } catch (error) {
    console.error('Failed to install Playwright browsers:', error.message);
    process.exit(1);
  }
}
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Parse command line arguments
const args = process.argv.slice(2);
const inputFile = args.find((arg) => !arg.startsWith('--'));
const format = args.includes('--gif') ? 'gif' : 'webm';
const outputExtension = format === 'gif' ? 'gif' : 'webm';

// Validate input
if (!inputFile) {
  console.error('Usage: node capture-diagram.mjs <html-file> [--gif]');
  console.error('');
  console.error('Examples:');
  console.error('  node capture-diagram.mjs diagram.html');
  console.error('  node capture-diagram.mjs diagram.html --gif');
  process.exit(1);
}

// Resolve paths
const htmlFile = path.resolve(inputFile);
const htmlDir = path.dirname(htmlFile);
const htmlBasename = path.basename(htmlFile, '.html');
const webmFile = path.join(htmlDir, `${htmlBasename}.webm`);
const outputFile = path.join(htmlDir, `${htmlBasename}.${outputExtension}`);

// Check if input file exists
try {
  await fs.access(htmlFile);
} catch (error) {
  console.error(`Error: File not found: ${htmlFile}`);
  process.exit(1);
}

console.log(`Input file: ${htmlFile}`);
console.log(`Output format: ${format.toUpperCase()}`);

// Ensure Playwright browsers are installed
ensurePlaywrightBrowsers();

// Launch browser without video recording first to measure dimensions
const browser = await chromium.launch({
  headless: true,
});
const context = await browser.newContext();
const page = await context.newPage();

console.log('Loading diagram to measure dimensions...');
await page.goto('file://' + htmlFile, { waitUntil: 'networkidle' });

// Wait for Mermaid to fully render
console.log('Waiting for diagram to render...');
await page.waitForTimeout(8000);

// Get the actual diagram dimensions
const dimensions = await page.evaluate(() => {
  const svg = document.querySelector('svg');
  if (!svg) return { width: 1920, height: 1080 };

  const bbox = svg.getBoundingClientRect();
  // Add some padding
  const padding = 40;
  return {
    width: Math.ceil(bbox.width + padding * 2),
    height: Math.ceil(bbox.height + padding * 2),
  };
});

console.log(`Diagram dimensions: ${dimensions.width}x${dimensions.height}`);

// Close the initial browser
await browser.close();

// Launch browser again with video recording enabled
console.log('Starting recording...');
const recordingBrowser = await chromium.launch({
  headless: true,
});

const recordingContext = await recordingBrowser.newContext({
  viewport: {
    width: dimensions.width,
    height: dimensions.height,
  },
  deviceScaleFactor: 2,
  recordVideo: {
    dir: htmlDir,
    size: {
      width: dimensions.width,
      height: dimensions.height,
    },
  },
});

const recordingPage = await recordingContext.newPage();

// Load the page with recording
console.log('Loading diagram with recording...');
await recordingPage.goto('file://' + htmlFile, { waitUntil: 'networkidle' });
await recordingPage.waitForTimeout(8000);

// Wait a bit while recording
console.log('Recording...');
await recordingPage.waitForTimeout(5000); // Capture animation for 5 seconds

console.log('Stopping recording...');
await recordingContext.close();
await recordingBrowser.close();

// Get the recorded video file
const videoFiles = await fs.readdir(htmlDir);
const videoFile = videoFiles.find(
  (file) => file.endsWith('.webm') && file !== path.basename(webmFile),
);

if (videoFile) {
  const tempVideoPath = path.join(htmlDir, videoFile);
  await fs.rename(tempVideoPath, webmFile);
} else {
  console.error('Error: Video file not found after recording');
  process.exit(1);
}

// Convert to GIF if requested
if (format === 'gif') {
  console.log('Converting WebM to GIF...');
  // Improved GIF conversion with better quality and higher framerate
  const ffmpegCmd = `ffmpeg -i "${webmFile}" -vf "fps=24,scale=${dimensions.width}:-1:flags=lanczos,split[s0][s1];[s0]palettegen=max_colors=256:stats_mode=diff[p];[s1][p]paletteuse=dither=bayer:bayer_scale=5:diff_mode=rectangle" -loop 0 "${outputFile}" -y`;

  try {
    await execAsync(ffmpegCmd);
    console.log('Conversion complete. Cleaning up WebM file...');
    await fs.unlink(webmFile);
  } catch (error) {
    console.error('Error converting to GIF:', error);
    console.log(`WebM file kept at: ${webmFile}`);
    process.exit(1);
  }
}

console.log(`${format.toUpperCase()} saved to: ${outputFile}`);
