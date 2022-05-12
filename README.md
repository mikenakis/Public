# mikenakis-public
### Most of my public projects in one big repository. (Monorepo.)
Because I have better things to do than curating a whole swarm of little repositories. 

<p align="center">
<img title="mikenakis-public logo" src="mikenakis-public-logo.svg" width="256"/><br/>
The mikenakis-public logo<br>
by Mike Nakis, based on original work by Chris Tucker from the Noun Project,<br> 
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

Configuration necessary after a fresh clone:
  - Configure IntellijIdea
    - Configure breaking on unhandled exceptions
      - Go to "Run" -> "View Breakpoints..."
        - Under "Java Exception Breakpoints":
          - check "Any exception".
          - ensure "Caught exception" is **unchecked**.
          - ensure "Uncaught exception" is **checked**.
    - Configure breaking on handled exceptions in foreign threads
      - See https://stackoverflow.com/a/71482180/773113 
      - Go to "Run" -> "View Breakpoints..."
        - Under "Java Exception Breakpoints":
          - Add a Java Exception Breakpoint.
          - Set the exception class to be `java.lang.Throwable`.
          - PEARL: If you start typing 'Throwable' or 'java.lang.Throwable' in the search box of the "Search by name" tab,
            (even after you have checked 'Include non-project items',) 
            IntelliJ IDEA will fail/refuse to find `java.lang.Throwable`, presumably because they are already providing the
            "Any exception" entry, and they cannot imagine why one would need to add one more entry for the same class.  
            So, to add an entry for `java.lang.Throwable` you need to:
            - Switch to the "Project" tab
            - Navigate to "External Libraries", then to your JDK, then to the `java.base` module, then to the `java.lang` package
            - Locate "Throwable" and select it.
          - Ensure "Caught exception" is **checked**.
          - Check the checkbox of "Catch class filters:" and enter `mikenakis.kit.debug.Debug`.
          - PEARL: The "Breakpoints" dialog of IntelliJ IDEA contains many text boxes expecting class names, and in any of
            these text boxes you can enter any random garbage, and IntelliJ IDEA will happily accept it, without the slightest
            hint that it is wrong. Then, when you expect it to break, it will silently fail to break. To avoid this, never type
            a class name in these text boxes, always use the navigation feature to locate a class which is guaranteed to exist.
    - Configure breaking on programmatic breakpoints.
      - PEARL: Java does not support programmatic breakpoints the way C# does with `System.Diagnostics.Debugger.Break()`. 
        We emulate this behavior by throwing and immediately catching a special `BreakpointException`, but for this to work,
        we have to manually place a breakpoint on the line that catches it.
      - Navigate to `mikenakis.kit.debug.Debug` and add a breakpoint on the line which says "Place breakpoint here". 
      - Note: since I introduced the "breaking on handled exceptions in foreign threads" item, the mechanism that accomplishes
        it also takes care of breaking on `BreakpointException`, so strictly speaking it is unnecessary to place a breakpoint
        on that line; however, these are two different things, so I am documenting them separately.
  - Configure the "Grep Console" plugin:
``` 
    Expression                Bold        Background    Foreground
    [checked] ".* FATAL .*"   [checked  ] [unchecked]   [checked] DC7075
    [checked] ".* ERROR .*"   [unchecked] [unchecked]   [checked] F59B9D
    [checked] ".* WARN .*"    [unchecked] [unchecked]   [checked] DD9203
    [checked] ".* INFO .*"    [unchecked] [unchecked]   [checked] A6A6A6
    [checked] ".* DEBUG .*"   [unchecked] [unchecked]   [checked] 0DCCDD
    [checked] ".* TRACE .*"   [unchecked] [unchecked]   [checked] 7D87BA 
```
