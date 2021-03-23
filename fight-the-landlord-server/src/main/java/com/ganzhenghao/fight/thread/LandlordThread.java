package com.ganzhenghao.fight.thread;

import com.ganzhenghao.fight.bean.Flag;
import com.ganzhenghao.fight.bean.PlayCardsInfo;
import com.ganzhenghao.fight.bean.PlayerInfo;
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

    /**
     * 标志位 判断地主是否出现 出现 true 没有 false
     */
    private final Flag flag;

    @Override
    public void run() {

        //二次连接 开始随机分发地主  判断三个连接的id和服务器id列表相同
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //第二次连接时 从客户端接受ID 并且比对随机到的Id
            String line = reader.readLine();
            PlayerInfo playerInfo = PlayerInfo.parse(line);

            ObjectOutputStream oos = new ObjectOutputStream(os);
            //ArrayList<Object> info = new ArrayList<>();

            PlayCardsInfo playCardsInfo = new PlayCardsInfo();

            if (randomId.equals(playerInfo.getId())){
                // 0 -> 是否为地主   1 -> 出牌顺序   3-> 底牌
                playCardsInfo.setLandlord(true);
                synchronized (lock) {
                    //info.add(sort.intValue());
                    playCardsInfo.setSort(sort.intValue());
                    //添加完成后 唤醒其他等待线程 修改flag位true
                    flag.setFlag(true);
                    lock.notifyAll();
                    System.out.println("地主确认  唤醒 所有其他线程 flag="+flag.isFlag());
                }

            }else{

                //info.add(false);
                playCardsInfo.setLandlord(false);

                //如果不是地主 且地主未出现 线程等待  直到是地主才唤醒
                synchronized (lock){
                    try {
                        if (!flag.isFlag()){
                            System.out.println("不是地主 , 且地主未出现 线程等待 flag="+flag.isFlag());
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //唤醒后 赋值sort sort ++
                synchronized (sort) {
                    //info.add(sort.intValue());
                    playCardsInfo.setSort(sort.intValue());
                    sort.getAndIncrement();
                }
            }
            //info.add(bottom);

            playCardsInfo.setBottomCards(bottom);

            oos.writeObject(playCardsInfo);
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
