# mikenakis-testana

#### A command-line utility for running only those tests that actually need to run.

<p align="center">
<img title="mikenakis-testana logo" src="mikenakis-testana.svg" width="256"/><br/>
The mikenakis-testana logo, <i>profile of a crash test dummy</i>.<br/>
By Mike Nakis, based on original work by
<a href="https://thenounproject.com/term/crash-test-dummy/401583/">Wes Breazell</a>,
<a href="https://thenounproject.com/term/woman/129498/">Alexander Skowalsky / Alex Tai</a>, 
and <a href="https://thenounproject.com/term/crash/1175057/">Jan Pleva</a>.<br/>
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

## What is mikenakis-testana? 

Testana is a utility for running JUnit-compatible tests in Maven-based Java projects, offering some amazing features, the most important being that every time you make some changes to your code and run Testana, it detects which tests need to run, and runs only those tests, saving you tons of time. 

Note: in this document the term project refers to the topmost (most-encompassing) organizational entity of your source code, while the term module refers to the smaller organizational entity that produces a single library or a single executable. This is IntelliJ IDEA terminology. In Eclipse the corresponding terms are workspace and project. In Visual Studio the corresponding terms are solution and project. 

## What does mikenakis-testana do? 

- You launch Testana each time you want to run any tests in your project.
- In the general case you do not need to supply any arguments, you just have to run it on the root of your source folder tree, and it figures out the rest. So, you can just bind it to a key and have all your testing needs covered with the press of a button.
- Testana figures out which tests need to run by:
  - Scanning the entire source tree for module description files. Note that this does not take much time, because directory recursion stops as soon as a module description file has been found, and these files tend to be high up in the source tree.
  - Parsing the module description files to obtain all the output directories. (Currently, maven is supported.)
  - Loading classes from the output directories and analyzing bytecode in order to:
    - Discover which ones are test classes. 
    - Determine the dependencies of each test class. (Recursively, so that transitive dependencies are included, too.)
  - Checking the timestamp of each class file to see if it has been modified since last run time of any tests that depend on it.
  - Remembering the last successful run time of each test. 
- Testana then only runs the tests that need to run.
- Testana selects which tests to run from among all tests of all modules in your entire project, thus guaranteeing that any and all tests that need to run will run. 
- Testana runs your test classes in order of dependency, meaning that classes that are most dependent upon will be tested first, and classes that depend upon those will be tested next, and so on until everything has been tested.
- Testana runs the methods of a test class in their natural order, which is the order in which the methods appear in the source file. (Duh!) 
- If you have a test class that inherits from another test class, Testana will run the test methods of the ancestor first. 

## Why should I care about running only the tests that need to run? 

In workplaces with huge projects the usual situation is that tests take an unreasonably long time to run, so developers tend to take shortcuts in running them. One approach some developers follow is that they simply commit code without running any tests, relying on the continuous build to run the tests and notify them of any test failures. This has multiple disadvantages:

It causes repeated interruptions in the workflow, due to the slow turnaround of the continuous build, which is often several hours, sometimes overnight, and even in the fastest cases, always longer than a normal person's attention span. (That's by definition; if it was not, then there would be no problem with quickly running all tests locally each time before committing.) 

The failed tests require additional commits to fix, and each commit requires a meaningful commit message, which means that test failures often need to be registered and tracked as bugs, thus increasing the overall level of bureaucracy in the development process. 

The commit history becomes bloated with commits that were done in vain and that should never be checked out because they contain bugs that are fixed in later commits.

Worst of all, untested commits that contain bugs are regularly being made to the repository. These bugs then stay there, while the continuous build takes its time doing its thing, eventually the tests run and they fail, the developers take notice, troubleshoot the test failure, come up with a theory as to what went wrong, come up with a fix, and commit the fix. This whole process takes quite a long time, during which other unsuspecting developers inevitably pull from the repository, thus receiving the bugs. Kind of like Continuous Infection.

Testana solves all of the above problems by figuring out which tests need to run based on what has changed, and only running those tests. This cuts down the time it takes to run tests to a tiny fraction of what it is when blindly running all tests, which means that running the tests now becomes piece of cake and can usually be done real quick before committing, as it should.

Also, running tests real quick right after each pull from source control now becomes feasible, so a developer can avoid starting to work on source code on which the tests are failing.  (How often have you found yourself in a situation where you pull from source control, change something, run the tests, the tests fail, and you are wondering whether they fail due to changes you just made, or due to changes you pulled from the repository?)

## Why should I care about selecting the tests to run from among all tests of all modules of the entire project? 

Another approach followed by some developers to save time is manually choosing a set of packages that they believe should be tested, and running only the tests of those packages, so as to manage to test at least something before committing.

