package com.ganzhenghao.fight.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName PokerNoColor
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/20 18:07 星期六
 * @Version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
public class PokerNoColor implements Comparable<PokerNoColor>, Serializable {

    /**
     * 点数
     */
    private String point;

    /**
     * 实际值 3对应1  4对应2 依次类推....
     */
    private int value;

    public PokerNoColor(String point, int value) {
        this.point = point;
        this.value = value;
    }

    /**
     * 返回点数
     *
     * @return {@link String}
     */
    @Override
    public String toString() {
        if (point.length() == 1){
            point += " ";
        }
        return point;
    }

    /**
     * 根据实际值进行比较
     *
     * @param o o
     * @return int
     */
    @Override
    public int compareTo(PokerNoColor o) {
        return this.value - o.getValue();
    }

}
