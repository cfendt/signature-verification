@ECHO OFF
REM /************************************************************************
REM  * SVN MACROS
REM  *
REM  * $Revision: 274 $
REM  * $Author: selvarmu $
REM  * $LastChangedDate: 2008-07-07 21:24:00 +0530 (Mon, 07 Jul 2008) $
REM  ************************************************************************************/

ECHO "================ Check for JAVA_HOME ================"
IF NOT "%JAVA_HOME%"=="" goto FOUND_JAVA
   ECHO "================ Environment setup error ================"
   ECHO "Install JDK 1.5 or higher version and set JAVA_HOME environment variable"
   goto done

:FOUND_JAVA
ECHO "=====Found JAVA_HOME=%JAVA_HOME% ================"
ECHO "================ InkMLLibJ - prepare to build ================"
IF NOT EXIST ..\bin mkdir ..\bin
IF NOT EXIST ..\classes mkdir ..\classes
ECHO "================ InkMLLibJ - Compiling  Source files ================"

"%JAVA_HOME%\bin\javac" -d ..\classes -classpath "%JAVA_HOME%\lib\tools.jar" ..\src\com\hp\hpl\inkml\*.java ..\src\com\hp\hpl\inkml\test\*.java

ECHO "================ InkMLLibJ -  Compilation Done ================"

ECHO "================ InkMLLibJ -  Building distribution JAR file and copy to ..\bin ================"

"%JAVA_HOME%\bin\jar" cvfm ..\bin\inkmllibj.jar Manifest.mf -C ..\classes com

ECHO "================ InkMLLibJ - Distribution JAR file built ================"

ECHO "================ InkMLLibJ - Compile JavaDoc ================"

"%JAVA_HOME%\bin\javadoc" -sourcepath ..\src -d ..\javadocs -author -version -use -windowtitle "InkML Processor Library API" -doctitle "<![CDATA[<h1>InkML Processor Library</h1>]]>" -bottom "<![CDATA[<i>Copyright &#169; 2008 Hewlett-Packard Development Company, L.P. <br> License: <a href="http://www.opensource.org/licenses/mit-license.php"> MIT</a>.</i>]]>" com.hp.hpl.inkml

ECHO "================ InkMLLibJ - JavaDoc created in ..\javaDocs ================"

ECHO "================ InkMLLibJ - cleanup class files ================"
IF EXIST ..\classes rmdir /s /q ..\classes

:done

@ECHO ON

