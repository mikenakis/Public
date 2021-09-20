package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;

/**
 * Represents a Member. (Field or Method.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ByteCodeMember
{
	public final Mutf8Constant nameConstant;
	public final AttributeSet attributeSet;
	public final Mutf8Constant descriptorConstant;

	protected ByteCodeMember( Mutf8Constant nameConstant, Mutf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		this.nameConstant = nameConstant;
		this.attributeSet = attributeSet;
		this.descriptorConstant = descriptorConstant;
	}

	public final String name() { return nameConstant.stringValue(); }
}
