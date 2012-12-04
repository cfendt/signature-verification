InkML Toolkit (InkMLTk)
Version 0.5.0

This package contains the Core InkML Processor library developed in Java.

Overview to InkML Processor Library:
------------------------------------
InkML library is a necessary component in developing applications with InkML as digital ink format.The InkML library takes the InkML document and schema and processes the information so that it may then be used by applications requesting the information.

How to build the Source code?
-----------------------------
Please follow the instructions in the project documentation at http://inkmltk.wiki.sourceforge.net/BuildingJavaSrc.

For more information, please visit the project site, http://inkmltk.sf.net

Known Issue:
------------
If you use JDK 1.6 for compiling the source programs, it would throw the below warning message,

warning: com.sun.org.apache.xerces.internal.parsers.DOMParser is Sun proprietary API and may be removed in a future release
                DOMParser parser = new DOMParser();

It means that the built-in support of Xerces implementation in JDK may not be available in future. 
It does not harm the build process.
