package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_NameAndType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndDescriptorConstant extends Constant
{
	public static NameAndDescriptorConstant of( Mutf8Constant nameConstant, Mutf8Constant descriptorConstant )
	{
		return new NameAndDescriptorConstant( nameConstant, descriptorConstant );
	}

	public static final int TAG = 12; // JVMS::CONSTANT_NameAndType_info

	private final Mutf8Constant nameConstant;
	private final Mutf8Constant descriptorConstant;

	private NameAndDescriptorConstant( Mutf8Constant nameConstant, Mutf8Constant descriptorConstant )
	{
		super( TAG );
		this.nameConstant = nameConstant;
		this.descriptorConstant = descriptorConstant;
	}

	public Mutf8Constant nameConstant() { return nameConstant; }
	public Mutf8Constant descriptorConstant() { return descriptorConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}

	@Deprecated @Override public NameAndDescriptorConstant asNameAndDescriptorConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof NameAndDescriptorConstant otherNameAndDescriptorConstant )
			return equalsNameAndDescriptorConstant( otherNameAndDescriptorConstant );
		return false;
	}

	public boolean equalsNameAndDescriptorConstant( NameAndDescriptorConstant other )
	{
		if( !nameConstant.equalsMutf8Constant( other.nameConstant ) )
			return false;
		if( !descriptorConstant.equalsMutf8Constant( other.descriptorConstant ) )
			return false;
		return true;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, nameConstant, descriptorConstant );
	}
}
