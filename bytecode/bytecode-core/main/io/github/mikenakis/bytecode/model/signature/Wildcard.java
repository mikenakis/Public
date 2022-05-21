package io.github.mikenakis.bytecode.model.signature;

public class Wildcard implements TypeArgument
{
	private final ObjectSignature[] upperBounds;
	private final ObjectSignature[] lowerBounds;

	private Wildcard( ObjectSignature[] ubs, ObjectSignature[] lbs )
	{
		upperBounds = ubs;
		lowerBounds = lbs;
	}

	private static final ObjectSignature[] emptyBounds = new ObjectSignature[0];

	public static Wildcard make( ObjectSignature[] ubs, ObjectSignature[] lbs )
	{
		return new Wildcard( ubs, lbs );
	}

	public ObjectSignature[] getUpperBounds()
	{
		return upperBounds;
	}

	public ObjectSignature[] getLowerBounds()
	{
		if( lowerBounds.length == 1 && lowerBounds[0] == BottomSignature.instance )
			return emptyBounds;
		else
			return lowerBounds;
	}
}
