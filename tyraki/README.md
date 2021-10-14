# mikenakis-tyraki

#### A modern collections library for Java

<p align="center">
<img title="mikenakis-tyraki logo" src="mikenakis-tyraki.svg" width="256"/><br/>
The mikenakis-tyraki logo, <i>a piece of cheese</i><br/>
based on original work by <a href="https://thenounproject.com/term/cheese/402993/">Laura Barretta from the Noun Project</a>, license: <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY</a><br/>
</p>

## Discussion

The built-in collection model of Java is very outdated and lame.

- The `Iterator` interface is lame.
  - The `hasNext()` and `next()` methods are unusable in a for-loop.  (A for-each loop can be used with an `Iterable`, but then you have no access to the `Iterator`.)
  - A filtering iterator cannot be implemented without cumbersome look-ahead logic and then it is impossible to use it for removing items from the collection because looking ahead means that you are always past the item you want to delete.
- Lack of unmodifiable collection interfaces means no compile-time readonlyness. 
  - Every single collection instance looks mutable, since it implements an interface that has mutation methods, but quite often is **_secretly immutable_**.
    - "Secretly immutable" means that even though it looks mutable, if you make the mistake of invoking any of the mutation methods, you will be slapped with a runtime exception.
- Fluent collections (collection streams) are lame.
  - They are unnecessarily verbose
    - They require every single call chain to begin with a quite superfluous-looking `stream()` operation
    - They almost always have to be ended with an equally superfluous-looking `collect()` operation.
  - They are not particularly extensible because they are entirely based on a single interface (`Stream`). Their only point of extensibility is at the very end of each call chain, by means of custom-written collectors.
  - Collectors are convoluted, so writing one is not trivial.
  - Collection streams work by means of incredibly complex logic behind the scenes, so:
    - They are very difficult to debug.
    - They are noticeably slower than C#-style fluent collection operations even before we consider the collection step at the end.
  - The collection step, which is in most cases necessary, is tantamount to making an unnecessary safety copy of the information produced by the collection stream chain.
  - Their implementation is unnecessarily convoluted by having built-in support for the ill-conceived idea that the mechanism used for fluent collection operations should also be usable for parallel collection operations.
