# mikenakis-kit

#### My library of indispensable utilities

<p align="center">
<img title="mikenakis-kit logo" src="mikenakis-kit.svg" width="256"/><br/>
The mikenakis-kit logo, <i>a Swiss army knife</i><br/>
by Mike Nakis, based on original work by <a href="https://thenounproject.com/term/multi-tool/1641155/">Mariah Gardziola</a>, license: <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY</a><br/>
</p>

## Description

`mikenakis-kit` is a small library which contains various general purpose constructs that I invariably use in every project of mine.

The bulk of the functionality is in the `Kit` class, and there are a few other classes and interfaces around it.

Most of the functionality is aimed at fixing various deficiencies of Java.

For example:

- ***Java's Map***: The built-in Map interface of java suffers from certain major drawbacks, one of which is that it embraces silent failure instead of hard error. It offers a very
  odd `put()` method which either adds or replaces, whichever doesn't throw, instead of offering an `add()` method which throws if the key already exists, a `replace()` method
  which throws if the key does not already exist, and an `addOrReplace()` method for those extremely rare occasions where the desired functionality is in fact to either add or
  replace. Another problem with the `put()` function is that it does not document the programmers' intention, while distinct `add()`, `replace()`, and
  `addOrReplace()` methods do. For these reasons,`mikenakis-kit` provides a number of methods for manipulating instances of Java `Map` which enforce proper error checking and
  self-documenting code. Similar methods for Java `Collection` are also offered.

- ***Java's checked exceptions:*** Undeniably, Java's original idea about checked exceptions had some merit, but today the trend is towards widespread use of lambdas, which are
  largely incompatible with checked exceptions. So, in modern Java code there is a very frequent need to convert checked exceptions to unchecked ones. To this end, `mikenakis-kit`
  contains a family of `uncheck()` methods which accept lambdas that may throw and invoke those lambdas converting the checked exceptions to unchecked ones. The conversion is
  performed in a smart way
  (exclusively at compilation time, instead of catching and wrapping in a `RuntimeException`,)
  so as not to interfere with breaking into the debugger when an uncaught exception is thrown.

- ***Java's generic functional interfaces:*** When Java was first introduced, there was only `Runnable`. With version 8, Java started embracing the functional paradigm, so a bunch
  of new interfaces were introduced: `Supplier`, `Consumer`,
  `Function`, `BiFunction`, and `BiConsumer`. Unfortunately, these interfaces suffer from some drawbacks.
    - The names of the interfaces are inconsistent:
        - One would expect half of those interfaces to have something in common in their names to indicate that they return something, and the other half to have something else in
          common in their names to indicate that they do not return anything.
        - Within each group of interfaces, one would expect some consistent indicator of how many parameters are expected.

      None of that is true.
    - The names of the methods of these interfaces are similarly inconsistent:
        - `Runnable` has `run()`,
        - `Consumer` and `BiConsumer` have `accept()`,
        - `Supplier` has `get()`,
        - `Function` and `BiFunction` have `apply()`.
    - The generic argument that stands for the return type is last (right-most) instead of first (left-most) in the generic argument list. However, Java is not Scala: according to
      Java syntax, the return type of a function is to the left of the function prototype, so the convention favored by the syntax is result-first, and this is consistent with the
      universal convention of mathematics where the result is placed to the left of the equals sign. Furthermore, in accordance with the "correct code should look correct, while
      wrong code should look wrong" advice, good coding practices mandate an `a = aFromB( b )` style of notation, instead of the traditional `a = bToA( b )`
      style. Note that again, the result comes first. The above indicate that anything that has to do with any kind of result should always appear first, not last.
      (And the fact that Scala goes against this is one of Scala's major annoyances, in my opinion.)

So in an attempt to bring some order to this chaos, `mikenakis-kit` offers the following functional interfaces as a replacement for Java's built-in functional interfaces:

- `mikenakis.kit.Procedure ............... invoke()`
- `mikenakis.kit.Procedure1<P1> .......... invoke( P1 )`
- `mikenakis.kit.Procedure2<P1,P2> ....... invoke( P1, P2 )`
- `mikenakis.kit.Function<R> ............. invoke()`
- `mikenakis.kit.Function1<R,P1> ......... invoke( P1 )`
- `mikenakis.kit.Function2<R,P1,P2> ...... invoke( P1, P2 )`

For reference, the functional interfaces of Java which are being replaced are as follows:

- `java.lang.Runnable .................... run()`
- `java.util.function.Consumer<T> ........ accept( T )`
- `java.util.function.BiConsumer<T,U> .... accept( T, U )`
- `java.util.function.Supplier<R> ........ get()`
- `java.util.function.Function<T,R> ...... apply( T )`
- `java.util.function.BiFunction<T,U,R> .. apply( T, U )`

## A note on the tests

The tests for `mikenakis-kit` are in a separate maven project because:

- The tests of `mikenakis-kit` depend on `mikenakis-benchmark`.
- `mikenakis-benchmark` depends on `mikenakis-kit`.

So, if `mikenakis-kit` was to contain its own tests, then there would be a circular dependency.
            
### Rant

When it comes to the general question of whether production code and tests should be kept together in the same maven project or in separate maven projects, my observations are as follows:

- The choice followed by the entire Java world, with no known exceptions, is to always keep them together.
- I have never come across a situation where keeping them together is necessary.
- I have never even come across a situation where keeping them together offers any convenience worth mentioning.
- In the DotNet world there is not even an option to keep them together, and yet I never heard any DotNet folks complaining about this being a missing feature.  
- I have on several occasions come across situations where they have to be kept separate in order to avoid a circular dependency.
- In the interest of keeping things simple, it is best to have one and only one way of accomplishing any given thing.
- If there are two ways of accomplishing a given thing, and one of them is never necessary, while the other is sometimes necessary, then the second one wins, and it should be chosen as the only way of accomplishing that thing.

From the above I suspect that build systems (such as maven) that offer the ability to keep both production code and test code in the same project essentially achieve nothing but the following:

- They complicate their inner workings with an unnecessary feature.
- To support this feature, their interface is more complex than it needs to be.
- This added complexity must be dealt with by us programmers, on a daily basis.
- So they are unnecessarily complicating our life.

(And, as usual, what I am essentially suspecting here is that the entire Java world is doing it wrong.)

## License

This creative work is explicitly published under ***No License***. 
This means that I remain the exclusive copyright holder of this creative work, 
and you may not do anything with it other than view its source code and admire it. 
More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style. However, when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)
