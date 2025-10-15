const { promisify } = require('util');
const exec = promisify(require('child_process').exec);
const fs = require('fs');
const JSON5 = require('json5');

// Returns the list of Nx projects.
const getNxProjectNames = async () => {
  const projects = await exec('nx print-affected --all --select=projects');
  return projects.stdout.trim().split(', ');
};

const getNxProjects = async () => {
  let projectFiles = await exec(
    'find . -name project.json -not -path "./node_modules/*" -not -path "./dist/*" -not -path "./.pdm-build/*"',
  );
  projectFiles = projectFiles.stdout.trim().split(/\n/);
  // const prefix = './';
  // projectFiles = projectFiles.map((projectFile) => {
  //   return projectFile.slice(projectFile.indexOf(prefix) + prefix.length);
  // });
  projects = projectFiles.map((projectFile) => {
    projectDir = projectFile.substring(0, projectFile.indexOf('/project.json'));
    const jsonData = fs.readFileSync(projectFile);
    const json = JSON5.parse(jsonData);
    const projectName = json['name'];
    return {
      projectDir,
      projectFile,
      projectName,
    };
  });
  return projects;
};

module.exports = {
  getNxProjectNames,
  getNxProjects,
};
