This directory contains test output data.
It is part of the source tree so that we can keep track of how test output changes over time.

The contents of this directory should be periodically wiped, and all tests should be re-run, 
so as to get rid of stale files. 

This directory contains prints of class files for the purpose of comparing the output of the printer 
against expected output.
The test that reads these .print files ignores missing ones.
If a .print file is missing, or if it is found to be different from the output of the printer, it is
replaced with the new output.
This means that any of these .print files can be freely deleted at any time, and they will be re-created.
