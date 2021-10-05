package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
	public static BootstrapMethodsAttribute read( AttributeReader attributeReader )
	{
		int bootstrapMethodCount = attributeReader.readUnsignedShort();
		assert bootstrapMethodCount > 0;
		List<BootstrapMethod> entries = new ArrayList<>( bootstrapMethodCount );
		for( int i = 0; i < bootstrapMethodCount; i++ )
		{
			MethodHandleConstant methodHandleConstant = attributeReader.readIndexAndGetConstant().asMethodHandleConstant();
			int argumentConstantCount = attributeReader.readUnsignedShort();
			assert argumentConstantCount > 0;
			List<Constant> argumentConstants = new ArrayList<>( argumentConstantCount );
			for( int j = 0; j < argumentConstantCount; j++ )
			{
				Constant argumentConstant = attributeReader.readIndexAndGetConstant();
				argumentConstants.add( argumentConstant );
			}
			BootstrapMethod entry = BootstrapMethod.of( methodHandleConstant, argumentConstants );
			entries.add( entry );
		}
		return of( entries );
	}

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

	public BootstrapMethod getBootstrapMethodByIndex( int index ) { return bootstrapMethods.get( index ); }
	@Deprecated @Override public BootstrapMethodsAttribute asBootstrapMethodsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return bootstrapMethods.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( BootstrapMethod bootstrapMethod : bootstrapMethods )
			bootstrapMethod.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( bootstrapMethods.size() );
		for( BootstrapMethod bootstrapMethod : bootstrapMethods )
			bootstrapMethod.write( constantWriter );
	}
}
