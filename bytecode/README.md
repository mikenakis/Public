# mikenakis-bytecode

#### A bytecode framework.

<p align="center">
<img title="mikenakis-bytecode logo" src="mikenakis-bytecode.svg" width="256"/><br/>
The mikenakis-bytecode Logo, <i>an old-fashioned coffee grinder</i>.<br/>
By Mike Nakis, based on <a href="https://thenounproject.com/browse/?i=19046">original work by Gregory Sujkowski from the Noun Project</a>.<br/>
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

## Description

*mikenakis-bytecode* is a framework for manipulating JVM bytecode, aiming to be elegant, simple, and easy to use.

## How is it better than javassist, ASM, byte-buddy and the like?

There are no benefits other than that the interface seems to me to be more simple, elegant and intuitive; of course, my opinion is biased, because I wrote it. So, let's just say that *mikenakis-bytecode* exists simply because I wanted to write it. For fun. That's how I roll.

## What does *mikenakis-bytecode* have to offer?

*mikenakis-bytecode* was built with a simple philosophy: the framework should contain the bare minimum of features that are necessary in order to manipulate bytecode in an elegant, easy, and error-free way, so that if you already know JVM bytecode, and you just want a framework to work with it, you do not have to spend a lot of time learning the framework before you can begin using it.

For example:

- *mikenakis-bytecode* enables you to work with the contents of an individual class-file, but it is not concerned with relationships between class files. Referenced external types (e.g. the supertype, implemented interfaces, etc.) are identified by descriptor objects that encapsulate fully qualified type name strings. The framework does not offer any kind of abstraction for groups or hierarchies of classes, because that would have greatly increased not only the complexity of the implementation of the framework, but also the complexity of its interface: there would be a lot that you would have to learn before you could start using it.

- *mikenakis-bytecode* spares you from ever having to deal with utterly inelegant descriptor strings like `"()V"` and `"[Ljava.lang.String;"`. It does of course use them under the hood, but its interface does not even hint at them. (It does not allow you to use them if you wanted to; seriously, stop using that junk.) Instead, *mikenakis-bytecode* offers a hierarchy of `TypeDescriptor` classes that fully encapsulate these descriptor strings, so that as a programmer, you are manipulating objects, not strings. You can create a type descriptor like this: `TypeDescriptor typeDescriptor = TypeDescriptor.of( ArrayList.class );` or `TypeDescriptor typeDescriptor = TypeDescriptor.of( "java.lang.String[]" );` - note that even when you create a descriptor from a string, the string contains a normal fully-qualified java type name, not some lame JVM-internal descriptor string. There are similar descriptors for fields and methods. 

- *mikenakis-bytecode* facilitates easy generation of valid class files by completely shielding the programmer from having to deal with bytecode instruction offsets. To achieve this, *mikenakis-bytecode* makes exclusive use of references to actual instructions instead. This means that you can simply code a jump to a certain instruction, and *mikenakis-bytecode* will determine whether a 'short' or a 'long' form of the jump instruction should be used. (It does several passes over the instruction sequence of a method to ensure maximum savings.)  If necessary, the framework will even replace a short conditional jump with an inverted conditional short jump around an unconditional long jump.
                                                                
- *mikenakis-bytecode* makes it easier to deal with bytecode by completely shielding the programmer from having to deal with the constant pool and with cumbersome indexes into the constant pool. 
  - When parsing a byte array, the framework converts all constant pool indexes into references to objects representing the constants, and discards the entire constant pool immediately after parsing is complete.  
  - When generating a byte array, the framework re-creates the constant pool from scratch, making sure to merge duplicates as necessary.

- *mikenakis-bytecode* treats the `BootstrapMethods` attribute the same way it treats the constant pool, since this attribute is essentially nothing but an extra constant pool for exclusive use by the `INVOKEDYNAMIC` instruction. 
  - When parsing a byte array, each instance of the `INVOKEDYNAMIC` instruction receives its own copy of bootstrap method information, and the `BootstrapMethods` attribute is discarded.
  - When generating a byte array, the framework re-creates the bootstrap methods attribute, making sure to merge duplicate bootstrap method information as necessary.        

