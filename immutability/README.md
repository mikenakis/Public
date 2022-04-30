# mikenakis:immutability
#### Promising and asserting deep immutability.

<p align="center">
<img title="mikenakis:immutability logo" src="mikenakis-immutability.svg" width="256"/><br/>
The mikenakis:immutability logo<br/>
Based on <a href="https://thenounproject.com/icon/diamond-32841/">original from The Noun Project</a> by <a href="https://thenounproject.com/mthw/">Matthew S Hall</a><br/>
used under <a href="https://creativecommons.org/licenses/by/3.0/us/legalcode">CC BY</a> license.
</p>

## Description

TODO

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

## Poor man's issue and TODO tracking

TODO: (possibly) combine the assessments of iterables and composites.

TODO: reduce the size of the assessment hierarchy by replacing some leaf classes with parameters to their common base 
class.
                  
TODO: fix some TODOs in the code.

TODO: start making use of `mikenakis.immutability.object.ObjectImmutabilityAssessor.instance` in existing places in the 
codebase, for example in all hashmaps and hashsets.

TODO: Add sealed class analysis -- This may allow an otherwise provisory field to be assessed as immutable, if 
it is of extensible type when that extensible type belongs to a sealed group of which all member-classes have been
determined to be immutable.

TODO: add a quick check for records? -- probably won't gain anything because a record may contain mutable members.

TODO: the generic arguments of fields can be discovered using reflection; therefore, it might be possible in many cases
to conclusively assess whether a collection field is immutable if the collection is unmodifiable and the 
element type is immutable.

TODO: possibly use bytecode analysis to determine whether a class mutates a field or an array outside its constructor. 
This may forgo the need for invariability annotations in some cases. However, this will probably not gain us much,
because most fields that are only mutated within constructors are actually declared final. Effectively final fields
that are not declared final are usually so because they are in fact mutated outside the constructor. (For example, the 
cached hashcode in `java.lang.String`.)

TODO: possible bug: how will assessment go if an object has provisory fields and is also iterable?

TODO: add @Pure method annotation and use bytecode analysis to make sure it is truthful.  (However, it will not buy us
much, because purity does not imply thread-safety: a pure function may read memory that is concurrently written by
another function.)
