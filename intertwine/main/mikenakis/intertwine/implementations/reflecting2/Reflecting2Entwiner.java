package mikenakis.intertwine.implementations.reflecting2;

import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.implementations.IntertwineHelpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

final class Reflecting2Entwiner<T>
{
	private final Reflecting2Intertwine<T> intertwine;
	private final AnyCall<T> exitPoint;
	final T entryPoint;

	Reflecting2Entwiner( Reflecting2Intertwine<T> intertwine, AnyCall<T> exitPoint )
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
		Optional<Reflecting2Key<T>> key = intertwine.tryGetKeyByMethod( method );
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
		Mutf8Constant thisClassName = Mutf8Constant.of( "test" );
		ClassConstant thisClassConstant = ClassConstant.of( thisClassName );
		ByteCodeType byteCodeType = ByteCodeType.of( ByteCodeType.modifierFlagsEnum.of(), thisClassConstant, Optional.empty() );
		ByteCodeField byteCodeField = ByteCodeField.of( ByteCodeField.modifierFlagsEnum.of(), "handler", "" );
		byteCodeType.addField( byteCodeField );
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
			Mutf8Constant methodNameConstant = Mutf8Constant.of( method.getName() );
			Mutf8Constant methodDescriptorConstant = Mutf8Constant.of( "" ); //TODO
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierFlagsEnum.of(), methodNameConstant, methodDescriptorConstant, AttributeSet.of() );
		}
		return byteCodeType;
	}
}
