package io.github.mikenakis.bytecode.test.model;

/**
 * test class.
 *
 * @author michael.gr
 */
@SuppressWarnings( { "unused", "EmptyMethod" } )
@RuntimeVisibleAnnotation1( stringParameter = "a" )
@RuntimeInvisibleAnnotation1( 6 )
public abstract class Class6WithAnnotations
{
	@SuppressWarnings( "FieldMayBeStatic" ) //
	@RuntimeVisibleAnnotation1( stringParameter = "b" ) //
	@RuntimeInvisibleAnnotation1( 7 ) //
	public final int fieldWithAnnotation = 5;

	@RuntimeVisibleAnnotation1( stringParameter = "c" ) //
	@RuntimeInvisibleAnnotation1( 8 ) //
	protected Class6WithAnnotations()
	{
	}

	@RuntimeVisibleAnnotation1( annotationParameter = @SuppressWarnings( "foo" ) ) //
	public final void methodWithAnnotationWithAnnotationParameter()
	{
	}

	@RuntimeVisibleAnnotation1( arrayParameter = { "d", "e" } ) //
	public final void methodWithAnnotationWithArrayParameter()
	{
	}

	@RuntimeVisibleAnnotation1( booleanParameter = false, byteParameter = (byte)2, charParameter = 'd', doubleParameter = 0.1, floatParameter = 0.2f, //
		intParameter = 3, longParameter = 0x1_0000_0000L, shortParameter = (short)1000 ) //
	public final void methodWithAnnotationWithPrimitiveParameters()
	{
	}

	@RuntimeVisibleAnnotation1( stringParameter = "y" ) //
	public final void methodWithAnnotationWithStringParameter()
	{
	}

	@RuntimeVisibleAnnotation1( classParameter = Class6WithAnnotations.class ) //
	public final void methodWithAnnotationWithClassParameter()
	{
	}

	@RuntimeVisibleAnnotation1 //
	public final void methodWithAnnotationWithNoParameter()
	{
	}

	@RuntimeVisibleAnnotation1( 6 ) //
	public final void methodWithAnnotationWithDefaultParameter()
	{
	}

	@SuppressWarnings( "MethodMayBeStatic" ) public final void methodWithParameterAnnotations( int a, //
		@RuntimeVisibleAnnotation1( stringParameter = "g" ) int b, //
		@RuntimeVisibleAnnotation1( stringParameter = "h" ) @RuntimeInvisibleAnnotation1( 1 ) int c )
	{
		System.out.println( "a=" + a + ", b=" + b + ", c=" + c );
	}

	//	public final <@RuntimeVisibleAnnotation1( stringParameter = "i" ) T> void methodWithGenericTypeAnnotations( T argument )
	//	{
	//		this.<@RuntimeVisibleAnnotation1( stringParameter = "h" ) T>methodWithGenericTypeAnnotations( null );
	//	}
}
