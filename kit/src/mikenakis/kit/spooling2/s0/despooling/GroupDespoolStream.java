package mikenakis.kit.spooling2.s0.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.spooling2.codec.Codec;

public interface GroupDespoolStream
{
	<T> T despoolMember( String memberName, Function1<T,EntryDespoolStream> entryDespooler );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<T> T despoolValueMember( String memberName, Codec<T> codec, T defaultValue );

	<T> T despoolValueMember( String memberName, Codec<T> codec );

	<T> T despoolGroupMember( String memberName, Function1<T,GroupDespoolStream> groupDespooler );

	<T> T despoolArrayMember( String memberName, String elementName, Function1<T,ArrayDespoolStream> arrayDespooler );

	interface Defaults extends GroupDespoolStream
	{
		@Override default <T> T despoolValueMember( String memberName, Codec<T> codec, T defaultValue )
		{
			return despoolMember( memberName, memberDespoolStream -> memberDespoolStream.despoolValue( codec, defaultValue ) );
		}

		@Override default <T> T despoolValueMember( String memberName, Codec<T> codec )
		{
			return despoolMember( memberName, memberDespoolStream -> memberDespoolStream.despoolValue( codec ) );
		}

		@Override default <T> T despoolGroupMember( String memberName, Function1<T,GroupDespoolStream> groupDespooler )
		{
			return despoolMember( memberName, memberDespoolStream -> memberDespoolStream.despoolGroup( groupDespooler ) );
		}

		@Override default <T> T despoolArrayMember( String memberName, String arrayElementName, Function1<T,ArrayDespoolStream> arrayDespooler )
		{
			return despoolMember( memberName, memberDespoolStream -> memberDespoolStream.despoolArray( arrayElementName, arrayDespooler ) );
		}
	}
}
