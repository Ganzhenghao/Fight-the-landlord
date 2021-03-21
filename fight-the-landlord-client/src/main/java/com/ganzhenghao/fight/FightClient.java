package com.ganzhenghao.fight;

import com.ganzhenghao.fight.bean.PokerNoColor;
import com.ganzhenghao.fight.utlis.PokerUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @ClassName FightClient
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 17:50 星期六
 * @Version 1.0
 */
public class FightClient {
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

    /**
     * ip地址
     */
    private static final String IP = "127.0.0.1";

    private static Integer sort;

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        //第一次连接
        firstConnect();

        //第二次连接 发送自己的id号 获得底牌信息 以及 地主信息 展示自己的手牌
        try (Socket socket = new Socket(IP, 5555)) {

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            //发送自己的Id号
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(id);
            writer.flush();
            socket.shutdownOutput();

            //获取返回信息
            ObjectInputStream ois = new ObjectInputStream(is);

            try {
                ArrayList<Object> info = (ArrayList<Object>) ois.readObject();
                // 0 -> 是否为地主   1 -> 出牌顺序   3-> 底牌
                isLandlord = (boolean) (info.get(0));

                sort = (Integer) info.get(1);

                List<PokerNoColor> bottom = (List<PokerNoColor>) info.get(2);
                if (isLandlord){list.addAll(bottom);}

                //展示底牌
                System.out.println("地主底牌是:");
                PokerUtils.printPoker(bottom);

                //展示手牌
                System.out.println("您的手牌是:");
                Collections.sort(list);
                PokerUtils.printPoker(list);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void firstConnect() {
        try (Socket socket = new Socket(IP, 5555)) {
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
            writer.write(id+"--> receive");
            writer.flush();
            socket.shutdownOutput();
            System.out.println("player "+id+" 手牌为:"+list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
