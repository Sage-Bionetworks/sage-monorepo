import puppeteer from 'puppeteer';
import { PuppeteerScreenRecorder } from 'puppeteer-screen-recorder';
import path from 'path';
import { fileURLToPath } from 'url';
import { exec } from 'child_process';
import { promisify } from 'util';
import fs from 'fs/promises';

const execAsync = promisify(exec);
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

const browser = await puppeteer.launch({
  headless: 'new',
  args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage', '--disable-gpu'],
  executablePath: process.env.PUPPETEER_EXECUTABLE_PATH || undefined,
});
const page = await browser.newPage();

console.log('Loading diagram to measure dimensions...');
await page.goto('file://' + htmlFile, { waitUntil: 'networkidle0' });

// Wait for Mermaid to fully render
console.log('Waiting for diagram to render...');
await new Promise((resolve) => setTimeout(resolve, 8000));

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

// Set viewport to match diagram size
await page.setViewport({
  width: dimensions.width,
  height: dimensions.height,
  deviceScaleFactor: 2,
});

// Reload with correct viewport
console.log('Reloading with correct viewport...');
await page.goto('file://' + htmlFile, { waitUntil: 'networkidle0' });
await new Promise((resolve) => setTimeout(resolve, 8000));

// Start recording with correct dimensions
console.log('Starting recording...');
const recorderConfig = {
  followNewTab: false,
  fps: 30, // Increased from 12 to 30 for smoother animation
  videoFrame: { width: dimensions.width, height: dimensions.height },
  videoBitsPerSecond: 10000000, // 10 Mbps for better quality
};

// Add GIF-specific configuration
if (format === 'gif') {
  recorderConfig.recordDurationLimit = 10; // 10 seconds max for GIF
}

const recorder = new PuppeteerScreenRecorder(page, recorderConfig);

// Always record to WebM first
await recorder.start(webmFile);

// Wait a bit while recording
console.log('Recording...');
await new Promise((resolve) => setTimeout(resolve, 5000)); // Increased from 2s to 5s to capture more animation

console.log('Stopping recording...');
await recorder.stop();
await browser.close();

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
