package mikenakis.kit;

/**
 * Identifies a source code location by means of a class name, a method name, a source filename, and a line number.
 */
public class SourceLocation
{
	public static SourceLocation fromStackFrame( StackWalker.StackFrame stackFrame )
	{
		return new SourceLocation( stackFrame.getClassName(), stackFrame.getMethodName(), stackFrame.getFileName(), stackFrame.getLineNumber() );
	}

	public final String className;
	public final String methodName;
	public final String fileName;
	public final int lineNumber;

	public SourceLocation( String className, String methodName, String fileName, int lineNumber )
	{
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public String stringRepresentation()
	{
		return className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
	}

	@Override public String toString()
	{
		return stringRepresentation();
	}
}
