package mikenakis.bytecode.model.oracle;

import java.util.List;

/**
 * AST representing class types.
 */
public class ClassTypeSignature implements ObjectSignature
{
	private final List<SimpleClassTypeSignature> path;

	private ClassTypeSignature( List<SimpleClassTypeSignature> p )
	{
		path = p;
	}

	public static ClassTypeSignature make( List<SimpleClassTypeSignature> p ) { return new ClassTypeSignature( p ); }

	public List<SimpleClassTypeSignature> getPath() { return path; }
}
