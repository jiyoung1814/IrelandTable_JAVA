package UI;

import Callback.SensingDataCallback;
import Packet.DataPacket;
import Serial.Serial;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeUI extends JPanel implements ActionListener {
    JPanel tempPn, humiPn,illumPn,co2Pn,gasPn;
    JLabel time_value, temp_title, temp_value,humi_title, humi_value,co2_title, co2_value,illum_title, illum_value,gas_title, gas_value;
    JButton resetBtn;
    Serial serial;
    DataPacket dataPacket;
//    String time,temp,humi,co2,illum,gas;

    public HomeUI(Serial serial, DataPacket dataPacket){
        this.serial  = serial;
        this.dataPacket = dataPacket;

        time_value= new JLabel("00:00");
        time_value.setLocation(120,30);
        time_value.setSize(210,60);
        time_value.setFont(new Font("Serif",Font.BOLD,20));
        time_value.setHorizontalAlignment(JLabel.CENTER);

        temp_title = new JLabel("Temperature");
        temp_value = new JLabel("0 ℃");
        humi_title = new JLabel("Humidity");
        humi_value = new JLabel("0%");
        illum_title = new JLabel("Illuminance");
        illum_value = new JLabel("0 [lux]");
        co2_title = new JLabel("Co2");
        co2_value = new JLabel("0 ppm");
        gas_title = new JLabel("Gas");
        gas_value = new JLabel("0");



        tempPn = new JPanel(new BorderLayout());
        setJPanel(tempPn,60,150);
        setJLabel(tempPn, temp_title,temp_value);
        humiPn = new JPanel(new BorderLayout());
        setJPanel(humiPn,180,150);
        setJLabel(humiPn, humi_title,humi_value);
        illumPn = new JPanel(new BorderLayout());
        setJPanel(illumPn,300,150);
        setJLabel(illumPn, illum_title,illum_value);
        co2Pn = new JPanel(new BorderLayout());
        setJPanel(co2Pn,120,270);
        setJLabel(co2Pn,co2_title,co2_value);
        gasPn = new JPanel(new BorderLayout());
        setJPanel(gasPn,240,270);
        setJLabel(gasPn, gas_title,gas_value);


        resetBtn = new RoundedButton("reset",new Color(150,83,66));
        resetBtn.setLocation(360,45);
        resetBtn.setSize(60,30);
        resetBtn.addActionListener(this);


        add(time_value);
        add(tempPn);
        add(humiPn);
        add(illumPn);
        add(co2Pn);
        add(gasPn);
        add(resetBtn);


        serial.sw.write_byte("0202FF53FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
        SensingData_Callback();
    }

    private  void setJPanel(JPanel jp, int x, int y){
        jp.setLocation(x,y);
        jp.setSize(90,90);

    }

    private void setJLabel(JPanel jp, JLabel jl_title, JLabel jl){
        jl_title.setHorizontalAlignment(JLabel.CENTER);
        jl_title.setBackground(new Color(69,161,0));
        jl_title.setForeground(new Color(255,255,255));
        jl_title.setOpaque(true);


//        jl = new JLabel();
        jl.setHorizontalAlignment(JLabel.CENTER);
        jl.setFont(new Font("Serif",Font.BOLD,15));
        jl_title.setOpaque(true);

        jp.add(jl_title, BorderLayout.NORTH);
        jp.add(jl,BorderLayout.CENTER);


    }

    public void SensingData_Callback(){
        dataPacket.setSensingDataCallback(new SensingDataCallback() {
            @Override
            public void set_sensing_data(String time, String temp, String humi, String co2, String illum, String gas) {
                System.out.println("time: "+time);
                System.out.println("temp: "+temp+ " ℃ ");
                System.out.println("humi: "+humi+ " %");
                System.out.println("Co2: "+co2+ " ppm");
                System.out.println("illum: "+illum+ "[lx]");
                System.out.println("gas: "+gas);

                time_value.setText(time);
                temp_value.setText(temp+" ℃ ");
                humi_value.setText(humi+ " %");
                co2_value.setText(co2+ " ppm");
                illum_value.setText(illum+ "[lx]");
                gas_value.setText(gas);
                repaint();

            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == resetBtn){
            serial.sw.write_byte("0202FF53FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
            SensingData_Callback();
        }
    }

}


