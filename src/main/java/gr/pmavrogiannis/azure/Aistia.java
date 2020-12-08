package gr.pmavrogiannis.azure;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import gr.pmavrogiannis.azure.components.Config;
import gr.pmavrogiannis.azure.components.MailSender;
import gr.pmavrogiannis.azure.components.SqlHandler;
import gr.pmavrogiannis.azure.pageobjects.XePage;

public class Aistia {

    static String fromEmail = Config.get("fromEmail");
    static String fromEmailPassword = Config.get("fromEmailPassword");
    static String toEmail = Config.get("toEmail");

    static String server = Config.get("server");
    static String database = Config.get("database");
    static String user = Config.get("user");
    static String password = Config.get("password");


    public static void main(String[] args){
        TimerTask task = new TimerTask() {
            public void run() {
                Aistia.run();
            }
        };
        Timer timer = new Timer("Aistia");


        long delay  = 1000L;
        long period = 30*60*1000L;
        timer.scheduleAtFixedRate(task, delay, period);


    }


    public static void run() {

        DateTimeFormatter timestamp = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        ArrayList<String> newURLs = new ArrayList<>();
        SqlHandler agent = new SqlHandler(server, database, user, password);

        XePage xe_page = new XePage(agent);

        try {
            newURLs= xe_page.getNewPosts(agent);
        } catch (IOException e) {
            System.out.println(timestamp.format(now) + " - Aistia finished execution. \n " + e.getMessage());
        }

        if (newURLs.size() > 0) {
            MailSender.send(fromEmail, fromEmailPassword, toEmail, "AISTIA - feedback from XE", convertToText(newURLs));
            //CallSender.call();
            sendNewPostsInDatabase(agent, newURLs);
            System.out.println(timestamp.format(now) + " - Aistia finished execution. \n New posts: \n " + convertToText(newURLs));
        } else
            System.out.println(timestamp.format(now) + " - Aistia finished execution. \n No new posts. ");

    }

    private static String convertToText(ArrayList<String> urls) {
        StringBuilder builder = new StringBuilder();
        for (String url :
                urls) {
            builder.append(url);
        }
        return builder.toString();
    }

    private static void sendNewPostsInDatabase(SqlHandler agent, ArrayList<String> urls) {
        for (String url : urls) {
            String query = String.format("insert into ignored values('%s','',getDate())", url);
            agent.executeQueryWithoutResults(query);
        }

    }
}
