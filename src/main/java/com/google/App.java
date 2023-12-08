package com.google;

import java.util.HashMap;
import java.util.Map;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.hcatalog.HCatalogIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {

		PipelineOptions options = PipelineOptionsFactory.fromArgs(args).create();
		Pipeline pipeline = Pipeline.create(options);
		Map<String, String> configProperties = new HashMap<String, String>();
		configProperties.put("hive.metastore.uris", "thrift://localhost:9083");
		configProperties.put("hive.metastore.uri.selection", "SEQUENTIAL");
		configProperties.put("fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem");
		configProperties.put(
				"fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS");
		LOG.info("config properties: " + configProperties);
		pipeline
				.apply(
						"read the table contents",
						HCatalogIO.read()
								.withConfigProperties(configProperties)
								.withDatabase("default")
								.withTable("mytable"))
				.apply(
						"output hcat",
						ParDo.of(
								new DoFn<HCatRecord, String>() {
									@ProcessElement
									public void processElement(ProcessContext c) {
										c.output(c.element().toString());
									}
								}))
				.apply(
						TextIO.write()
								.to(
										"gs://yahoo-poc-data"));

		LOG.info("Completed read");

		// Run the pipeline

		pipeline.run().waitUntilFinish();
	}
}
