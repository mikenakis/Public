package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class ThrowsTarget extends Target // "throws_target" in jvms-4.7.20.1
{
	public final int throwsTypeIndex;

	public ThrowsTarget( Type type, int throwsTypeIndex )
	{
		super( type );
		assert type == Type.TypeInThrowsClauseOfMethodOrConstructor;
		this.throwsTypeIndex = throwsTypeIndex;
	}

	@Deprecated @Override public Optional<ThrowsTarget> tryAsThrowsTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "throwsTypeIndex = " + throwsTypeIndex;
	}
}
