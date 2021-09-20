package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class SupertypeTarget extends Target // "supertype_target" in jvms-4.7.20.1
{
	public final int supertypeIndex;

	public SupertypeTarget( Type type, int supertypeIndex )
	{
		super( type );
		assert type == Type.TypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration;
		this.supertypeIndex = supertypeIndex;
	}

	@Deprecated @Override public Optional<SupertypeTarget> tryAsSupertypeTarget() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "superTypeIndex = " + supertypeIndex;
	}
}
