package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.benchmark.Benchmark;
import io.github.mikenakis.benchmark.Benchmarkable;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.test.model.Model;
import io.github.mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class BytecodeBenchmark
{
	public static void main( String[] args )
	{
		List<Path> classFilePathNames = Model.getClassFilePathNames();
		List<byte[]> bytesList = classFilePathNames.stream().map( path -> Kit.unchecked( () -> Files.readAllBytes( path ) ) ).toList();
		double readingDurationSeconds = benchmarkReading( bytesList );
		double printingDurationSeconds = benchmarkPrinting( bytesList );
		System.out.printf( Locale.ROOT, "Reading:  %6.2f millis\n", readingDurationSeconds * 1e3 );
		System.out.printf( Locale.ROOT, "Printing: %6.2f millis\n", printingDurationSeconds * 1e3 );
	}

	private static double benchmarkReading( Iterable<byte[]> bytesList )
	{
		Benchmark benchmark = new Benchmark( 0.001, 50 );
		return benchmark.measure( Benchmarkable.of( () -> //
		{
			int sum = 0;
			for( var bytes : bytesList )
			{
				ByteCodeType byteCodeType = ByteCodeType.read( bytes );
				sum += byteCodeType.hashCode();
			}
			return sum;
		} ) );
	}

	private static double benchmarkPrinting( Iterable<byte[]> bytesList )
	{
		Benchmark benchmark = new Benchmark( 0.001, 50 );
		return benchmark.measure( Benchmarkable.of( () -> //
		{
			int sum = 0;
			for( var bytes : bytesList )
			{
				ByteCodeType byteCodeType = ByteCodeType.read( bytes );
			//	ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() );
				sum += byteCodeType.hashCode();
			}
			return sum;
		} ) );
	}
}