- *mikenakis-bytecode* makes it even easier to deal with bytecode by completely shielding the programmer from having to deal with what the Java Virtual Machine Specification calls "constants". You just work with descriptor objects and with normal Java types. For example, if you want to load the constant "Hello, World!" you just code `codeAttribute.LDC( "Hello, World!" );`. Internally, the framework will create a "CONSTANT_Utf8_info" constant to hold the string, then it will create a "CONSTANT_String_info" constant to hold the mutf8 constant, and then it will create an LDC instruction with a reference to the string constant. You just don't have to worry about all this nonsense.

- *mikenakis-bytecode* reduces cognitive overhead by offering a few slight simplifications of the JVM instruction set. For example:
  - The JVM offers a cornucopia of instructions for loading a constant: `ICONST_M1`, `ICONST_0`, `ICONST_1`, `ICONST_2`, `ICONST_3`, `ICONST_4`, `ICONST_5`, `FCONST_0`, `FCONST_1`, `FCONST_2`, `LCONST_0`, `LCONST_1`, `DCONST_0`, `DCONST_1`, `BIPUSH`, `SIPUSH`, `LDC`, `LDC_W`, and `LDC2_W`. All these instructions achieve the exact same thing: they push a single constant into the JVM stack. Their sole purpose of existence is to make bytecode more compact. So, *mikenakis-bytecode* simplifies things by replacing this entire rigmarole with just `LDC`.
  - When examining bytecode, the framework will show you `LDC` regardless of what the original instruction was. 
  - When generating bytecode, the framework only allows you to specify `LDC`, and will emit the most compact opcode from the zoo, based on the type of the constant, the value of the constant, and where in the constant pool it is located.

- When it comes to coding stack frames, *mikenakis-bytecode* will not automatically generate stack frame entries for you, (because that's quite difficult,) but it will allow you to specify stack frames that simply reference target instructions, and it will pick the right subtype based on how long the actual offset to the target instruction turns out to be.

## What is the state of the project?

The project is still in an immature state of development, so the following must be noted:

1. Since I don't want to be dealing with versioning just yet, the artifacts are all snapshots. This means that every time you get the artifacts, you get the latest version of whatever it is that I am working on.
2. My style of development involves extensive refactoring all the time, so with each commit everything changes. And since the artifacts are snapshots, this means that if you start coding against my stuff, your code ***will*** fail to compile each time you re-obtain my artifacts.
3. Signatures are not supported yet. Luckily, type erasure makes it (strictly speaking) unnecessary for a bytecode framework to support generic type information, but it is still nice to have, and it should be noted that it is currently missing.    
4. The highest fully supported java version is 8. In order to support Java 16 I still have to add handling for the following:
   1. Records.
   2. Modules.
5. Even within the subset of functionality of the JVM that I am in theory currently supporting, there are a few edge / minor / rarely-stumbled-upon things that are lacking. For example, `throws_target` still contains an index into the exception table instead of a reference to the actual exception type descriptor; this means that you have to be careful to code the right index; it is not a big deal, and you are unlikely to even encounter the scenario, but it is worth noting. Since I am constantly working on these minor issues, their list is changing, so search for "TODO" in the code to find them.   
6. There will almost certainly be bugs. 

## How does working with *mikenakis-bytecode* look like?

If you want to get an idea of how it looks like to be working with *mikenakis-bytecode*, you can check out the tests, and also see how it is used in these projects:

For dumping the contents of class files in `mikenakis-classdump`
(https://github.com/mikenakis/Public/tree/master/classdump)

For dependency analysis in `mikenakis-testana`
(https://github.com/mikenakis/Public/tree/master/testana)

For bytecode generation in `mikenakis-intertwine`
(https://github.com/mikenakis/Public/tree/master/intertwine/main/mikenakis/intertwine/implementations/compiling)

## Repositories 

Source code is hosted on GitHub: https://github.com/mikenakis/Public/tree/master/bytecode

Continuous Integration is hosted on CircleCI.

The Artifact Repository is on Repsy: https://repo.repsy.io/mvn/mikenakis/mikenakis-public/mikenakis/

## License

This creative work is explicitly published under No License. This means that I remain the exclusive copyright holder of this creative work, and you may not do anything with it other than view its source code and admire it. More information here: michael.gr - Open Source but No License.

If you would like to do anything more with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](http://blog.michael.gr/2018/04/on-coding-style.html)

## Home page

The home page for this project is this page:
[michael.gr - GitHub project: mikenakis-bytecode](https://blog.michael.gr/2018/04/github-project-bytecode.html)
(but currently it just points back to this README.md file.)
