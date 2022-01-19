package controller.example;

import model.example.Example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DBConnection {

    private static final DBConnection db = new DBConnection();
    private Connection connection;

    private static final String DB_NAME = "db";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String CONNECTION_URL = "jdbc:derby:" + DB_NAME + ";create=true";

    private static final String TABLE_EXAMPLE = "Example";
    private static final String TABLE_HAS_TAG = "HasTag";

    private static final String CREATE_EXAMPLE = "CREATE TABLE " + TABLE_EXAMPLE + " (" +
            " example_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
            " name VARCHAR(255), " +
            " code VARCHAR(4000), " +
            " territory VARCHAR(4000), " +
            " PRIMARY KEY (example_id))";
    private static final String CREATE_HAS_TAG = "CREATE TABLE " + TABLE_HAS_TAG + " (" +
            " has_tag_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, " +
            " example_id INT, " +
            " tag VARCHAR(255), " +
            " PRIMARY KEY (has_tag_id), " +
            " FOREIGN KEY (example_id) REFERENCES " + TABLE_EXAMPLE + " (example_id))";


    private DBConnection() {
        createDatabaseIfNotExists();
    }

    public static DBConnection getInstance() {
        return db;
    }

    /** Create the database if it does not exist already and create the tables. */
    public void createDatabaseIfNotExists() {
        // load database driver
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Alert
        }

        // create connection to database and tables
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL);

            statement = connection.createStatement();
            statement.execute(CREATE_EXAMPLE);
            statement.execute(CREATE_HAS_TAG);

        } catch (SQLException e) {
            // Apache Derby error codes https://db.apache.org/derby/docs/10.8/ref/rrefexcept71493.html
            // "X0Y32" means table already defined which is fine
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
            }
        } finally {
            // close opened statement and connection
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /** Save an Example by inserting it and all its Tags into the database. */
    public void saveExample(Example example) throws SQLException {
        try {
            connection = DriverManager.getConnection(CONNECTION_URL);

            // start transaction
            connection.setAutoCommit(false);

            // insert into example which return the ID of the inserted element
            int exampleId = insertExample(example.getName(), example.getCode(), example.getTerritoryString());

            // insert all tags
            for (String tag : example.getTags()) {
                insertTag(exampleId, tag);
            }

            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
            }

            // rethrow exception to indicate an error occurred while saving
            throw e;

        } finally {
            // close open connections and statements and re-enable autocommit
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ignored) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    private void insertTag(int exampleId, String tag) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_HAS_TAG + " (example_id, tag) VALUES (?, ?)")) {
            statement.setInt(1, exampleId);
            statement.setString(2, tag.toLowerCase());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Integer insertExample(String name, String code, String territoryString) {
        Integer exampleId = null;

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_EXAMPLE + " (name, code, territory) VALUES (?, ?, ?)",
                RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, code);
            statement.setString(3, territoryString);

            statement.executeUpdate();

            // get id of last inserted tuple
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            exampleId = result.getInt(1);
            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exampleId;
    }


    /** Get a list of Examples in a short form which fit the given Tags. */
    public List<String> loadExamplesForTags(String[] tags) {
        /*
        SELECT a.example_id, a.name
        FROM Example AS a JOIN (SELECT example_id, COUNT(*) AS occurrences
					FROM (	SELECT example_id
							FROM HasTag
							WHERE text like ?

							UNION ALL

							SELECT example_id
							FROM HasTag
							WHERE text like ?

							...
					) AS t
					GROUP BY example_id
					ORDER BY occurrences DESC) AS b
            ON a.example_id = b.example_id
         */

        PreparedStatement statement = null;
        List<String> exampleList = null;

        try {
            connection = DriverManager.getConnection(CONNECTION_URL);

            // start transaction
            connection.setAutoCommit(false);

            StringBuilder tagQuery = new StringBuilder();
            tagQuery.append("SELECT example_id, COUNT(*) AS occurrences ")
                    .append("FROM (");
            for (int i = 0; i < tags.length; i++) {
                tagQuery.append("SELECT example_id ")
                        .append("FROM HasTag ")
                        .append("WHERE tag like ?");

                if (i != tags.length - 1) {
                    tagQuery.append(" UNION ALL ");
                }
            }
            tagQuery.append(") AS t ")
                    .append("GROUP BY example_id ")
                    .append("ORDER BY occurrences DESC");

            String fullQuery = "SELECT a.example_id, a.name " +
                    "FROM Example AS a JOIN " +
                    "(" + tagQuery + " ) as b " +
                    "ON a.example_id = b.example_id";

            statement = connection.prepareStatement(fullQuery);

            for (int i = 0; i < tags.length; i++) {
                statement.setString(i + 1, tags[i].toLowerCase());
            }

            ResultSet r = statement.executeQuery();

            connection.commit();

            exampleList = new ArrayList<>();
            while (r.next()) {
                exampleList.add(r.getString(1) + " - " + r.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
            }
        } finally {
            // close open connections and statements and re-enable autocommit
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ignored) {
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return exampleList;
    }


    /** Get the data for a specific Example from the database. */
    public Example loadExampleForId(Integer exampleId) {
        Example result = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(CONNECTION_URL);

            statement = connection.prepareStatement("SELECT name, code, territory " +
                    "FROM " + TABLE_EXAMPLE + " " +
                    "WHERE example_id = ?");
            statement.setInt(1, exampleId);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Example(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), null);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return result;
    }

    public void debugSelectAll() {
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL);

            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT example_id, name, tag FROM Example NATURAL JOIN HasTag");

            while (rs.next()) {
                System.out.println(Integer.parseInt(String.valueOf(rs.getInt(1))) + " " +
                        rs.getString(2) + " " +
                        rs.getString(3));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }

    }

    public static void shutDown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ignored) {
        }
    }
}
