package org.schedoscope.export.ftp.outputformat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.schedoscope.export.ftp.upload.Uploader;

public class CSVFileOutputCommitter extends FileOutputCommitter {

	private Path outputPath;

	private String endpoint;

	private String filePrefix;

	private Uploader uploader;

	public CSVFileOutputCommitter(Path outputPath, TaskAttemptContext context) throws IOException {

		super(outputPath, context);

		Configuration conf = context.getConfiguration();

		this.outputPath = outputPath;
		this.endpoint = conf.get(CSVOutputFormat.FTP_EXPORT_ENDPOINT);
		this.filePrefix = conf.get(CSVOutputFormat.FTP_EXPORT_FILE_PREFIX);

		String user = conf.get(CSVOutputFormat.FTP_EXPORT_USER);
		String pass = conf.get(CSVOutputFormat.FTP_EXPORT_PASS);
		String keyFile = conf.get(CSVOutputFormat.FTP_EXPORT_KEY_FILE);
		boolean passiveMode = conf.getBoolean(CSVOutputFormat.FTP_EXPORT_PASSIVE_MODE, true);
		boolean userIsRoot = conf.getBoolean(CSVOutputFormat.FTP_EXPORT_USER_IS_ROOT, true);

		try {

			String protocol = new URI(endpoint).getScheme();

			if (!protocol.equals("ftp") && !protocol.equals("sftp")) {
				throw new IllegalArgumentException("protocol not supported, must be either 'ftp' or 'sftp'");
			}

			if (keyFile == null) {
				uploader = new Uploader(user, pass, conf, passiveMode, userIsRoot);
			} else {
				uploader = new Uploader(user, keyFile, pass, conf, passiveMode, userIsRoot);
			}

		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void commitTask(TaskAttemptContext context) throws IOException {

		super.commitTask(context);

		String fileName = CSVOutputFormat.getOutputName(context);
		String remote = endpoint + "/" + filePrefix + context.getTaskAttemptID().getTaskID().getId() + CSVOutputFormat.getOutputNameExtension();

		uploader.uploadFile(new Path(outputPath, fileName).toString(), remote);
	}
}
