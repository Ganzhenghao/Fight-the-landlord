package com.ganzhenghao.fight.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

/**
 * @ClassName PlayerInfo
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/23 18:20 星期二
 * @Version 1.0
 */
@Getter
@Setter
public class PlayerInfo implements Serializable {

    private String id;

    private List<PokerNoColor> handCards;

    private int position;

    public PlayerInfo() {
        if (position == 0){
            Properties properties = new Properties();
            FileInputStream is = null;
            try {
                is = new FileInputStream("fight-the-landlord-client/src/main/resources/config.properties");
                properties.load(is);

                String position = properties.getProperty("position");
                this.position = Integer.parseInt(position);

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return id+":"+position;
    }

    /**
     * 解析为 PlayerInfo对象
     *
     * @param info 信息
     * @return {@link PlayerInfo}
     */
    public static PlayerInfo parse(String info){
        String[] split = info.split(":");
        if (split.length != 2){
            throw new RuntimeException("PlayerInfo Pare 异常");
        }
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setId(split[0]);
        playerInfo.setPosition(Integer.parseInt(split[1]));
        return playerInfo;
    }
}
