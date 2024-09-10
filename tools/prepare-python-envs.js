// For each Python project in the workspace, this script checks if their dependencies, as defined in
// their lock files, have changed since the last git operation. The Nx target `prepare` is then run
// for all the projects that had their dependencies updated.

'use strict';

const fs = require('fs');
const { spawn } = require('child_process');
const { getGitDiffFiles } = require('./git-util');
const { getNxProjects, getNxProjectFiles } = require('./nx-util');

// Returns true if the directory specified includes a Poetry lock file.
const isPoetryProject = (projectDir) => {
  const filenames = fs.readdirSync(projectDir);
  return filenames.includes('poetry.lock');
};

// Returns true if the project dir specified includes a Poetry lock file that has changed.
const hasPoetryProjectDefinitionChanged = (projectDir, changedFiles) => {
  if (isPoetryProject(projectDir)) {
    const projectDefinitionPaths = ['poetry.lock'].map((filename) => `${projectDir}/${filename}`);
    if (
      projectDefinitionPaths.some((projectDefinitionPath) =>
        changedFiles.includes(projectDefinitionPath),
      )
    ) {
      return true;
    }
  }
  return false;
};

// Installs the Python dependencies of the comma-separated list of projects.
const installPythonDependencies = (projectNames) => {
  spawn('nx', ['run-many', '--target=prepare', `--projects=${projectNames}`], {
    stdio: 'inherit',
  }).on('exit', function (error) {
    if (error) {
      console.log(`error: ${error.message}`);
      return;
    }
  });
};

console.log('✨ Preparing Python dependencies');
getGitDiffFiles().then((changedFiles) => {
  // changedFiles.push('apps/openchallenges/notebook/poetry.lock');
  // changedFiles.push('apps/schematic/notebook/poetry.lock');
  // console.log(changedFiles);
  getNxProjects()
    .then((projects) => {
      const toUpdate = (project) =>
        hasPoetryProjectDefinitionChanged(project['projectDir'], changedFiles);
      return projects.filter(toUpdate);
    })
    .then((projectsToUpdate) => {
      // generate a list of comma-separated project names
      let projectNames = projectsToUpdate.map((project) => project['projectName']);
      projectNames = projectNames.join(',');
      if (projectNames) {
        installPythonDependencies(projectNames);
      }
    });
});
