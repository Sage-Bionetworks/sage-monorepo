#!/bin/bash

# BixArena Rate Limit Load Test Runner
# This script provides convenient shortcuts for running common rate limit test scenarios.

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
HOST="${HOST:-http://localhost:8113}"
OUTPUT_DIR="./results"

# Ensure output directory exists
mkdir -p "$OUTPUT_DIR"

# Print banner
echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║         BixArena Rate Limit Load Test Runner                ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Function to check if services are running
check_services() {
    echo -e "${YELLOW}Checking if API Gateway is accessible...${NC}"
    if curl -s -o /dev/null -w "%{http_code}" "$HOST/actuator/health" | grep -q "200\|401\|404"; then
        echo -e "${GREEN}✓ API Gateway is running at $HOST${NC}"
    else
        echo -e "${RED}✗ API Gateway is not accessible at $HOST${NC}"
        echo -e "${YELLOW}  Make sure the services are running:${NC}"
        echo -e "${YELLOW}  docker compose up -d${NC}"
        exit 1
    fi
    echo ""
}

# Function to display menu
show_menu() {
    echo -e "${BLUE}Available Test Scenarios:${NC}"
    echo ""
    echo "  1) Interactive GUI Mode (Recommended for exploration)"
    echo "  2) General Rate Limit Test (5 users, 2 minutes)"
    echo "  3) Burst Test (Rapidly hit low-limit endpoint)"
    echo "  4) Recovery Test (Verify window reset)"
    echo "  5) Low-Limit Endpoints Test (Login, callback - 20/min)"
    echo "  6) High-Limit Endpoints Test (Stats, battles - 100-300/min)"
    echo "  7) Stress Test (50 users, 5 minutes)"
    echo "  8) Quick Demo (1 user, burst + recovery)"
    echo "  9) Run All Tests (Sequential)"
    echo ""
    echo "  0) Exit"
    echo ""
}

# Test scenarios
run_interactive() {
    echo -e "${GREEN}Starting Interactive Mode...${NC}"
    echo -e "${YELLOW}Open browser to: http://localhost:8089${NC}"
    echo ""
    uv run locust -f src/bixarena/rate_limit_test.py --host="$HOST"
}

run_general_test() {
    echo -e "${GREEN}Running General Rate Limit Test...${NC}"
    echo -e "${YELLOW}Duration: 2 minutes, Users: 5${NC}"
    echo ""
    uv run locust -f src/bixarena/rate_limit_test.py \
        --host="$HOST" \
        --headless \
        -u 5 \
        -r 1 \
        -t 2m \
        --csv="$OUTPUT_DIR/rate_limit_general" \
        --html="$OUTPUT_DIR/rate_limit_general_report.html"

    echo ""
    echo -e "${GREEN}✓ Test complete!${NC}"
    echo -e "${BLUE}Results saved to: $OUTPUT_DIR/rate_limit_general_*${NC}"
}

run_burst_test() {
    echo -e "${GREEN}Running Burst Test...${NC}"
    echo -e "${YELLOW}Rapidly hitting /auth/login (20 req/min limit)${NC}"
    echo ""
    uv run locust -f src/bixarena/rate_limit_test.py \
        --host="$HOST" \
        --headless \
        -u 1 \
        -r 1 \
        -t 1m \
        --tags burst \
        --csv="$OUTPUT_DIR/rate_limit_burst" \
        --html="$OUTPUT_DIR/rate_limit_burst_report.html"

    echo ""
    echo -e "${GREEN}✓ Burst test complete!${NC}"
    echo -e "${BLUE}Results saved to: $OUTPUT_DIR/rate_limit_burst_*${NC}"
}

run_recovery_test() {
    echo -e "${GREEN}Running Recovery Test...${NC}"
    echo -e "${YELLOW}This will take ~70 seconds (hitting limit + waiting for reset)${NC}"
    echo ""
    uv run locust -f src/bixarena/rate_limit_test.py \
        --host="$HOST" \
        --headless \
        -u 1 \
        -r 1 \
        -t 2m \
        --tags recovery \
        --csv="$OUTPUT_DIR/rate_limit_recovery" \
        --html="$OUTPUT_DIR/rate_limit_recovery_report.html"

    echo ""
    echo -e "${GREEN}✓ Recovery test complete!${NC}"
    echo -e "${BLUE}Results saved to: $OUTPUT_DIR/rate_limit_recovery_*${NC}"
}

run_low_limit_test() {
    echo -e "${GREEN}Running Low-Limit Endpoints Test...${NC}"
    echo -e "${YELLOW}Testing: /auth/login, /auth/callback (20 req/min)${NC}"
    echo ""
    uv run locust -f src/bixarena/rate_limit_test.py \
        --host="$HOST" \
        --headless \
        -u 3 \
        -r 1 \
        -t 2m \
        --tags low-limit \
        --csv="$OUTPUT_DIR/rate_limit_low" \
        --html="$OUTPUT_DIR/rate_limit_low_report.html"

    echo ""
    echo -e "${GREEN}✓ Low-limit test complete!${NC}"
    echo -e "${BLUE}Results saved to: $OUTPUT_DIR/rate_limit_low_*${NC}"
}

