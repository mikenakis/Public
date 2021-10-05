package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class BootstrapPool
{
	private final Collection<Procedure1<List<BootstrapMethod>>> fixUps = new ArrayList<>();

	BootstrapPool()
	{
	}

	void setBootstrapMethod( int index, Procedure1<BootstrapMethod> setter )
	{
		fixUps.add( (List<BootstrapMethod> bootstrapMethods) ->
			{
				BootstrapMethod bootstrapMethod = bootstrapMethods.get( index );
				setter.invoke( bootstrapMethod );
			} );
	}

	void setBootstrapMethods( List<BootstrapMethod> bootstrapMethods )
	{
		for( Procedure1<List<BootstrapMethod>> fixUp : fixUps )
			fixUp.invoke( bootstrapMethods );
	}
}
