package io.github.mikenakis.kit;

public class Unit
{
	@SuppressWarnings( "InstantiationOfUtilityClass" ) private static final Unit instance = new Unit();
	public static Unit instance() { return instance; }
	private Unit() { }
}
