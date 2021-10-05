package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;

import java.util.LinkedHashMap;
import java.util.Map;

public class OmniSwitch1<R, T1>
{
	public static class Builder<R, T1>
	{
		private final Map<T1,Function1<R,T1>> map = new LinkedHashMap<>();
		private Function1<R,T1> defaultCaseHandler = null;

		private Builder()
		{
		}

		public Builder<R,T1> with( T1 value, Function1<R,T1> caseHandler )
		{
			Kit.map.add( map, value, caseHandler );
			return this;
		}

		public Builder<R,T1> withDefault( Function1<R,T1> defaultCaseHandler )
		{
			assert this.defaultCaseHandler == null;
			assert defaultCaseHandler != null;
			this.defaultCaseHandler = defaultCaseHandler;
			return this;
		}

		public OmniSwitch1<R,T1> build()
		{
			return new OmniSwitch1<>( map, defaultCaseHandler );
		}
	}

	public static <R, T1> Builder<R,T1> newBuilder()
	{
		return new Builder<>();
	}

	private final Map<T1,Function1<R,T1>> map;
	private final Function1<R,T1> defaultCaseHandler;

	private OmniSwitch1( Map<T1,Function1<R,T1>> map, Function1<R,T1> defaultCaseHandler )
	{
		this.map = map;
		this.defaultCaseHandler = defaultCaseHandler;
	}

	public R on( T1 value )
	{
		Function1<R,T1> caseHandler = Kit.map.tryGet( map, value );
		if( caseHandler == null )
		{
			assert defaultCaseHandler != null;
			caseHandler = defaultCaseHandler;
		}
		return caseHandler.invoke( value );
	}
}
