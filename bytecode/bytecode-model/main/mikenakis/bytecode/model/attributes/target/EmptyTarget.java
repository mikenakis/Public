package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class EmptyTarget extends Target // "empty_target" in jvms-4.7.20.1
{
	public EmptyTarget( Type type )
	{
		super( type );
		assert type == Type.TypeInFieldDeclaration || type == Type.ReturnTypeOfMethodOrTypeOfNewlyConstructedObject || type == Type.ReceiverTypeOfMethodOrConstructor;
	}

	@Deprecated @Override public EmptyTarget asEmptyTarget() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "(empty)";
	}
}
