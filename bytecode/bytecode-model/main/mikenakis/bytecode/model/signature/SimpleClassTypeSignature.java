package mikenakis.bytecode.model.signature;

public class SimpleClassTypeSignature implements ObjectSignature
{
	private final boolean dollar;
	private final String name;
	private final TypeArgument[] typeArgs;

	private SimpleClassTypeSignature( String n, boolean dollar, TypeArgument[] tas )
	{
		name = n;
		this.dollar = dollar;
		typeArgs = tas;
	}

	public static SimpleClassTypeSignature make( String n, boolean dollar, TypeArgument[] tas )
	{
		return new SimpleClassTypeSignature( n, dollar, tas );
	}

	/*
	 * Should a '$' be used instead of '.' to separate this component
	 * of the name from the previous one when composing a string to
	 * pass to Class.forName; in other words, is this a transition to
	 * a nested class.
	 */
	public boolean getDollar() { return dollar; }
	public String getName() { return name; }
	public TypeArgument[] getTypeArguments() { return typeArgs; }
}
