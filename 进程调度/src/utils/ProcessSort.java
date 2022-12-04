package utils;

import process.ProcessData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Auther: Administrator
 * @Date: 2018/11/2 15:12
 * @Description:
 */
public class ProcessSort {

    public static void sortByPriority(List<ProcessData> processDataList){
        // 根据优先级排序
        Collections.sort(processDataList, new Comparator<ProcessData>() {
            @Override
            public int compare(ProcessData p1, ProcessData p2) {
                if(p1.getPrio()==p2.getPrio())
                    // 优先级相同先来先服务
                    return p1.getId() - p2.getId();
                // 数大优先级高
                return p2.getPrio() - p1.getPrio();
            }
        });
    }   //按照优先级排序

    public static void sortByArriveTime(List<ProcessData> processDataList){
        // 根据到达时间排序
        Collections.sort(processDataList, new Comparator<ProcessData>() {
            @Override
            public int compare(ProcessData p1, ProcessData p2) {
                return p1.getArriveTime() - p2.getArriveTime();
            }
        });
    }   //按照到达时间排序

    public static void sortByServeTime(List<ProcessData> processDataList){
        // 根据作业长短排序
        Collections.sort(processDataList, new Comparator<ProcessData>() {
            @Override
            public int compare(ProcessData p1, ProcessData p2) {
                // 按到达时间升序排列
                if(p1.getNeedTime()==p2.getNeedTime())
                    // 到达时间相同先来先服务
                    return p1.getArriveTime() - p2.getArriveTime();
                return p1.getNeedTime() - p2.getNeedTime();
            }
        });
    }   //按照要求服务时间排序

    public static void calculateRate(List<ProcessData> processDataList,int time) {
        for(ProcessData pd:processDataList)
        {
            pd.rate=((double)pd.getNeedTime()+time-pd.getArriveTime())/pd.getNeedTime();
            System.out.println(pd.rate);
        }
    }   //计算响应比

    public static double calculateTurnaroundTime(List<ProcessData> processDataList) {
        double res=0;
        for(ProcessData pd:processDataList)
        {
            res+=pd.turnAroundTime;
        }
        return res/processDataList.size();
    }   //计算周转时间

    public static double calculateWeightedTurnaroundTime(List<ProcessData> processDataList) {
        double res=0;
        for(ProcessData pd:processDataList)
        {
            res+=(double)pd.turnAroundTime/pd.getNeedTime();
        }
        return res/processDataList.size();
    }   //计算带权周转时间

    public static void sortByRate(List<ProcessData> processDataList){
        // 根据作业长短排序
        Collections.sort(processDataList, new Comparator<ProcessData>() {
            @Override
            public int compare(ProcessData p1, ProcessData p2) {
                // 按到达时间升序排列
                if(p1.rate==p2.rate)
                    // 到达时间相同先来先服务
                    return p1.getArriveTime() - p2.getArriveTime();
                return p1.rate>p2.rate?-1:1;
            }
        });
    }   //按照响应比排序

    public static void main(String[] args) {
        List<ProcessData> processDataList = new ArrayList<>();
        sortByPriority(processDataList);
        System.out.println(processDataList.remove(0));

        for(ProcessData processData:processDataList){
            System.out.println(processData);
        }
    }


}
