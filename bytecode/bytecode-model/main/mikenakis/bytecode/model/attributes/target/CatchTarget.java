package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class CatchTarget extends Target // "catch_target" in jvms-4.7.20.1
{
	public final int exceptionTableIndex;

	public CatchTarget( Type type, int exceptionTableIndex )
	{
		super( type );
		assert type == Type.TypeInExceptionParameterDeclaration;
		this.exceptionTableIndex = exceptionTableIndex;
	}

	@Deprecated @Override public Optional<CatchTarget> tryAsCatchTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "exceptionTableIndex = " + exceptionTableIndex;
	}
}
