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

## Description

Testana is a utility for running `JUnit`-compatible tests in `Maven`-based `Java` projects, offering some amazing features, the most important of which being that every time you
make some changes to your code and run Testana, it detects which tests need to run, and runs only those tests, saving you tons of time.

More information: [michael.gr - GitHub project: mikenakis-testana](https://blog.michael.gr/2018/04/github-project-mikenakis-testana.html)

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](http://blog.michael.gr/2018/04/on-coding-style.html)

## Running

Launch Testana at the root source folder of your project, specifying `mikenakis.testana.console.TestanaConsoleMain` as the main class.

A `--help` command-line switch is available.

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
