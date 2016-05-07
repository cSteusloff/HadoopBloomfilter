package com.christian.BloomFilter;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;

public final class BloomFilterTester {

	private static final String USAGE_MESSAGE = "usage: jar BloomFilterTester filter-inpath first-test-key [other-test-keys]";
		
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		if (args.length >= 2) {
			final BloomFilter<String> filter = new BloomFilter<String>();
			// read binary file from hdfs
			final Path inPath = new Path(args[0]);
			final FSDataInputStream in = inPath.getFileSystem(
					new Configuration()).open(inPath);
			// set filter by binary file
			filter.readFields(in);

			String currentValue = filter.ToString();
			
			// check all keys
			for (int i = 1; i < args.length; ++i) {
				if (!filter.contains(args[i])) {
					System.out.println("Key not found in filter: " + args[i]);
				} else {
					System.out.println("Key found: " + args[i]);
				}
			}
		} else {
			System.err.println(USAGE_MESSAGE);
		}
	}
}
