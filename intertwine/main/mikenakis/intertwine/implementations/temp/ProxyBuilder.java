package mikenakis.intertwine.implementations.temp;

import mikenakis.kit.Kit;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Builder for a proxy class.
 * <p>
 * If the module is not specified in this ProxyBuilder constructor,
 * it will map from the given loader and interfaces to the module
 * in which the proxy class will be defined.
 */
final class ProxyBuilder
{
	//XXX private static final JavaLangAccess JLA = SharedSecrets.getJavaLangAccess();

	// prefix for all proxy class names
	private static final String proxyClassNamePrefix = "$Proxy";

	// next number to use for generation of unique proxy class names
	private static final AtomicLong nextUniqueNumber = new AtomicLong();

	// a reverse cache of defined proxy classes
	private static final ClassLoaderValue<Boolean> reverseProxyCache = new ClassLoaderValue<>();

	private static Class<?> defineProxyClass( /* XXX Module module,*/ List<Class<?>> interfaceTypes, ClassLoader loader )
	{
//		String proxyPkg = null;     // package to define proxy class in
		int accessFlags = Modifier.PUBLIC | Modifier.FINAL;
//		boolean nonExported = false;

		/*
		 * Record the package of a non-public proxy interface so that the
		 * proxy class will be defined in the same package.  Verify that
		 * all non-public proxy interfaces are in the same package.
		 */
//		for( Class<?> interfaceType : interfaceTypes )
//		{
//			int flags = interfaceType.getModifiers();
//			// module-private types
//			if( !Modifier.isPublic( flags ) )
//			{
//				accessFlags = Modifier.FINAL;  // non-public, final
//				String pkg = interfaceType.getPackageName();
//				if( proxyPkg == null )
//					proxyPkg = pkg;
//				else if( !pkg.equals( proxyPkg ) )
//					throw new IllegalArgumentException( "non-public interfaces from different packages" );
//			}
//			else if( !interfaceType.getModule().isExported( interfaceType.getPackageName() ) )
//				nonExported = true;
//		}

//		if( proxyPkg == null )
//		{
//			// all proxy interfaces are public and exported
//			if( !module.isNamed() )
//				throw new InternalError( "ununamed module: " + module );
//			proxyPkg = nonExported ? Proxy.PROXY_PACKAGE_PREFIX + "." + module.getName() : module.getName();
//		}
//		else if( proxyPkg.isEmpty() && module.isNamed() )
//			throw new IllegalArgumentException( "Unnamed package cannot be added to " + module );

//XXX 		if( module.isNamed() )
//XXX 			if( !module.getDescriptor().packages().contains( proxyPkg ) )
//XXX 				throw new InternalError( proxyPkg + " not exist in " + module.getName() );

		/*
		 * Choose a name for the proxy class to generate.
		 */
		long num = nextUniqueNumber.getAndIncrement();
		String proxyName = /*proxyPkg.isEmpty() ?*/ proxyClassNamePrefix + num /*: proxyPkg + "." + proxyClassNamePrefix + num*/;

//		ClassLoader loader = Proxy.getLoader( module );

		/*
		 * Generate the specified proxy class.
		 */
		byte[] proxyClassFile = ProxyGenerator.generateProxyClass( loader, proxyName, interfaceTypes, accessFlags );
//		try
//		{
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			Class<?> pc = Kit.unchecked( () -> lookup.defineClass( proxyClassFile ) );
			//XXX Class<?> pc = JLA.defineClass( loader, proxyName, proxyClassFile, null, "__dynamic_proxy__" );
			reverseProxyCache.sub( pc ).putIfAbsent( loader, Boolean.TRUE );
			return pc;
//		}
//		catch( ClassFormatError e )
//		{
//			/*
//			 * A ClassFormatError here means that (barring bugs in the
//			 * proxy class generation code) there was some other
//			 * invalid aspect of the arguments supplied to the proxy
//			 * class creation (such as virtual machine limitations
//			 * exceeded).
//			 */
//			throw new IllegalArgumentException( e.toString() );
//		}
	}

	// ProxyBuilder instance members start here....
	private final List<Class<?>> interfaceTypes;
//	private final Module module;

	ProxyBuilder( ClassLoader loader, List<Class<?>> interfaceTypes )
	{
		//XXX		if( !VM.isModuleSystemInited() )
		//XXX			throw new InternalError( "Proxy is not supported until " + "module system is fully initialized" );
		assert interfaceTypes.size() <= 65535 : new IllegalArgumentException( "interface limit exceeded: " + interfaceTypes.size() );

		Set<Class<?>> refTypes = referencedTypes( loader, interfaceTypes );

		// IAE if violates any restrictions specified in newProxyInstance
		validateProxyInterfaces( loader, interfaceTypes, refTypes );

		this.interfaceTypes = interfaceTypes;
//		module = mapToModule( loader, interfaceTypes, refTypes );
//		assert Proxy.getLoader( module ) == loader;
	}

