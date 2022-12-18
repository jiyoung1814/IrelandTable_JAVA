package UI;

import Callback.AlarmCallback;
import Packet.DataPacket;
import Serial.Serial;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class AlarmUI extends JPanel implements ActionListener {
    Serial serial;
    DataPacket dataPacket;

    Color[] bntColor = {new Color(235,119,97), new Color(108,169,232),new Color(255,197,51)};
    RoundedButton[] chooseBnt;
    static int choseNum = 0;
    String[] title = {"RED","BLUE","FAN"};
    String[] title_OnOff = {"ON", "OFF"};
    JLabel[] jl;
    String[] hour = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19", "20","21","22","23"};
    String[] minute = {"00","05","10","15","20","25","30","35","40","45","50","55"};
    String[] redTime;
    String[] blueTime;
    String[] fanTime;
    JComboBox<String>[] on_combo;
    JComboBox<String>[] off_combo;

    JButton setBtn;

    public AlarmUI(Serial serial, DataPacket dataPacket){
        this.serial = serial;
        this.dataPacket = dataPacket;

        redTime = new String[4];
        blueTime = new String[4];
        fanTime = new String[4];

        JLabel label= new JLabel("LED/FAN Alarm");
        label.setLocation(120,30);
        label.setSize(210,60);
        label.setFont(new Font("Serif",Font.BOLD,20));
        label.setHorizontalAlignment(JLabel.CENTER);



        chooseBnt = new RoundedButton[3];
        for(int i=0;i<chooseBnt.length;i++){
            chooseBnt[i] = new RoundedButton(title[i], bntColor[i]);
            chooseBnt[i].addActionListener(this);
            chooseBnt[i].setLocation(i*120+60, 90);
            chooseBnt[i].setSize(90,30);
            add(chooseBnt[i]);
        }


        jl = new JLabel[2];
        JLabel[] jl_timeMark = new JLabel[2];
        for(int i=0;i<jl.length;i++){
            jl[i] = new JLabel(title[0]+" "+title_OnOff[i]);
            jl[i].setHorizontalAlignment(JLabel.CENTER);
            jl[i].setOpaque(true);
            jl[i].setBackground(bntColor[0].brighter());
//            jl[i].setFont(new Font("Serif",Font.BOLD,20));
            jl[i].setLocation(30,i*120+165);
            jl[i].setSize(60,30);
            add(jl[i]);

            jl_timeMark[i] = new JLabel(":");
            jl_timeMark[i].setOpaque(true);
            jl_timeMark[i].setBackground(Color.white);
            jl_timeMark[i].setHorizontalAlignment(JLabel.CENTER);
            jl_timeMark[i].setLocation(240,i*120+150);
            jl_timeMark[i].setSize(30,60);
            add(jl_timeMark[i]);


        }
        on_combo = new JComboBox[2];
        off_combo = new JComboBox[2];

        for(int i=0;i<on_combo.length;i++){

            if(i==0){
                on_combo[i] = new JComboBox<>(hour);
                off_combo[i] = new JComboBox<>(hour);
            }
            else{
                on_combo[i] = new JComboBox<>(minute);
                off_combo[i] = new JComboBox<>(minute);
            }

            on_combo[i].setLocation(i*180+120,165);
            on_combo[i].setSize(90,30);
            off_combo[i].setLocation(i*180+120,285);
            off_combo[i].setSize(90,30);

            add(on_combo[i]);
            add(off_combo[i]);
        }

//        chooseBnt[0].setColor(bntColor[1]);

        setBtn = new RoundedButton("set",new Color(150,83,66));
        setBtn.setLocation(360,360);
        setBtn.setSize(60,30);
        setBtn.addActionListener(this);

        serial.sw.write_byte("0201FF52FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
        AlarmCallback();

        add(label);
        add(setBtn);

    }

    private void setTime(){
        System.out.println(choseNum);
        for(int i = 0;i<redTime.length;i++) {
//            if(i == choseNum){
//        }
        }
//        String[][] time = {redTime, blueTime,fanTime};
//        JComboBox[][] combo = {on_combo, off_combo};
//        for(int i=0;i<3;i++){//선택된 버튼
//            if(i == choseNum){
//                for(int k = 0; k<2 ; k++){//on / off
//                    for(int j=0;j<hour.length;i++){ //combo와 맞는
//                        if(hour[j].equals(time[i][k*2])) {
//                            combo[k][0].setSelectedIndex(j);
//                        }
//                    }
//                    for(int j=0;j<minute.length;i++){
//                        if(minute[j].equals(time[i][k*2+1])){
//                            combo[k][1].setSelectedIndex(j);
//                        }
//                    }
//                }
//
//            }
//        }

        repaint();
    }

    private void AlarmCallback(){
        dataPacket.setAlarmCallback(new AlarmCallback() {
            @Override
            public void set_alarm(String[] red, String[] blue, String[] fan) {

                 redTime = red;
                blueTime = blue;
                fanTime = fan;

                setTime();
//                for()
//                System.out.println(red);
            }
        });
//        dataPacket.setAlarmCallback(new AlarmCallback() {
//            @Override
//            public void set_alarm(String[] red, String[] blue, String[] fan) {
//                redTime = red;
//                blueTime = blue;
//                fanTime = fan;
//
//                setTime();
//            }
//        });
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == chooseBnt[0] || e.getSource() == chooseBnt[1] || e.getSource() == chooseBnt[2]){
            RoundedButton jb = (RoundedButton) e.getSource();
            for(int i = 0;i< chooseBnt.length; i++){
                if(jb == chooseBnt[i]){
                    choseNum = i;
                    for(int j = 0; j<jl.length ; j++){
                        jl[j].setText(title[i] + " "+ title_OnOff[j]);
                        jl[j].setBackground(bntColor[i].brighter());
                    }
                }
            }
        }
        else if(e.getSource() == setBtn){
            String ch = "";
            for(int i=0;i<3;i++){
                if(choseNum==i) ch = "0"+choseNum;
            }
            String onTime = hour[on_combo[0].getSelectedIndex()] + minute[on_combo[1].getSelectedIndex()];
            String offTime =  hour[off_combo[0].getSelectedIndex()] + minute[off_combo[1].getSelectedIndex()];

            serial.sw.write_byte("0201FF55FF"+ch+"FF"+onTime+offTime+"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
            System.out.println(ch+" is on from "+onTime+"to"+offTime);
        }


    }
}
