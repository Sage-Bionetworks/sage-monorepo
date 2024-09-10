// For each R project in the workspace, this script checks if their dependencies, as defined in
// their lock files, have changed since the last git operation. The Nx target `prepare-r` is then
// run for all the projects that had their dependencies updated.

'use strict';

const fs = require('fs');
const { spawn } = require('child_process');
const { getGitDiffFiles } = require('./git-util');
const { getNxProjects, getNxProjectFiles } = require('./nx-util');

// Returns true if the directory specified includes an renv lock file.
const isRenvProject = (projectDir) => {
  const filenames = fs.readdirSync(projectDir);
  return filenames.includes('renv.lock');
};

// Returns true if the project dir specified includes an renv lock file that has changed.
const hasRenvProjectDefinitionChanged = (projectDir, changedFiles) => {
  if (isRenvProject(projectDir)) {
    const projectDefinitionPaths = ['renv.lock'].map((filename) => `${projectDir}/${filename}`);
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

// Installs the R dependencies of the comma-separated list of projects.
const prepareREnvironment = (projectNames) => {
  spawn('nx', ['run-many', '--target=prepare', `--projects=${projectNames}`], {
    stdio: 'inherit',
  }).on('exit', function (error) {
    if (error) {
      console.log(`error: ${error.message}`);
    }
  });
};

console.log('✨ Preparing R environments');
getGitDiffFiles().then((changedFiles) => {
  getNxProjects()
    .then((projects) => {
      const toUpdate = (project) =>
        hasRenvProjectDefinitionChanged(project['projectDir'], changedFiles);
      return projects.filter(toUpdate);
    })
    .then((projectsToUpdate) => {
      // generate a list of comma-separated project names
      let projectNames = projectsToUpdate.map((project) => project['projectName']);
      projectNames = projectNames.join(',');
      if (projectNames) {
        prepareREnvironment(projectNames);
      }
    });
});
