package kr.dkay.qrcoderace;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class AccessToServer extends AsyncTask<String, Void,String> {
	
	String myLat; 
	String myLng;
	String myTeam;
	String device;
	String version;
	String mode;
	
	
    protected String doInBackground(String ... data) {
    	mode = data[0];
    	myLat = data[1];
    	myLng = data[2];
    	myTeam = data[3];
    	device = data[4];
    	version = data[5];
        return HttpPostData();
    }
    
	
    //public String HttpPostData(String mode, String myLat, String myLng, String myTeam, String device, String version) {  	
    public String HttpPostData() {
    	String result = null;
    	try {
            HttpPost request = new HttpPost("http://drama.kaist.ac.kr/qr/getLocation.php");
            
            Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
            nameValue.add(new BasicNameValuePair("mode", mode));
            nameValue.add(new BasicNameValuePair("lat", myLat));
            nameValue.add(new BasicNameValuePair("long", myLng));
            nameValue.add(new BasicNameValuePair("team", myTeam));
            nameValue.add(new BasicNameValuePair("device", device));
            nameValue.add(new BasicNameValuePair("version", version));
            request.setEntity(makeEntity(nameValue));
            
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(request);
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity);
            
        } catch (MalformedURLException e) {	
        	e.printStackTrace();
        } catch (IOException e) {
    		e.printStackTrace();
        } catch (Exception e){
        	e.printStackTrace();
        }//try
        return result;
    } // HttpPostData
    

    private HttpEntity makeEntity(Vector<NameValuePair> nameValue){
    	HttpEntity result = null;
    	try{
    		result = new UrlEncodedFormEntity(nameValue,HTTP.UTF_8);
    	} catch (UnsupportedEncodingException e){
    		e.printStackTrace();
    	}
    	return result;
    }//makeEntity
}
