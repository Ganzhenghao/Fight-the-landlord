package com.ganzhenghao.fight.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PlayCardsInfo
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/23 18:30 星期二
 * @Version 1.0
 */
@Data
public class PlayCardsInfo implements Serializable {

    private String id;

    private boolean isLandlord;

    private int sort;

    private List<PokerNoColor> playCards;

    private List<PokerNoColor> handCards;

    private List<PokerNoColor> bottomCards;

}
