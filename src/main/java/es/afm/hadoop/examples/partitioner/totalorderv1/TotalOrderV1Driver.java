package es.afm.hadoop.examples.partitioner.totalorderv1;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.BasicConfigurator;

/**
 * Read SequenceFiles (#occurrences,word) and perform a total order on them
 */
public class TotalOrderV1Driver extends Configured implements Tool {

	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.err
					.println("URLCountDriver required params: <input_path> <output_path>");
			System.exit(-1);
		}

		deleteOutputFileIfExists(args);

		final Job job = Job.getInstance(getConf());
		// local mode execution will turn this to 1
		job.setNumReduceTasks(2);

		job.setJarByClass(TotalOrderV1Driver.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// We may use a combiner
		job.setPartitionerClass(TotalOrderV1Partitioner.class);

		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);

		return 0;
	}

	private void deleteOutputFileIfExists(String[] args) throws IOException {
		final Path output = new Path(args[1]);
		FileSystem.get(output.toUri(), getConf()).delete(output, true);
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		ToolRunner.run(new TotalOrderV1Driver(), args);
	}

}
