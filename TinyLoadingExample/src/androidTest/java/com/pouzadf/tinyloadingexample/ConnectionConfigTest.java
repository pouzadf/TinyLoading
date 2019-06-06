package com.pouzadf.tinyloadingexample;



import com.pouzadf.tinyloading.Utils.ConnectionConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.HttpURLConnection;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ConnectionConfigTest {



    @Test
    public void assertNotNull_whenProcessingGetRequest_withNoHeaders()
    {
        final  String path = "https://helpx.adobe.com/content/dam/help/en/stock/" +
                "how-to/visual-reverse-image-search/jcr_content/main-pars/image" +
                "/visual-reverse-image-search-v2_intro.jpg";
        try
        {
            HttpURLConnection connection = ConnectionConfiguration.initConnection(path, null);
            assertNotNull(connection);
        }
        catch (IOException e)
        {

        }
    }



    @Test
    public void throwsIOException_whenFetchingFromUnknownSrc()
    {
        final  String path = "https://unknown";
        try
        {
            HttpURLConnection connection = ConnectionConfiguration.initConnection(path, null);
            fail("Should have thrown an exception");
        }
        catch (IOException e)
        {
            assertNotNull(e);
        }
    }


}
