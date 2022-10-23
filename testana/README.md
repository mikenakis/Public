# Testana

#### A command-line utility for running tests by order of dependency and for only running tests that actually need to run.

<p align="center">
<img title="mikenakis-testana logo" src="mikenakis-testana.svg" width="256"/><br/>
The mikenakis-testana logo, <i>profile of a crash test dummy</i>.<br/>
By Mike Nakis, based on original work by
<a href="https://thenounproject.com/term/crash-test-dummy/401583/">Wes Breazell</a>,
<a href="https://thenounproject.com/term/woman/129498/">Alexander Skowalsky / Alex Tai</a>, 
and <a href="https://thenounproject.com/term/crash/1175057/">Jan Pleva</a>.<br/>
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

## What is Testana? 

Testana is a utility for running tests, offering some very useful features that are missing from existing testing frameworks. Currently, JUnit-compatible tests in Maven-based Java projects are supported.

## What does Testana do? 

- Testana runs only the subset of tests that actually need to run, based on the last successful run time of each test class, and whether its dependencies have changed or not. 
- Testana runs test classes by order of dependency, so that the tests of the most dependent-upon modules run first, tests of modules that depend on those run next, and so on. Thus, Testana facilitates **_Incremental Integration Testing_** (see https://blog.michael.gr/2022/10/incremental-integration-testing.html) 
- Testana runs the test methods within each class is the order in which they appear in the source file, otherwise known as **_Natural Method Order_**.

## How does Testana work? 
             
Note: in this document the term _**project**_ refers to the topmost (most-encompassing) organizational entity of your source code, while the term _**module**_ refers to the smaller organizational entity that produces a single library or a single executable. This is IntelliJ IDEA terminology. In Eclipse the corresponding terms are workspace and project. In Visual Studio the corresponding terms are solution and project.

You launch Testana each time you want to run any tests in your project. In the general case you do not need to supply any arguments, you just have to run it on the root of your source folder tree, and it figures out the rest.

Testana does the following:

- Scans the entire source tree for module description files.
  - Currently, maven is supported.
  - Note that this scan does not take much time, because directory recursion stops as soon as a module description file has been found, and these files tend to be high up in the source tree.
- Parses the module description files in order to discover all output directories.
  - Testana makes use of the `com.apache.maven` library to accomplish this, and since the interface of this library is so insanely complicated as to be virtually unusable, Testana also makes use of `com.eclipse.aether` in order to work with `com.apache.maven`. 
- Loads all classes from the output directories and analyzes bytecode in order to:
  - Determine the dependencies of each test class. (Recursively, so that transitive dependencies are included.)
  - Discover which classes are test classes.
    - Currently, JUnit is supported.
- Checks the timestamp of each class file to see if it has been modified since last run time of any tests that depend on it.
- Runs the tests.
  - Selecting which tests to run from among all tests of all modules in your entire project, thus guaranteeing that any and all tests that need to run will run. 
  - Running only the tests that need to run, based on the last successful run time of each test, and on whether any of its dependencies have changed.
  - Running the test classes in order of dependency.
    - This means that classes that are most dependent upon will be tested first, and classes that depend upon those will be tested next, and so on until everything has been tested.
  - Running the methods of a test class in their natural order, which is the order in which the methods appear in the source file. 
  - Running ancestor test methods first, and descendant test methods next, in cases where a test class inherits from another test class.
- Testana then remembers the last successful run time of each test, so as to be able to determine whether this test should run next time Testana is launched. 

## Why should I care about running only the tests that need to run? 

In workplaces with huge projects the usual situation is that tests take an unreasonably long time to run, so developers tend to take shortcuts in running them. One approach some developers follow is that they simply commit code without running any tests, relying on the continuous build to run the tests and notify them of any test failures. This has multiple disadvantages:

It causes repeated interruptions in the workflow, due to the slow turnaround of the continuous build, which is often several hours, sometimes overnight, and even in the fastest cases, always longer than a normal person's attention span. (That's by definition; if it was not, then there would be no problem with quickly running all tests locally each time before committing.) 

The failed tests require additional commits to fix, and each commit requires a meaningful commit message, which means that test failures often need to be registered and tracked as bugs, thus increasing the overall level of bureaucracy in the development process. 

The commit history becomes bloated with commits that were done in vain and that should never be checked out because they contain bugs that are fixed in later commits.

