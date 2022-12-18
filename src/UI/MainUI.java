package UI;

import Callback.SensingDataCallback;
import Packet.DataPacket;
import Serial.Serial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainUI extends JFrame implements ActionListener {
    Container c;
    JPanel mainPanel;
    JPanel menubar, menubar_menu, menubar_img;
    JButton homeBtn, controlBtn, alarmBtn, dataBtn;

    static int jpNum =0;
    boolean isFirst_Control,isFirst_Alarm,isFirst_Data;

    JPanel homeUI,controlUI,alarmUI,dataUI;

    DataPacket dataPacket;
    Serial serial;

//    static boolean updateSensing = false;
//    JPanel homePn, controlPn,alarmPn,dataPn;


    public MainUI(){

        SerialConnect();

        c = getContentPane();
        c.setLayout(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setLocation(0,0);
//        mainPanel.setBackground(new Color(255,255,255));
        mainPanel.setSize(600,450);

        menubar = new JPanel(new GridLayout(2,1));
        menubar.setLocation(0,0);
        menubar.setSize(150,450);
        menubar.setBackground(new Color(255,255,255));
        menubar_menu = new JPanel(new GridLayout(4,1));


        homeBtn = new RoundedButton("Home", new Color(99,185,0));
        homeBtn.addActionListener(this);
        controlBtn = new RoundedButton("Control", new Color(99,185,0));
        controlBtn.addActionListener(this);
        alarmBtn = new RoundedButton("Alarm", new Color(99,185,0));
        alarmBtn.addActionListener(this);
        dataBtn = new RoundedButton("Data", new Color(99,185,0));
        dataBtn.addActionListener(this);

        menubar_img = new ImagePanel(new ImageIcon("src/plant.png").getImage());

//        jp = new JPanel[4];
//        for(int i=1;i<jp.length;i++){
//            jp[i] =  new JPanel();
//            jp[i].setLayout(null);
//            jp[i].setLocation(150,0);
//            jp[i].setBackground(new Color(255,255,255));
//            jp[i].setSize(450,450);
//            jp[i].setVisible(false);
//            mainPanel.add(jp[i]);
//
//        }

        c.add(mainPanel);
        mainPanel.add(menubar);
        menubar.add(menubar_menu);
        menubar.add(menubar_img);
        menubar_menu.add(homeBtn);
        menubar_menu.add(controlBtn);
        menubar_menu.add(alarmBtn);
        menubar_menu.add(dataBtn);

        homeUI = new HomeUI(serial,dataPacket);
        setJPanel(homeUI);
//        controlUI = new ControlUI(serial,dataPacket);
//        setJPanel(controlUI);
//        alarmUI = new AlarmUI(serial,dataPacket);
//        setJPanel(alarmUI);
//        dataUI = new DataUI();
//        setJPanel(dataUI);
        homeUI.setVisible(true);


        setTitle("Ireland Table");
        setSize(616,489);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setJPanel(JPanel jp){
        jp.setLayout(null);
        jp.setLocation(150,0);
        jp.setBackground(new Color(255,255,255));
        jp.setSize(450,450);
        jp.setVisible(false);
        mainPanel.add(jp);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==homeBtn){
            jpNum = 0;
//            serial.sw.write_byte("0202FF53FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
//            homeUI.SensingData_Callback();
        }
        else if(e.getSource()==controlBtn){
            if(!isFirst_Control){
                isFirst_Control = true;
                controlUI = new ControlUI(serial,dataPacket);
                setJPanel(controlUI);
            }
            jpNum = 1;
        }
        else if(e.getSource()==alarmBtn){
            if(!isFirst_Alarm){
                isFirst_Alarm = true;
                alarmUI = new AlarmUI(serial,dataPacket);
                setJPanel(alarmUI);
            }
            jpNum = 2;
        }
        else if(e.getSource()==dataBtn){
            if (!isFirst_Data) {
                isFirst_Data = true;
                dataUI = new DataUI();
                setJPanel(dataUI);
            }
            jpNum = 3;
        }
        showedUI(jpNum);
    }

    private void showedUI(int num){
        JPanel[] jp= {homeUI, controlUI, alarmUI, dataUI};
        boolean[] isFirst = {isFirst_Control,isFirst_Alarm,isFirst_Data};
        for(int i=0;i<jp.length;i++){
            if(i!=0){
                if(!isFirst[i-1]) continue;;
            }
            if(i == num)
                jp[i].setVisible(true);
            else
                jp[i].setVisible(false);
        }

    }

    private void SerialConnect(){
        dataPacket = new DataPacket();
        serial = new Serial(dataPacket);
        try {
            serial.connect("COM10");
            System.out.println("Serial Connect");
        }catch (Exception e){
            System.out.println("Serial connect Error: "+e.getMessage());
        }

    }


}
