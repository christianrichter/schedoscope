package org.schedoscope.export.outputschema;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

abstract public class AbstractSchema implements Schema {

	protected Configuration conf;

	@Override
	public String getTable() {
		return conf.get(Schema.JDBC_OUTPUT_TABLE);
	}

	@Override
	public String[] getColumnNames() {
		return conf.getStrings(Schema.JDBC_OUTPUT_COLUMN_NAMES);
	}

	@Override
	public String[] getColumnTypes() {
		return conf.getStrings(Schema.JDBC_OUTPUT_COLUMN_TYPES);
	}

	@Override
	public String getCreateTableQuery() {
		return conf.get(Schema.JDBC_CREATE_TABLE_QUERY);
	}

	@Override
	public int getNumberOfPartitions() {
		return conf.getInt(Schema.JDBC_NUMBER_OF_PARTITIONS, 1);
	}

	@Override
	public int getCommitSize() {
		return conf.getInt(Schema.JDBC_COMMIT_SIZE, 1);
	}

	@Override
	public String getFilter() {
		return conf.get(Schema.JDBC_INPUT_FILTER);
	}

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public Map<String, String> getColumnNameMapping() {
		Map<String, String> columnNames = new HashMap<String, String>();
		columnNames.put("year", "data_year");
		columnNames.put("month", "data_month");
		columnNames.put("day", "data_day");
		return columnNames;
	}
}
