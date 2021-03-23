import com.ganzhenghao.fight.FightClient;
import org.junit.Test;

/**
 * @ClassName FightTest
 * @Description TODO
 * @Author Ganzhenghao
 * @Date 2021/3/21 16:27 星期日
 * @Version 1.0
 */
public class FightTest {

    @Test
    public void player1() {
        FightClient.main(new String[]{"1"});
    }

    @Test
    public void player2() {
        FightClient.main(new String[]{"2"});
    }

    @Test
    public void player3() {
        FightClient.main(new String[]{"3"});
    }


}
