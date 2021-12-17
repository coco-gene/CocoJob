import com.yunqiic.cocojob.common.CocoJobDKey;
import com.yunqiic.cocojob.common.utils.NetUtils;
import org.junit.jupiter.api.Test;

/**
 * NetUtilsTest
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class NetUtilsTest {

    @Test
    public void testOrigin() {
        System.out.println(NetUtils.getLocalHost());
    }

    @Test
    public void testPreferredNetworkInterface() {
        System.setProperty(CocoJobDKey.PREFERRED_NETWORK_INTERFACE, "en5");
        System.out.println(NetUtils.getLocalHost());
    }

    @Test
    public void testIgnoredNetworkInterface() {
        System.setProperty(CocoJobDKey.IGNORED_NETWORK_INTERFACE_REGEX, "utun.|llw.");
        System.out.println(NetUtils.getLocalHost());
    }

}