	ProxyBuilder( ClassLoader loader, Class<?> interfaceType )
	{
		this( loader, Collections.singletonList( interfaceType ) );
	}

	/**
	 * Generate a proxy class and return its proxy Constructor with
	 * accessible flag already set. If the target module does not have access
	 * to any interface types, IllegalAccessError will be thrown by the VM
	 * at defineClass time.
	 * <p>
	 * Must call the checkProxyAccess method to perform permission checks
	 * before calling this.
	 */
	Constructor<?> build( ClassLoader classLoader )
	{
		Class<?> proxyClass = defineProxyClass( /*module,*/ interfaceTypes, classLoader );
//		assert !module.isNamed() || module.isOpen( proxyClass.getPackageName(), Proxy.class.getModule() );
		Constructor<?> cons = Kit.unchecked( () -> proxyClass.getConstructor( Proxy.constructorParams ) );
		cons.setAccessible( true );
		return cons;
	}

	/**
	 * Validate the given proxy interfaces and the given referenced types
	 * are visible to the defining loader.
	 *
	 * @throws IllegalArgumentException if it violates the restrictions specified in Proxy.newProxyInstance()
	 */
	private static void validateProxyInterfaces( ClassLoader loader, List<Class<?>> interfaceTypes, Set<Class<?>> refTypes )
	{
		Map<Class<?>,Boolean> interfaceSet = new IdentityHashMap<>( interfaceTypes.size() );
		for( Class<?> interfaceType : interfaceTypes )
		{
			/*
			 * Verify that the Class object actually represents an
			 * interface.
			 */
			assert interfaceType.isInterface() : new IllegalArgumentException( interfaceType.getName() + " is not an interface" );

			assert !interfaceType.isHidden() : new IllegalArgumentException( interfaceType.getName() + " is a hidden interface" );

			/*
			 * Verify that the class loader resolves the name of this
			 * interface to the same Class object.
			 */
			ensureVisible( loader, interfaceType );

			/*
			 * Verify that this interface is not a duplicate.
			 */
			Kit.map.add( interfaceSet, interfaceType, Boolean.TRUE );
		}

		for( Class<?> type : refTypes )
			ensureVisible( loader, type );
	}

	/*
	 * Returns all types referenced by all public non-static method signatures of
	 * the proxy interfaces
	 */
	private static Set<Class<?>> referencedTypes( ClassLoader classLoader, List<Class<?>> interfaceTypes )
	{
		var types = new HashSet<Class<?>>();
		for( var interfaceType : interfaceTypes )
			for( Method method : interfaceType.getMethods() )
				if( !Modifier.isStatic( method.getModifiers() ) )
				{
					addElementType( types, method.getReturnType() );
					addElementTypes( types, method.getParameterTypes() /* XXX getSharedParameterTypes() */ );
					addElementTypes( types, method.getExceptionTypes() /* XXX getSharedExceptionTypes() */ );
				}
		return types;
	}

	private static void addElementTypes( HashSet<Class<?>> types, Class<?>... classes )
	{
		for( var cls : classes )
			addElementType( types, cls );
	}

	private static void addElementType( HashSet<Class<?>> types, Class<?> cls )
	{
		var type = getElementType( cls );
		if( !type.isPrimitive() )
			types.add( type );
	}

