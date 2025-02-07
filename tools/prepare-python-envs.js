// For each Python project in the workspace, this script checks if their dependencies, as defined in
// their lock files, have changed since the last git operation. The Nx target `prepare` is then run
// for all the projects that had their dependencies updated.

'use strict';

const fs = require('fs');
const { spawn } = require('child_process');
const { getGitDiffFiles } = require('./git-util');
const { getNxProjects, getNxProjectFiles } = require('./nx-util');

// Returns true if the directory specified includes a uv lock file.
const hasUvLockFile = (projectDir) => {
  const filenames = fs.readdirSync(projectDir);
  return filenames.includes('uv.lock');
};

// Returns true if the dir specified includes a uv lock file that has changed.
const hasUvDefinitionChanged = (directory, changedFiles) => {
  if (hasUvLockFile(directory)) {
    const projectDefinitionPaths = ['uv.lock'].map((filename) =>
      ['.', ''].includes(directory) ? `${filename}` : `${directory}/${filename}`,
    );
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

const runCommand = (command, args) => {
  return new Promise((resolve, reject) => {
    const process = spawn(command, args, { stdio: 'inherit' });
    process.on('exit', (code) => {
      if (code !== 0) {
        reject(new Error(`Command failed: ${command} ${args.join(' ')}`));
      } else {
        resolve();
      }
    });
  });
};

const installWorkspacePythonDependencies = async () => {
  try {
    await runCommand('uv', ['sync']);
    // The bin folder of the virtualenv has already been added to the path in dev-env.sh
  } catch (error) {
    console.error(`Error: ${error.message}`);
  }
};

// Installs the Python dependencies of the comma-separated list of projects.
const installProjectPythonDependencies = async (projectNames) => {
  try {
    await runCommand('nx', ['run-many', '--target=prepare', `--projects=${projectNames}`]);
  } catch (error) {
    console.error(`Error: ${error.message}`);
  }
};

console.log('✨ Preparing Python dependencies');
getGitDiffFiles().then((changedFiles) => {
  if (hasUvDefinitionChanged('.', changedFiles)) {
    installWorkspacePythonDependencies();
  }
  getNxProjects()
    .then((projects) => {
      const toUpdate = (project) => hasUvDefinitionChanged(project['projectDir'], changedFiles);
      return projects.filter(toUpdate);
    })
    .then((projectsToUpdate) => {
      // generate a list of comma-separated project names
      let projectNames = projectsToUpdate.map((project) => project['projectName']);
      projectNames = projectNames.join(',');
      if (projectNames) {
        installProjectPythonDependencies(projectNames);
      }
    });
});
