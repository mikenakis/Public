package mikenakis.bytecode.test;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.debug.Debug;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import org.junit.Test;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * test.
 *
 * @author michael.gr
 */
@SuppressWarnings( "MethodMayBeStatic" )
public class T002_Descriptors
{
	public T002_Descriptors()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@SuppressWarnings( { "InnerClassMayBeStatic", "unused" } )
	private class NestedClass
	{
	}

	@Test public void Classes_Check_Out()
	{
		classTest( int.class );
		classTest( String.class );
		classTest( NestedClass.class );
		classTest( Collection.class );
		classTest( int[][].class );
		classTest( String[][].class );
		classTest( NestedClass[][].class );
		classTest( Collection[][].class );
		classTest( new java.io.Serializable()
		{
		}.getClass() );
		classTest( Array.newInstance( new java.io.Serializable()
		{
		}.getClass(), 1 ).getClass() );
	}

	private static void classTest( Class<?> javaClass1 )
	{
		Log.debug( javaClass1.getTypeName() );
		ClassDesc descriptor = javaClass1.describeConstable().orElseThrow();
		String descriptorString = descriptor.descriptorString();
		assert descriptorString.equals( javaClass1.descriptorString() );
		assert descriptor.equals( ClassDesc.ofDescriptor( descriptorString ) );
		assert ByteCodeHelpers.typeNameFromClassDesc( descriptor ).equals( javaClass1.getTypeName() );
	}

	@SuppressWarnings( "unused" ) private int intField;
	@SuppressWarnings( "unused" ) private String classField;
	@SuppressWarnings( "unused" ) private NestedClass nestedClassField;
	@SuppressWarnings( "unused" ) private Collection<?> interfaceField;
	@SuppressWarnings( "unused" ) private int[][] intArrayField;
	@SuppressWarnings( "unused" ) private String[][] classArrayField;
	@SuppressWarnings( "unused" ) private Collection<?>[][] interfaceArrayField;
	@SuppressWarnings( "unused" ) private NestedClass[][] nestedClassArrayField;

	@Test public void Fields_Check_Out()
	{
		Field[] fields = T002_Descriptors.class.getDeclaredFields();
		Arrays.stream( fields ) //
			.filter( f -> f.getName().endsWith( "Field" ) ) //
			.forEach( f -> fieldTest( f ) );
	}

	private static void fieldTest( Field field )
	{
		classTest( field.getType() );
	}

	@Test public void Methods_Check_Out()
	{
		Arrays.stream( T002_Descriptors.class.getDeclaredMethods() ) //
			.filter( m -> m.getName().startsWith( "m_" ) ) //
			.sorted( methodComparator ) //
			.forEach( m -> methodTest( m ) );
	}

	private static final Comparator<Method> methodComparator = Comparator.comparing( m -> m.getName() );

	@SuppressWarnings( "unused" ) private void m00_voidMethod() { }

	@SuppressWarnings( "unused" ) private int               /**/ m_01_intReturningMethod()              /**/ { return 0; }
	@SuppressWarnings( "unused" ) private String            /**/ m_02_classReturningMethod()            /**/ { return null; }
	@SuppressWarnings( "unused" ) private NestedClass       /**/ m_03_nestedClassReturningMethod()      /**/ { return null; }
	@SuppressWarnings( "unused" ) private Collection<?>     /**/ m_04_interfaceReturningMethod()        /**/ { return null; }
	@SuppressWarnings( "unused" ) private int[][]           /**/ m_05_intArrayReturningMethod()         /**/ { return null; }
	@SuppressWarnings( "unused" ) private NestedClass[][]   /**/ m_06_nestedClassArrayReturningMethod() /**/ { return null; }
	@SuppressWarnings( "unused" ) private Collection<?>[][] /**/ m_07_interfaceArrayReturningMethod()   /**/ { return null; }

	@SuppressWarnings( "unused" ) private void m_11_intAcceptingMethod( int i ) { }
	@SuppressWarnings( "unused" ) private void m_12_classAcceptingMethod( String s ) { }
	@SuppressWarnings( "unused" ) private void m_13_nestedClassAcceptingMethod( NestedClass c ) { }
	@SuppressWarnings( "unused" ) private void m_14_interfaceAcceptingMethod( Collection<?> i ) { }
	@SuppressWarnings( "unused" ) private void m_15_intArrayAcceptingMethod( int[][] a ) { }
	@SuppressWarnings( "unused" ) private void m_16_nestedClassArrayAcceptingMethod( NestedClass[][] a ) { }
	@SuppressWarnings( "unused" ) private void m_17_interfaceArrayAcceptingMethod( Collection<?>[][] a ) { }

	private static void methodTest( Method method )
	{
		MethodTypeDesc methodDescriptor = getDescriptorForMethod( method );
		Log.debug( getDisplayDescriptorForMethod( method ) + "  ->  " + methodDescriptor.descriptorString() );
		String methodDescriptorString = methodDescriptor.descriptorString();
		assert methodDescriptorString.equals( getDescriptorStringForMethod( method ) );
		MethodTypeDesc methodDescriptor2 = MethodTypeDesc.ofDescriptor( methodDescriptorString );
		String methodDescriptorString2 = methodDescriptor2.descriptorString();
		assert methodDescriptorString2.equals( methodDescriptorString );
		assert methodDescriptor.equals( methodDescriptor2 );
	}

	private static String getDisplayDescriptorForMethod( Method m )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( m.getReturnType().getCanonicalName() );
		stringBuilder.append( ' ' );
		stringBuilder.append( m.getName() );
		String argumentList = Kit.string.make( ", ", Arrays.stream( m.getParameterTypes() ).map( p -> p.getCanonicalName() ).toList() );
		if( argumentList.isEmpty() )
			stringBuilder.append( "()" );
		else
			stringBuilder.append( "( " ).append( argumentList ).append( " )" );
		return stringBuilder.toString();
	}

	private static String getDescriptorStringForMethod( Method m )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( '(' );
		for( Class<?> c : m.getParameterTypes() )
			stringBuilder.append( c.descriptorString() );
		stringBuilder.append( ')' );
		stringBuilder.append( m.getReturnType().descriptorString() );
		return stringBuilder.toString();
	}

	private static MethodTypeDesc getDescriptorForMethod( Method m )
	{
		ClassDesc returnTypeDescriptor = m.getReturnType().describeConstable().orElseThrow();
		Class<?>[] parameterTypes = m.getParameterTypes();
		ClassDesc[] parameterTypeDescriptors = new ClassDesc[parameterTypes.length];
		for( int i = 0; i < parameterTypes.length; i++ )
			parameterTypeDescriptors[i] = parameterTypes[i].describeConstable().orElseThrow();
		return MethodTypeDesc.of( returnTypeDescriptor, parameterTypeDescriptors );
	}
}