Worst of all, untested commits that contain bugs are regularly being made to the repository. These bugs then stay there, while the continuous build takes its time doing its thing, eventually the tests run, and they fail, the developers take notice, troubleshoot the test failure, come up with a theory as to what went wrong, come up with a fix, and commit the fix. This whole process takes quite a long time, during which other unsuspecting developers inevitably pull from the repository, thus receiving the bugs. Kind of like Continuous Infection.

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

Testana addresses this problem by executing test classes in order of dependency, which means that classes with no dependencies will be tested first, classes that depend upon them will be tested next, and so on until everything has been tested. This generally means that as soon as you see a test failure you can stop running the tests, because the most fundamental class with a defect has already been located.

For more information about this way of testing, see [michael.gr - Incremental Integration Testing](https://blog.michael.gr/2022/10/incremental-integration-testing.html)

## Why should I care about running test methods in natural order? 

By default, JUnit will run your test methods in random order, which is at best useless, and arguably treacherous. 

One reason for wanting the test methods to run in the order in which they appear in the source file is because we usually test fundamental features of our software before we test features that depend upon them. (Note: it is the features under test that depend upon each other, not the tests themselves that depend upon each other.) So if a fundamental feature fails, we want that to be the very first error that will be reported. Tests of features that rely upon a feature whose test has failed might as well be skipped, because they can be expected to all fail, and as a matter of fact, reporting these failures before the failure of the more fundamental feature (due to a messed up order of test method execution) is an act of sabotage against the developer: it is sending us looking for problems in places where there are no problems to be found, and it is making it more difficult to locate the real problem, which typically lies in the test that failed first **_in the source file_**.

To give an example, it is completely pointless to be told that my `search-for-item-in-collection` test failed, and only later to be told that my `insert-item-to-collection` test failed. If `insert-item-to-collection` fails, it is game over, there is no need to go any further, no need to try anything else with the collection, no point beating a dead horse. How hard is this to understand? 

Finally, another very simple, very straightforward, and very important reason for wanting the test methods to be executed in natural order is because seeing the test method names listed in any other order is **_brainfuck._**

A related rant can be found here: [michael.gr - On JUnit's random order of test method execution](https://blog.michael.gr/2018/04/random-order-of-tests.html)

## Why should I care for running test methods of ancestors first? 

So, maybe you have never used inheritance in test classes, so this issue might be irrelevant to you, but I have, and I consider it very useful. I also consider `JUnit`'s behavior on this matter very annoying, because it does the exact opposite of what is useful. 

Inheritance in test classes can help to achieve great code coverage while reducing the total amount of test code. Suppose you have a collection hierarchy to test: you have an `ArrayList` class and a `HashSet` class, and you also have their corresponding test classes, `ArrayListTest` and `HashSetTest`. Now, both `ArrayList` and `HashSet` inherit from `Collection`, which means that lots of tests are going to be identical between `ArrayListTest` and `HashSetTest`. One way to eliminate duplication is to have a `CollectionTest` abstract base class, which tests only `Collection` methods, and it does so without knowing what class is implementing them. Then, both `ArrayListTest` and `HashSetTest` can inherit from `CollectionTest` and provide specialized tests for functionality that is specific to `ArrayList` and `HashSet` respectively. Under such a scenario, when `ArrayListTest` or `HashSetTest` runs, we want the methods of `CollectionTest` to be executed first, because they are testing the fundamental (more general) functionality. 

To make the example more specific, `CollectionTest` is likely to add an item to the collection and then check whether the collection contains the item. If this test fails, then there is absolutely no point in proceeding with tests of `ArrayListTest` which will, for example, try adding multiple items to the collection and check to make sure that `IndexOf()` returns the right results. 

Again, `JUnit` handles this in a way which is exactly the opposite of what we would want: it executes the descendant (more specialized) methods first, and the ancestor (more general) methods last. 

Testana corrects it by executing ancestor methods first, descendant methods last. 

## What are the limitations of Testana? 

- Testana only works with Java. Support for more languages may be added in the future. 
- Testana only understands Maven modules. Support for other module formats may be added in the future. 
- Testana only understands the following JUnit annotations: `@Test`, `@Before`, `@After`, and `@Ignore`. Support for more JUnit annotations might be added in the future. (But don't hold your breath for it.) 
- Testana's dependency detection relies on absolutely strong static typing. Dependencies that have been disavowed by being encoded in configuration files, (e.g. swagger files, spring configuration files, etc.) denatured by being encoded as strings, (stringly-typed,) obscured through hackery such as duck typing, or squandered by weak typing (euphemistically called dynamic typing) are not supported, and there is no plan to ever support them. Seriously, stop all this fuckery and use strong static typing **_only_**. 
- Testana only has a command-line interface. A GUI may be added in the future. (But don't hold your breath for it.)  
- Testana assumes that your local maven repository is under `~/.m2/repository`. It does not check `~/.m2/settings.xml` to see whether you have configured your local repository to reside elsewhere. There are plans to fix this.
- Testana currently does not consider resources when checking dependencies: a test may depend on a class which depends on a resource, and if the resource is modified, then the test should be re-run, but this does not currently happen. There are plans to fix this. 
- Testana does not currently have a well-defined strategy for handling dependency cycles. Nothing bad happens, but the order of test execution in those cases is kind of vague. There are plans to do something about this. 
- Testana is still in Beta. There will be bugs. There will be cases not covered. There will be usage scenarios not considered. There will be change. (What else is new?) 

## How can I see Testana in action? 

Without going into too much detail in this document, (because I want to have only one place to maintain, and that's the code,) here is roughly what you need to do:

- Check out the repository.
- Import the root pom.xml of testana into your project in your IDE.
- Set up your IDE so that it builds your entire project before running Testana on it. 
  - That's necessary because normally, prior to running Testana, your IDE will only build the modules that Testana depends on; however, Testana does not depend on any of the modules of your project, and yet Testana will discover them at runtime, so they all need to be up-to-date when Testana runs. 
  - The way to achieve this with IntelliJ IDEA is to edit the run/debug configuration of Testana and under `Before launch:` specify `Build Project` instead of the default, which is `Build`. 
- Make sure that when running something from within your IDE, the working directory is the root directory of the source code of your project. (It usually is.)
- Find the class that contains `public static void main` in the Testana subtree, and run it. (It should be `TestanaConsoleMain` unless I have refactored it into something different by the time you read this.)
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

This creative work is explicitly published under ***No License***. 
This means that I remain the exclusive copyright holder of this creative work, 
and you may not do anything with it other than view its source code and admire it. 
More information here: [michael.gr - Open Source but No License.](https://blog.michael.gr/2018/04/open-source-but-no-license.html)

If you would like to do anything with this creative work, contact me.

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](https://blog.michael.gr/2018/04/on-coding-style.html)

## Home page

The home page of the project is:
[michael.gr - GitHub project: mikenakis-testana](https://blog.michael.gr/2018/04/github-project-mikenakis-testana.html) 
(Though it just points back to this README.md file)


## Poor man's issue tracking
                           
- TODO: troubleshoot and fix bugs:
  - If two different modules contain a class with the exact same fully qualified name, testana will arbitrarily pick one 
    of the two, so:
    - A method may exist in both classes, but have a different implementation in each, resulting in malfunction.
    - A method may not exist in both classes, resulting in "no such method" exceptions.    

- TODO: add handling of resources (non-class files.)
  Resources are assumed to always be accessed through resource locator classes. A change in a resource needs to be handled as if it was a change in its resource locator class.
  Besides modification of resources, we also need to account for creation and deletion of resources, because a resource locator may be engaging in resource discovery. Given a
  resource, its resource locator class can be found as follows:
    - Begin at the folder of the resource.
    - If the folder contains one and only one class, then this is presumed to be the resource locator class of the resource; done. Otherwise,
    - If the folder contains a class called `ResourceLocator.class`, then this is the resource locator class of the resource; done. Otherwise,
    - If the folder contains multiple classes, then we have an error: the resource locator is unknown or ambiguous. Otherwise,
    - Move to the folder above and repeat the search, unless we are in the root output folder of the module, in which case we have an error: the resource locator could not be
      found.

- TODO: expand the syntax of settings so that we can exclude folders in the source tree, like `.idea` and `.git`, which are now handled by hard-coded exclusion. Alternatively, parse `.gitignore`. (Uhm, probably not.)

- TODO: introduce a `TestanaTestEngine`, which differs from JUnit:

    - Avoid creating a new instance of the test class prior to invoking each test method.
    - Get rid of `@Before` and `@After`: initialization should be done in the constructor and cleanup should be done 
      in `close()` if the test class `implements AutoCloseable`.

- TODO: try determining the order of testing based on packages instead of individual classes, see if it makes any 
        interesting difference. For one thing, we will then be able to execute all classes within a package by 
        alphabetical order, thus honoring the T001_... test class name prefixes.

- TODO: possibly research how we could optimize transitive dependency expansion by means of a dependency matrix. See whether transitive dependency expansion can be made fast enough to actually save more time than it wastes; otherwise, get rid of it.

- TODO: add the option to skip all remaining tests once a test fails. Ideally, we would want to skip only tests of classes that depend on the class whose test failed, but this may be a bit hard to determine.

- TODO: research whether it is possible to detect, with reasonable accuracy, which class or classes are explicitly being tested by a given test class.

- TODO: keep track of not only the last successful run timestamp of each test, but also of the duration it took for the test to complete, so that as soon as the test plan is built, we can give an estimation as to how long it will take to complete the tests. Plus, with each log message we can display a new estimate.

- TODO: (optionally) analyze dependencies on a test-method basis instead of a test-class basis, to determine whether to perform or
  omit a test at the individual test method level instead of at the test class level. See if it makes any useful difference.

- TODO: see if there is any way to optimize the process of determining what is a test class and what isn't.

- TODO: add better handling of dependency cycles. Measures to consider:

    - Display warnings about cyclic dependencies so that the programmer may consider fixing them. Also introduce an annotation for suppressing those warnings, because circular dependencies are inevitable, there will always be some.

    - When a cycle is detected, try to artificially give priority to one of the classes in the cycle by applying more rules. For example, given two classes A and B that have been found to circularly depend on each other, there could be rules like the following:
        - If A is abstract and B is concrete, then the concrete class wins, so consider B as depending on A.
        - If A depends on B indirectly through class X, but B depends on A directly, then the shortest number of hops wins, so consider B as depending on A.
        - If A is public and B is package-private, then public wins, so consider A as depending on B.
        - If A contains `new B()` but B does not contain `new A()` then the factory wins, so consider A as depending on B.
        - and so on.

- <strike>TODO: Eliminate the test rig projects using an in-memory file system</strike> WILL-NOT-DO

    - By using https://github.com/google/jimfs each testana test can create whatever test rig projects it needs in memory, so that the test rig projects do not have to exist in the source code repository as actual projects.
    - We actually do not want this: the test rig projects must be regular projects so that we can use the IDE for editing them, with syntax coloring, code completion, error highlighting, refactoring, etc.

- MVN-TODO-1: get the location of the local repository from the settings instead of assuming it is under .m2/repository!

      Path mavenSettingsPath = m2Path.resolve( "settings.xml" );
      extract <localRepository>...</localRepository> from settings.xml

- TODO: Add support for scala.

  Currently, even though we have some tests written in scala, they are not being executed. See how to fix this so that they are being executed.

- TODO: Fix the cause of the NoClassDefFound warnings.

  Currently, testana is issuing warnings about java.lang.NoClassDefFoundError exceptions being thrown. These exceptions fall into the following categories:
    - Thrown while analyzing classes that are derived from external dependencies
        - Example: "Could not get constructors of 'mikenakis.testing0.NaturalMethodOrderJUnit4ClassRunner':
          java.lang.NoClassDefFoundError: org/hamcrest/SelfDescribing"
    - Thrown while trying to analyze classes that are external dependencies.
      (And therefore should not be analyzed.)
        - Example: "could not get declared methods of 'org.scalatest.funsuite.AnyFunSuite':
          java.lang.NoClassDefFoundError: scala/Serializable"
          (Might need to clear the cache and the persistence to see this problem)
          
- <strike>TODO:</strike> Optimize the saving of each test's "last successful run" time.
  - As it stands right now, the entire timestamps file is written after each test class is run, which is somewhat wasteful.
  - I tried using a binary file for the last-successful-run times; either there was no improvement, or the improvement was so small as to be completely lost in the noise.

- <strike>TODO:</strike> DONE:
  - IntellijIdea does not stop on breakpoints inside anonymous inner classes loaded by Testana. 
    See Stackoverflow: "Intellij Idea breakpoints do not hit in anonymous inner class" https://stackoverflow.com/q/70949498/773113
