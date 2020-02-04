import model.Login;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import util.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String clientCookie = "_ga=GA1.2.1697275246.1568148019; cookiesession4=5WQ7JLGFyGbUQ1MBwqwR1U9HSbPdUUGjQCt/PXseBim0sL1XTXfCJn3+uzOOJ+1flybxuSMB8zDmaHr0kMQcWGdS9KrnRZVXGdqzYRueEz+7RlT90gbCzEMsUYexl/GAoH5ddKR6JIWv7oLOI5oio0TjkhDhJ4XjYh2bPT41f0QWtmgIPHwLdu9Zt7aV42DSkzjKHHCABmQ=; _gid=GA1.2.142154059.1580845722; SESSION_COOKIE=m4q40ppy4nquhqucleepwfwv";

        String setCookie = downloadCaptcha(clientCookie);


        Login login = new Login();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter captcha : ");
        login.setCaptcha(sc.nextLine());
        login.setUsername(Utility.getConfig("username"));
        login.setPassword(Utility.getConfig("password"));

        DefaultHttpClient httpClient = new DefaultHttpClient();

        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("username", login.getUsername()));
        form.add(new BasicNameValuePair("password", login.getPassword()));
        form.add(new BasicNameValuePair("captcha", login.getCaptcha()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, "UTF-8");


        HttpPost httpPost = new HttpPost(Utility.getConfig("login"));
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");
        httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpPost.setHeader("Accept-Language", "en-US,en;q=0.5");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Cookie", clientCookie.replace("_gat=1", "").trim() + " " + setCookie.replace(";", ""));
        httpPost.setHeader("Host", "online.agah.com");
        httpPost.setHeader("Origin", "https://online.agah.com");
        httpPost.setHeader("Referer", "https://online.agah.com/Auth/Login?ReturnUrl=%2f");
        httpPost.setHeader("Upgrade-Insecure-Requests", "1");
        System.out.println("Executing request " + httpPost.getRequestLine());
        HttpResponse response = httpClient.execute(httpPost);
        // Create a custom response handler
        System.out.println(response.getEntity().getContentLength());
        System.out.println("----------------------------------------");

        Header[] allHeaders = response.getAllHeaders();
        Header[] headers = response.getHeaders("Set-Cookie");
        String c1 = headers[0].getValue().split(" ")[0];
        String c2 = headers[1].getValue().split(" ")[0];

        HttpGet getRequest = new HttpGet("https://online.agah.com/");
        getRequest.setHeader("Cookie", clientCookie + " " + c1 + " " + c2);
        response = httpClient.execute(getRequest);
        String s1 = EntityUtils.toString(response.getEntity());
        System.out.println(s1);
    }


    private static String downloadCaptcha(String clientCookie) throws IOException {
        System.out.println("-------- start save captcha to file ---------");
        String captchaUrl = Utility.getConfig("captcha");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            //Define a HttpGet request; You can choose between HttpPost, HttpDelete or HttpPut also.
            //Choice depends on type of method you will be invoking.
            HttpGet getRequest = new HttpGet(captchaUrl);

            //Set the API media type in http accept header
            getRequest.addHeader("accept", "application/xml");
            getRequest.setHeader("Cookie", clientCookie);

            //Send the request; It will immediately return the response in HttpResponse object
            HttpResponse response = httpClient.execute(getRequest);

            //verify the valid error code first
            int statusCode = response.getStatusLine().getStatusCode();
            Header firstHeader = response.getFirstHeader("Set-Cookie");
            if (statusCode != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }

            //Now pull back the response object
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            String imageName = "sample-" + new Date().getTime() + ".jpg";
            String filePath = imageName;
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            int inByte;
            while ((inByte = is.read()) != -1)
                fos.write(inByte);
            is.close();
            fos.close();

            System.out.println("-------- finish save captcha to file " + imageName + " ---------");
            return firstHeader.getValue();

        } finally {
            //Important: Close the connect
            httpClient.getConnectionManager().shutdown();
        }

    }
}
