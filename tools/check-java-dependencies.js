// For each Java project in the workspace, this script checks if the project definition has changed
// since the last git move. If it has changed, then the target `prepare-java` of the project is
// executed.

'use strict';

const fs = require('fs');
const { spawn } = require("child_process");
const { getGitDiffFiles } = require('./git-util');

const jsonData = fs.readFileSync(`workspace.json`);
const json = JSON.parse(jsonData);

const isGradleProject = (projectLocation) => {
  const filenames = fs.readdirSync(projectLocation);
  return filenames.includes('build.gradle');
}

const isMavenProject = (projectLocation) => {
  const filenames = fs.readdirSync(projectLocation);
  return filenames.includes('pom.xml');
}

const hasGradleProjectDefinitionChanged = (projectLocation, changedFiles) => {
  if (isGradleProject(projectLocation)) {
    const projectDefinitionPaths = ['build.gradle', 'settings.gradle', 'gradle.properties']
      .map(filename => `${projectLocation}/${filename}`);
    if (projectDefinitionPaths.some(projectDefinitionPath => changedFiles.includes(projectDefinitionPath))) {
      return true;
    }
  }
  return false;
}

const hasMavenProjectDefinitionChanged = (projectLocation, changedFiles) => {
  if (isMavenProject(projectLocation)) {
    const projectDefinitionPaths = ['pom.xml'].map(filename => `${projectLocation}/${filename}`);
    if (projectDefinitionPaths.some(projectDefinitionPath => changedFiles.includes(projectDefinitionPath))) {
      return true;
    }
  }
  return false;
}

const installJavaDependencies = (projectName) => {
  spawn('yarn', ['nx', 'prepare-java', projectName], {stdio:'inherit'})
    .on('exit', function (error) {
      if (error) {
        console.log(`error: ${error.message}`);
        return;
      }
    });
}

console.log('✨ Preparing Java dependencies');
getGitDiffFiles().then((changedFiles) => {
  Object.entries(json['projects']).forEach(([projectName, projectLocation]) => {
    if (hasGradleProjectDefinitionChanged(projectLocation, changedFiles) || hasMavenProjectDefinitionChanged(projectLocation, changedFiles)) {
      installJavaDependencies(projectName);
    }
  });
});



