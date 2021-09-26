package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "BootstrapMethods" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BootstrapMethodsAttribute extends KnownAttribute
{
	public static BootstrapMethodsAttribute of()
	{
		List<BootstrapMethod> entries = new ArrayList<>();
		return new BootstrapMethodsAttribute( entries );
	}

	public static BootstrapMethodsAttribute of( List<BootstrapMethod> entries )
	{
		return new BootstrapMethodsAttribute( entries );
	}

	public final List<BootstrapMethod> bootstrapMethods;

	private BootstrapMethodsAttribute( List<BootstrapMethod> bootstrapMethods )
	{
		super( tag_BootstrapMethods );
		this.bootstrapMethods = bootstrapMethods;
	}

	@Deprecated @Override public BootstrapMethodsAttribute asBootstrapMethodsAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return bootstrapMethods.size() + " entries";
	}

	public BootstrapMethod getBootstrapMethodByIndex( int index )
	{
		return bootstrapMethods.get( index );
	}

	public int getIndexOfBootstrapMethod( BootstrapMethod entry )
	{
		assert entry != null;
		int index = bootstrapMethods.indexOf( entry );
		if( index == -1 )
		{
			index = bootstrapMethods.size();
			bootstrapMethods.add( entry );
		}
		return index;
	}
}
