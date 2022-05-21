package io.github.mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author michael.gr
 */
@SuppressWarnings( "ClassMayBeInterface" )
public abstract class Class7WithInnerClass
{
	public class InnerClass
	{
		@Override public String toString()
		{
			return "inner class of " + Class7WithInnerClass.this.toString();
		}
	}
}
