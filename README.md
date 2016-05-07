# HadoopBloomfilter
Otto-von-Guericke-University Magdeburg BigData Lesson Task3

## task description
3. Next, write a MR program (see hints) that reads Locations.csv to create a Bloom filter
over all location names (column 21, e.g., “Knusperscheune”). An application of your
Bloom filter shall be client-side cache that –prior to a call to HBase- checks whether
respective location exists or not (to prevent a remote API call). The Bloom filter shall be
written to HDFS as binary file. The Bloom filter shall have a “bitArraySize” of 10.000
(m=10.000) and shall use 6 hash functions (k=6).

4. Finally, the Bloom filter shall be used within a client application (copy it out from HDFS,
so that you can use it within your client application) to show the existence of locations
(e.g., „Aphrodite GmbH“ oder „Toulouse du Lac“) within the Bloom filter. Compare the
size of the Bloom filter with the size of a cache (only using the keys), if the keys of the
cache were generated using an MD5 hash.