One simple reason why this is problematic is that it requires cognitive effort to figure out which packages might need testing, and manual work to launch tests on each one of them individually; it is not as easy as pressing a single button that stands for "test whatever needs to be tested".

A far bigger problem is that in manually selecting the tests to run, the developer is making assumptions about the dependencies of the code that they have modified and are about to commit. In complex systems, dependency graphs can be very difficult to grasp, and as the systems evolve, the dependencies keep changing. Unfortunately, unknown or not-fully-understood dependencies are a major source of bugs, and yet by hand-selecting what to test based on our assumptions about the dependencies, it is precisely the unknown and the not fully understood dependencies that are guaranteed to not be tested. This is a recipe for failure.

Testana solves the above problems by always selecting the tests to run from among all tests of all modules of the entire project. It does not hurt to do that, because tests that do not need to run will not run anyway.

## Why should I care about running test classes in order of dependency? 

Under JUnit, the order of execution of packages and classes within a package appears to be alphabetic. This is probably unintentional, and it is not configurable: As far as I know, JUnit does not offer any means of specifying the order in which packages should be tested, nor the order in which test classes within a package should be run, and it does not do anything in the direction of automatically figuring out by itself some order of execution that has any purpose or merit.

Alphabetic order of execution is not particularly useful. For example, in an alphabetic list of packages, `util` comes near the end, so it is usually tested last, and yet `util` tends to be a package that depends on no other packages, while most other packages depend on it, so if tests of other packages succeed, and yet tests of `util` fail, it can only be due to pure accident. It would be very nice to see `util` being tested first, so that if there is something wrong with it, then we know that we can stop testing: there is no point in testing packages that depend on a failing package.

Testana addresses this problem by executing test classes in order of dependency, which means that classes that do not depend on other classes will be tested first, and classes that depend upon them will be tested afterwards. This generally means that as soon as you see a test failure you can stop running the tests, because the most fundamental class with a defect has already been located. 

## Why should I care about running test methods in natural order? 

By default, JUnit will run your test methods in random order, which is at best useless, and in my opinion actually outright treacherous. 

One reason for wanting the test methods to run in the order in which they appear in the source file is because we usually test fundamental features of our software before we test features that depend upon them. (Note: it is the features under test that depend upon each other, not the tests themselves that depend upon each other.) So if a fundamental feature fails, we want that to be the very first error that will be reported. Tests of features that rely upon a feature whose test has failed might as well be skipped, because they can be expected to all fail, and as a matter of fact, reporting these failures before the failure of the more fundamental feature (due to a messed up order of test method execution) is an act of sabotage against the developer: it is sending us looking for problems in places where there are no problems to be found, and it is making it more difficult to locate the real problem, which usually lies in the test that failed first **_in the source file_**.

To give an example, it is completely pointless to be told that my `search-for-item-in-collection` test failed, and only later to be told that my `insert-item-to-collection` test failed. If `insert-item-to-collection` fails, it is game over, there is no need to go any further, no need to try anything else with the collection, no point beating a dead horse. How hard is this to understand? 

Finally, another very simple, very straightforward, and very important reason for wanting the test methods to be executed in natural order is because seeing the test method names listed in any other order is **_brainfuck._**

