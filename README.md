# React-Templates Plugin #

[React-Templates](https://github.com/wix/react-templates) Light weight templates for [React](http://facebook.github.io/react/index.html). see more [here](https://github.com/wix/react-templates).<br/>
React-Templates plugin for WebStorm, PHPStorm and other Idea family IDE, provides integration with React-Templates and shows errors and warnings inside the editor.
* Support displaying react-templates warnings as intellij inspections
* Support for react-templates file types
* Automatically generate the javascript files from react-templates files 
* Group rt file with generated js file

## Getting started ##
### Prerequisites ###
* [NodeJS](http://nodejs.org/)
* IntelliJ 13.1.4 / WebStorm 8.0.4, or above.

Install react-templates npm package [react-templates npm](https://www.npmjs.com/package/react-templates)</a>:<br/>
```bash
$ cd <project path>
$ npm install react-templates
```
Or, install react-templates globally:<br/>
```bash
$ npm install -g react-templates
```

### Settings ###
To get started, you need to set the react-templates plugin settings:<br/>

* Go to preferences, react-templates plugin page and check the Enable plugin.
* Set the path to the nodejs interpreter bin file.
* Set the path to the react-templates bin file. should point to ```<project path>node_modules/react-templates/bin/rt.js``` if you installed locally or ```/usr/local/bin/rt``` if you installed globally. 
  * For Windows: install react-templates globally and point to the react-templates cmd file like, e.g.  ```C:\Users\<username>\AppData\Roaming\npm\rt.cmd```
* Select output modules system (AMD/CommonJS/Globals)
* Select whether to group controller file with rt and rt.js files  


### A Note to contributors ###
react-templates plugin uses the code from [here](https://github.com/idok/scss-lint-plugin/tree/master/intellij-common) as a module, to run the project you need to clone that project as well.
