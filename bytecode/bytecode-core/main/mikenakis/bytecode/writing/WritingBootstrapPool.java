package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class WritingBootstrapPool
{
	private final List<BootstrapMethod> bootstrapMethods;

	public WritingBootstrapPool()
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

	public int getBootstrapIndex( BootstrapMethod constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	public void intern( BootstrapMethod bootstrapMethod )
	{
		assert bootstrapMethod != null;
		int existingIndex = tryGetIndex( bootstrapMethod );
		if( existingIndex == -1 )
			bootstrapMethods.add( bootstrapMethod );
	}

	public Collection<BootstrapMethod> bootstrapMethods()
	{
		return bootstrapMethods;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return bootstrapMethods.size() + " entries";
	}
}
