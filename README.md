# mikenakis-public
### My public projects all under one repository.
Because I have better things to do than curating a whole swarm of little repositories. 

<p align="center">
<img title="mikenakis-public logo" src="mikenakis-public.svg" width="256"/><br/>
The mikenakis-public logo<br>
by Mike Nakis, based on original work by Chris Tucker from the Noun Project,<br> 
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

Configuring IntelliJ IDEA after a fresh clone:
  - Configure break-on-unhandled-exception
    - Go to "Run" -> "View Breakpoints... (Ctrl+Shift+F8)"
      - Under "Java Exception Breakpoints":
        - check "Any exception".
        - ensure "Caught exception" is unchecked.
        - ensure "Uncaught exception" is checked.
  - Setup run configurations
    - NOTE: IntelliJ IDEA run configurations have been included in the project, see *.run.xml files.
      Therefore, the following is unnecessary and should be removed:
    - Configuration:
      - Name: `Testana`
      - Before launch: `Build Project`
      - Use classpath of module: `testana-console`
      - VM options: `-enableassertions`
      - Main class: `mikenakis.testana.console.TestanaConsoleMain`
      - Program arguments: none needed, but this can be useful: `--show-structure Verbose --show-test-plan Verbose`
      - Working directory: the project source root directory (where the topmost pom.xml file lies)
    - Configuration:
      - Name: `Testana - clear history`
      - Use classpath of module: `testana-console`
      - VM options: `-enableassertions`
      - Main class: `mikenakis.testana.console.TestanaConsoleMain`
      - Program arguments: `--clear-history`
      - Working directory: the project source root directory (where the topmost pom.xml file lies)
    - Configure code coverage
      - Note: Code coverage appears to only work when launching tests from
within intellij idea; I have not found a way to make it work via testana.
When using testana, code coverage is collected only for packages that
testana directly depends on.
      - To set-up a run/debug configuration of tests for coverage:
        - Create the configuration by telling IntelliJ IDEA to run tests with coverage.
        - JRE: Use default.
        - Use classpath of module: Use default.
        - Search for tests: `All in package` and package-name-with-tests-and-code-to-be-covered
        - Working directory: $MODULE_WORKIND_DIR$
        - Search for tests: Across module dependencies
        - Code coverage:
          - Packages and classes to include in coverage data: package-name-with-tests-and-code-to-be-covered
          - Collect coverage in test folders: yes
          - Choose coverage runner: "JaCoCo"
