package mikenakis.kit.spooling2.s0.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.spooling2.codec.Codec;

public interface EntryDespoolStream
{
	<T> T despoolValue( Codec<T> codec, T defaultValue );

	<T> T despoolGroup( Function1<T,GroupDespoolStream> groupDespooler );

	<T> T despoolArray( String arrayElementName, Function1<T,ArrayDespoolStream> arrayDespooler );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<T> T despoolValue( Codec<T> codec );

	interface Defaults extends EntryDespoolStream
	{
		@Override default <T> T despoolValue( Codec<T> codec )
		{
			return despoolValue( codec, codec.defaultInstance() );
		}
	}
}
