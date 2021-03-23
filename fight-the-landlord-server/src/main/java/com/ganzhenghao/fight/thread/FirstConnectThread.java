package com.ganzhenghao.fight.thread;

import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;

/**
 * @ClassName DealThread
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 18:35 星期六
 * @Version 1.0
 */
@AllArgsConstructor
public class FirstConnectThread extends Thread {
    /**
     * 玩家信息
     * 0 -> id
     * 1-> 手牌
     */
    private final Object[] playerInfo;
    private final Socket socket;
    private final Object lock;

    @Override
    public void run() {
        //线程阻塞,直到凑齐三个玩家
        try {
            System.out.println("lock--thread = " + lock);
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //第一次连接
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(playerInfo);
            socket.shutdownOutput();
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            if (line.isBlank()) {
                throw new RuntimeException("第一次连接时,未回复消息");
            } else {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
