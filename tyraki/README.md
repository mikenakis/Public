# mikenakis-tyraki

#### A modern collections library for Java

<p align="center">
<img title="mikenakis-tyraki logo" src="mikenakis-tyraki.svg" width="256"/><br/>
The mikenakis-tyraki logo, <i>a piece of cheese</i><br/>
based on original work by <a href="https://thenounproject.com/term/cheese/402993/">Laura Barretta from the Noun Project</a>, license: <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY</a><br/>
</p>

## Discussion

#### The built-in collection model of Java is very outdated and lame.

- Lack of unmodifiable collection interfaces means no compile-time readonlyness. 
  - Every single collection instance looks mutable, since it implements an interface that has mutation methods, but quite often is **_secretly immutable_**.
    - "Secretly immutable" means that it exposes mutation methods that you can call, but if you make the mistake of invoking any of them, you will be slapped with a runtime exception.
- The collection classes tend to embrace silent failure.
  - The `Collection` interface has no pair of `add()` and `tryAdd()` methods; instead, it only has a `tryAdd()` method which is wrongly named `add()`. This means that there is no single method that you can call to definitively add an item to a collection and fail if the item already exists. So, you may invoke `add()` thinking that you are adding an item, while in fact unbeknownst to you an item is replaced. This is very error-prone.   
  - The `Collection` interface has no pair of `remove()` and `tryRemove()` methods; instead, it only has a `tryRemove()` method which is wrongly named `remove()`. This means that there is no single method that you can call to definitively remove an item from a collection and fail if the item does not already exist. So, you may invoke `remove()` thinking that you are removing an item, while in fact nothing gets removed. This is very error-prone.    
  - The `Map` interface has no `add()`, `tryAdd()`, `replace()`, `tryReplace()`, and `addOrReplace()` methods; instead, it only has an `addOrReplace()` method, which is misleadingly called `put()`. This means that there is no single method that you can call to:
    - Definitively add a key-value pair to a map and fail if the key already exists.
    - Definitively replace the value associated with a key and fail if the key does not already exist.

    So, you may invoke `put()` thinking that you are adding a key-value pair, while in fact unbeknownst to you a value associated with an existing key gets replaced, or you may invoke the same method thinking that you are replacing an existing value, while in fact a new key-value pair is added. This is very error-prone.
    - Adding insult to injury, the use of a single method for a multitude of purposes prevents us from documenting our intentions via the use of the right method for the job.
  - In my experience, given the kind of code that I usually write:
    - In 95% of the cases I want to definitively add a key to the map and fail if the key already exists.
    - In another 4.95% of the cases I want to definitively replace a value and fail if the associated key does not exist.
    - In only 0.05% of the cases do I actually want to either add or replace a key-value pair; and yet, the only method offered by the `Map` interface does only this.
- The `Iterator` interface is lame.
  - Its `hasNext()` and `next()` methods work in such a way that they are unusable in a for-loop. (A for-each loop can be used with an `Iterable`, but then you have no access to the `Iterator`.)
  - A filtering iterator cannot be implemented without cumbersome look-ahead logic.
    - And if you write such a filtering iterator, it is then impossible to use it for removing items from the collection because looking ahead means that you are always past the item you would want to remove.
  
#### Fluent collections (collection streams) address none of the old issues and come with many problems of their own.

- They are unnecessarily verbose
  - Each call chain must begin with a quite superfluous-looking `stream()` operation
  - Most call chains have to end with an equally superfluous-looking `collect()` operation.
- They are not restartable. 
- They are not particularly extensible because they are entirely based on a single interface (`Stream`). Their only point of extensibility is at the very end of each call chain, by means of custom-written collectors.
- Collectors are convoluted, so writing one is not trivial.
- Collection streams work by means of incredibly complex logic behind the scenes, so:
  - They are practically impossible to debug.
  - They are noticeably slower than C#-style fluent collection operations, even before we consider the overhead of the collection step at the end.
- The collection step, which is in most cases necessary, is tantamount to always making a most often unnecessary safety copy of the information produced by the collection stream chain.
- Their implementation is unnecessarily convoluted by having built-in support for the ill-conceived idea that the mechanism used for fluent collection operations should also be usable for parallel collection operations.

#### Java collections have no support for freezing.

- The ability to freeze a mutable collection and thus make it immutable in-place is very useful, and for certain purposes even necessary. For example:
  - Freezing an existing mutable collection performs much better than constructing a new immutable collection by copying the contents of the mutable collection and then letting the mutable collection be garbage-collected. The `unmodifiableCollection()` wrapper does not help either, (even if we ignore for a moment the fact that it is secretly immutable,) because it wraps each incoming call, so although it may save us from the initial copy, it will represent unnecessary extra overhead throughout the remaining lifetime of the resulting collection. 
  - Freezing is indispensable when constructing an immutable cyclic graph of collections: The collections that participate in the graph must begin their life as mutable, and must become immutable in-place once the graph has been fully constructed. This cannot be accomplished with immutable collection builders, nor by constructing immutable collections our of mutable ones, not even by wrapping the collections in unmodifiable wrappers, because the transition from mutable to immutable must happen in-place, since collections in the graph contain references to other collections within the same graph.

#### Java collections do absolutely nothing to guard against concurrency-related bugs.

(To be fair, no collection model that I know of does anything in that direction.)

- The built-in mutable collections of Java will happily perform on themselves any mutation operation requested of them, _praying_ that they are executing in a thread-safe context. A collection can be allocated in one thread, and modified in another thread, and it will not complain at all. This is very error-prone.
- It is in fact possible to keep track of the execution context and to always know whether a mutable collection can be safely accessed. In simple scenarios where we are using only discrete threads, a mutable collection can assert that the thread accessing it is the same as the thread in which the collection was allocated. In more complex scenarios where thread pools are involved, things are a bit more complicated, but still manageable with a bit of discipline and infrastructure. Java collections do not help with any of that. 
