package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Utf8Constant;

/**
 * Represents a Member. (Field or Method.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ByteCodeMember
{
	public final Utf8Constant nameConstant;
	public final Utf8Constant descriptorConstant;
	public final AttributeSet attributeSet;

	protected ByteCodeMember( Utf8Constant nameConstant, Utf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		this.nameConstant = nameConstant;
		this.descriptorConstant = descriptorConstant;
		this.attributeSet = attributeSet;
	}

	public abstract FlagSet<?> modifierSet();
}
