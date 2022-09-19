package io.github.mikenakis.kit;

/**
 * Identifies a source code location by means of a class name, a method name, a source filename, and a line number.
 */
public interface SourceLocation
{
	String className();
	String methodName();
	String fileName();
	int lineNumber();

	String stringRepresentation();
}
