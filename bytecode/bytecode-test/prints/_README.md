This directory contains prints of class files for the purpose of comparing the output of the printer against expected output.
It also serves the purpose of tracking how the output format changes over time.
The test that reads these .print files ignores missing ones.
If a .print file is missing, or if it is found to be different from the output of the printer, it is replaced with the new output.
This means that any of these .print files can be freely deleted at any time, and it will be re-created.
