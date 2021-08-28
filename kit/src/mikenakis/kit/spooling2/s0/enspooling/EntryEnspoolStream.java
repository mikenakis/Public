package mikenakis.kit.spooling2.s0.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.codec.Codec;

public interface EntryEnspoolStream
{
	<T> void enspoolValue( Codec<T> codec, T value, T defaultValue );

	void enspoolGroup( Procedure1<GroupEnspoolStream> groupEnspooler );

	void enspoolArray( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<T> void enspoolValue( Codec<T> codec, T value );

	interface Defaults extends EntryEnspoolStream
	{
		@Override default <T> void enspoolValue( Codec<T> codec, T value )
		{
			enspoolValue( codec, value, codec.defaultInstance() );
		}
	}
}
