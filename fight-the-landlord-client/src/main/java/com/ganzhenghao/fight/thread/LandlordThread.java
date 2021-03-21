package com.ganzhenghao.fight.thread;

import com.ganzhenghao.fight.bean.PokerNoColor;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName LandlordThread
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 20:04 星期六
 * @Version 1.0
 */
@AllArgsConstructor
public class LandlordThread extends Thread{
    private Socket socket;
    private List<PokerNoColor> bottom;
    private String randomId;

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
                // 0 -> 是否为地主   1 -> 底牌
                info.add(true);
            }else{
                info.add(false);
            }
            info.add(bottom);

            oos.writeObject(info);
            socket.shutdownOutput();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
