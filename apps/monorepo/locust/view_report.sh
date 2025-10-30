#!/bin/bash
# Simple script to view Locust HTML reports

PORT=8089
REPORT_FILE="${1:-results/rate_limit_burst_report.html}"

if [ ! -f "$REPORT_FILE" ]; then
    echo "Report file not found: $REPORT_FILE"
    echo "Usage: ./view_report.sh [report_file]"
    exit 1
fi

echo "========================================"
echo "Starting HTTP server to view report"
echo "========================================"
echo "Report: $REPORT_FILE"
echo "URL: http://localhost:$PORT/$(basename $REPORT_FILE)"
echo ""
echo "Press Ctrl+C to stop the server"
echo "========================================"
echo ""

cd "$(dirname "$REPORT_FILE")"
python3 -m http.server $PORT
