package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function5;

import java.util.LinkedHashMap;
import java.util.Map;

public class OmniSwitch5<R, T1, T2, T3, T4, T5>
{
	public static class Builder<R, T1, T2, T3, T4, T5>
	{
		private final Map<T1,Function5<R,T1,T2,T3,T4,T5>> map = new LinkedHashMap<>();
		private Function5<R,T1,T2,T3,T4,T5> defaultCaseHandler = null;

		private Builder()
		{
		}

		public Builder<R,T1,T2,T3,T4,T5> with( T1 value, Function5<R,T1,T2,T3,T4,T5> caseHandler )
		{
			Kit.map.add( map, value, caseHandler );
			return this;
		}

		public Builder<R,T1,T2,T3,T4,T5> withDefault( Function5<R,T1,T2,T3,T4,T5> caseHandler )
		{
			assert defaultCaseHandler == null;
			assert caseHandler != null;
			defaultCaseHandler = caseHandler;
			return this;
		}

		public OmniSwitch5<R,T1,T2,T3,T4,T5> build()
		{
			return new OmniSwitch5<>( map, defaultCaseHandler );
		}
	}

	public static <R, T1, T2, T3, T4, T5> Builder<R,T1,T2,T3,T4,T5> newBuilder()
	{
		return new Builder<>();
	}

	private final Map<T1,Function5<R,T1,T2,T3,T4,T5>> map;
	private final Function5<R,T1,T2,T3,T4,T5> defaultCaseHandler;

	private OmniSwitch5( Map<T1,Function5<R,T1,T2,T3,T4,T5>> map, Function5<R,T1,T2,T3,T4,T5> defaultCaseHandler )
	{
		this.map = map;
		this.defaultCaseHandler = defaultCaseHandler;
	}

	public R on( T1 value, T2 value2, T3 value3, T4 value4, T5 value5 )
	{
		Function5<R,T1,T2,T3,T4,T5> caseHandler = Kit.map.tryGet( map, value );
		if( caseHandler == null )
		{
			assert defaultCaseHandler != null;
			caseHandler = defaultCaseHandler;
		}
		return caseHandler.invoke( value, value2, value3, value4, value5 );
	}
}
