package UI;

import Callback.LedFanCallback;
import Callback.LedFanValueCallBack;
import Packet.DataPacket;
import Serial.Serial;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControlUI extends JPanel implements ActionListener {
    Serial serial;
    DataPacket dataPacket;
    JButton[] switchJB;
    ImageIcon[] icon;
    JSlider[] jSlider;
    JPanel[] jp;

    int[] value = {0,0,0};
    boolean redOn, blueOn, fanOn;

    public ControlUI(Serial serial, DataPacket dataPacket){
        this.serial = serial;
        this.dataPacket = dataPacket;

//        System.out.println( System.getProperty("user.dir"));

        JLabel label= new JLabel("LED/FAN Control");
        label.setLocation(120,30);
        label.setSize(210,60);
        label.setFont(new Font("Serif",Font.BOLD,20));
        label.setHorizontalAlignment(JLabel.CENTER);

        JLabel redJl = new JLabel("Red LED");
        Color r = new Color(235,119,97);
        setJLabel(redJl, r, 30,90);
        JLabel blueJl = new JLabel("Blue LED");
        Color g = new Color(108,169,232);
        setJLabel(blueJl, g, 30,210);
        JLabel fanJl = new JLabel("Fan");
        Color f = new Color(255,197,51);
        setJLabel(fanJl, f, 30,330);

        jp = new JPanel[3];
        jSlider = new JSlider[3];
        int[] location_jp = {90,210,330};
        int[] location_js = {120,240,360};
        Color[] colors = {r,g,f};

        for(int i=0;i<jp.length;i++){
            jp[i] = new JPanel();
            int finalI = i;
            jSlider[i] = new JSlider(0, 100,0) {
                private SliderPopupListener popupHandler;

                @Override
                public void updateUI() {
                    removeMouseMotionListener(popupHandler);
                    removeMouseListener(popupHandler);
                    removeMouseWheelListener(popupHandler);
                    super.updateUI();
                    popupHandler = new SliderPopupListener(colors[finalI], finalI);
                    addMouseMotionListener(popupHandler);
                    addMouseListener(popupHandler);
                    addMouseWheelListener(popupHandler);
//                setUI(new CustomSliderUI(this));
                }

            };
            jSlider[i].setPaintTicks(true);
            jSlider[i].setSize(280,30);
            jSlider[i].setLocation(120,location_js[i]);
            jSlider[i].setBackground(Color.white);
            jSlider[i].setMajorTickSpacing(20); //큰 눈금 간격 20로 설정
            jSlider[i].setMinorTickSpacing(10); //큰 눈금 간격 20로 설정
            jSlider[i].setPaintLabels(true); //값을 레이블로 표시한다.



            jp[i] .setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
            jp[i] .add(jSlider[i]);
            jp[i] .setLocation(90,location_jp[i]);
            jp[i] .setSize(270,90);
            jp[i] .setBackground(Color.white);

            add(jp[i]);
        }

        icon = new ImageIcon[2];
        String[] imgPath = {"src/off.png","src/on.png"};
        for(int i=0;i<icon.length;i++){
            icon[i] = new ImageIcon(imgPath[i]);
            icon[i] = setImage(icon[i]);
//            icon[1] = new ImageIcon("src/on.png");
        }


        switchJB = new JButton[3];
        for(int i=0;i<switchJB.length;i++){
            switchJB[i] = new JButton(icon[0]);
            setJbutton(switchJB[i],360,(120*i+90));
            add(switchJB[i]);

        }


        serial.sw.write_byte("0201FF53FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");//LED on/off check
        LedFanCallback();

        add(label);
        add(redJl);
        add(blueJl);
        add(fanJl);


    }

    private void LedFanCallback(){
       dataPacket.setLedFanCallback(new LedFanCallback() {

           @Override
           public void set_switch(boolean red, boolean blue, boolean fan) {

               redOn = red;
               blueOn = blue;
               fanOn = fan;

               System.out.println(redOn);
               System.out.println(blueOn);
               System.out.println(fanOn);

               serial.sw.write_byte("0201FF73FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
               LedFanValueCallBack();

           }
       });
    }

    private void LedFanValueCallBack(){
        dataPacket.setLedFanValueCallBack(new LedFanValueCallBack() {
            @Override
            public void set_power(int red, int blue, int fan) {

                value[0] = red;
                value[1] = blue;
                value[2] = fan;

                System.out.println(red);
                System.out.println(blue);
                System.out.println(fan);

                if(redOn){switchJB[0].setIcon(icon[1]);}
                else if (!redOn){switchJB[0].setIcon(icon[0]);}

                if(blueOn){switchJB[1].setIcon(icon[1]);}
                else if (!blueOn){switchJB[1].setIcon(icon[0]);}

                if(fanOn){switchJB[2].setIcon(icon[1]);}
                else if (!fanOn){switchJB[2].setIcon(icon[0]);}

                repaint();

                for(int i=0;i<value.length;i++){
                    jSlider[i].setValue(value[i]);
                }



            }
        });

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String[] name = {"red","blue","fan"};

        if(e.getSource()== switchJB[0]||e.getSource()== switchJB[1]||e.getSource()== switchJB[2]){
            JButton jb = (JButton) e.getSource();
            for(int i = 0;i<switchJB.length;i++){
                if(jb == switchJB[i]){
                    if(switchJB[i].getIcon() == icon[0]){//LED/FAN off -> on
                        System.out.println(name[i] + " on");
                        switchJB[i].setIcon(icon[1]);
                        serial.sw.write_byte("0201FF4CFF0" + (i + 1) + "FF01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
                    }
                    else{//LED/FAN on -> off

                        System.out.println(name[i] + " off");
                        switchJB[i].setIcon(icon[0]);
                        serial.sw.write_byte("0201FF4CFF0" + (i + 1) + "FF00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03");
                    }
                }

            }
        }

    }

    private void setJLabel(JLabel jl, Color c, int x, int y){
        jl.setOpaque(true);
        jl.setHorizontalAlignment(JLabel.CENTER);
        jl.setBackground(c);
        jl.setLocation(x,y);
        jl.setSize(60,90);
    }

    private void setJbutton(JButton jb, int x, int y){
        jb.setSize(30,90);
        jb.setLocation(x,y);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setFocusPainted(false);
        jb.addActionListener(this);
    }

    public ImageIcon setImage(ImageIcon icon){
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(30,90, Image.SCALE_SMOOTH);
        ImageIcon newIcon= new ImageIcon(newImg);
        return newIcon;
    }

//    class CustomSliderUI extends BasicSliderUI{
//
//        public CustomSliderUI(JSlider b) {
//            super(b);
//        }
//
//        @Override
//        public void paint(final Graphics g, final JComponent c) {
//            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            super.paint(g, c);
//        }
//
//        @Override
//        public void paintTrack(Graphics g) {
//            Graphics2D g2 = (Graphics2D) g;
////            g2.setColor(new Color(170, 170 ,170));
////
////            g2.setColor(new Color(200, 200 ,200));
//
//            g2.setColor(new Color(235,119,97));
//        }
//
//        @Override
//        public void paintThumb(final Graphics g) {
//            g.setColor(new Color(255, 197, 51));
////            g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
//        }
//
//    }

    class SliderPopupListener extends MouseAdapter {
        private final JWindow toolTip = new JWindow();
        private final JLabel label = new JLabel("", SwingConstants.CENTER);
        private final Dimension size = new Dimension(60, 20);
        private int prevValue = -1;

        int sliderNum = 0;

        public SliderPopupListener(Color c ,int num) {
            super();
            sliderNum = num;
            label.setOpaque(false);
            label.setBackground(c);
            label.setOpaque(true);
            toolTip.add(label);
            toolTip.setSize(size);
        }

        protected void updateToolTip(MouseEvent me) {
            JSlider slider = (JSlider) me.getComponent();
            int intValue = (int) slider.getValue();
            if (prevValue != intValue) {
                label.setText(String.format("%d", slider.getValue()));
                Point pt = me.getPoint();
                pt.y = -size.height;
                SwingUtilities.convertPointToScreen(pt, me.getComponent());
                pt.translate(-size.width / 2, 0);
                toolTip.setLocation(pt);
            }
            prevValue = intValue;
            value[sliderNum] = intValue;
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            toolTip.setVisible(true);
            updateToolTip(me);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            JSlider js = (JSlider) me.getSource();
            toolTip.setVisible(false);
            for(int i=0;i<jSlider.length;i++){
                if(jSlider[i]==js){
                    String hex = String.format("%02X", value[i]);
                    System.out.println("dec: "+value[i]+", hex : " + hex);
                    serial.sw.write_byte(
                            "0201FF50FF0" + (i + 1) + "FF0000640000" + hex + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF03"); // Duty
                }


            }
        }
    }


}
