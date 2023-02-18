const { promisify } = require('util');
const exec = promisify(require('child_process').exec);
const fs = require('fs');
const JSON5 = require('json5');

// Returns the list of Nx projects.
const getNxProjectNames = async () => {
  const projects = await exec('nx print-affected --all --select=projects');
  return projects.stdout.trim().split(', ');
}

const getNxProjects = async () => {
  let projectFiles = await exec('find $WORKSPACE_DIR -name project.json -not -path "$WORKSPACE_DIR/node_modules/*"');
  projectFiles = projectFiles.stdout.trim().split(/\n/);
  projects = projectFiles.map((projectFile) => {
    projectDir = projectFile.substring(0, projectFile.indexOf('project.json'));
    const jsonData = fs.readFileSync(projectFile);
    const json = JSON5.parse(jsonData);
    const projectName = json['name'];
    return {
      projectDir,
      projectFile,
      projectName
    };
  })
  return projects;
}

module.exports = {
  getNxProjectNames,
  getNxProjects
};