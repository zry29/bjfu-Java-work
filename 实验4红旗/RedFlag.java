import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RedFlag extends JFrame {
    private JTextField text = new JTextField();
    private JTextField text2 = new JTextField();
    JPanel panelup = new JPanel();
    JPanel paneldown = new JPanel();
    JButton button = new JButton("开始");
    JButton button2 = new JButton("加速");
    JButton button3 = new JButton("拦截");
    JButton button4 = new JButton("减速");
    JButton button5 = new JButton("停止");
    public ArrayList<Ball> list;
    public Image image;
    public Graphics2D graphics2d;
    public int speed = 3;//速度
    public int rfnumber = 0;//屏幕上的拦截器数,max=10
    public int t1 = 0;//
    public int hitnumber = 0;
    public boolean Tp = true;

    public RedFlag() {
        list = new ArrayList<Ball>();
        this.setTitle("红旗 zry");
        this.setSize(1000, 700);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());//布局

        //组件大小
        Dimension size1 = new Dimension(75, 25);
        Dimension size2 = new Dimension(125, 25);
        text.setPreferredSize(size1);
        text2.setPreferredSize(size1);
        button.setPreferredSize(size1);
        button2.setPreferredSize(size1);
        button4.setPreferredSize(size1);
        button5.setPreferredSize(size1);
        button3.setPreferredSize(size2);

        //组件颜色
        button.setBackground(Color.WHITE);
        button2.setBackground(Color.WHITE);
        button3.setBackground(Color.ORANGE);
        button4.setBackground(Color.WHITE);
        button5.setBackground(Color.WHITE);

        //去掉按钮文字周围的框
        button.setFocusPainted(false);
        button2.setFocusPainted(false);
        button3.setFocusPainted(false);
        button4.setFocusPainted(false);
        button5.setFocusPainted(false);

        //停止按钮的初始状态为失效
        button5.setEnabled(false);

        paneldown.add(text);
        paneldown.add(button);
        paneldown.add(button2);
        paneldown.add(button3);
        paneldown.add(button4);
        paneldown.add(button5);
        paneldown.add(text2);

        //布局
        this.add(panelup);
        this.add(paneldown, BorderLayout.SOUTH);
        this.setVisible(true);

        //按钮监听和线程开始
        ballListener blisten = new ballListener(this, list);
        button.addActionListener(blisten);
        button2.addActionListener(blisten);
        button3.addActionListener(blisten);
        button4.addActionListener(blisten);
        button5.addActionListener(blisten);
        blisten.run();
        Thread thread = new Thread(blisten);
        thread.start();
    }

    //画list中存储的所有球
    public void paint(Graphics g) {
        image = panelup.createImage(panelup.getWidth(), panelup.getHeight());
        graphics2d = (Graphics2D) image.getGraphics();
        for (int i = 0; i < list.size(); i++) {
            Ball b = (Ball) list.get(i);
            b.move();
            b.drawball(graphics2d);
            b.collide(g, this, list);
        }
        g.drawImage(image, 0, 0, this);
    }

    //一个球的信息:包含了类型(导弹的类型为0,拦截器的类型为1,以便后期的碰撞判断)、位置(x和y)、移动方向(x和y)、颜色、大小
    //位置、画图和碰撞判断
    public class Ball {
        private int balltype, x, y, movex, movey, size;
        private Color color;

        public Ball(int balltype, int x, int y, int movex, int movey, Color color, int size) {
            super();
            this.balltype = balltype;
            this.x = x;
            this.y = y;
            this.movex = movex;
            this.movey = movey;
            this.color = color;
            this.size = size;
        }

        public int getBalltype() {
            return balltype;
        }

        public void setBalltype(int balltype) {
            this.balltype = balltype;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getMovex() {
            return movex;
        }

        public void setMovex(int movex) {
            this.movex = movex;
        }

        public int getMovey() {
            return movey;
        }

        public void setMovey(int movey) {
            this.movey = movey;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        //方向乘以当前速度,使导弹和拦截器的速度会随着速度的切换立刻改变
        public void move() {
            x += movex * speed;
            y += movey * speed;
        }

        public void drawball(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }

        //碰撞判断,包括导弹对边框、拦截器对边框和拦截器对导弹
        public void collide(Graphics g, RedFlag rf, ArrayList<Ball> list) {
            //如果是导弹,碰到左右边框后消失
            if (balltype == 0 && (x + size >= rf.getWidth() || x <= 0)) {
                for (int i = 0; i < list.size(); i++) {
                    Ball b = (Ball) list.get(i);
                    if (b == this) {
                        list.remove(b);
                    }
                }
                //为了保持屏幕中的导弹数稳定,每消失一个导弹就会随机生成一个新导弹
                Random random = new Random();
                int newy = random.nextInt(panelup.getHeight() - 100) + 30;
                //导弹随机生成在屏幕左侧或右侧
                int mx = random.nextInt(2) * 2 - 1;
                if (mx == 1) {
                    Ball ball = new Ball(0, 0, newy, 10 * mx, 0, Color.ORANGE, 25);
                    list.add(ball);
                } else if (mx == -1) {
                    Ball ball = new Ball(0, 975, newy, 10 * mx, 0, Color.ORANGE, 25);
                    list.add(ball);
                }
            }

            //如果是拦截器,碰到上边框后消失,屏幕上的拦截器数-1
            if (balltype == 1 && y <= 25) {
                rfnumber--;
                button3.setEnabled(true);
                for (int i = 0; i < list.size(); i++) {
                    Ball b = (Ball) list.get(i);
                    if (b == this) {
                        list.remove(b);
                    }
                }
            }

            //拦截器对导弹的碰撞判断,由于将拦截器和导弹放在一个list里,所以遍历list,每次比较两个球类型是否相同,不同则进行距离判断
            for (int i = 0; i < list.size(); i++) {
                Ball b = (Ball) list.get(i);
                //类型不同
                if ((b != this) && b.balltype != this.balltype) {
                    int xx = Math.abs(this.x - b.x);
                    int yy = Math.abs(this.y - b.y);
                    int xy = (int) Math.sqrt(xx * xx + yy * yy);
                    int tempx = 0;
                    int tempy = 0;
                    //如果距离小于半径之和,则拦截器和导弹同时消失,击中数+1,屏幕上拦截器数-1
                    if (xy <= (this.size / 2 + b.size / 2)) {
                        list.remove(this);
                        list.remove(b);
                        rfnumber--;
                        hitnumber++;
                        text.setText("   计数:" + hitnumber);//计数并显示
                        button3.setEnabled(true);//拦截器消失则屏幕上拦截器数必定小于max,"拦截"按钮恢复有效状态
                        //为了保持屏幕中的导弹数稳定,每消失一个导弹就会随机生成一个新导弹,生成方法同"导弹与边框碰撞"
                        Random random = new Random();
                        int newy = random.nextInt(panelup.getHeight() - 100) + 30;
                        int mx = random.nextInt(2) * 2 - 1;
                        if (mx == 1) {
                            Ball ball = new Ball(0, 0, newy, 10 * mx, 0, Color.ORANGE, 25);
                            list.add(ball);
                        } else if (mx == -1) {
                            Ball ball = new Ball(0, 975, newy, 10 * mx, 0, Color.ORANGE, 25);
                            list.add(ball);
                        }
                    }
                }
            }
        }
    }

    //线程和监听
    public class ballListener implements Runnable, ActionListener {
        private RedFlag rf;
        private ArrayList<Ball> list;
        private volatile boolean pauseFlag = false;
        private Random random = new Random();

        public ballListener(RedFlag rf, ArrayList<Ball> list) {
            this.rf = rf;
            this.list = list;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("开始")) {
                //开始线程
                pauseFlag = true;
                button5.setEnabled(true);
                button.setEnabled(false);

                //开始计时
                TTime tt = new TTime();
                tt.start();
            } else if (e.getActionCommand().equals("停止")) {
                pauseFlag = false;//暂停
                button.setEnabled(true);
                button5.setEnabled(false);

                //暂停计时
                TTime tt2 = new TTime();
                tt2.pausetime();
            } else if (e.getActionCommand().equals("加速")) {
                speed++;
                button4.setEnabled(true);//加速后速度必定高于最低档,"减速"按钮恢复有效状态
                if (speed == 5)
                    button2.setEnabled(false);//5档时不可加速
            } else if (e.getActionCommand().equals("减速")) {
                speed--;
                button2.setEnabled(true);//同上
                if (speed == 1)
                    button4.setEnabled(false);//1档时不可减速
            } else if (e.getActionCommand().equals("拦截")) {
                //屏幕上拦截器数max=10,数量到达10后"拦截"按钮无效
                if (rfnumber == 9) {
                    button3.setEnabled(false);
                }
                //生成拦截器并加入list
                Ball ball = new Ball(1, 500, 680, 0, -10, Color.BLACK, 25);
                list.add(ball);
                rfnumber++;
            }
        }

        public void run() {
            //默认初始状态各有10个向左/向右的导弹(初始状态方向确定,之后生成的方向是随机的),分布位置随机
            for (int num = 0; num < 10; num++) {
                int x = random.nextInt(panelup.getWidth() - 24) + 12;
                int y = random.nextInt(panelup.getHeight() - 50) + 38;
                Ball ball = new Ball(0, x, y, 10, 0, Color.ORANGE, 25);
                list.add(ball);
            }
            for (int num = 0; num < 10; num++) {
                int x = random.nextInt(panelup.getWidth() - 24) + 12;
                int y = random.nextInt(panelup.getHeight() - 50) + 38;
                Ball ball = new Ball(0, x, y, -10, 0, Color.ORANGE, 25);
                list.add(ball);
            }
            while (true) {
                if (pauseFlag) {
                    rf.repaint();
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    //利用线程计时
    private class TTime extends Thread {
        public void pausetime() {
            Tp = false;
        }

        public void run() {
            Tp = true;
            while (Tp) {
                t1++;
                text2.setText("   计时(秒):" + t1);
                try {
                    Thread.sleep(1000);//按秒计时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new RedFlag();
    }
}
