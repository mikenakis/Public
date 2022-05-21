package io.github.mikenakis.bytecode.test.model;

/**
 * test class.
 * <p>
 * TODO: look at (<a href="https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-4.html#jvms-4.7.20.2">jvms-4.7.20.2</a>) for additional ways to specify type annotations
 *
 * @author michael.gr
 */
@SuppressWarnings( { "unused", "EmptyMethod", "MethodMayBeStatic" } )
@RuntimeVisibleTypeAnnotation1( stringParameter = "a" )
@RuntimeInvisibleTypeAnnotation1( 6 )
public abstract class Class6WithTypeAnnotations
{
	@SuppressWarnings( "FieldMayBeStatic" ) //
	@RuntimeVisibleTypeAnnotation1( stringParameter = "b" ) //
	@RuntimeInvisibleTypeAnnotation1( 7 ) //
	public final int fieldWithAnnotation = 5;

	@RuntimeVisibleTypeAnnotation1( stringParameter = "c" ) //
	@RuntimeInvisibleTypeAnnotation1( 8 ) //
	protected Class6WithTypeAnnotations()
	{
	}

	@RuntimeVisibleTypeAnnotation1( annotationParameter = @SuppressWarnings( "foo" ) ) //
	public final char methodWithAnnotationWithAnnotationParameter()
	{
		return 'c';
	}

	@RuntimeVisibleTypeAnnotation1( arrayParameter = { "d", "e" } ) //
	public final short methodWithAnnotationWithArrayParameter()
	{
		return 10;
	}

	@RuntimeVisibleTypeAnnotation1( booleanParameter = false, byteParameter = (byte)2, charParameter = 'd', doubleParameter = 0.1, floatParameter = 0.2f, //
		intParameter = 3, longParameter = 0x1_0000_0000L, shortParameter = (short)1000 ) //
	public final long methodWithAnnotationWithPrimitiveParameters()
	{
		return 11;
	}

	@RuntimeVisibleTypeAnnotation1( stringParameter = "y" ) //
	public final float methodWithAnnotationWithStringParameter()
	{
		return 5f;
	}

	@RuntimeVisibleTypeAnnotation1( classParameter = Class6WithTypeAnnotations.class ) //
	public final double methodWithAnnotationWithClassParameter()
	{
		return 6;
	}

	@RuntimeVisibleTypeAnnotation1 //
	public final int methodWithAnnotationWithNoParameter()
	{
		return 7;
	}

	@RuntimeVisibleTypeAnnotation1( 6 ) //
	public final byte methodWithAnnotationWithDefaultParameter()
	{
		return 1;
	}

	@SuppressWarnings( "MethodMayBeStatic" ) public final void methodWithParameterAnnotations( int a, //
		@RuntimeVisibleTypeAnnotation1( stringParameter = "g" ) int b, //
		@RuntimeVisibleTypeAnnotation1( stringParameter = "h" ) @RuntimeInvisibleTypeAnnotation1( 1 ) int c )
	{
		System.out.println( "a=" + a + ", b=" + b + ", c=" + c );
	}

	//	public final <@RuntimeVisibleAnnotation1( stringParameter = "i" ) T> void methodWithGenericTypeAnnotations( T argument )
	//	{
	//		this.<@RuntimeVisibleAnnotation1( stringParameter = "h" ) T>methodWithGenericTypeAnnotations( null );
	//	}
}
