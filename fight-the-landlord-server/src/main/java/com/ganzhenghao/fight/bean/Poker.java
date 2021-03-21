package com.ganzhenghao.fight.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName Poker
 * @Description
 * @Author Ganzhenghao
 * @Date 2021/3/20 17:47 星期六
 * @Version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
public class Poker implements Comparable<Poker>, Serializable {

    /**
     * 花色
     */
    private String color;

    /**
     * 点数
     */
    private String point;

    /**
     * 实际值 3对应1  4对应2 依次类推....
     */
    private int value;

    public Poker(String color, String point, int value) {
        this.color = color;
        this.point = point;
        this.value = value;
    }

    /**
     * 返回花色加点数
     *
     * @return {@link String}
     */
    @Override
    public String toString() {
        return color+point;
    }

    /**
     * 根据实际值进行比较
     *
     * @param o o
     * @return int
     */
    @Override
    public int compareTo(Poker o) {
        return this.value - o.getValue();
    }

}
