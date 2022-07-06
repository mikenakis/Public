package io.github.mikenakis.kit;

import io.github.mikenakis.kit.functional.Procedure0;

public class Unit
{
	@SuppressWarnings( "InstantiationOfUtilityClass" ) public static final Unit instance = new Unit();
	private Unit() { }

	public static Unit map( Procedure0 procedure )
	{
		procedure.invoke();
		return instance;
	}
}
