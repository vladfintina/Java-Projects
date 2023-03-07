package group.chat;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.ZonedDateTime;

/**
 * Graphical user interface for the Client Side of the application
 * @author vlad
 */
public class Client extends JFrame implements IClient, ActionListener {
    private Socket socket;
    private BufferedReader bufferedReader; // used to read messages that have been sent from the server to the client
    private BufferedWriter bufferedWriter; // //used to send data to the server
    private String username;
    private JFileChooser fileChooser;
    private File newImage;
    private static String profilePicturePath;
    private static String groupTitle;

    private JPanel panelTop;
    private JTextField textFieldMessage;
    private JButton buttonSendMessage;
    private JTextArea textAreaConversation;

    private JLabel groupPhotoLabel;
    private JLabel groupName;

    /**
     * Client class constructor. Opens the reading/writing channels, sets the username and shows the GUI window
     * @param socket the socket on which the client is connected
     * @param username the username received from the previous set-up window
     */
    public Client(Socket socket, String username) {
        this.setUpWindow();
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

        } catch (IOException e) {
            this.closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Sets up the graphical user interface method
     */
    private void setUpWindow() {
        panelTop = new JPanel();
        panelTop.setLayout(null);
        panelTop.setBackground(new Color(7, 94, 84));
        panelTop.setBounds(0, 0, 450, 70);
        add(panelTop);

        ImageIcon arrowIcon = new ImageIcon(ClassLoader.getSystemResource("group/chat/icons/3.png"));
        Image arrowImage = arrowIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        ImageIcon arrowScaledIcon = new ImageIcon(arrowImage);
        JLabel arrowIconLabel = new JLabel(arrowScaledIcon);
        arrowIconLabel.setBounds(5, 17, 30, 30);
        panelTop.add(arrowIconLabel);

        arrowIconLabel.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent ae){
                System.exit(0);
            }
        });

        setUpProfilePicturePath();
        ImageIcon imageIcon = new ImageIcon(profilePicturePath);
        this.groupPhotoLabel = new JLabel();
        groupPhotoLabel.setBounds(40, 5, 60, 60);
        Image scaledImage = imageIcon.getImage().getScaledInstance(groupPhotoLabel.getWidth(), groupPhotoLabel.getHeight(), Image.SCALE_DEFAULT);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        groupPhotoLabel.setIcon(scaledImageIcon);
        panelTop.add(groupPhotoLabel);

        var titlesPanel = new JPanel();
        titlesPanel.setLayout(new BoxLayout(titlesPanel, BoxLayout.Y_AXIS));
        setUpGroupTitle();
        this.groupName = new JLabel(groupTitle);
        groupName.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        groupName.setForeground(Color.WHITE);
        groupName.setBounds(110, 15, 100, 18);
        panelTop.add(groupName);
        groupName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getNewGroupTitle();
            }
        });

        JLabel groupChatLabel = new JLabel("Group chat");
        groupChatLabel.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
        groupChatLabel.setForeground(Color.WHITE);
        groupChatLabel.setBounds(110, 35, 160, 20);
        panelTop.add(groupChatLabel);

        ImageIcon videoIcon = new ImageIcon(ClassLoader.getSystemResource("group/chat/icons/video.png"));
        Image videoImage = videoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        ImageIcon videoScaledIcon = new ImageIcon(videoImage);
        JLabel videoIconLabel = new JLabel(videoScaledIcon);
        videoIconLabel.setBounds(290, 20, 30, 30);
        panelTop.add(videoIconLabel);

        ImageIcon phoneIcon = new ImageIcon(ClassLoader.getSystemResource("group/chat/icons/phone.png"));
        Image phoneImage = phoneIcon.getImage().getScaledInstance(35, 30, Image.SCALE_DEFAULT);
        ImageIcon phoneScaledIcon = new ImageIcon(phoneImage);
        JLabel phoneIconLabel = new JLabel(phoneScaledIcon);
        phoneIconLabel.setBounds(350, 20, 35, 30);
        panelTop.add(phoneIconLabel);

        ImageIcon dotsIcon = new ImageIcon(ClassLoader.getSystemResource("group/chat/icons/3icon.png"));
        Image dotsImage = dotsIcon.getImage().getScaledInstance(13, 25, Image.SCALE_DEFAULT);
        ImageIcon dotsScaledIcon = new ImageIcon(dotsImage);
        JLabel dotsIconLabel = new JLabel(dotsScaledIcon);
        dotsIconLabel.setBounds(410, 20, 13, 25);
        panelTop.add(dotsIconLabel);

        dotsIconLabel.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent ae){
                changeGroupPicture();
            }});


        textAreaConversation = new JTextArea();
        textAreaConversation.setBounds(5, 75, 440, 570);
        textAreaConversation.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        textAreaConversation.setEditable(false);
        textAreaConversation.setLineWrap(true);
        textAreaConversation.setWrapStyleWord(true);
        add(textAreaConversation);

        textFieldMessage = new JTextField();
        textFieldMessage.setBounds(5, 655, 310, 40);
        textFieldMessage.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        add(textFieldMessage);

        buttonSendMessage = new JButton("Send");
        buttonSendMessage.setBounds(320, 655, 123, 40);
        buttonSendMessage.setBackground(new Color(7, 94, 84));
        buttonSendMessage.setForeground(Color.WHITE);
        buttonSendMessage.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        buttonSendMessage.addActionListener(this);
        add(buttonSendMessage);

        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        setSize(460, 735);
        setLocation(20, 20);
        setResizable(false);
    }

    private void setUpGroupTitle() {
        File file = new File("src/group/chat/settings/group-title.txt");
        if (file.length() == 0)
            groupTitle = "Charles University";
        else {
            try (BufferedReader reader = new BufferedReader(new FileReader("src/group/chat/settings/group-title.txt"))){
                String line = reader.readLine();
                if (line != null)
                    groupTitle = line;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getNewGroupTitle() {
        String newTitle = JOptionPane.showInputDialog("Enter new group title: ");
        String specialMessage = "*GN*" + newTitle + "*GN*";
        try {
            bufferedWriter.write(specialMessage);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

        changeGroupTitle(newTitle);
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("src/group/chat/settings/group-title.txt"))){
            fileWriter.write(newTitle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the group title
     * @param title new title
     */
    @Override
    public void changeGroupTitle(String title) {
        groupName.setText(title);
    }


    /**
     * Checks if the group picture was previously changed.
     * If so, reads the path from the profile-picture-path.txt and displays the photo
     * If there was no other picture loaded than the default one, the path of charles.png picture is given
     */
    private void setUpProfilePicturePath() {
        File file = new File("src/group/chat/settings/profile-picture-path.txt");
        if (file.length() == 0)
            profilePicturePath = "src/group/chat/icons/charles.png";
        else {
            try (BufferedReader reader = new BufferedReader(new FileReader("src/group/chat/settings/profile-picture-path.txt"))){
                String line = reader.readLine();
                if (line != null)
                    profilePicturePath = line;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Changing group picture action is triggered by clicking on the 3 dots icon
     */
    @Override
    public void changeGroupPicture() {
        fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("IMAGES", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(fileNameExtensionFilter);
        int x = fileChooser.showOpenDialog(null);
        if (x == JFileChooser.APPROVE_OPTION) {
            newImage = fileChooser.getSelectedFile();
            String selectedImagePath = newImage.getAbsolutePath();
            JOptionPane.showMessageDialog(null, selectedImagePath);

            ImageIcon ii = new ImageIcon(selectedImagePath);
            Image scaledImage = ii.getImage().getScaledInstance(groupPhotoLabel.getWidth(), groupPhotoLabel.getHeight(), Image.SCALE_DEFAULT);
            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
            groupPhotoLabel.setIcon(scaledImageIcon);

            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("src/group/chat/settings/profile-picture-path.txt"))){
                fileWriter.write(selectedImagePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String specialMessage = "*PP*" + selectedImagePath + "*PP*";
            try {
                bufferedWriter.write(specialMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(socket, bufferedReader, bufferedWriter);
            }

        }
    }

    /**
     * Sends the username of the client to the clienthandler list
     */
    @Override
    public void sendUsername() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Clients wait to receive messages from the other clients.
     * When it is able to read from the stream, it displays the message on the text area
     * Client may also receive path for a new profile picture, which is distinguished by prefix and sufix *PP*
     * Because listening to message is a blocking method (waits for a new message to be received), it runs on a separate thread.
     */
    @Override
    public void listenToMessage() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    if (messageFromGroupChat.startsWith("*PP*") && messageFromGroupChat.endsWith("*PP*")) {
                        changePictureWithReceivedPath(messageFromGroupChat);
                    } else if (messageFromGroupChat.startsWith("*GN*") && messageFromGroupChat.endsWith("*GN*")) {
                        changeGroupTitle(messageFromGroupChat.substring(4, messageFromGroupChat.length() - 4));
                    } else {
                        //appending the timestamp when receiving the message from the other clients
                        ZonedDateTime time = java.time.ZonedDateTime.now();
                        textAreaConversation.append(time.getHour() + ":" + time.getMinute() + " - " + messageFromGroupChat + "\n");
                        System.out.println(messageFromGroupChat);
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    /**
     * Changes the profile picture with the one from the path received
     * @param messageFromGroupChat photo path with delimiters received from the client that actually changed the group photo
     */
    public void changePictureWithReceivedPath(String messageFromGroupChat) {
        String selectedImagePath = messageFromGroupChat.substring(4, messageFromGroupChat.length() - 4);
        System.out.println(selectedImagePath);
        ImageIcon ii = new ImageIcon(selectedImagePath);
        Image scaledImage = ii.getImage().getScaledInstance(groupPhotoLabel.getWidth(), groupPhotoLabel.getHeight(), Image.SCALE_DEFAULT);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        groupPhotoLabel.setIcon(scaledImageIcon);

    }

    /**
     * Closing the connection and the communicating streams
     * @param socket the socket on which the client is connected
     * @param reader reading channel
     * @param writer writing channel
     */
    @Override
    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends the message of the current client to their respective interface, then sends the message to the other clients
     * @param e event when the "Send" message button was pressed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (socket.isConnected()) {
            try {
                bufferedWriter.write(username + ": " + textFieldMessage.getText());
                bufferedWriter.newLine();
                bufferedWriter.flush();
                ZonedDateTime time = java.time.ZonedDateTime.now();
                this.textAreaConversation.append(time.getHour() + ":" + time.getMinute() + " - You: " + textFieldMessage.getText() + "\n");
                this.textFieldMessage.setText("");
            } catch (IOException ex) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Main method through we launch the set-up window, then the client window is launched
     * @param args
     */
    public static void main(String[] args) {
        SetupClient setupClient = new SetupClient();
    }

}
