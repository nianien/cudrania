package com.nianien.cudrania.core.utils;

/**
 * 计时器,衡量某个时间段内所经历的毫秒数
 *
 * @author skyfalling
 */
public class TimeCounter {

    /**
     * 开始时间(纳秒, 单调时钟)
     */
    private long begin;
    /**
     * 结束时间(纳秒, 单调时钟)
     */
    private long end;
    /**
     * 是否停止
     */
    private boolean isStopped;

    /**
     * 默认构造方法,计时器起止时间均设置为当前时间
     */
    public TimeCounter() {
        this.begin = this.end = System.nanoTime();
    }

    /**
     * 开始计时, 返回开始时刻的墙钟毫秒时间戳
     */
    public long start() {
        this.isStopped = false;
        this.begin = System.nanoTime();
        return current();
    }

    /**
     * 停止计时, 返回结束时刻的墙钟毫秒时间戳
     */
    public long stop() {
        this.isStopped = true;
        this.end = System.nanoTime();
        return current();
    }

    /**
     * 计算经历的时间,单位毫秒<br>
     * 如果调用start方法和stop方法,则标识从start开始到stop结束所经历的时间<br>
     * 如果调用start方法而未调用stop方法,则表示从start开始到当前所经历的时间<br>
     * 如果调用stop方法而未调用start方法,则表示从对象创建开始到stop结束所经历的时间<br>
     * 如果未调用任何方法,则表示从对象创建开始到当前时间所经历的时间
     *
     * @return 经历的毫秒数
     */
    public long timePassed() {
        //用单调的nanoTime测时长, 换算成毫秒; 避免currentTimeMillis受系统改时间/NTP影响
        long endNanos = this.isStopped ? this.end : System.nanoTime();
        return (endNanos - this.begin) / 1_000_000L;
    }

    /**
     * 判断是否超时,即timePassed方法的结果是否大于mills
     *
     * @param mills
     * @return
     */
    public boolean timeOut(long mills) {
        return this.timePassed() > mills;
    }

    /**
     * 获取当前时间,返回毫秒数
     *
     * @return
     */
    public static long current() {
        return System.currentTimeMillis();
    }

}
