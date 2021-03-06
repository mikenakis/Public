package io.github.mikenakis.bytecode.model.signature;

public class TypeVariableSignature implements ObjectSignature
{
	private final String identifier;

	private TypeVariableSignature( String id ) { identifier = id; }

	public static TypeVariableSignature make( String id )
	{
		return new TypeVariableSignature( id );
	}

	public String getIdentifier() { return identifier; }
}
