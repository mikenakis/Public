# mikenakis-public
### All my public projects in one repository.
Because I have better things to do than curating a whole swarm of little repositories. 

<p align="center">
<img title="mikenakis-public logo" src="mikenakis-public.svg" width="256"/><br/>
The mikenakis-public logo<br>
by Mike Nakis, based on original work by Chris Tucker from the Noun Project,<br> 
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

Configuration necessary after a fresh clone:
  - Set up the 'OUT' environment variable
    - Environment variable name: `OUT`
    - Environment variable value examples:
      - Linux: `~/OUT`
      - Windows: `%USERPROFILE%\OUT`
  - Configure IntellijIdea
    - Configure break-on-unhandled-exception
      - Go to "Run" -> "View Breakpoints... (Ctrl+Shift+F8)"
        - Under "Java Exception Breakpoints":
          - check "Any exception".
          - ensure "Caught exception" is unchecked.
          - ensure "Uncaught exception" is checked.
