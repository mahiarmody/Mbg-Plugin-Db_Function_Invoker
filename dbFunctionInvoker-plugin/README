Copyright 2014 Mahiar Mody.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.




Created by: Mahiar Mody

This is the ReadMe file for the MyBatis Generator Database Function Invoker Plugin

Contents:

1. THE DATABASE FUNCTION INVOKER PLUGIN JAR FILE NAME AND LOCATION IN THE BINARY DISTRIBUTION ARCHIVE
2. DEPENDENCIES
3. PLUGIN USAGE
4. STRUCTURE OF THE DATABASE FUNCTION INVOKER PLUGIN PROJECT
5. TO BUILD AND TEST THE DATABASE FUNCTION INVOKER PLUGIN FROM SOURCE



1. THE DATABASE FUNCTION INVOKER PLUGIN JAR FILE NAME AND LOCATION IN THE BINARY DISTRIBUTION ARCHIVE
======================================================================================================
DbFunctionInvoker-1.0.0.jar is the only file needed to add Db Function Invocation
support to the MyBatis Generator Tool (JAR file). Extract it from the lib directory in the
mbg-plugin-DbFunctionInvoker-1.0.0-binary.zip or mbg-plugin-DbFunctionInvoker-1.0.0-binary.tar.gz,
add it to the classpath of your project, along with the MyBatis Generator's JAR file.



2. DEPENDENCIES
================
There are no dependencies beyond the JRE and the MyBatis Generator Tool (JAR file) itself.
JRE 6.0 or above is required to use this plugin.

To build and test this plugin from source, in addition to the above-mentioned dependencies,
the HSQLDB dependency is required to create the test case database and test case tables.
Furthermore, this plugin is developed using Maven and consequently Maven also needs to be
installed on your computer.





3. PLUGIN USAGE
================
Please refer to the plugin's Java Doc API for a detailed description on how to
use this plugin. The Java Doc API can be found in the docs directory of the
mbg-plugin-DbFunctionInvoker-1.0.0-binary.zip and
mbg-plugin-DbFunctionInvoker-1.0.0-binary.tar.gz, archives.

For convenience the above-mentioned javadocs are also compressed into a jar archive
named DbFunctionInvoker-1.0.0-javadoc.jar that is located in the lib dirctory of the
mbg-plugin-DbFunctionInvoker-1.0.0-binary.zip and
mbg-plugin-DbFunctionInvoker-1.0.0-binary.tar.gz, archives.

To use this plugin, add the DbFunctionInvoker-1.0.0.jar to the classpath of your project,
along with the MyBatis Generator's JAR file, in order to add database function invocations
to the generated SQL Mapper XML files. The DbFunctionInvoker-1.0.0.jar file is located
in the lib directory of the mbg-plugin-DbFunctionInvoker-1.0.0-binary.zip and
mbg-plugin-DbFunctionInvoker-1.0.0-binary.tar.gz, archives.






4. STRUCTURE OF THE DATABASE FUNCTION INVOKER PLUGIN PROJECT
=============================================================

Project install directory
---------------------------
The entire project is rooted at (installed in) the directory named dbFunctionInvoker-plugin.


The Project submodules
------------------------
This project is divided into two submodules named dbFunctionInvoker and dbFunctionInvokerTests.

The dbFunctionInvoker submodule contains the database function invoker plugin source code, the
database function invoker plugin documentation (in the source code itself as JavaDoc comments),
and the maven assembly descriptor files (in the assembly directory) required to created
the final plugin binary and source distributions.

The dbFunctionInvokerTests submodule contains the database function invoker plugin JUnit test
case source code, along with the corresponding test resources.
The SQL Mapper XML files that are to be tested for the presense of database function invocations
by the JUnit test cases cannot exist in this submodule, because these SQL Mapper files are only
dynamically generated when the MyBatis Generator is run, based upon the underlying database
table structures.






5. TO BUILD AND TEST THE DATABASE FUNCTION INVOKER PLUGIN FROM SOURCE
======================================================================

The Database Function Invoker Plugin is developed using Maven. Hence, to build this plugin from
source you'll need to have Maven installed on your computer.

Copy the dbFunctionInvoker-plugin directory recursively to any directory of your choice.
Next, move into the dbFunctionInvoker-plugin directory just copied and give the command:

mvn clean install

That's it.


How this plugin build works
----------------------------

A.
The submodule dbFunctionInvoker is the first to be built.

B.
The dbFunctionInvoker submodule build includes compiling all the Database Function Invoker plugin
source files, compiled for jdk version 1.6, followed by the creation of the Database Function Invoker
plugin JAR file, and the Java Docs for this plugin. During the "package" phase, the Database Function
Invoker plugin source and binary distribution archives are created.
During the final "install" phase the Database Function Invoker plugin JAR file is copied to the
local maven repository usually named: ".m2".

C.
The submodule dbFunctionInvokerTests is built next. The following steps pertain to the building
of the dbFunctionInvokerTests submodule.

D.
First, when the "generate-sources" phase of the Maven default lifecycle, is invoked
for the dbFunctionInvokerTests submodule, the HSQLDB is started in server mode as an
in-memory database, in the background. HSQLDB is used as the database for testing this plugin.

E.
Next, in the "generate-sources" phase, after the HSQLDB is started, the Database Function Invoker
plugin test tables are created and filled with data. The SQL script responsible for creating and
filling the test tables is located in:
dbFunctionInvokerTests/src/main/resources/sqlScript/SetupDbTestScripts.sql

F.
Next, in the "generate-sources" phase, the MyBatis Generator is invoked to generated the MyBatis
Artifacts (Java model class, Java client classes, and SQL Mapper XML files) by introspecting the
test table structure created in the previous step. These auto-generated SQL Mapper XML files
are to be tested for the presense of database function invocations. To the Maven MyBatis Generator plugin,
the other dbFunctionInvoker submodule is specified as a dependency, so that the MyBatis Generator plugin
knows how to inject the auto-generated SQL Mapper XML files with database function invocations.
The XML configuration file required the MyBatis Generator to introspect the tables is located at:
dbFunctionInvokerTests/src/main/resources/mbgConfig/MyBatisGeneratorConfig.xml
MyBatis Generator creates the artifacts at location:
dbFunctionInvokerTests/target/generated-sources/mybatis-generator

G.
Next, when the "compile" phase is invoked, the MyBatis Generator generated artifacts
are compiled using jdk 1.6.

H.
Next, when the "test-compile" phase is invoked, the JUnit test cases developed for testing
this plugin are compiled using jdk 1.6.

I.
Next, when the "test" phase is invoked, the compiled JUnit test cases are run against
the SQL Mapper XML files to test for the presense or absense of
the appropriate database function invocations. Running of the JUnit tests involves
using MyBatis hence the MyBatis dependency is needed here (not the MyBatis Generator
dependency). To run, MyBatis requires an XML configuration file. This configuration
file is located at:
dbFunctionInvokerTests/src/test/resources/mybatis-config.xml

J.
Next, in the "test" phase, after the JUnit tests are run, the HSQLDB server is
gracefully shutdown.

K.
Next, when the "package" phase is invoked, the JUnit tests are packaged in a JAR
archive. The JUnit test JAR is of no importance and hence is to be ignored.

L.
Finally, when the "install" phase is invoked, all the JAR files are copied to
the local repository generally named ".m2".


