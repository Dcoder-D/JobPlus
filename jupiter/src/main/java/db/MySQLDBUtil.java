package db;

public class MySQLDBUtil {
	// Need to be updated once the database instance in created in AWS
	private static final String INSTANCE = "laiproject-instance.cjlxcdq9qdvk.us-east-2.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	// Need to be updated once the database instance in created in AWS
	public static final String DB_NAME = "laiproject";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "laiprojectadmin";
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";


}
