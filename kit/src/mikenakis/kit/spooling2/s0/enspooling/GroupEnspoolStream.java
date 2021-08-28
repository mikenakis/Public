package mikenakis.kit.spooling2.s0.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.codec.Codec;

public interface GroupEnspoolStream
{
	void enspoolMember( String memberName, Procedure1<EntryEnspoolStream> entryEnspooler );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<T> void enspoolValueMember( String memberName, Codec<T> codec, T value, T defaultValue );

	<T> void enspoolValueMember( String memberName, Codec<T> codec, T value );

	void enspoolGroupMember( String memberName, Procedure1<GroupEnspoolStream> groupEnspooler );

	void enspoolArrayMember( String memberName, String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler );

	interface Defaults extends GroupEnspoolStream
	{
		@Override default <T> void enspoolValueMember( String memberName, Codec<T> codec, T value, T defaultValue )
		{
			enspoolMember( memberName, memberEnspoolStream -> memberEnspoolStream.enspoolValue( codec, value, defaultValue ) );
		}

		@Override default <T> void enspoolValueMember( String memberName, Codec<T> codec, T value )
		{
			enspoolMember( memberName, memberEnspoolStream -> memberEnspoolStream.enspoolValue( codec, value ) );
		}

		@Override default void enspoolGroupMember( String memberName, Procedure1<GroupEnspoolStream> groupEnspooler )
		{
			enspoolMember( memberName, memberEnspoolStream -> memberEnspoolStream.enspoolGroup( groupEnspooler ) );
		}

		@Override default void enspoolArrayMember( String memberName, String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
		{
			enspoolMember( memberName, memberEnspoolStream -> memberEnspoolStream.enspoolArray( arrayElementName, arrayEnspooler ) );
		}
	}
}
