// For each Java project in the workspace, this script checks if their Gradle binary, as defined in
// has changed since the last git operation. The Nx target `prepare` is then run for all the
// projects that must be updated.

'use strict';

const fs = require('fs');
const { spawn } = require('child_process');
const { getGitDiffFiles } = require('./git-util');
const { getNxProjects, getNxProjectFiles } = require('./nx-util');

// Returns true if the directory specified includes a Gradle build file.
const isGradleProject = (projectDir) => {
  const filenames = fs.readdirSync(projectDir);
  return filenames.includes('build.gradle');
};

// Returns true if the project dir specified includes a Gradle file that has changed. Only the files
// that require to then run `prepare-java` are considered.
const hasGradleProjectDefinitionChanged = (projectDir, changedFiles) => {
  if (isGradleProject(projectDir)) {
    const projectDefinitionPaths = [
      'gradlew',
      'gradlew.bat',
      'gradle/wrapper/gradle-wrapper.properties',
    ].map((filename) => `${projectDir}/${filename}`);
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
const prepareJavaProject = (projectNames) => {
  spawn('nx', ['run-many', '--target=prepare', `--projects=${projectNames}`], {
    stdio: 'inherit',
  }).on('exit', function (error) {
    if (error) {
      console.log(`error: ${error.message}`);
      return;
    }
  });
};

console.log('✨ Preparing Java environments');
getGitDiffFiles().then((changedFiles) => {
  // changedFiles.push('apps/openchallenges/challenge-service/gradlew');
  getNxProjects()
    .then((projects) => {
      const toUpdate = (project) =>
        hasGradleProjectDefinitionChanged(project['projectDir'], changedFiles);
      return projects.filter(toUpdate);
    })
    .then((projectsToUpdate) => {
      // generate a list of comma-separated project names
      let projectNames = projectsToUpdate.map((project) => project['projectName']);
      projectNames = projectNames.join(',');
      if (projectNames) {
        prepareJavaProject(projectNames);
      }
    });
});
