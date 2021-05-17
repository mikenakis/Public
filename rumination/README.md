# mikenakis-rumination
#### Making plain old java objects aware of their own mutations.

<p align="center">
<img title="mikenakis-rumination logo" src="mikenakis-rumination.svg" width="256"/><br/>
The mikenakis-rumination logo.<br/>
Based on original from free-illustrations.gatag.net<br/>
Used under <a href="https://creativecommons.org/licenses/by/3.0/us/">CC BY License.</a>
</p>

## Description

`mikenakis-rumination` is a Java Agent and associated class library that modifies the bytecode
of appropriately annotated java classes to ensure that certain so-called _ruminator_ method of the class gets
invoked whenever the state of the object is changed by one of its setters.
The ruminator method receives the name of the field that was changed. (Since java does not support field literals.)

More information: [michael.gr - GitHub project: mikenakis-rumination](https://blog.michael.gr/2018/04/github-project-mikenakis-rumination.html)

## Coding style

When I write code as part of a team of developers, I use the teams' coding style.  
But when I write code for myself, I use _**my very own™**_ coding style.

More information: [michael.gr - On Coding Style](http://blog.michael.gr/2018/04/on-coding-style.html)
