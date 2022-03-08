# Empty Directory

This is an unused production source directory.

PEARL: IntellijIdea seems to have a few issues with projects that contain only test 
code. For example, if you do a global search for a string, and under "Group by" you
include the "Test/Production" option, then all matches in production code should 
appear first, followed by all matches in test code; however, due what is probably
a bug in IntellijIdea, the matches found in the source directory of a test-only 
project are grouped under "production". To work around this problem, besides the
`testSourceDirectory` we also specify a `sourceDirectory` which is empty and unused. 
