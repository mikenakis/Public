# mikenakis-classdump
#### A utility for dumping the contents of class files.

<p align="center">
<img title="mikenakis-classdump logo" src="mikenakis-classdump-512.png" width="256"/><br/>
The mikenakis-classdump logo.<br/>
Based on an image found on the interwebz.<br/>
</p>

## Description

`mikenakis-classdump` is a command-line utility for dumping the contents of class files.


## What is mikenakis-classdump ?

This is a java+maven module which produces the ClassDump command-line utility. It has dependencies on a few other java+maven projects of mine, most importantly mikenakis-kit and mikenakis-bytecode.

## What is the purpose of mikenakis-classdump ?

The purpose of this command-line utility is to provide a human-readable representation of a java class file. Its main use is for examining a class file produced by the compiler so as to produce a similar custom-built class file using the mikenakis-bytecode library.

## Is mikenakis-classdump a replacement for 'javap'?

mikenakis-classdump is certainly **_not_** aiming to be a replacement for the 'javap' utility that comes bundled with the JDK.   That's because mikenakis-classdump does not show the contents of the constant pool, and it does not show the byte offsets of bytecode instructions. Thus, the utility is not suitable for troubleshooting what is wrong with bytecode. If you have a classfile in your hands which fails to load, you need to use javap to troubleshoot it.

## What does the output of mikenakis-classdump look like?

Let us consider the following tiny "Hello, world!" java program:

	package mikenakis.bytecode.test.model;
	
	public abstract class Class0HelloWorld
	{
		public static void main( String[] args )
		{
			System.out.print( "Hello, world!\n" );
		}
	}

The output of mikenakis-classdump for the classfile produced by compiling the above class looks like this:

	ClassDumpMain.classDump(ClassDumpMain.java:149) | DEBUG | 1 | 18:52:17.843 | main | Dumping [...]\mikenakis\bytecode-test\test-classes\mikenakis\bytecode\test\model\Class0HelloWorld.class
	■ ByteCodeType version = 60.0, accessFlags = [public, super, abstract], this = Class0HelloWorld, super = java.lang.Object
	├─■ interfaces: 0 items
	├─■ extraTypes: 0 items
	├─■ fields: 0 items
	├─■ methods: 2 items
	│ ├─■ [0] ByteCodeMethod accessFlags = [public], prototype = void <init>()
	│ │ └─■ attributes: 1 items
	│ │   └─■ [0] CodeAttribute maxStack = 1, maxLocals = 1
	│ │     ├─■ instructions: 3 entries
	│ │     │ ├─■ L3:     ALOAD 0
	│ │     │ ├─■         INVOKESPECIAL reference = Plain; void java.lang.Object.<init>()
	│ │     │ └─■         RETURN
	│ │     ├─■ exceptionInfos: 0 items
	│ │     └─■ attributeSet: 0 items
	│ └─■ [1] ByteCodeMethod accessFlags = [public, static], prototype = void main( java.lang.String[] )
	│   └─■ attributes: 1 items
	│     └─■ [0] CodeAttribute maxStack = 2, maxLocals = 1
	│       ├─■ instructions: 4 entries
	│       │ ├─■ L7:     GETSTATIC java.io.PrintStream java.lang.System.out
	│       │ ├─■         LDC "Hello, world!\n"
	│       │ ├─■         INVOKEVIRTUAL reference = Plain; void java.io.PrintStream.print( java.lang.String )
	│       │ └─■ L8:     RETURN
	│       ├─■ exceptionInfos: 0 items
	│       └─■ attributeSet: 0 items
	└─■ attributeSet: 0 items

Information is added to help the human reader, but quite a bit of the raw information is removed.  This makes mikenakis-classdump useful for comparing class files, because it omits information that may differ due to random reasons without effecting the semantics of the bytecode. For example:
- mikenakis-classdump refrains from displaying the constant pool of the class file, because constant-pool entries are ordered randomly, so the constant pools of two files may look entirely different, and yet the two files may be semantically identical. 
- similarly, mikenakis-classdump refrains from showing the constant pool indexes that are used by various instructions and attributes, again because they are essentially random. 
  - When displaying an instruction or attribute that references a constant, mikenakis-classdump omits the constant index, and emits the actual value of the constant. In the case of string constants, mikenakis-classdump even skips the string constant and goes directly to the "utf8" constant to display its value. For example, in the above listing of HelloWorld.class, the `LDC` instruction is shown as loading the `"Hello, world!\n"` constant.
- Alternative forms of various opcodes such as `GOTO` and `LDC` are replaced with a single opcode. For more information about all the different opcodes that are simplified, please look at the documentation of the mikenakis-bytecode library. (https://github.com/mikenakis/Public/tree/master/bytecode)

## How is mikenakis-classdump used?

If you are using IntelliJ IDEA, you can configure it so that by pressing a couple of keys it launches the ClassDump utility to dump the bytecode of the class file generated from the java source file that you are currently viewing in the editor. Here is how:
- Create a launcher ("Run/Debug Configuration") for the ClassDump utility. By default, this should have the right SDK, classpath, main class, and working directory.
- In the list of actions to perform before launching, remove "Build" and add "Build Project".  This will ensure that the class you are viewing in the editor has been compiled into a class file before ClassDump gets launched.  It is necessary because normally, IntelliJ IDEA only builds the module that you are launching and its dependencies, but your classes are not dependencies of ClassDump, so IntelliJ IDEA must be told to build everything.
- In the command-line arguments, add the following:

  --sources $SourcepathEntry$ $OutputPath$ $FileClass$

  - The --sources $SourcePathEntry$ argument tells ClassDump where your source code is located. The Intellij IDEA macro expands to the root directory of the source code of the module that contains the java file that you are currently viewing in the editor. 
  - The $OutputPath$ argument tells ClassDump where your class files are located. The Intellij IDEA macro expands to the output directory of the module that contains the java file that you are currently viewing in the editor.
  - The $FileClass$ argument tells ClassDump which class you want to dump. The Intellij IDEA macro expands to the fully-qualified class name that you are currently viewing in the editor.  ClassDump uses this fully qualified class name as follows:
    - In combination with $OutputPath$ to locate the class file and dump its contents.
    - In combination with $SourcePathEntry$ to locate the corresponding java source file and display the source line that corresponds to each group of bytecode instructions.

## Repositories 

Source code is hosted on GitHub: https://github.com/mikenakis/Public/tree/master/classdump

Continuous Integration is hosted on CircleCI. (But they do not allow viewing unless you are logged in.)

The Artifact Repository is on Repsy: https://repo.repsy.io/mvn/mikenakis/mikenakis-public/mikenakis/

## Home page

The home page of the project is here:
[michael.gr - GitHub project: mikenakis-classdump](https://blog.michael.gr/2018/04/github-project-classdump.html)
(Though it just points to this README.md file.)

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
