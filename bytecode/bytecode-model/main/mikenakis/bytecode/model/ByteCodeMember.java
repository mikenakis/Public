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

	protected ByteCodeMember( Mutf8Constant nameConstant, AttributeSet attributeSet )
	{
		this.nameConstant = nameConstant;
		this.attributeSet = attributeSet;
	}

	public final String name() { return nameConstant.stringValue(); }
}
