package mikenakis.bytecode.model.descriptors;

import mikenakis.kit.Kit;

import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class MethodDescriptor extends Descriptor
{
	public static MethodDescriptor of( Method method )
	{
		return of( method.getReturnType(), method.getParameterTypes() );
	}

	public static MethodDescriptor of( Class<?> returnType, Class<?>... parameterTypes )
	{
		return of( returnType, List.of( parameterTypes ) );
	}

	public static MethodDescriptor of( Class<?> returnType, Collection<Class<?>> parameterTypes )
	{
		return of( TypeDescriptor.of( returnType ), parameterTypes.stream().map( c -> TypeDescriptor.of( c ) ).toList() );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, TypeDescriptor... parameterTypeDescriptors )
	{
		return of( returnTypeDescriptor, List.of( parameterTypeDescriptors ) );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, List<TypeDescriptor> parameterTypeDescriptors )
	{
		return new MethodDescriptor( returnTypeDescriptor, parameterTypeDescriptors );
	}

	public final TypeDescriptor returnTypeDescriptor;
	public final List<TypeDescriptor> parameterTypeDescriptors;

	private MethodDescriptor( TypeDescriptor returnTypeDescriptor, List<TypeDescriptor> parameterTypeDescriptors )
	{
		this.returnTypeDescriptor = returnTypeDescriptor;
		this.parameterTypeDescriptors = parameterTypeDescriptors;
	}

	@Override public String name()
	{
		return name( "" );
	}

	public String name( String methodName )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( returnTypeDescriptor.name() );
		if( !methodName.isEmpty() )
			stringBuilder.append( " " ).append( methodName );
		if( parameterTypeDescriptors.isEmpty() )
			stringBuilder.append( "()" );
		else
		{
			stringBuilder.append( "( " );
			boolean first = true;
			for( var argumentType : parameterTypeDescriptors )
			{
				first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, ", " );
				stringBuilder.append( argumentType.name() );
			}
			stringBuilder.append( " )" );
		}
		return stringBuilder.toString();
	}

	public boolean equalsMethodTypeDesc( MethodTypeDesc methodTypeDesc )
	{
		assert false; //todo
		return false;
	}
}
