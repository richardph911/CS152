# Update these variables to match the locations
JUNIT_JAR=junit-4.12.jar
HAMCREST_JAR=hamcrest-core-1.3.jar

# These variables should not need to be changed
TEST_CLASSPATH=${JUNIT_JAR}:${HAMCREST_JAR}
# Choosing build instead of bin to avoid conflicts with Eclipse
BUILD_DIR=build
SRC_FOLDERS=edu/sjsu/fwjs
PACKAGE_NAME=edu.sjsu.fwjs
ZIP_FILE=solution.zip

.PHONY: all test run clean spotless
all:
	mkdir -p ${BUILD_DIR}/${SRC_FOLDERS}
	javac -cp ${TEST_CLASSPATH} -d ${BUILD_DIR} src/${SRC_FOLDERS}/*.java testSrc/${SRC_FOLDERS}/*.java

test:
	java -cp ${BUILD_DIR}:${TEST_CLASSPATH} org.junit.runner.JUnitCore ${PACKAGE_NAME}.ExpressionTest

run:
	java -cp ${BUILD_DIR} ${PACKAGE_NAME}.Interpreter

${ZIP_FILE}:
	zip ${ZIP_FILE} src/${SRC_FOLDERS}/*.java

clean:
	-rm -r ${BUILD_DIR}

spotless: clean
	-rm ${ZIP_FILE}


