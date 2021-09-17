package mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Class4ExtendingClass3 extends Class3ImplementingInterface implements Interface2
{
	@SuppressWarnings( "unused" )
	private static final class NestedClass
	{
		private boolean foo = false;
	}

	private final Interface1 interface1 = this;
	private final NestedClass nestedObject = new NestedClass();

	public Class4ExtendingClass3()
	{
	}

	@Override public Interface1 getInterface1()
	{
		return interface1;
	}

	@Override public long getLong( Class3ImplementingInterface class3ImplementingInterface )
	{
		return Thread.currentThread().getId();
	}

	public void testMethodHandle()
	{
		Runnable runnable = () -> new Class4ExtendingClass3();
		runnable.run();
	}

	interface Foo
	{
		String x( String s );
	}

	public void testInvokeDynamic()
	{
		Foo foo = s -> s + "x";
		foo.x( "aaa" );
	}

	public boolean testSyntheticMethod1()
	{
		return nestedObject.foo;
	}

	public void testSyntheticMethod2( boolean foo )
	{
		nestedObject.foo = foo;
	}
}
