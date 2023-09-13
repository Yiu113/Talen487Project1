package com.example.project487;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.*;
import javafx.event.*;

import java.io.IOException;
import java.sql.*;
import java.util.Random;

import oracle.jdbc.*;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.proxy.annotation.Pre;
import org.controlsfx.control.PopOver;

import javax.sql.DataSource;

public class HelloApplication extends Application {

    @FXML TextField username;
    @FXML TextField password;
    @FXML Button signin;
    @FXML Label connected;

    @FXML Label swipedin;
    @FXML Pane commandpane;

    @FXML Pane resultpane;

    @FXML private Button activate;

    @FXML private TextField cardid;

    @FXML private Label confirmer;
    @FXML private Button reactivate;

    @FXML private TextArea resulttable;
    @FXML private Button searchdate;

    @FXML private Button searchid;

    @FXML private TextField searchtext;

    @FXML private Button searchtime;

    @FXML private Button suspend;
    @FXML private Label swipedlabel;

    @FXML private Button swipein;

    @FXML private Button swipeout;


    String url = "jdbc:mysql://localhost:3306";

    public Connection conn;


    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("test.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 420 );
        stage.setScene(scene);
        stage.show();
    }
    @FXML public void signinAction(ActionEvent event) throws Exception{
        String user = username.getText();
        String pass = password.getText();
        conn = DriverManager.getConnection
                ("jdbc:mysql://localhost:3306/project487?useSSL=false", user, pass);
        System.out.println("Connected!");
        connected.setVisible(true);
        //select * from accesses


        PreparedStatement statement = conn.prepareStatement("select * from accesses");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
        resulttable.appendText(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + "\n");

        commandpane.setVisible(true);
        resultpane.setVisible(true);
    }




    @FXML
    void swipeIn(ActionEvent event) throws SQLException {

        PreparedStatement statement = conn.prepareStatement("select status from users where ID =?");
        statement.setInt(1, Integer.parseInt(cardid.getText()));
        System.out.println(statement);
        ResultSet resultSet = statement.executeQuery();

        Random rand = new Random();
        int rand1 = rand.nextInt(1000000);
        if (resultSet.next()) {
            if (resultSet.getString(1).equals("active")){
                System.out.println("Adding swipein to database");
                statement = conn.prepareStatement("INSERT INTO accesses (time, ID, `inout`) VALUES (?, ?, 'in')");
                statement.setInt(1, rand1);
                statement.setInt(2,Integer.parseInt(cardid.getText()));
                statement.executeUpdate();

                swipedin.setText("Swiped In");
            }else {
                swipedin.setText("Suspended Account.");
            }
        }else {
            swipedin.setText("Swipein Failed.");
        }

        swipedin.setVisible(true);
    }

    @FXML
    void swipeOut(ActionEvent event) throws SQLException {

        Random rand = new Random();
        int rand2 = rand.nextInt(1000000);
        System.out.println("Adding swipeout to database");
        PreparedStatement statement = conn.prepareStatement("INSERT INTO accesses (time, ID, `inout`) VALUES (?, ?, 'out')");
        statement.setInt(1, rand2);
        statement.setInt(2,Integer.parseInt(cardid.getText()));
        statement.executeUpdate();
        swipedin.setVisible(true);

        swipedin.setText("Swiped out.");
    }

    @FXML
    private Button suspendconfirm;

    @FXML
    private TextField databaseid;

    @FXML
    void suspendFinish(ActionEvent event) throws SQLException {

        //UPDATE users SET status = 'suspended' where ID = Integer.valueOf(suspendid.getText())
        System.out.println("Suspending " + databaseid.getText());
        PreparedStatement statement = conn.prepareStatement("UPDATE users SET status = 'suspended' where ID = ?");
        statement.setInt(1, Integer.parseInt(databaseid.getText()));
        statement.executeUpdate();
    }
    @FXML
    private Button activateconfirm;

    @FXML
    private TextField activateid;

    @FXML
    private TextField activatename;

    @FXML
    private TextField activaterank;

    @FXML
    void activateFinish(ActionEvent event) throws SQLException {

        System.out.println("Activating " + databaseid.getText());
        PreparedStatement statement = conn.prepareStatement("INSERT INTO users (ID, name, type, status) VALUES (?, ?, ?, 'active')");
        statement.setInt(1, Integer.parseInt(databaseid.getText()));
        statement.setString(2, activatename.getText());
        statement.setString(3, activaterank.getText());
        statement.executeUpdate();

    }
    @FXML
    private Button reactivateconfirm;

    @FXML
    private TextField reactivateid;

    @FXML
    void reactivateFinish(ActionEvent event) throws SQLException {

        //UPDATE users SET status = 'active' where ID = Integer.valueOf(suspendid.getText())
        System.out.println("Reactivating " + databaseid.getText());
        PreparedStatement statement = conn.prepareStatement("UPDATE users SET status = 'active' where ID = ?");
        statement.setInt(1, Integer.parseInt(databaseid.getText()));
        statement.executeUpdate();
    }
    @FXML
    private TextArea searchIDResults;

    @FXML
    private Button searchidconfirm;

    @FXML
    private TextField searchidtext;

    @FXML
    private TextField idsearch;
    @FXML
    void searchIDFinish(ActionEvent event) throws SQLException {

        //select * from accesses where ID = searchidtext.getText()
//        PreparedStatement statement = conn.prepareStatement("select * from accesses where ID = ?");
        System.out.println("Searching by ID: " + idsearch);
        PreparedStatement statement = null;

            statement = conn.prepareStatement("select * from accesses where ID = ?");


            statement.setInt(1, Integer.parseInt(idsearch.getText()));

        ResultSet resultSet = statement.executeQuery();

        resulttable.setText("");
        while (resultSet.next()) {
            resulttable.appendText(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + "\n");
        }

    }

    @FXML private TextField timestamp1;
    @FXML private TextField timestamp2;
    @FXML
    void searchTime(ActionEvent event) throws IOException, SQLException {
        System.out.println("Searching by Time: " + timestamp1.getText() + " to " + timestamp2.getText());
        PreparedStatement statement = conn.prepareStatement("select * from accesses where time between ? AND ?");
        statement.setInt(1, Integer.valueOf(timestamp1.getText()));
        statement.setInt(2, Integer.valueOf(timestamp2.getText()));
        ResultSet resultSet = statement.executeQuery();

        resulttable.setText("");
        while (resultSet.next()) {
            resulttable.appendText(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + "\n");
        }
    }

    public static void main(String[] args) throws Exception{

        launch();

    }
}
