# mikenakis-intertwine
#### A framework for converting back and forth between interface method invocations and the _normal form_ `Object anyCall( MethodKey key, Object[] arguments )`.

<p align="center">
<img title="mikenakis-intertwine logo" src="mikenakis-intertwine.svg" width="256"/><br/>
The mikenakis-intertwine logo, by Mike Nakis<br/>
</p>

## Description
                                                                                                                   
Any conceivable interface can be described using a _normal form_, which is a single-method interface defined as follows:

    interface AnyCall
    {
        Object anyCall( MethodKey key, Object[] arguments );
    }

*mikenakis-intertwine* is a framework that uses bytecode generation to create an _entwiner_ and an _untwiner_ for any interface.
- The entwiner is an object that implements the interface in question, converts incoming invocations into invocations of the _normal form_, and delegates to an instance of `AnyCall` which has been given to it as a constructor parameter.
- The untwiner is an object that implements the `AnyCall` interface, converts incoming invocations into invocations of the original interface in question, and delegates to an instance of the interface in question which has been given to it as a constructor parameter.

The entwiner is a lot like the built-in Dynamic Proxy of Java, (see `java.lang.reflect.Proxy.newProxyInstance()`,) with one very important difference: it does not mess with exceptions.

The built-in java dynamic proxy has the extremely annoying habit of catching and rethrowing exceptions, which is a practice devoid of any usefulness whatsoever, but it does severely hamper debugging. That's because debugging relies on having the debugger always stop on any unhandled exception, but a catch-and-rethrow construct in the call tree causes exceptions thrown underneath it to appear as caught exceptions to the debugger, so the debugger does not stop at the location where they are thrown; instead, it stops at the location where they are rethrown, which is useless. So, *mikenakis-intertwine* can serve as a debugger-friendly replacement for Java's ill-behaving built-in dynamic proxy facility.     

The untwiner is opposite and complement of the entwiner. For some reason, even though Java out of the box contains a dynamic proxy facility, it does not contain its other half. *mikenakis-bytecode* fixes this omission. 

## License

This creative work is explicitly published under ***No License***. This means that I remain the exclusive copyright holder of this creative work, and you may not do anything with it other than view its source code and admire it. More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything more with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
However, when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](http://blog.michael.gr/2018/04/on-coding-style.html)
