package mikenakis.intertwine.implementations.compiling;

import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.implementations.IntertwineHelpers;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.Kit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

final class CompilingEntwiner<T>
{
	private final CompilingIntertwine<T> intertwine;
	private final AnyCall<T> exitPoint;
	final T entryPoint;

	CompilingEntwiner( CompilingIntertwine<T> intertwine, AnyCall<T> exitPoint )
	{
		this.intertwine = intertwine;
		assert exitPoint != null;
		this.exitPoint = exitPoint;
		ClassLoader classLoader = intertwine.interfaceType().getClassLoader();
		//Object proxy = newProxyInstance( classLoader, intertwine.interfaceType(), this::invocationHandler );
		Object proxy = Proxy.newProxyInstance( classLoader, new Class<?>[] { intertwine.interfaceType() }, this::invocationHandler );
		@SuppressWarnings( "unchecked" ) T temp = (T)proxy;
		entryPoint = temp;
	}

	private Object invocationHandler( Object proxy, Method method, Object[] arguments )
	{
		assert proxy == entryPoint;
		Optional<CompilingKey<T>> key = intertwine.tryGetKeyByMethod( method );
		if( key.isPresent() )
			return exitPoint.anyCall( key.get(), arguments );
		if( method.equals( IntertwineHelpers.hashCodeMethod ) )
			return System.identityHashCode( proxy );
		if( method.equals( IntertwineHelpers.equalsMethod ) )
			return proxy == arguments[0];
		if( method.equals( IntertwineHelpers.toStringMethod ) )
			return Proxy.class.getName() + "@" + Integer.toHexString( System.identityHashCode( proxy ) );
		throw new AssertionError();
	}

	private static Object newProxyInstance( ClassLoader loader, Class<?> theInterface, InvocationHandler handler )
	{
		ByteCodeType byteCodeType = ByteCodeType.of( ByteCodeType.modifierEnum.of(), TerminalTypeDescriptor.of( "TestClass" ), Optional.empty(), List.of() );
		ByteCodeField byteCodeField = ByteCodeField.of( ByteCodeField.modifierEnum.of(), null );
		Kit.collection.add( byteCodeType.fields, byteCodeField );
		for( Method method : theInterface.getMethods() )
		{
			int modifiers = method.getModifiers();
			if( java.lang.reflect.Modifier.isStatic( modifiers ) )
				continue;
			if( !java.lang.reflect.Modifier.isAbstract( modifiers ) )
				continue;
			assert java.lang.reflect.Modifier.isPublic( modifiers );
			assert !java.lang.reflect.Modifier.isFinal( modifiers );
			assert !java.lang.reflect.Modifier.isNative( modifiers );
			assert !java.lang.reflect.Modifier.isSynchronized( modifiers ); // ?
			assert !method.isSynthetic(); // ?
			assert !method.isDefault(); // ?
			assert !method.isBridge(); // ?
			Mutf8ValueConstant methodNameConstant = Mutf8ValueConstant.of( method.getName() );
			Mutf8ValueConstant methodDescriptorConstant = Mutf8ValueConstant.of( "" ); //TODO
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of(), methodNameConstant, methodDescriptorConstant, AttributeSet.of() );
		}
		return byteCodeType;
	}
}
