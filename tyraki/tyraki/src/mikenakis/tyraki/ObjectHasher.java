package mikenakis.tyraki;

import io.github.mikenakis.bathyscaphe.ObjectAssessor;
import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;

import java.lang.reflect.Method;

/**
 * Implements {@link Hasher} by delegating to {@link Object#hashCode()}.
 *
 * @author michael.gr
 */
public final class ObjectHasher implements Hasher<Object>
{
	public static final Hasher<Object> INSTANCE = new ObjectHasher();

	private ObjectHasher()
	{
	}

	@Override public int getHashCode( Object object )
	{
		assert mustOverrideIdentityMethodsAssertion( object );
		assert ObjectAssessor.instance.mustBeImmutableAssertion( object );
		return object.hashCode();
	}

	private static boolean mustOverrideIdentityMethodsAssertion( Object object )
	{
		assert isOverriding( object, Object.class, "hashCode", (Class<?>[])null );
		assert isOverriding( object, Object.class, "equals", Object.class );
		return true;
	}

	private static boolean isOverriding( Object object, Class<?> baseClass, String methodName, Class<?>... parameterTypes )
	{
		assert baseClass.isInstance( object );
		Class<?> jvmClass = object.getClass();
		if( jvmClass == baseClass )
			return false; //not sure what to return here.
		Method method = Kit.unchecked( () -> jvmClass.getMethod( methodName, parameterTypes ) );
		return method.getDeclaringClass() != baseClass;
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
