// For each project in the workspace, this script computes a sha256 hash of the pom.xml or
// {gradle.build,gradle.properties} files and compares it to a value on file. If the two hashes are
// different for a given project, the target `prepare-java` of this project is executed.

'use strict';

const crypto = require('crypto');
const fs = require('fs');
const { spawn } = require("child_process");

console.log('todo');