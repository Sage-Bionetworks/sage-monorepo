# Platform Config Angular

A reusable Angular configuration library that provides Spring Boot-inspired configuration management.

## Features

- **YAML-based configuration** with profile support (dev, test, stage, prod)
- **Environment variable overrides** with automatic type conversion
- **Server-side rendering (SSR) support** with separate browser/Node.js code paths
- **Type-safe configuration** using generics and Zod validation
- **Spring Boot-style hierarchy** (priority from lowest to highest):
  1. Base configuration (`application.yaml`)
  2. Profile-specific configuration (`application-{profile}.yaml`)
  3. Environment variables (highest priority)

## Usage

This is a generic library that can be extended for specific applications. See `@sagebionetworks/openchallenges/web/angular/config` for an example implementation.

## Running unit tests

Run `nx test platform-config-angular` to execute the unit tests.
