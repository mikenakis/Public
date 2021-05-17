# mikenakis-slf4j-to-kit

An `Slf4j` redirector to `mikenakis-kit` logging.

Sometimes, if you use certain external libraries, 
the following message might appear in the standard error stream:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

What is happening is that an external library is making use of `slf4j-api`,
which searches the classPath for class `org.slf4j.impl.StaticLoggerBinder`,
which is supposed to provide the actual logger implementation.

If no such class is in the classpath, then Slf4j has no means of generating
log messages, so it emits the above message to alert the user.

The html page linked in the message provides various possible solutions,
which though all refer to various logging mechanisms other than the
`mikenakis-kit` logging mechanism.

In order to route `slf4j-api` logging to the `mikenakis-kit` logger,
include the `saganaki-slf4j` project as a dependency of your project.

Adapted from https://github.com/apache/maven/tree/maven-3.6.3/maven-slf4j-provider/src/main/java/org/slf4j/impl
