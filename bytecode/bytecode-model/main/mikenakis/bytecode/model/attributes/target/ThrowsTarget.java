package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class ThrowsTarget extends Target // "throws_target" in jvms-4.7.20.1
{
	public final int throwsTypeIndex;

	public ThrowsTarget( Type type, int throwsTypeIndex )
	{
		super( type );
		assert type == Type.TypeInThrowsClauseOfMethodOrConstructor;
		this.throwsTypeIndex = throwsTypeIndex;
	}

	@Deprecated @Override public ThrowsTarget asThrowsTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "throwsTypeIndex = " + throwsTypeIndex;
	}
}
