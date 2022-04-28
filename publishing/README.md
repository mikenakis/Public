# mikenakis-publishing

#### A publish/subscribe mechanism.

## Description

*mikenakis-publishing* is a small class library implementing a publish-subscribe 
mechanism.

- The `Publisher` class allows registering subscribers and exposes an entrypoint which,
when invoked, multicasts the invocation to all subscribers that are currently registered.
- The `Subscription` class represents the registration of a subscriber to a publisher.
- The `Publisher` exposes a method for registering a subscriber but not for
de-registering. Instead, when a subscriber is registered, a new `Subscription` object 
is created, which is `Mortal`.  Cancelling the subscription and thus de-registering
the subscriber from the publisher is done by means of invoking `Subscription.close()`.
- A `Publisher` is also `Mortal`, so that it can check, upon closing, to ensure that
all subscriptions have been closed beforehand.

Two layers are provided:

 - The `anycall` layer: allows subscribing instances of the `Anycall` interface and
thus receiving notifications in the form of `Anycall` invocations. Useful for
framework-level software, where the subscriber may have no knowledge of the nature
of the notifications, and just forwards the notifications elsewhere.
 - The `bespoke` layer: allows subscribing an interface of your choice, and thus receiving
notifications in the form of invocations of that interface.  Utilizes `mikenakis-intertwine`
to achieve this. Useful for application-level software.

## License

This creative work is explicitly published under ***No License***. 
This means that I remain the exclusive copyright holder of this creative work, 
and you may not do anything with it other than view its source code and admire it. 
More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)
