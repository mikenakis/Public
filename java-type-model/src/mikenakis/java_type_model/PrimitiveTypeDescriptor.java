package mikenakis.java_type_model;

import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a primitive type.
 *
 * @author michael.gr
 */
public final class PrimitiveTypeDescriptor extends TypeDescriptor
{
	private static final PrimitiveTypeDescriptor Boolean /**/ = new PrimitiveTypeDescriptor( boolean.class );
	private static final PrimitiveTypeDescriptor Byte    /**/ = new PrimitiveTypeDescriptor( byte.class );
	private static final PrimitiveTypeDescriptor Char    /**/ = new PrimitiveTypeDescriptor( char.class );
	private static final PrimitiveTypeDescriptor Double  /**/ = new PrimitiveTypeDescriptor( double.class );
	private static final PrimitiveTypeDescriptor Float   /**/ = new PrimitiveTypeDescriptor( float.class );
	private static final PrimitiveTypeDescriptor Int     /**/ = new PrimitiveTypeDescriptor( int.class );
	private static final PrimitiveTypeDescriptor Long    /**/ = new PrimitiveTypeDescriptor( long.class );
	private static final PrimitiveTypeDescriptor Short   /**/ = new PrimitiveTypeDescriptor( short.class );
	private static final PrimitiveTypeDescriptor Void    /**/ = new PrimitiveTypeDescriptor( void.class );
	private static final Collection<PrimitiveTypeDescriptor> all = List.of( Boolean, Byte, Char, Double, Float, Int, Long, Short, Void );
	private static final Map<Class<?>,PrimitiveTypeDescriptor> byJvmClass = all.stream().collect( Collectors.toMap( t -> t.jvmClass, t -> t ) );
	private static final Map<String,PrimitiveTypeDescriptor> byTypeName = all.stream().collect( Collectors.toMap( t -> t.typeName(), t -> t ) );

	public static PrimitiveTypeDescriptor of( Class<?> jvmClass )
	{
		return Kit.map.get( byJvmClass, jvmClass );
	}

	public static PrimitiveTypeDescriptor of( String typeName )
	{
		return Kit.map.get( byTypeName, typeName );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public final Class<?> jvmClass;

	private PrimitiveTypeDescriptor( Class<?> jvmClass )
	{
		this.jvmClass = jvmClass;
	}

	@Deprecated @Override public String typeName() { return jvmClass.getTypeName(); }
	@Deprecated @Override public boolean isArray() { return false; }
	@Deprecated @Override public boolean isTerminal() { return false; }
	@Deprecated @Override public boolean isPrimitive() { return true; }
	@Deprecated @Override public PrimitiveTypeDescriptor asPrimitiveTypeDescriptor() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof PrimitiveTypeDescriptor kin && equals( kin ); }
	public boolean equals( PrimitiveTypeDescriptor other ) { return jvmClass == other.jvmClass; }
	@Override public int hashCode() { return Objects.hash( PrimitiveTypeDescriptor.class, jvmClass ); }
}
