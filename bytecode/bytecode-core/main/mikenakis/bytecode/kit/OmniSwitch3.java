package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function3;

import java.util.LinkedHashMap;
import java.util.Map;

public class OmniSwitch3<R, T1, T2, T3>
{
	public static class Builder<R, T1, T2, T3>
	{
		private final Map<T1,Function3<R,T1,T2,T3>> map = new LinkedHashMap<>();
		private Function3<R,T1,T2,T3> defaultCaseHandler = null;

		private Builder()
		{
		}

		public Builder<R,T1,T2,T3> with( T1 value, Function3<R,T1,T2,T3> caseHandler )
		{
			Kit.map.add( map, value, caseHandler );
			return this;
		}

		public Builder<R,T1,T2,T3> withDefault( Function3<R,T1,T2,T3> defaultCaseHandler )
		{
			assert this.defaultCaseHandler == null;
			assert defaultCaseHandler != null;
			this.defaultCaseHandler = defaultCaseHandler;
			return this;
		}

		public OmniSwitch3<R,T1,T2,T3> build()
		{
			return new OmniSwitch3<>( map, defaultCaseHandler );
		}
	}

	public static <R, T1, T2, T3> Builder<R,T1,T2,T3> newBuilder()
	{
		return new Builder<>();
	}

	private final Map<T1,Function3<R,T1,T2,T3>> map;
	private final Function3<R,T1,T2,T3> defaultCaseHandler;

	private OmniSwitch3( Map<T1,Function3<R,T1,T2,T3>> map, Function3<R,T1,T2,T3> defaultCaseHandler )
	{
		this.map = map;
		this.defaultCaseHandler = defaultCaseHandler;
	}

	public R on( T1 value, T2 value2, T3 value3 )
	{
		Function3<R,T1,T2,T3> caseHandler = Kit.map.tryGet( map, value );
		if( caseHandler == null )
		{
			assert defaultCaseHandler != null;
			caseHandler = defaultCaseHandler;
		}
		return caseHandler.invoke( value, value2, value3 );
	}
}
