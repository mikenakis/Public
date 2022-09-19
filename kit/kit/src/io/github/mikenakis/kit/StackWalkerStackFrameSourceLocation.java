package io.github.mikenakis.kit;

/**
 * Implements {@link SourceLocation} using a {@link StackWalker.StackFrame}.
 */
class StackWalkerStackFrameSourceLocation
{
	static SourceLocation of( StackWalker.StackFrame stackFrame )
	{
		class Implementation implements SourceLocation
		{
			@Override public final String className() { return stackFrame.getClassName(); }
			@Override public final String methodName() { return stackFrame.getMethodName(); }
			@Override public final String fileName() { return stackFrame.getFileName(); }
			@Override public final int lineNumber() { return stackFrame.getLineNumber(); }

			@Override public String stringRepresentation()
			{
				String s1 = stackFrame.toString();
				String s2 = className() + "." + methodName() + "(" + fileName() + ":" + lineNumber() + ")";
				assert s1.equals( s2 );
				return s2;
			}

			@Override public String toString()
			{
				return stringRepresentation();
			}
		}

		return new Implementation();
	}
}
