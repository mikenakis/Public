package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class TypeArgumentTarget extends Target // "type_argument_target" in jvms-4.7.20.1
{
	public final int offset;
	public final int typeArgumentIndex;

	public TypeArgumentTarget( int offset, int typeArgumentIndex )
	{
		this.offset = offset;
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Deprecated @Override public Optional<TypeArgumentTarget> tryAsTypeArgumentTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "offset = " + offset + ", typeArgumentIndex = " + typeArgumentIndex;
	}
}
