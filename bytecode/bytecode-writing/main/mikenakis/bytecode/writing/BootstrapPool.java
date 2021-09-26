package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class BootstrapPool
{
	private final List<BootstrapMethod> bootstrapMethods;

	BootstrapPool()
	{
		bootstrapMethods = new ArrayList<>();
	}

	private int tryGetIndex( BootstrapMethod bootstrapMethod )
	{
		assert bootstrapMethod != null;
		for( int i = 0; i < bootstrapMethods.size(); i++ )
		{
			BootstrapMethod existingBootstrapMethod = bootstrapMethods.get( i );
			if( existingBootstrapMethod.equals( bootstrapMethod ) )
				return i;
		}
		return -1;
	}

	int getIndex( BootstrapMethod constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	void intern( BootstrapMethod bootstrapMethod )
	{
		assert bootstrapMethod != null;
		int existingIndex = tryGetIndex( bootstrapMethod );
		if( existingIndex == -1 )
			bootstrapMethods.add( bootstrapMethod );
	}

	Collection<BootstrapMethod> bootstrapMethods()
	{
		return bootstrapMethods;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return bootstrapMethods.size() + " entries";
	}
}
