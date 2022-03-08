# mikenakis-dispatch
#### Event dispatching.

## Description

`mikenakis-dispatch` is a Java class library that defines the `Dispatcher`
interface, which represents the heart of an event-driven system, otherwise 
known as `message pump`, `event loop`, `event driver`, etc.

Unlike other dispatcher implementations, the `post()` and `call()` methods
are not part of this interface.  Instead, these methods can be found in a
separate `DispatcherProxy` interface, which can be obtained by invoking
`Dispatcher.proxy()`. This way, code running outside of the dispatcher
thread can be given only a `DispatcherProxy` to work with, so that it 
cannot invoke any of the other methods of the `Dispatcher` interface,
because these methods only make sense to be invoked from within the 
dispatcher thread.

A companion `Timekeeper` interface provides a means of creating timers.

Two implementations are provided:

 - The `fake` implementation, which is useful for testing event-driven software.
 - The `autonomous` implementation, which provides a stand-alone `Dispatcher` for 
building event-driven systems.

## License

This creative work is explicitly published under ***No License***. This means that I remain the exclusive copyright holder of this creative work, and you may not do anything with it other than view its source code and admire it. More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything more with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)
