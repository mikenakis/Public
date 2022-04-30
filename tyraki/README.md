# mikenakis-tyraki

#### A modern collections library for Java

<p align="center">
<img title="mikenakis-tyraki logo" src="mikenakis-tyraki.svg" width="256"/><br/>
The mikenakis-tyraki logo, <i>a piece of cheese</i><br/>
based on original work by <a href="https://thenounproject.com/term/cheese/402993/">Laura Barretta from the Noun Project</a>, license: <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY</a><br/>
</p>

## Description

I do not yet have a description of this library, because it will be a lot of work to write, and because the library is still evolving. Instead, here is a (very lengthy) list of reasons why the built-in collections of java are lame, and the reader may assume that every reason mentioned in this list is addressed and taken care of by mikenakis:tyraki.  

### Why the built-in collections of Java are lame

#### No compile-time readonlyness

  - Every single collection can be **_secretly immutable_**.
    - "Secretly immutable" means that it appears mutable, because it exposes mutation methods that you can call, but if you make the mistake of calling any of them, you will be slapped with a runtime exception.
	
#### Silent failure

  - The `Collection` interface has no pair of `add()` and `tryAdd()` methods.
    - Instead, it only has a `tryAdd()` method which is sabotagingly named `add()`.
    - This means that there is no single method that you can call to definitively add an item to a collection and fail if the item already exists, so: 
      - You may invoke `add()` thinking that you are adding an item, while in fact unbeknownst to you an item is replaced. This is very error-prone.   
  - The `Collection` interface has no pair of `remove()` and `tryRemove()` methods.
    - Instead, it only has a `tryRemove()` method which is sabotagingly named `remove()`. 
    - This means that there is no single method that you can call to definitively remove an item from a collection and fail if the item does not already exist, so: 
      - You may invoke `remove()` thinking that you are removing an item, while in fact nothing gets removed. This is very error-prone.    
  - The `Map` interface has no `add()`, `tryAdd()`, `replace()`, `tryReplace()`, and `addOrReplace()` methods.
    - Instead, it only has an `addOrReplace()` method, which is misleadingly called `put()`. 
    - This means that:
      - There is no single method that you can call to definitively add a key-value pair to a map and fail if the key already exists, so:
        - You may invoke `put()` thinking that you are adding a key-value pair, while in fact unbeknownst to you a value associated with an existing key gets replaced. This is very error-prone.
      - There is no single method that you can call to definitively replace the value associated with a key and fail if the key does not already exist, so:
        - You may invoke `put()` thinking that you are replacing an existing value, while in fact a new key-value pair is added. This is very error-prone.
    - Adding insult to injury, the use of a single method for a multitude of purposes prevents us from documenting our intentions via the use of the right method for the job.
    - In my experience, given the kind of code that I usually write:
      - In 99.9% of the cases I want to definitively add a key to the map and fail if the key already exists.
      - In another 0.09% of the cases I want to definitively replace a value and fail if the associated key does not exist.
      - In only 0.01% of the cases do I actually want to either add or replace a key-value pair; and yet, the only method offered by the `Map` interface of Java does only that.
	                 
#### Cumbersome iterator

  - The `hasNext()` and `next()` methods of the `Iterator` interface work in such a way that they are unusable in a for-loop. (A for-each loop can be used with an `Iterable`, but then you have no access to the `Iterator`.)
  - A filtering iterator cannot be implemented without cumbersome look-ahead logic.
    - And if you write such a filtering iterator, it is then impossible to use it for removing items from the collection because looking ahead means that you are always past the item you would want to remove.
   
#### Cumbersome fluency

  - Streams have many problems:
    - They are unnecessarily verbose
      - Each call chain must begin with a quite superfluous-looking `stream()` operation
      - Most call chains have to end with an equally superfluous-looking `collect()` operation.
    - They are not restartable. 
    - They are not particularly extensible because they are entirely based on a single interface (`Stream`). Their only point of extensibility is at the very end of each call chain, by means of custom-written collectors.
    - Collectors are convoluted, so writing one is not trivial.
    - Collection streams work by means of incredibly complex logic behind the scenes, so:
      - They are practically impossible to debug.
      - They are noticeably slower than C#-style fluent collection operations, even before we consider the overhead of the collection step at the end.
    - The collection step, which is in most cases necessary, is tantamount to always creating a most often unnecessary temporary collection to hold the information produced by the collection stream chain, instead of simply consuming the information produced by the chain.
    - Their implementation is unnecessarily convoluted by having built-in support for the ill-conceived idea that the mechanism used for fluent collection operations should also be usable for parallel collection operations.
	                        
#### No support for freezing

  - Freezing is the ability to have a collection begin its life as mutable, so that it can be populated, and to then turn it into an immutable collection. The conversion from mutable to immutable happens in-place, and is permanent from the moment it is applied. 
    - Freezing is useful for performance:
      - Populating a mutable collection and then freezing it (which essentially consists of simply marking it as frozen) performs much better than populating the mutable collection and then making a copy of its contents into a new immutable collection. 
        - The `unmodifiableCollection()` wrapper does not help much, (even if we ignore for a moment the fact that it is secretly immutable,) because it wraps each incoming call, so although it may save us from the initial copy, it will represent unnecessary extra overhead throughout the remaining lifetime of the immutable collection.
    - Freezing can be used to achieve certain things that are otherwise hard, or impossible:
      - The creation of cyclic graphs of immutable collections is impossible using collections that are immutable upon construction, because immutable-upon-construction collections require all of their members to be present at construction time, which cannot happen when the graph has cycles. So, to construct such a graph, the collections that participate in the graph must begin their life as mutable, so that the graph can be constructed, and must become immutable in-place once construction is complete.

#### No guards against concurrency-related bugs

  - The built-in mutable collections of Java will happily perform on themselves any mutation operation requested of them, requiring that the context of execution is safe for mutation, but not doing anything to ensure that it is in fact safe. (Essentially, _praying_ "please be safe".) Thus, a collection can be allocated in one thread, and inadvertently invoked to mutate in another thread, (with catastrophic consequences,) and it will not complain at all. This is very error-prone.
                                                
#### No guards against immutability-related bugs

  - The built-in mutable collections of Java will happily accept any object as a member of a hash-set or as a key of a hash-map, requiring it to be immutable but not doing anything to ensure that it is in fact immutable. (Essentially, _praying_ "please be immutable".) Thus, it is possible to add keys to a hash-map which are inadvertently mutable, and to then mutate them, (with catastrophic consequences,) and the hash-map will not complain at all. This is very error-prone.
