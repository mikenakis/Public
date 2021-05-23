package mikenakis.intertwine.implementations.temp;

import mikenakis.kit.Kit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Proxy implements java.io.Serializable
{
	/**
	 * parameter types of a proxy class constructor
	 */
	/* XXX private */ static final Class<?>[] constructorParams = { InvocationHandler.class };

	/**
	 * a cache of proxy constructors with
	 * {@link Constructor#setAccessible(boolean) accessible} flag already set
	 */
	private static final ClassLoaderValue<Constructor<?>> proxyCache = new ClassLoaderValue<>();

	/**
	 * the invocation handler for this proxy instance.
	 *
	 * @serial
	 */
	//    @SuppressWarnings("serial") // Not statically typed as Serializable
	protected InvocationHandler h;

	/**
	 * Prohibits instantiation.
	 */
	private Proxy()
	{
	}

	/**
	 * Constructs a new {@code Proxy} instance from a subclass
	 * (typically, a dynamic proxy class) with the specified value
	 * for its invocation handler.
	 *
	 * @param h the invocation handler for this proxy instance
	 *
	 * @throws NullPointerException if the given invocation handler, {@code h},
	 *                              is {@code null}.
	 */
	protected Proxy( InvocationHandler h )
	{
		Objects.requireNonNull( h );
		this.h = h;
	}

	/**
	 * Returns the {@code Constructor} object of a proxy class that takes a
	 * single argument of type {@link InvocationHandler}, given a class loader
	 * and an array of interfaces. The returned constructor will have the
	 * {@link Constructor#setAccessible(boolean) accessible} flag already set.
	 *
	 * @param loader         the class loader to define the proxy class
	 * @param interfaceTypes the list of interfaces for the proxy class
	 *                       to implement
	 *
	 * @return a Constructor of the proxy class taking single
	 *    {@code InvocationHandler} parameter
	 */
	private static Constructor<?> getProxyConstructor( /* XXX Class<?> caller, */ ClassLoader loader, Class<?>... interfaceTypes )
	{
		// optimization for single interface
		if( interfaceTypes.length == 1 )
		{
			Class<?> interfaceType = interfaceTypes[0];
			//XXX			if( caller != null )
			//XXX				checkProxyAccess( caller, loader, intf );
			return proxyCache.sub( interfaceType ).computeIfAbsent( loader, ( ld, clv ) -> new ProxyBuilder( ld, clv.key() ).build( loader ) );
		}
		else
		{
			// interfaces cloned
			Class<?>[] interfaceTypesArray = interfaceTypes.clone();
			//XXX			if( caller != null )
			//XXX				checkProxyAccess( caller, loader, intfsArray );
			List<Class<?>> interfaceTypesList = Arrays.asList( interfaceTypesArray );
			return proxyCache.sub( interfaceTypesList ).computeIfAbsent( loader, ( ld, clv ) -> new ProxyBuilder( ld, clv.key() ).build( loader ) );
		}
	}

	/*
	 * Check permissions required to create a Proxy class.
	 *
	 * To define a proxy class, it performs the access checks as in
	 * Class.forName (VM will invoke ClassLoader.checkPackageAccess):
	 * 1. "getClassLoader" permission check if loader == null
	 * 2. checkPackageAccess on the interfaces it implements
	 *
	 * To get a constructor and new instance of a proxy class, it performs
	 * the package access check on the interfaces it implements
	 * as in Class.getConstructor.
	 *
	 * If an interface is non-public, the proxy class must be defined by
	 * the defining loader of the interface.  If the caller's class loader
	 * is not the same as the defining loader of the interface, the VM
	 * will throw IllegalAccessError when the generated proxy class is
	 * being defined.
	 */
	//XXX	private static void checkProxyAccess( Class<?> caller, ClassLoader loader, Class<?>... interfaces )
	//XXX	{
	//XXX		SecurityManager sm = System.getSecurityManager();
	//XXX		if( sm != null )
	//XXX		{
	//XXX			ClassLoader ccl = caller.getClassLoader();
	//XXX			if( loader == null && ccl != null )
	//XXX				sm.checkPermission( SecurityConstants.GET_CLASSLOADER_PERMISSION );
	//XXX			ReflectUtil.checkProxyPackageAccess( ccl, interfaces );
	//XXX		}
	//XXX	}

	/**
	 * Returns a proxy instance for the specified interfaces
	 * that dispatches method invocations to the specified invocation
	 * handler.
	 * <p>
	 * <a id="restrictions">{@code IllegalArgumentException} will be thrown
	 * if any of the following restrictions is violated:</a>
	 * <ul>
	 * <li>All of {@code Class} objects in the given {@code interfaces} array
	 * must represent {@linkplain Class#isHidden() non-hidden} interfaces,
	 * not classes or primitive types.
	 *
	 * <li>No two elements in the {@code interfaces} array may
	 * refer to identical {@code Class} objects.
	 *
	 * <li>All of the interface types must be visible by name through the
	 * specified class loader. In other words, for class loader
	 * {@code cl} and every interface {@code i}, the following
	 * expression must be true:<p>
	 * {@code Class.forName(i.getName(), false, cl) == i}
	 *
	 * <li>All of the types referenced by all
	 * public method signatures of the specified interfaces
	 * and those inherited by their superinterfaces
	 * must be visible by name through the specified class loader.
	 *
	 * <li>All non-public interfaces must be in the same package
	 * and module, defined by the specified class loader and
	 * the module of the non-public interfaces can access all of
	 * the interface types; otherwise, it would not be possible for
	 * the proxy class to implement all of the interfaces,
	 * regardless of what package it is defined in.
	 *
	 * <li>For any set of member methods of the specified interfaces
	 * that have the same signature:
	 * <ul>
	 * <li>If the return type of any of the methods is a primitive
	 * type or void, then all of the methods must have that same
	 * return type.
	 * <li>Otherwise, one of the methods must have a return type that
	 * is assignable to all of the return types of the rest of the
	 * methods.
	 * </ul>
	 *
	 * <li>The resulting proxy class must not exceed any limits imposed
	 * on classes by the virtual machine.  For example, the VM may limit
	 * the number of interfaces that a class may implement to 65535; in
	 * that case, the size of the {@code interfaces} array must not
	 * exceed 65535.
	 * </ul>
	 *
	 * <p>Note that the order of the specified proxy interfaces is
	 * significant: two requests for a proxy class with the same combination
	 * of interfaces but in a different order will result in two distinct
	 * proxy classes.
	 *
	 * @param loader     the class loader to define the proxy class
	 * @param interfaces the list of interfaces for the proxy class
	 *                   to implement
	 * @param h          the invocation handler to dispatch method invocations to
	 *
	 * @return a proxy instance with the specified invocation handler of a
	 * 	proxy class that is defined by the specified class loader
	 * 	and that implements the specified interfaces
	 *
	 * @throws IllegalArgumentException if any of the <a href="#restrictions">
	 *                                  restrictions</a> on the parameters are violated
	 * @throws SecurityException        if a security manager, <em>s</em>, is present
	 *                                  and any of the following conditions is met:
	 *                                  <ul>
	 *                                  <li> the given {@code loader} is {@code null} and
	 *                                       the caller's class loader is not {@code null} and the
	 *                                       invocation of {@link SecurityManager#checkPermission
	 *                                       s.checkPermission} with
	 *                                       {@code RuntimePermission("getClassLoader")} permission
	 *                                       denies access;</li>
	 *                                  <li> for each proxy interface, {@code intf},
	 *                                       the caller's class loader is not the same as or an
	 *                                       ancestor of the class loader for {@code intf} and
	 *                                       invocation of {@link SecurityManager#checkPackageAccess
	 *                                       s.checkPackageAccess()} denies access to {@code intf};</li>
	 *                                  <li> any of the given proxy interfaces is non-public and the
	 *                                       caller class is not in the same {@linkplain Package runtime package}
	 *                                       as the non-public interface and the invocation of
	 *                                       {@link SecurityManager#checkPermission s.checkPermission} with
	 *                                       {@code ReflectPermission("newProxyInPackage.{package name}")}
	 *                                       permission denies access.</li>
	 *                                  </ul>
	 * @throws NullPointerException     if the {@code interfaces} array
	 *                                  argument or any of its elements are {@code null}, or
	 *                                  if the invocation handler, {@code h}, is
	 *                                  {@code null}
	 * @see <a href="#membership">Package and Module Membership of Proxy Class</a>
	 */
	/*XXX @CallerSensitive*/
	public static Object newProxyInstance( ClassLoader loader, Class<?>[] interfaces, InvocationHandler h )
	{
		Objects.requireNonNull( h );

		//XXX Class<?> caller = System.getSecurityManager() == null ? null : Reflection.getCallerClass();

		/*
		 * Look up or generate the designated proxy class and its constructor.
		 */
		Constructor<?> cons = getProxyConstructor( /* XXX caller,*/ loader, interfaces );

		return newProxyInstance( /* XXX caller,*/ cons, h );
	}

	private static Object newProxyInstance( /* XXX Class<?> caller, */ // null if no SecurityManager
		Constructor<?> cons, InvocationHandler h )
	{
		return Kit.unchecked( () -> cons.newInstance( new Object[] { h } ) );
	}

	/**
	 * Returns the class loader for the given module.
	 */
	/* XXX private */
	static ClassLoader getLoader( Module m )
	{
		PrivilegedAction<ClassLoader> pa = m::getClassLoader;
		return AccessController.doPrivileged( pa );
	}

	/* XXX private */ static final String PROXY_PACKAGE_PREFIX = "mikenakis.proxy"; //XXX ReflectUtil.PROXY_PACKAGE; /* "com.sun.proxy" */
}
