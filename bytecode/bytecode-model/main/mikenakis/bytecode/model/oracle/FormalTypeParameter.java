package mikenakis.bytecode.model.oracle;

/**
 * AST that represents a formal type parameter.
 */
public class FormalTypeParameter implements TypeTree
{
	private final String name;
	private final ObjectSignature[] bounds;

	private FormalTypeParameter( String n, ObjectSignature[] bs )
	{
		name = n;
		bounds = bs;
	}

	/**
	 * Factory method.
	 * Returns a formal type parameter with the requested name and bounds.
	 *
	 * @param n  the name of the type variable to be created by this method.
	 * @param bs - the bounds of the type variable to be created by this method.
	 *
	 * @return a formal type parameter with the requested name and bounds
	 */
	public static FormalTypeParameter make( String n, ObjectSignature[] bs )
	{
		return new FormalTypeParameter( n, bs );
	}

	public ObjectSignature[] getBounds() { return bounds; }
	public String getName() { return name; }
}
