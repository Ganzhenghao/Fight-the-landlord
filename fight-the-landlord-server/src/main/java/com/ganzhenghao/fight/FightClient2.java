package com.ganzhenghao.fight;

import com.ganzhenghao.fight.bean.PokerNoColor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName FightClient
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 17:50 星期六
 * @Version 1.0
 */
public class FightClient2 {
    /**
     * 手牌
     */
    private static List<PokerNoColor> list;
    /**
     * 身份id
     */
    private static String id;
    /**
     * 牌局是否结束 默认true
     */
    private static boolean end = true;
    /**
     * 是否为地主 默认false
     */
    private static boolean isLandlord = false;

    public static void main(String[] args) {
        //第一次连接
        try (Socket socket = new Socket("127.0.0.1", 5555)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            //获取返回的Id和手牌信息
            ObjectInputStream ois = new ObjectInputStream(is);
            try {
                Object[] playerInfo = (Object[]) ois.readObject();
                id = (String) playerInfo[0];
                list = (List<PokerNoColor>) playerInfo[1];
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //返回确认收到信息
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(id+"--> receive(2)");
            writer.flush();
            socket.shutdownOutput();
            System.out.println("player 2 手牌为:"+list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //第二次连接 发送自己的id号
       /* try (Socket socket = new Socket("127.0.0.1", 5555)) {

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(id);
            writer.flush();
            socket.shutdownOutput();

            ObjectInputStream ois = new ObjectInputStream(is);

            try {
                ArrayList<Object> info = (ArrayList<Object>) ois.readObject();
                // 0 -> 是否为地主   1 -> 底牌
                boolean b = (boolean) info.get(0);
                List<PokerNoColor> bottom = (List<PokerNoColor>) info.get(1);
                list.addAll(bottom);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Collections.sort(list);
            System.out.println(list);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }
}
