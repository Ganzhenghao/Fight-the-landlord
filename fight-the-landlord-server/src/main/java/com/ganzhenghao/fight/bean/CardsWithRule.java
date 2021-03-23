package com.ganzhenghao.fight.bean;

import com.ganzhenghao.fight.enu.Rule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PokerWithRule
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/23 18:49 星期二
 * @Version 1.0
 */
@Data
public class CardsWithRule implements Serializable {

    private Rule rule;

    private List<PokerNoColor> playCards;

}
