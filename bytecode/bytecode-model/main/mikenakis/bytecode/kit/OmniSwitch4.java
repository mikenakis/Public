package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function4;

import java.util.LinkedHashMap;
import java.util.Map;

public class OmniSwitch4<R, T1, T2, T3, T4>
{
	public static class Builder<R, T1, T2, T3, T4>
	{
		private final Map<T1,Function4<R,T1,T2,T3,T4>> map = new LinkedHashMap<>();
		private Function4<R,T1,T2,T3,T4> defaultCaseHandler = null;

		private Builder()
		{
		}

		public Builder<R,T1,T2,T3,T4> with( T1 value, Function4<R,T1,T2,T3,T4> caseHandler )
		{
			Kit.map.add( map, value, caseHandler );
			return this;
		}

		public Builder<R,T1,T2,T3,T4> withDefault( Function4<R,T1,T2,T3,T4> defaultCaseHandler )
		{
			assert this.defaultCaseHandler == null;
			assert defaultCaseHandler != null;
			this.defaultCaseHandler = defaultCaseHandler;
			return this;
		}

		public OmniSwitch4<R,T1,T2,T3,T4> build()
		{
			return new OmniSwitch4<>( map, defaultCaseHandler );
		}
	}

	public static <R, T1, T2, T3, T4> Builder<R,T1,T2,T3,T4> newBuilder()
	{
		return new Builder<>();
	}

	private final Map<T1,Function4<R,T1,T2,T3,T4>> map;
	private final Function4<R,T1,T2,T3,T4> defaultCaseHandler;

	private OmniSwitch4( Map<T1,Function4<R,T1,T2,T3,T4>> map, Function4<R,T1,T2,T3,T4> defaultCaseHandler )
	{
		this.map = map;
		this.defaultCaseHandler = defaultCaseHandler;
	}

	public R on( T1 value, T2 value2, T3 value3, T4 value4 )
	{
		Function4<R,T1,T2,T3,T4> producer = Kit.map.tryGet( map, value );
		if( producer == null )
		{
			assert defaultCaseHandler != null;
			producer = defaultCaseHandler;
		}
		return producer.invoke( value, value2, value3, value4 );
	}
}
