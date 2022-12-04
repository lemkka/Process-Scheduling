package view;
import process.ProcessData;
import utils.ProcessSort;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
/*
 * Created by JFormDesigner on Thu May 19 21:03:08 CST 2022
 */
/*
a 1 5 1   1
b 2 4 2   2
c 3 2 3   2.5

fcfs:
a   5   1
b   8   2
c   9   4.5

sjf:
a   5   1
b   10  2.5
c   5   2.5

hrrf:
a   5   1
b   10  2.5
c   5   2.5

hpf:
a   11  2.2
b   6   1.5
c   2   1
*/

/**
 * @author unknown
 */
public class myFrame extends JFrame {
    public List<ProcessData> processDataList=new ArrayList<ProcessData>();   //记录用户输入的进程
    public List<String> nameList=new ArrayList<String>();   //记录正在运行进程的名字
    public List<Integer> arriveTimeList=new ArrayList<Integer>();   //记录正在运行进程的到达时间
    public List<Integer> moreTimeList=new ArrayList<Integer>();   //记录正在运行进程的还需要运行的时间
    public List<Integer> currentTimeList=new ArrayList<Integer>();   //记录当前时间
    public myFrame() {
        initComponents();
    }
    public int time=0;
    public int index=0;

    private void button1MouseClicked(MouseEvent e) {
        // TODO add your code here
        String name=nameT.getText();
        if(!isNumeric(arriveT.getText())||!isNumeric(needT.getText())||!isNumeric(prioT.getText()))
        {
            JOptionPane.showMessageDialog(null,"输入有误!");
            return;
        }
        int arriveTime=Integer.parseInt(arriveT.getText());
        int needTime=Integer.parseInt(needT.getText());
        int prio=Integer.parseInt(prioT.getText());
        ProcessData pd=new ProcessData(name,arriveTime,needTime,prio);
        processDataList.add(pd);
        JOptionPane.showMessageDialog(null,"输入成功!");
        nameT.setText("");
        arriveT.setText("");
        needT.setText("");
        prioT.setText("");
    }

