package mikenakis.bytecode;

import mikenakis.bytecode.kit.StringParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type descriptor.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Descriptor
{
	public static Descriptor from( String descriptorString )
	{
		List<String> argumentTypeNames;
		if( !descriptorString.startsWith( "(" ) )
			argumentTypeNames = null;
		else
		{
			argumentTypeNames = new ArrayList<>();
			int i = descriptorString.indexOf( ')' );
			assert i != -1;
			StringParser stringParser = new StringParser( descriptorString, 1, i );
			while( !stringParser.isAtEnd() )
			{
				String typeName = ByteCodeHelpers.parseNextJavaTypeNameFromDescriptor( stringParser );
				argumentTypeNames.add( typeName );
			}
			descriptorString = descriptorString.substring( i + 1 );
		}
		String typeName = ByteCodeHelpers.getJavaTypeNameFromDescriptorTypeName( descriptorString );
		return new Descriptor( typeName, argumentTypeNames );
	}

	public final String typeName;
	public final List<String> argumentTypeNames;

	public Descriptor( String typeName )
	{
		this.typeName = typeName;
		argumentTypeNames = new ArrayList<>();
	}

	public Descriptor( String typeName, List<String> argumentTypeNames )
	{
		this.typeName = typeName;
		this.argumentTypeNames = argumentTypeNames;
	}

	public String toUtf8Constant()
	{
		var builder = new StringBuilder();
		if( !argumentTypeNames.isEmpty() )
		{
			builder.append( '(' );
			for( String argumentTypeName : argumentTypeNames )
			{
				builder.append( argumentTypeName );
				builder.append( ';' );
			}
			builder.append( ')' );
		}
		return builder.toString();
	}
}