A related rant can be found here: [michael.gr - On JUnit's random order of test method execution](http://blog.michael.gr/2018/04/random-order-of-tests.html)

## Why should I care for running test methods of ancestors first? 

So, maybe you have never used inheritance in test classes, so this issue might be irrelevant to you, but I have, and I consider it very useful. I also consider `JUnit`'s behavior on this matter very annoying, because it does the exact opposite of what is useful. 

Inheritance in test classes can help to achieve great code coverage while reducing the total amount of test code. Suppose you have a collection hierarchy to test: you have an `ArrayList` class and a `HashSet` class, and you also have their corresponding test classes, `ArrayListTest` and `HashSetTest`. Now, both `ArrayList` and `HashSet` inherit from `Collection`, which means that lots of tests are going to be identical between `ArrayListTest` and `HashSetTest`. One way to eliminate duplication is to have a `CollectionTest` abstract base class, which tests only `Collection` methods, and it does so without knowing what class is implementing them. Then, both `ArrayListTest` and `HashSetTest` can inherit from `CollectionTest` and provide specialized tests for functionality that is specific to `ArrayList` and `HashSet` respectively. Under such a scenario, when `ArrayListTest` or `HashSetTest` runs, we want the methods of `CollectionTest` to be executed first, because they are testing the fundamental (more general) functionality. 

To make the example more specific, `CollectionTest` is likely to add an item to the collection and then check whether the collection contains the item. If this test fails, then there is absolutely no point in proceeding with tests of `ArrayListTest` which will, for example, try adding multiple items to the collection and check to make sure that `IndexOf()` returns the right results. 

Again, `JUnit` handles this in a way which is exactly the opposite of what we would want: it executes the descendant (more specialized) methods first, and the ancestor (more general) methods last. 

Testana corrects it by executing ancestor methods first, descendant methods last. 

## What are the limitations of Testana? 

- Testana only works with Java. Support for more languages may be added in the future. 
- Testana only understands Maven modules. Support for other module formats may be added in the future. 
- Testana only understands the following JUnit annotations: `@Test`, `@Before`, `@After`, and `@Ignore`. Support for more JUnit annotations may be added in the future. 
- Testana's dependency detection relies on absolutely strong static typing. Dependencies that have been disavowed by being encoded in configuration files, (e.g. swagger files, spring configuration files, etc) denatured by being encoded as strings, (stringly-typed,) obscured through hackery such as duck typing, or squandered by weak typing (euphemistically called dynamic typing) are not supported.  
- Testana only has a command-line interface. A GUI may be added in the future. 
- Testana assumes that your local maven repository is under `~/.m2/repository`. It does not check `~/.m2/settings.xml` to see whether you have configured your local repository to reside elsewhere. There are plans to fix this.
- Testana currently does not consider resources when checking dependencies: a test may depend on a class which depends on a resource, and if the resource is modified, then the test should be re-run, but this does not currently happen. There are plans to fix this. 
- Testana does not currently have a well defined strategy for handling dependency cycles. Nothing bad happens, but the order of test execution in those cases is kind of vague. There are plans to do something about this. 
- Testana is still in Beta. There will be bugs. There will be cases not covered. There will be usage scenarios not considered. There will be change. (What else is new?) 

## How can I see Testana in action? 

Without going into too much detail in this document, (because I want to have only one place to maintain, and that's the code,) here is roughly what you need to do:

- Check out the repository.
- Import the root pom.xml into your project in your IDE.
- Setup your IDE so that it builds your entire project before running Testana on it. 
  - That's necessary because normally, prior to running Testana, your IDE will only build the modules that Testana depends on; however, Testana does not depend on any of the modules of your project, and yet Testana will discover them at runtime, so they all need to be up to date when Testana runs. 
  - The way to achieve this with IntelliJ IDEA is to edit the run/debug configuration of Testana and under `Before launch:` specify `Build Project` instead of the default, which is `Build`. 
- Make sure that when running something from within your IDE, the working directory is the root directory of the source code of your project. (It usually is.)
- Find the class that contains `public static void main` in the Testana subtree, and run it. (It should be `mikenakis.testana.console.TestanaConsoleMain` unless I have refactored it into something different by the time you read this.)
- If you run Testana without any arguments, its default behavior should be to do the right thing. (Note that the first time you run Testana, there may be a long delay while classes are being parsed; the information collected is cached, so this delay will not be there next time Testana is run.)
  - On the first run, it should run all tests. 
  - On the second run, it should not run any tests, because nothing will have changed. 
  - If you touch one of your source files, and re-run Testana, you will notice that only tests that either directly or indirectly depend on the changed file will be run. 
- If you run Testana with `--help` it will give you a rundown of all the options it supports. 

## Repositories 

Source code is hosted on GitHub: https://github.com/mikenakis/Public/tree/master/testana

Continuous Integration is hosted on CircleCI.

The Artifact Repository is on Repsy: https://repo.repsy.io/mvn/mikenakis/mikenakis-public/mikenakis/

## License

This creative work is explicitly published under **_No License_**. This means that I remain the exclusive copyright holder of this creative work, and you may not do anything with it other than view its source code and admire it. More information here: michael.gr - Open Source but No License.

If you would like to do anything more with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](http://blog.michael.gr/2018/04/on-coding-style.html)

## Home page

The home page of the project is:
[michael.gr - GitHub project: mikenakis-testana](https://blog.michael.gr/2018/04/github-project-mikenakis-testana.html) 
(Though it just points back to this README.md file)



























## Poor man's issue tracking

- TODO: add handling of resources (non-class files.)
  Resources are assumed to always be accessed through resource locator classes. A change in a resource needs to be handled as if it was a change in its resource locator class.
  Besides modification of resources, we also need to account for creation and deletion of resources, because a resource locator may be engaging in resource discovery. Given a
  resource, its resource locator class can be found as follows:

    - Begin at the folder of the resource.

    - If the folder contains one and only one class, then this is pesumed to be the resource locator class of the resource; done. Otherwise,

    - If the folder contains a class called `ResourceLocator.class`, then this is the resource locator class of the resource; done. Otherwise,

    - If the folder contains multiple classes, then we have an error: the resource locator is unknown or ambiguous. Otherwise,

    - Move to the folder above and repeat the search, unless we are in the root output folder of the module, in which case we have an error: the resource locator could not be
      found.

- TODO: expand the syntax of settings so that we can exclude folders in the source tree, like `.idea` and `.git`, which are now handled by hard-coded exlusion. Alternatively,
  parse `.gitignore`. (Uhm, probably not.)

- TODO: introduce a `TestanaTestEngine`, and make it have all the new functionality enabled by default. Then, make all of Testana's functionality optional in `JUnitTestEngine`, so
  that by default Testana's functionality is not applied to JUnit tests.

- TODO: further enhancements to the Testana test engine, that deviate from the JUnit standard:

    - Avoid invoking the constructor of the test class for each test method.

    - Get rid of `@Before` and `@After`, initialization should be done in the constructor and cleanup should be done in `close()` if the test class `implements AutoCloseable`.

    - Optionally supply the `Umbilical` interface to the test class constructor, so that the test class can invoke it without having to rely on the `GlobalUmbilicalHolder` hack.
      Then, remove that hack from the foundation package, and either completely abolish it, or make it something that only `JUnitTestEngine` needs to worry about.

- TODO: try determining the order of testing based on packages instead of individual classes, see if it makes any interesting difference.

- TODO: do something to reduce the verbosity of the project structure dump. For example, refrain from showing the full dependency subtree of each external library every single time
  the library is mentioned in the dump, and instead provide a separate dump of the dependency subtree of each external library, either in the beginning or the end of the project
  structure dump.

- TODO: possibly research how we could optimize dependency expansion by means of a dependency matrix. See whether dependency expansion can be made fast enough to actually save more
  time than it wastes; otherwise, get rid of it.

- TODO: add the option to skip all remaining tests once a test fails. Ideally, we would want to skip only tests of classes that depend on the class whose test failed, but this may
  be a bit hard to determine.

- TODO: research whether it is possible to detect, with reasonable accuracy, which class or classes are explicitly being tested by a given test class.

- TODO: keep track of not only the last successful run timestamp of each test, but also of the duration it took for the test to complete, so that as soon as the test plan is built,
  we can give an estimation as to how long it will take to complete the tests. Plus, with each log message we can display a new estimate.

- TODO: (optionally) analyze the dependencies of not only the `ConstantPool` of each test class, but also the bytecode of each test method, so as to determine whether to perform or
  omit a test at the individual test method level instead of at the test class level. See if it makes any useful difference.

- TODO: see if there is any way to optimize the process of determining what is a test class and what isn't.

- TODO: optimize the saving of each test's "last successful run" time after it runs. As it stands right now, the entire timestamps file is written after each test class is run,
  which is quite wasteful.

- TODO: add better handling of dependency cycles. Measures to consider:

    - Display warnings about cyclic dependencies so that the programmer may consider fixing them. Also introduce an annotation for suppressing those warnings, because circular
      dependencies are inevitable, there will always be some.

    - When a cycle is detected, try to artificially give priority to one of the classes in the cycle by applying more rules. For example, given two classes A and B that have been
      found to circularly depend on each other, there could be rules like the following:
        - If A is abstract and B is concrete, then the concrete class wins, so consider B as depending on A.
        - If A depends on B indirectly through class X, but B depends on A directly, then the shortest number of hops wins, so consider B as depending on A.
        - If A is public and B is package-private, then public wins, so consider A as depending on B.
        - If A contains `new B()` but B does not contain `new A()` then the factory wins, so consider A as depending on B.
        - and so on.

- MVN-TODO-1: get the location of the local repository from the settings instead of assuming it is under .m2/repository!

      Path mavenSettingsPath = m2Path.resolve( "settings.xml" );
      extract <localRepository>...</localRepository> from settings.xml

- TODO: Add support for scala.

  Currently, even though we have some tests written in scala, they are not being executed. See how to fix this so that they are being executed.

- TODO: Fix the cause of the NoClassDefFound warnings.

  Currently testana is issuing warnings about java.lang.NoClassDefFoundError exceptions being thrown. These exceptions fall into the following categories:
    - Thrown while analyzing classes that are derived from external dependencies
        - Example: "Could not get constructors of 'mikenakis.testing0.NaturalMethodOrderJUnit4ClassRunner':
          java.lang.NoClassDefFoundError: org/hamcrest/SelfDescribing"
    - Thrown while trying to analyze classes that are external dependencies.
      (And therefore should not be analyzed.)
        - Example: "could not get declared methods of 'org.scalatest.funsuite.AnyFunSuite':
          java.lang.NoClassDefFoundError: scala/Serializable"
          (Might need to clear the cache and the persistence to see this problem) 