	/**
	 * Returns the module that the generated proxy class belongs to.
	 * <p>
	 * If any of proxy interface is package-private, then the proxy class
	 * is in the same module of the package-private interface.
	 * <p>
	 * If all proxy interfaces are public and in exported packages,
	 * then the proxy class is in a dynamic module in an unconditionally
	 * exported package.
	 * <p>
	 * If all proxy interfaces are public and at least one in a non-exported
	 * package, then the proxy class is in a dynamic module in a
	 * non-exported package.
	 * <p>
	 * The package of proxy class is open to java.base for deep reflective access.
	 * <p>
	 * Reads edge and qualified exports are added for dynamic module to access.
	 */
//	private static Module mapToModule( ClassLoader loader, List<Class<?>> interfaceTypes, Set<Class<?>> refTypes )
//	{
//		Map<Class<?>,Module> packagePrivateTypes = new HashMap<>();
//		for( Class<?> interfaceType : interfaceTypes )
//		{
//			Module module = interfaceType.getModule();
//			if( !Modifier.isPublic( interfaceType.getModifiers() ) )
//				Kit.map.addOrReplace( packagePrivateTypes, interfaceType, module );
//		}
//
//		if( !packagePrivateTypes.isEmpty() )
//		{
//			// all package-private types must be in the same runtime package
//			// i.e. same package name and same module (named or unnamed)
//			//
//			// Configuration will fail if M1 and in M2 defined by the same loader
//			// and both have the same package p (so no need to check class loader)
//			Module targetModule = null;
//			String targetPackageName = null;
//			for( Map.Entry<Class<?>,Module> e : packagePrivateTypes.entrySet() )
//			{
//				Class<?> interfaceType = e.getKey();
//				Module module = e.getValue();
//				if( (targetModule != null && targetModule != module) || (targetPackageName != null && !targetPackageName.equals( interfaceType.getPackageName() )) )
//					throw new IllegalArgumentException( "cannot have non-public interfaces in different packages" );
//				// the specified loader is not the same class loader
//				// of the non-public interface
//				if( Proxy.getLoader( module ) != loader )
//					throw new IllegalArgumentException( "non-public interface is not defined by the given loader" );
//
//				targetModule = module;
//				targetPackageName = e.getKey().getPackageName();
//			}
//
//			// validate if the target module can access all other interfaces
//			for( Class<?> interfaceType : interfaceTypes )
//			{
//				Module m = interfaceType.getModule();
//				if( m == targetModule )
//					continue;
//
//				if( !targetModule.canRead( m ) || !m.isExported( interfaceType.getPackageName(), targetModule ) )
//					throw new IllegalArgumentException( targetModule + " can't access " + interfaceType.getName() );
//			}
//
//			// opens the package of the non-public proxy class for java.base to access
//			if( targetModule.isNamed() )
//				Modules.addOpens( targetModule, targetPackageName, Proxy.class.getModule() );
//			// return the module of the package-private interface
//			return targetModule;
//		}
//
//		// All proxy interfaces are public.  So maps to a dynamic proxy module
//		// and add reads edge and qualified exports, if necessary
//		Module targetModule = getDynamicModule( loader );
//
//		// set up proxy class access to proxy interfaces and types
//		// referenced in the method signature
//		Set<Class<?>> types = new HashSet<>( interfaceTypes );
//		types.addAll( refTypes );
//		for( Class<?> c : types )
//			ensureAccess( targetModule, c );
//		return targetModule;
//	}

	/*
	 * Ensure the given module can access the given class.
	 */
//	private static void ensureAccess( Module target, Class<?> c )
//	{
//		Module m = c.getModule();
//		// add read edge and qualified export for the target module to access
//		if( !target.canRead( m ) )
//			Modules.addReads( target, m );
//		String pn = c.getPackageName();
//		if( !m.isExported( pn, target ) )
//			Modules.addExports( m, pn, target );
//	}

	/*
	 * Ensure the given class is visible to the class loader.
	 */
	private static void ensureVisible( ClassLoader ld, Class<?> c )
	{
		Class<?> type = Kit.unchecked( () -> Class.forName( c.getName(), false, ld ) );
		if( type != c )
			throw new IllegalArgumentException( c.getName() + " referenced from a method is not visible from class loader" );
	}

	private static Class<?> getElementType( Class<?> type )
	{
		Class<?> e = type;
		while( e.isArray() )
			e = e.getComponentType();
		return e;
	}

//	private static final ClassLoaderValue<Module> dynProxyModules = new ClassLoaderValue<>();
//	private static final AtomicInteger counter = new AtomicInteger();

	/*
	 * Define a dynamic module with a packge named $MODULE which
	 * is unconditionally exported and another package named
	 * com.sun.proxy.$MODULE which is encapsulated.
	 *
	 * Each class loader will have one dynamic module.
	 */
//	private static Module getDynamicModule( ClassLoader loader )
//	{
//		return dynProxyModules.computeIfAbsent( loader, ( ld, clv ) -> {
//			// create a dynamic module and setup module access
//			String mn = "jdk.proxy" + counter.incrementAndGet();
//			String pn = Proxy.PROXY_PACKAGE_PREFIX + "." + mn;
//			ModuleDescriptor descriptor = ModuleDescriptor.newModule( mn, Set.of( SYNTHETIC ) ).packages( Set.of( pn, mn ) ).exports( mn ).build();
//			Module m = Modules.defineModule( ld, descriptor, null );
//			Modules.addReads( m, Proxy.class.getModule() );
//			Modules.addExports( m, mn );
//			// java.base to create proxy instance and access its Lookup instance
//			Modules.addOpens( m, pn, Proxy.class.getModule() );
//			Modules.addOpens( m, mn, Proxy.class.getModule() );
//			return m;
//		} );
//	}
}
