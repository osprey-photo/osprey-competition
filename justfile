# Main justfile to run all the development scripts
# To install 'just' see https://github.com/casey/just#installation

# Ensure all properties are exported as shell env-vars
set export
set dotenv-load

# use cmd.exe instead of sh:
# set shell := ["cmd.exe", "/c"]


# set the current directory, and the location of the test dats
CWDIR := justfile_directory()

_default:
  @just -f "{{justfile()}}" --list

[windows]
ui_dev:
   #! cmd /c
   cd {{CWDIR}}/osprey-competition-ui && npm run dev

# Runs production UI buidl
[windows]
ui_prod:  
   #! cmd /c
   cd {{CWDIR}}/osprey-competition-ui && npm i && npm run build
   
[unix]
ui_prod:  
   #!/bin/bash

   set -eo pipefail
   cd {{CWDIR}}/osprey-competition-ui && npm i && npm run build


[windows]
comp_dev:
   #! cmd /c
   cd {{CWDIR}}/competition && gradlew quarkusDev

build:
   #!/bin/bash

   set -eo pipefail

   pushd "${CWDIR}"/osprey-competition-ui
   npm ci && npm run build
   popd

   rm -rf "${CWDIR}"/competition/src/main/resources/META-INF/resources/*
   cp -r "${CWDIR}"/osprey-competition-ui/dist/* "${CWDIR}"/competition/src/main/resources/META-INF/resources/

   pushd "${CWDIR}"/competition
   ./gradlew build -Dquarkus.package.jar.type=uber-jar


   # https://github.com/sualeh/build-jpackage/blob/main/.github/workflows/build-jpackage.yml

[windows]
package-windows:
  cd competition
  gradle build "-Dquarkus.package.jar.type=uber-jar"
  cd ..
  jpackage --verbose "@_build/jpackage.cfg" "@_build/jpackage-windows.cfg" --main-jar competition-0.0.0-DEV-runner.jar