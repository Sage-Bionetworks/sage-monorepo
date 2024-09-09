// For each project in the workspace, this script checks if a file has changed since the last git
// operation. The Nx task `build-image` is then run for these projects.

'use strict';

const { spawn } = require('child_process');
const { getGitDiffFiles } = require('./git-util');
const { getNxProjects, getNxProjectFiles } = require('./nx-util');

// Returns true if the project dir specified includes a file that has changed.
const haveProjectFilesChanged = (projectDir, changedFiles) => {
  for (const changedFile of changedFiles) {
    if (changedFile.startsWith(projectDir)) {
      return true;
    }
  }
  return false;
};

// Builds the images of the comma-separated list of projects specified.
const buildImages = (projectNames) => {
  spawn('nx', ['run-many', '--target=build-image', `--projects=${projectNames}`], {
    stdio: 'inherit',
  }).on('exit', function (error) {
    if (error) {
      console.log(`error: ${error.message}`);
      return;
    }
  });
};

console.log('✨ Preparing Docker images');
getGitDiffFiles().then((changedFiles) => {
  changedFiles.push('apps/openchallenges/challenge-service/Dockerfile');
  getNxProjects()
    .then((projects) => {
      const toUpdate = (project) => haveProjectFilesChanged(project['projectDir'], changedFiles);
      return projects.filter(toUpdate);
    })
    .then((projectsToUpdate) => {
      // generate a list of comma-separated project names
      let projectNames = projectsToUpdate.map((project) => project['projectName']);
      projectNames = projectNames.join(',');
      if (projectNames) {
        buildImages(projectNames);
      }
    });
});
