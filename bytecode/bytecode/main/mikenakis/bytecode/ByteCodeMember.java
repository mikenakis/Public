package mikenakis.bytecode;

import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.Printable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Represents a Member. (Field or Method.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ByteCodeMember extends Printable implements Annotatable
{
	private final Runnable observer;
	public final ByteCodeType declaringType;
	public final Utf8Constant nameConstant;
	public final Utf8Constant descriptorConstant;
	public final Attributes attributes;

	protected ByteCodeMember( Runnable observer, ByteCodeType declaringType, Utf8Constant nameConstant, Utf8Constant descriptorConstant, Attributes attributes )
	{
		this.observer = observer;
		this.declaringType = declaringType;
		this.nameConstant = nameConstant;
		this.descriptorConstant = descriptorConstant;
		this.attributes = attributes;
	}

	@OverridingMethodsMustInvokeSuper
	protected void intern()
	{
		nameConstant.intern( declaringType.constantPool );
		descriptorConstant.intern( declaringType.constantPool );
	}

	public String getName()
	{
		return nameConstant.getStringValue();
	}

	public String getDescriptorString()
	{
		return descriptorConstant.getStringValue();
	}

	public Descriptor getDescriptor()
	{
		return Descriptor.from( descriptorConstant.getStringValue() );
	}

	@Override public Attributes getAttributes()
	{
		return attributes;
	}

	@Override public void markAsDirty()
	{
		observer.run();
	}

	public abstract int accessAsInt();
	public abstract void accessToStringBuilder( StringBuilder stringBuilder );
}
