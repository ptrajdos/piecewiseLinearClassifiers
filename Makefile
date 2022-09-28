ROOTDIR=$(realpath $(dir $(firstword $(MAKEFILE_LIST))))
MVN=mvn
JAVA=java

WEKA_JAR_PATH_INT=${WEKA_JAR_PATH}#WEKA_JAR_PATH should be set as environment variable

DIST_PATH=${ROOTDIR}/dist
PACKAGE_VERSION=$(shell ${MVN} help:evaluate -Dexpression=project.version -q -DforceStdout)
PACKAGE_NAME=$(shell ${MVN} help:evaluate -Dexpression=project.artifactId -q -DforceStdout)

PACKAGE_PATH=${DIST_PATH}/${PACKAGE_NAME}-${PACKAGE_VERSION}-package.zip



.PHONY: install

install_package:
	${MVN} -T 1C clean package

install: install_package
	${JAVA} -cp ${WEKA_JAR_PATH_INT} weka.core.WekaPackageManager -install-package ${PACKAGE_PATH}

install_built:
	${JAVA} -cp ${WEKA_JAR_PATH_INT} weka.core.WekaPackageManager -install-package ${PACKAGE_PATH}