package process;


/**
 * @Auther: Administrator
 * @Date: 2018/11/2 14:49
 * @Description:
 */
public class ProcessData {
    private static int count=0;
    private String name;//进程名
    private int id=0;//进程标识数
    private int arriveTime;//到达时间
    private int needTime;//服务时间
    private int prio;//进程优先数，数字越大优先级越高
    private int beginTime;  //开始时间
    private int finishTime;  // 完成时间
    public int turnAroundTime;     //周转时间
    public int restTime;   //还剩多长时间完成
    public double rate;    //响应比


    public ProcessData(String name, int arriveTime, int needTime, int prio) {
        this.name = name;
        this.arriveTime = arriveTime;
        this.needTime = needTime;
        this.restTime = needTime;
        this.prio = prio;
        // 初始状态
        id = count++;

    }

    public int getPrio() {
        return prio;
    }

    public int getArriveTime() {
        return arriveTime;
    }

    public String getName() {
        return name;
    }

    public int getNeedTime() {
        return needTime;
    }



    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public void setNeedTime(int needTime) {this.needTime = needTime;};



    public int getId() {
        return id;
    }

    public String toString(){
        return "name: " + name + " arriveTime: " + arriveTime +  "  needTime: " + needTime + " sortByPriority: " + prio
                +" id: "+ id + " finishTime: " + finishTime;
    }
}