    public Timer timer =new Timer(1000,new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(index== nameList.size()-1) timer.stop();
            currentTime.setText(String.valueOf(currentTimeList.get(index)));
            currentRun.setText(nameList.get(index));
            currentArrive.setText(String.valueOf(arriveTimeList.get(index)));
            restTime.setText(String.valueOf(moreTimeList.get(index)));
            index++;
    }
    });

    private void fcfsMouseClicked(MouseEvent e) {
        // TODO add your code here
        restart();
        if (processDataList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "尚未输入数据!");
            return;
        }
        currentTime.setText("0");
        ProcessSort.sortByArriveTime(processDataList);
        for (int i = 0; i < processDataList.size(); i++)
        {
            ProcessData pd=processDataList.get(i);
            int moreTime=pd.getNeedTime();
            while(time<pd.getArriveTime())
            {
                addToList(time,-1,-1,"null");
                time++;
            }

            pd.setBeginTime(time);
            while(moreTime>=0)
            {
                moreTime--;
                time++;
            }
            time--;
            pd.setFinishTime(time);
            for(int j=pd.getBeginTime();j<=+pd.getFinishTime();j++)
            {
                addToList(j,pd.getArriveTime(),pd.getNeedTime()-(j-pd.getBeginTime()),pd.getName());
            }
            pd.turnAroundTime=pd.getFinishTime()-pd.getArriveTime();
            System.out.println(pd);
        }
        double avg1 = ProcessSort.calculateTurnaroundTime(processDataList);
        double avg2 = ProcessSort.calculateWeightedTurnaroundTime(processDataList);
        DecimalFormat df = new DecimalFormat( "0.00");
        avgTAT.setText(String.valueOf(df.format(avg1)));
        avgWTAT.setText(String.valueOf(df.format(avg2)));
        timer.start();

    }

    boolean check(boolean[] books)
    {
        for (boolean book : books) {
            if (!book) {
                return false;
            }
        }
        return true;
    }
    
    void canBeAdded(List<ProcessData> tmp,boolean[] isIn)    //判断某一时间点哪些进程可以加入运行队列
    {
        for (ProcessData pd : processDataList)
        {
            if(time>=pd.getArriveTime()&&!isIn[pd.getId()])
            {
                tmp.add(pd);
                isIn[pd.getId()]=true;
            }
        }
    }
    
    private void sjfMouseClicked(MouseEvent e) {
        // TODO add your code here
        restart();
        if (processDataList.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "尚未输入数据!");
            return;
        }
        currentTime.setText("0");
        List<ProcessData> tmp=new ArrayList<ProcessData>();
        boolean[] isIn=new boolean[processDataList.size()];
        boolean[] isFinished=new boolean[processDataList.size()];
        for(int i=0;i<isIn.length;i++)
        {
            isIn[i]=false;
            isFinished[i]=false;
        }
        while(true)
        {
            if(check(isFinished)) break;
            canBeAdded(tmp,isIn);
            if(!tmp.isEmpty())
            {
                ProcessSort.sortByServeTime(tmp);
                ProcessData pd=tmp.get(0);
                int moreTime=pd.getNeedTime();
                pd.setBeginTime(time);
                while(moreTime>=0)
                {
                    moreTime--;
                    time++;
                }
                time--;
                pd.setFinishTime(time);
                System.out.println(pd);
                for(int j=pd.getBeginTime();j<=+pd.getFinishTime();j++)
                {
                    addToList(j,pd.getArriveTime(),pd.getNeedTime()-(j-pd.getBeginTime()),pd.getName());
                }
                pd.turnAroundTime=pd.getFinishTime()-pd.getArriveTime();
                isFinished[pd.getId()]=true;
                tmp.remove(0);
            }
            else
            {
                addToList(time,-1,-1,"null");
                time++;
            }
        }
        double avg1 = ProcessSort.calculateTurnaroundTime(processDataList);
        double avg2 = ProcessSort.calculateWeightedTurnaroundTime(processDataList);
        DecimalFormat df = new DecimalFormat( "0.00");
        avgTAT.setText(String.valueOf(df.format(avg1)));
        avgWTAT.setText(String.valueOf(df.format(avg2)));
        timer.start();
    }

    private void hpfMouseClicked(MouseEvent e) {
        // TODO add your code here
        restart();
        if (processDataList.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "尚未输入数据!");
            return;
        }
        currentTime.setText("0");
        List<ProcessData> tmp=new ArrayList<ProcessData>();
        boolean[] isIn=new boolean[processDataList.size()];
        boolean[] isFinished=new boolean[processDataList.size()];
        for(int i=0;i<isIn.length;i++)
        {
            isIn[i]=false;
            isFinished[i]=false;
        }
        while(true)
        {
            canBeAdded(tmp,isIn);
            while(!tmp.isEmpty())
            {
                for(ProcessData p:tmp)
                {
                    System.out.println(p);
                    System.out.println(time);
                }
                ProcessSort.sortByPriority(tmp);
                ProcessData pd=tmp.get(0);
                int moreTime=pd.restTime;
                currentTimeList.add(time);
                nameList.add(pd.getName());
                moreTimeList.add(moreTime);
                arriveTimeList.add(pd.getArriveTime());
                if(moreTime==0)
                {
                    pd.setFinishTime(time);
                    pd.turnAroundTime=pd.getFinishTime()-pd.getArriveTime();
                    isFinished[pd.getId()]=true;
                    tmp.remove(0);
                    time--;
                }
                moreTime--;
                time++;
                pd.restTime=moreTime;
                canBeAdded(tmp,isIn);
            }
            if(check(isFinished)) break;
            addToList(time,-1,-1,"null");
            time++;
        }
        double avg1 = ProcessSort.calculateTurnaroundTime(processDataList);
        double avg2 = ProcessSort.calculateWeightedTurnaroundTime(processDataList);
        DecimalFormat df = new DecimalFormat( "0.00");
        avgTAT.setText(String.valueOf(df.format(avg1)));
        avgWTAT.setText(String.valueOf(df.format(avg2)));
        timer.start();
    }

    private void hrrnMouseClicked(MouseEvent e) {
        // TODO add your code here
        restart();
        if (processDataList.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "尚未输入数据!");
            return;
        }
        currentTime.setText("0");
        List<ProcessData> tmp=new ArrayList<ProcessData>();
        boolean[] isIn=new boolean[processDataList.size()];
        boolean[] isFinished=new boolean[processDataList.size()];
        for(int i=0;i<isIn.length;i++)
        {
            isIn[i]=false;
            isFinished[i]=false;
        }
        while(true)
        {
            if(check(isFinished)) break;
            canBeAdded(tmp,isIn);
            if(!tmp.isEmpty())
            {
                ProcessSort.calculateRate(tmp,time);
                ProcessSort.sortByRate(tmp);
                ProcessData pd=tmp.get(0);
                int moreTime=pd.getNeedTime();
                pd.setBeginTime(time);
                while(moreTime>=0)
                {
                    moreTime--;
                    time++;
                }
                time--;
                pd.setFinishTime(time);
                System.out.println(pd);
                for(int j=pd.getBeginTime();j<=+pd.getFinishTime();j++)
                {
                    addToList(j,pd.getArriveTime(),pd.getNeedTime()-(j-pd.getBeginTime()),pd.getName());
                }
                pd.turnAroundTime=pd.getFinishTime()-pd.getArriveTime();
                isFinished[pd.getId()]=true;
                tmp.remove(0);
            }
            else
            {
                addToList(time,-1,-1,"null");
                time++;
            }
        }
        double avg1 = ProcessSort.calculateTurnaroundTime(processDataList);
        double avg2 = ProcessSort.calculateWeightedTurnaroundTime(processDataList);
        DecimalFormat df = new DecimalFormat( "0.00");
        avgTAT.setText(String.valueOf(df.format(avg1)));
        avgWTAT.setText(String.valueOf(df.format(avg2)));
        timer.start();
    }


    private void detailMouseClicked(MouseEvent e) {
        // TODO add your code here
        JFrame jf = new JFrame("进程信息详情");
        //jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setSize(500,500);
        // 创建内容面板，使用边界布局
        JPanel panel = new JPanel(new BorderLayout());

        // 表头（列名）
        Object[] columnNames = {"进程名", "到达时间", "完成时间", "周转时间", "带权周转时间"};

        // 表格所有行数据
        Object[][] rowData = new Object[10][5];
        int cnt=0;
        DecimalFormat df = new DecimalFormat( "0.00");
        for(ProcessData pd:processDataList)
        {
            Object[] obj=new Object[5];
            obj[0]=pd.getName();
            obj[1]=pd.getArriveTime();
            obj[2]=pd.getFinishTime();
            obj[3]=pd.turnAroundTime;
            double wtat=(double)pd.turnAroundTime/pd.getNeedTime();
            obj[4]=df.format(wtat);
            rowData[cnt]=obj;
            cnt++;
        }

        // 创建一个表格，指定 所有行数据 和 表头
        JTable table = new JTable(rowData, columnNames);

        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        // 把 表格内容 添加到容器中心
        panel.add(table, BorderLayout.CENTER);

        jf.setContentPane(panel);
//        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    private void pauseMouseClicked(MouseEvent e) {
        // TODO add your code here
        timer.stop();
    }

    private void retryMouseClicked(MouseEvent e) {
        // TODO add your code here
        timer.restart();
    }




    

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        title = new JLabel();
        arrive = new JLabel();
        need = new JLabel();
        prio = new JLabel();
        arriveT = new JTextField();
        needT = new JTextField();
        prioT = new JTextField();
        button1 = new JButton();
        name = new JLabel();
        nameT = new JTextField();
        fcfs = new JButton();
        sjf = new JButton();
        hrrn = new JButton();
        hpf = new JButton();
        label1 = new JLabel();
        currentTime = new JTextField();
        label2 = new JLabel();
        currentRun = new JTextField();
        label3 = new JLabel();
        currentArrive = new JTextField();
        label4 = new JLabel();
        restTime = new JTextField();
        label5 = new JLabel();
        avgTAT = new JTextField();
        label6 = new JLabel();
        avgWTAT = new JTextField();
        pause = new JButton();
        retry = new JButton();
        detail = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- title ----
        title.setText("\u8c03\u5ea6\u7b97\u6cd5\u6a21\u62df");
        contentPane.add(title);
        title.setBounds(215, 10, 115, 30);

        //---- arrive ----
        arrive.setText("\u5230\u8fbe\u65f6\u95f4");
        contentPane.add(arrive);
        arrive.setBounds(new Rectangle(new Point(135, 50), arrive.getPreferredSize()));

        //---- need ----
        need.setText("\u8981\u6c42\u8fd0\u884c\u65f6\u95f4");
        contentPane.add(need);
        need.setBounds(new Rectangle(new Point(200, 50), need.getPreferredSize()));

        //---- prio ----
        prio.setText("\u4f18\u5148\u7ea7");
        contentPane.add(prio);
        prio.setBounds(new Rectangle(new Point(295, 50), prio.getPreferredSize()));
        contentPane.add(arriveT);
        arriveT.setBounds(135, 80, 45, 30);
        contentPane.add(needT);
        needT.setBounds(215, 80, 45, 30);
        contentPane.add(prioT);
        prioT.setBounds(290, 80, 45, 30);

        //---- button1 ----
        button1.setText("\u786e\u5b9a");
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button1MouseClicked(e);
            }
        });
        contentPane.add(button1);
        button1.setBounds(new Rectangle(new Point(375, 80), button1.getPreferredSize()));

        //---- name ----
        name.setText("\u8fdb\u7a0b\u540d");
        contentPane.add(name);
        name.setBounds(new Rectangle(new Point(55, 50), name.getPreferredSize()));
        contentPane.add(nameT);
        nameT.setBounds(55, 80, 45, 30);

        //---- fcfs ----
        fcfs.setText("\u5148\u6765\u5148\u670d\u52a1\u7b97\u6cd5");
        fcfs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fcfsMouseClicked(e);
            }
        });
        contentPane.add(fcfs);
        fcfs.setBounds(new Rectangle(new Point(70, 160), fcfs.getPreferredSize()));

        //---- sjf ----
        sjf.setText("\u77ed\u4f5c\u4e1a\u4f18\u5148\u7b97\u6cd5");
        sjf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sjfMouseClicked(e);
            }
        });
        contentPane.add(sjf);
        sjf.setBounds(new Rectangle(new Point(330, 160), sjf.getPreferredSize()));

        //---- hrrn ----
        hrrn.setText("\u9ad8\u54cd\u5e94\u6bd4\u4f18\u5148\u7b97\u6cd5");
        hrrn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hrrnMouseClicked(e);
            }
        });
        contentPane.add(hrrn);
        hrrn.setBounds(70, 215, 150, hrrn.getPreferredSize().height);

        //---- hpf ----
        hpf.setText("\u6700\u9ad8\u4f18\u5148\u7ea7\u7b97\u6cd5");
        hpf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hpfMouseClicked(e);
            }
        });
        contentPane.add(hpf);
        hpf.setBounds(new Rectangle(new Point(330, 215), hpf.getPreferredSize()));

        //---- label1 ----
        label1.setText("\u5f53\u524d\u65f6\u95f4");
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(170, 270), label1.getPreferredSize()));

        //---- currentTime ----
        currentTime.setText("0");
        contentPane.add(currentTime);
        currentTime.setBounds(255, 270, 30, 20);

        //---- label2 ----
        label2.setText("\u5f53\u524d\u8fd0\u884c\u8fdb\u7a0b");
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(95, 315), label2.getPreferredSize()));
        contentPane.add(currentRun);
        currentRun.setBounds(115, 345, 45, 30);

        //---- label3 ----
        label3.setText("\u5230\u8fbe\u65f6\u95f4");
        contentPane.add(label3);
        label3.setBounds(new Rectangle(new Point(215, 315), label3.getPreferredSize()));
        contentPane.add(currentArrive);
        currentArrive.setBounds(215, 345, 45, 30);

        //---- label4 ----
        label4.setText("\u5269\u4f59\u8fd0\u884c\u65f6\u95f4");
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(305, 315), label4.getPreferredSize()));
        contentPane.add(restTime);
        restTime.setBounds(320, 345, 45, 30);

        //---- label5 ----
        label5.setText("\u5e73\u5747\u5468\u8f6c\u65f6\u95f4");
        contentPane.add(label5);
        label5.setBounds(new Rectangle(new Point(165, 405), label5.getPreferredSize()));
        contentPane.add(avgTAT);
        avgTAT.setBounds(295, 400, 45, 30);

        //---- label6 ----
        label6.setText("\u5e73\u5747\u5e26\u6743\u5468\u8f6c\u65f6\u95f4");
        contentPane.add(label6);
        label6.setBounds(new Rectangle(new Point(155, 445), label6.getPreferredSize()));
        contentPane.add(avgWTAT);
        avgWTAT.setBounds(295, 440, 45, 30);

        //---- pause ----
        pause.setText("\u6682\u505c");
        pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pauseMouseClicked(e);
            }
        });
        contentPane.add(pause);
        pause.setBounds(new Rectangle(new Point(95, 520), pause.getPreferredSize()));

        //---- retry ----
        retry.setText("\u6062\u590d");
        retry.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                retryMouseClicked(e);
            }
        });
        contentPane.add(retry);
        retry.setBounds(new Rectangle(new Point(330, 520), retry.getPreferredSize()));

        //---- detail ----
        detail.setText("\u663e\u793a\u8be6\u60c5");
        detail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                detailMouseClicked(e);
            }
        });
        contentPane.add(detail);
        detail.setBounds(new Rectangle(new Point(205, 580), detail.getPreferredSize()));

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        setSize(510, 680);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel title;
    private JLabel arrive;
    private JLabel need;
    private JLabel prio;
    private JTextField arriveT;
    private JTextField needT;
    private JTextField prioT;
    private JButton button1;
    private JLabel name;
    private JTextField nameT;
    private JButton fcfs;
    private JButton sjf;
    private JButton hrrn;
    private JButton hpf;
    private JLabel label1;
    private JTextField currentTime;
    private JLabel label2;
    private JTextField currentRun;
    private JLabel label3;
    private JTextField currentArrive;
    private JLabel label4;
    private JTextField restTime;
    private JLabel label5;
    private JTextField avgTAT;
    private JLabel label6;
    private JTextField avgWTAT;
    private JButton pause;
    private JButton retry;
    private JButton detail;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void restart()
    {
        time=0;
        index=0;
        nameList.clear();
        arriveTimeList.clear();
        moreTimeList.clear();
        currentTimeList.clear();
        for(ProcessData pd:processDataList)
        {
            pd.restTime=pd.getNeedTime();
        }
        avgTAT.setText("");
        avgWTAT.setText("");
    }

    public void addToList(int currentTime,int arriveTime,int moreTime,String name)
    {
        currentTimeList.add(currentTime);
        arriveTimeList.add(arriveTime);
        moreTimeList.add(moreTime);
        nameList.add(name);
    }
}

