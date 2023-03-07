import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        int users =0;
        int uid0Users = 0;
        int sbinNoLoginUsers = 0;
        int noCommentUsers = 0;
        String[] columns;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(args[0]));
            String myInput = reader.readLine();
            while (myInput != null) {
                columns = myInput.split(":");
                //for(String column : columns)
                    //System.out.println(column);
                users++;
                if (Objects.equals(columns[2], "0"))
                    uid0Users++;
                if (Objects.equals(columns[6], "/sbin/nologin"))
                    sbinNoLoginUsers++;
                if (Objects.equals(columns[4], ""))
                    noCommentUsers++;
                myInput = reader.readLine();
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        System.out.println("Users: " + users);
        System.out.println("Users with UID 0: "+ uid0Users);
        System.out.println("Users with /sbin/nologin shell: " + sbinNoLoginUsers);
        System.out.println("Users with empty comment field: "+ noCommentUsers);

    }
}
