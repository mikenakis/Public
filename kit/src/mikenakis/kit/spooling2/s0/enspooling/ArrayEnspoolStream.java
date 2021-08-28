package mikenakis.kit.spooling2.s0.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.codec.Codec;

public interface ArrayEnspoolStream
{
	void enspoolElement( Procedure1<EntryEnspoolStream> entryEnspooler );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<T> void enspoolValueElement( Codec<T> codec, T value );

	void enspoolGroupElement( Procedure1<GroupEnspoolStream> groupEnspooler );

	void enspoolArrayElement( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler );

	interface Defaults extends ArrayEnspoolStream
	{
		@Override default <T> void enspoolValueElement( Codec<T> codec, T value )
		{
			enspoolElement( elementEnspoolStream -> elementEnspoolStream.enspoolValue( codec, value ) );
		}

		@Override default void enspoolGroupElement( Procedure1<GroupEnspoolStream> groupEnspooler )
		{
			enspoolElement( elementEnspoolStream -> elementEnspoolStream.enspoolGroup( groupEnspooler ) );
		}

		@Override default void enspoolArrayElement( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
		{
			enspoolElement( elementEnspoolStream -> elementEnspoolStream.enspoolArray( arrayElementName, arrayEnspooler ) );
		}
	}
}
