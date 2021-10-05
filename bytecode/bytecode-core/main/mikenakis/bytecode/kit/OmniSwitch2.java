package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function2;

import java.util.LinkedHashMap;
import java.util.Map;

public class OmniSwitch2<R, T1, T2>
{
	public static class Builder<R, T1, T2>
	{
		private final Map<T1,Function2<R,T1,T2>> map = new LinkedHashMap<>();
		private Function2<R,T1,T2> defaultCaseHandler = null;

		private Builder()
		{
		}

		public Builder<R,T1,T2> with( T1 value, Function2<R,T1,T2> caseHandler )
		{
			Kit.map.add( map, value, caseHandler );
			return this;
		}

		public Builder<R,T1,T2> withDefault( Function2<R,T1,T2> defaultCaseHandler )
		{
			assert this.defaultCaseHandler == null;
			assert defaultCaseHandler != null;
			this.defaultCaseHandler = defaultCaseHandler;
			return this;
		}

		public OmniSwitch2<R,T1,T2> build()
		{
			return new OmniSwitch2<>( map, defaultCaseHandler );
		}
	}

	public static <R, T1, T2> Builder<R,T1,T2> newBuilder()
	{
		return new Builder<>();
	}

	private final Map<T1,Function2<R,T1,T2>> map;
	private final Function2<R,T1,T2> defaultCaseHandler;

	private OmniSwitch2( Map<T1,Function2<R,T1,T2>> map, Function2<R,T1,T2> defaultCaseHandler )
	{
		this.map = map;
		this.defaultCaseHandler = defaultCaseHandler;
	}

	public R on( T1 value, T2 value2 )
	{
		Function2<R,T1,T2> caseHandler = Kit.map.tryGet( map, value );
		if( caseHandler == null )
		{
			assert defaultCaseHandler != null;
			caseHandler = defaultCaseHandler;
		}
		return caseHandler.invoke( value, value2 );
	}
}
