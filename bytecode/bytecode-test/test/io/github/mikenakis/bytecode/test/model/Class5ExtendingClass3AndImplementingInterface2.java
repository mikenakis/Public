package io.github.mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author michael.gr
 */
public final class Class5ExtendingClass3AndImplementingInterface2 extends Class3ImplementingInterface implements Interface2
{
	@RuntimeVisibleAnnotation1( charParameter = 'x', doubleParameter = 1.1 ) //
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
		@RuntimeVisibleAnnotation2 //Java blooper: this is silently ignored: it is nowhere to be found in the .class file!
		long id = Thread.currentThread().getId();
		return id;
	}
}
