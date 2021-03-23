package com.ganzhenghao.fight.enu;

/**
 * @ClassName Rule
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/23 10:18 星期二
 * @Version 1.0
 */
public enum Rule {
    Single("单张"),
    Double("对子"),
    Straight("顺子"),
    SisterPair("姊妹对"),
    ThreeBeltOne("三带一"),
    Plain("飞机"),
    Bomb("炸弹"),
    KingBomb("王炸");

    Rule(String value) {
        this.value = value;
    }

    private String value;

}