run_high_limit_test() {
    echo -e "${GREEN}Running High-Limit Endpoints Test...${NC}"
    echo -e "${YELLOW}Testing: /api/v1/stats, /api/v1/battles, etc. (100-300 req/min)${NC}"
    echo ""
    uv run locust -f src/bixarena/rate_limit_test.py \
        --host="$HOST" \
        --headless \
        -u 10 \
        -r 2 \
        -t 2m \
        --tags high-limit \
        --csv="$OUTPUT_DIR/rate_limit_high" \
        --html="$OUTPUT_DIR/rate_limit_high_report.html"

    echo ""
    echo -e "${GREEN}✓ High-limit test complete!${NC}"
    echo -e "${BLUE}Results saved to: $OUTPUT_DIR/rate_limit_high_*${NC}"
}

run_stress_test() {
    echo -e "${GREEN}Running Stress Test...${NC}"
    echo -e "${YELLOW}Duration: 5 minutes, Users: 50${NC}"
    echo -e "${YELLOW}This will generate significant load!${NC}"
    echo ""
    read -p "Continue? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        uv run locust -f src/bixarena/rate_limit_test.py \
            --host="$HOST" \
            --headless \
            -u 50 \
            -r 10 \
            -t 5m \
            --csv="$OUTPUT_DIR/rate_limit_stress" \
            --html="$OUTPUT_DIR/rate_limit_stress_report.html"

        echo ""
        echo -e "${GREEN}✓ Stress test complete!${NC}"
        echo -e "${BLUE}Results saved to: $OUTPUT_DIR/rate_limit_stress_*${NC}"
    else
        echo -e "${YELLOW}Stress test cancelled.${NC}"
    fi
}

run_quick_demo() {
    echo -e "${GREEN}Running Quick Demo...${NC}"
    echo -e "${YELLOW}This demonstrates burst and recovery in sequence${NC}"
    echo ""

    echo -e "${BLUE}Step 1: Burst Test${NC}"
    run_burst_test

    echo ""
    echo -e "${BLUE}Step 2: Recovery Test${NC}"
    run_recovery_test

    echo ""
    echo -e "${GREEN}✓ Quick demo complete!${NC}"
}

run_all_tests() {
    echo -e "${GREEN}Running All Tests (Sequential)...${NC}"
    echo -e "${YELLOW}This will take approximately 15 minutes${NC}"
    echo ""
    read -p "Continue? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        run_general_test
        echo ""
        run_burst_test
        echo ""
        run_recovery_test
        echo ""
        run_low_limit_test
        echo ""
        run_high_limit_test
        echo ""

        echo -e "${GREEN}╔══════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║                 All Tests Complete!                          ║${NC}"
        echo -e "${GREEN}╚══════════════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo -e "${BLUE}All results saved to: $OUTPUT_DIR/${NC}"
        echo ""
        echo -e "${YELLOW}View HTML reports:${NC}"
        ls -1 "$OUTPUT_DIR"/*.html 2>/dev/null | while read file; do
            echo -e "  ${BLUE}file://${PWD}/${file}${NC}"
        done
    else
        echo -e "${YELLOW}Test suite cancelled.${NC}"
    fi
}

# Main script
main() {
    # Check if we're in the correct directory
    if [ ! -f "src/bixarena/rate_limit_test.py" ]; then
        echo -e "${RED}Error: Must be run from apps/monorepo/locust directory${NC}"
        echo -e "${YELLOW}cd /workspaces/sage-monorepo/apps/monorepo/locust${NC}"
        exit 1
    fi

    # Check services
    check_services

    # If argument provided, run that test directly
    if [ $# -eq 1 ]; then
        case $1 in
            1|interactive) run_interactive ;;
            2|general) run_general_test ;;
            3|burst) run_burst_test ;;
            4|recovery) run_recovery_test ;;
            5|low) run_low_limit_test ;;
            6|high) run_high_limit_test ;;
            7|stress) run_stress_test ;;
            8|demo) run_quick_demo ;;
            9|all) run_all_tests ;;
            *)
                echo -e "${RED}Invalid option: $1${NC}"
                exit 1
                ;;
        esac
        exit 0
    fi

    # Interactive menu
    while true; do
        show_menu
        read -p "Select option: " choice
        echo ""

        case $choice in
            1) run_interactive ;;
            2) run_general_test ;;
            3) run_burst_test ;;
            4) run_recovery_test ;;
            5) run_low_limit_test ;;
            6) run_high_limit_test ;;
            7) run_stress_test ;;
            8) run_quick_demo ;;
            9) run_all_tests ;;
            0)
                echo -e "${GREEN}Goodbye!${NC}"
                exit 0
                ;;
            *)
                echo -e "${RED}Invalid option. Please try again.${NC}"
                ;;
        esac

        echo ""
        read -p "Press Enter to continue..."
        clear
    done
}

# Run main
main "$@"
