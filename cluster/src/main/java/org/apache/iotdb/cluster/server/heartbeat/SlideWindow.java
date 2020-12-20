package org.apache.iotdb.cluster.server.heartbeat;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;

public class SlideWindow {
    private int size;
    private ArrayList<Long> time_queue;
    private long sum_of_interval;
    private long sum_of_int_sq;
    private long latest_time = 0;
    private double minSD = 100.0;


    public SlideWindow(int size){
        this.size = size;
        time_queue = new ArrayList<>();
        sum_of_interval = 0;
        sum_of_int_sq = 0;
    }

    public void setMinSD(double minSD) {
        this.minSD = minSD;
    }

    public void addTime(long new_time){
        if (latest_time != 0) {
            if (time_queue.size() == 0) {
                time_queue.add(new_time - latest_time);
                sum_of_interval += time_queue.get(0);
                sum_of_int_sq += time_queue.get(0) * time_queue.get(0);
            } else {
                if (time_queue.size() >= this.size) {
                    sum_of_interval -= time_queue.get(0);
                    sum_of_int_sq -= time_queue.get(0) * time_queue.get(0);
                    time_queue.remove(0);
                }
                time_queue.add(new_time - latest_time);
                sum_of_interval += time_queue.get(time_queue.size() - 1);
                sum_of_int_sq += time_queue.get(time_queue.size() - 1) * time_queue.get(time_queue.size() - 1);

            }
        }
        latest_time = new_time;
    }

    public double phi(long now_time){

        if(time_queue.size() == 0 || time_queue.size() == 1){
            return 0;
        }
        long latest_time = this.latest_time;
        return -1.0 * Math.log10(probabilityLater(now_time - latest_time));
    }

    private double probabilityLater(long t){

        double mean =  (double) sum_of_interval / (double) time_queue.size();
        double sigma_sq = (sum_of_int_sq + time_queue.size() * mean * mean - 2.0 * mean * sum_of_interval) / (double) time_queue.size();
        double sigma = Math.sqrt(sigma_sq);

        if(sigma < minSD){
            sigma = minSD;
        }
        NormalDistribution normalDistribution = new NormalDistribution(mean, sigma);
        return 1.0 - normalDistribution.cumulativeProbability(t);
    }
}