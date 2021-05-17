package bytecode_tests.model;

/**
 * test class.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Class5ExtendingClass3AndImplementingInterface2 extends Class3ImplementingInterface implements Interface2
{
	@RuntimeVisibleAnnotation1( charParameter = 'x', doubleParameter = 1.1 )
	private final Interface1 interface1 = this;

	public Class5ExtendingClass3AndImplementingInterface2()
	{
	}

	@Override public Interface1 getInterface1()
	{
		return interface1;
	}

	@Override public long getLong( Class3ImplementingInterface class3ImplementingInterface )
	{
		@RuntimeVisibleAnnotation1
		long id = Thread.currentThread().getId();
		return id;
	}
}
