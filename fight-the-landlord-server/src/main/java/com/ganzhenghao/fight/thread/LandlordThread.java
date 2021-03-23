package com.ganzhenghao.fight.thread;

import com.ganzhenghao.fight.bean.PokerNoColor;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName LandlordThread
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 20:04 星期六
 * @Version 1.0
 */
@AllArgsConstructor
public class LandlordThread extends Thread{
    private final Socket socket;
    private final List<PokerNoColor> bottom;
    private final String randomId;
    private final AtomicInteger sort;
    private final Object lock;
    @Override
    public void run() {

        //二次连接 开始随机分发地主  判断三个连接的id和服务器id列表相同
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //第二次连接时 从客户端接受ID 并且比对随机到的Id
            String id = reader.readLine();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            ArrayList<Object> info = new ArrayList<>();
            if (randomId.equals(id)){
                // 0 -> 是否为地主   1 -> 出牌顺序   3-> 底牌
                info.add(true);
                synchronized (lock) {
                    info.add(sort.intValue());

                    //添加完成后 唤醒其他等待线程
                    lock.notifyAll();
                }

            }else{

                info.add(false);

                //如果不是地主 线程等待  直到是地主才唤醒
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //唤醒后 赋值sort sort ++
                synchronized (sort) {
                    info.add(sort.intValue());
                    sort.getAndIncrement();
                }

            }
            info.add(bottom);

            oos.writeObject(info);
            socket.shutdownOutput();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
