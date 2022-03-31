# mikenakis-slf4j-to-kit

An `Slf4j` redirector to `mikenakis-kit` logging.

Sometimes, when you use certain external libraries,
messages like the following might appear in the standard error stream:
                  
```
Mar 31, 2022 1:58:12 PM io.netty.handler.logging.LoggingHandler channelRegistered
INFO: [id: 0xdc58cde9] REGISTERED
Mar 31, 2022 1:58:12 PM io.netty.handler.logging.LoggingHandler bind
INFO: [id: 0xdc58cde9] BIND: 0.0.0.0/0.0.0.0:8443
Mar 31, 2022 1:58:12 PM io.netty.handler.logging.LoggingHandler channelActive
INFO: [id: 0xdc58cde9, L:/[0:0:0:0:0:0:0:0]:8443] ACTIVE
```

What is happening here is that an external library is making use of `slf4j`, but 
no implementation of slf4j is found, so some default implementation is used which
simply sends messages formatted like that to the standard error.

Alternatively, the following message might appear:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

What is happening is that an external library is making use of `slf4j-api`,
which searches the classPath for a class named `org.slf4j.impl.StaticLoggerBinder`,
which is supposed to contain an actual `slf4j` logger implementation, but no
such class is found in the classpath, so `slf4j-api` does not know what to
do with log messages, so it emits the above message to alert the user.

The html page linked in the message provides various examples of how to 
route `slf4j-api` to various existing and well-known logging mechanisms;
in order to route `slf4j-api` logging to the `mikenakis-kit` logger,
include the `saganaki-slf4j` project as a dependency of your project.

Once that is done, all `slf4j` logging will be redirected to the 
`mikenakis-kit` logger, so from that moment on, the output will look 
similar to this:

```
io.netty.util.internal.logging.AbstractInternalLogger.log(AbstractInternalLogger.java:148) ... | INFO  | 55 | 12:02:49.338 | nioEventLoopGroup-2-1 | io.netty.handler.logging.LoggingHandler: [id: 0x272f45ee] REGISTERED
io.netty.util.internal.logging.AbstractInternalLogger.log(AbstractInternalLogger.java:148) ... | INFO  | 56 | 12:02:49.338 | nioEventLoopGroup-2-1 | io.netty.handler.logging.LoggingHandler: [id: 0x272f45ee] BIND: 0.0.0.0/0.0.0.0:8443
io.netty.util.internal.logging.AbstractInternalLogger.log(AbstractInternalLogger.java:148) ... | INFO  | 57 | 12:02:49.338 | nioEventLoopGroup-2-1 | io.netty.handler.logging.LoggingHandler: [id: 0x272f45ee, L:/[0:0:0:0:0:0:0:0]:8443] ACTIVE
```

The code was adapted from https://github.com/apache/maven/tree/maven-3.6.3/maven-slf4j-provider/src/main/java/org/slf4j/impl
