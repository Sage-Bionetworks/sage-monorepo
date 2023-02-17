// For each Python project in the workspace, this script checks if the project definition has
// changed since the last git move. If it has changed, then the target `prepare-python` of the
// project is executed.

'use strict';

const fs = require('fs');
const { spawn } = require("child_process");
const { getGitDiffFiles } = require('./git-util');

// const jsonData = fs.readFileSync(`workspace.json`);
// const json = JSON.parse(jsonData);

const isPoetryProject = (projectLocation) => {
  const filenames = fs.readdirSync(projectLocation);
  return filenames.includes('poetry.lock');
}

const hasPoetryProjectDefinitionChanged = (projectLocation, changedFiles) => {
  if (isPoetryProject(projectLocation)) {
    const projectDefinitionPaths = ['poetry.lock']
      .map(filename => `${projectLocation}/${filename}`);
    if (projectDefinitionPaths.some(projectDefinitionPath => changedFiles.includes(projectDefinitionPath))) {
      return true;
    }
  }
  return false;
}

const installPythonDependencies = (projectName) => {
  spawn('yarn', ['nx', 'prepare-python', projectName], {stdio:'inherit'})
    .on('exit', function (error) {
      if (error) {
        console.log(`error: ${error.message}`);
        return;
      }
    });
}

console.log('✨ Preparing Python dependencies (Disabled)');
getGitDiffFiles().then((changedFiles) => {
  // find project dirs
  changedFiles.push('apps/openchallenges/notebook/project.json');
  changedFiles.push('apps/openchallenges/notebook/poetry.lock');
  let projectDirs = [];
  changedFiles.forEach((changedFile) => {
    if (changedFile.endsWith('project.json')) {
      projectDirs.push(changedFile.substring(0, changedFile.indexOf('/project.json')));
    }
  })

  // Object.entries(projectDirs).forEach(([projectName, projectLocation]) => {
  //   if (hasPoetryProjectDefinitionChanged(projectLocation, changedFiles)) {
  //     installPythonDependencies(projectName);
  //   }
  // });
});



