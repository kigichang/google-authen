import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用 Google Authenticator 來做雙重認證。
 * Base32String, PasscodeGenerator 是網路上的 open source.
 * @author kigi
 *
 */
public class Main {

	public static void main(String[] args) throws IOException {
		
		// 幫使用者隨機產生一組 key，要記錄下來！
		final String plant_secret = "TestTest";
		
		// 將 key 用 Base32 編碼
		final String encoded_secret = Base32String.encode(plant_secret.getBytes());
		
		String url = getUrl("account", "FEEC", encoded_secret);
		
		// 產生 QRCode， 可以用 Google Authenticator 來拍照設定，用 Googles 拍。
		System.out.println("QRCode: " + url);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Input six number from Google Authenitcator: ");
		
		String line = br.readLine();
		
		if (line.equals(computePin(encoded_secret))) {
			System.out.print(line + " is OK");
		}
		else {
			System.out.print(line + " is Not OK");
		}
			
		br.close();
		
	}

	public static String getUrl(String user, String issuer, String secret) throws UnsupportedEncodingException {
		//String url = String.format("otpauth://totp/Hello:%s@%s?secret=%s&issuer=Example", user, host, secret);
		String url = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, user, secret, issuer);
        String encoder = "https://www.google.com/chart?chs=200x200&chld=M|0&cht=qr&chl=";
        String encodeURL = encoder + java.net.URLEncoder.encode(url, "UTF-8");
        return encodeURL;
    }
	
	public static String computePin(String secret) {
	    if (secret == null || secret.length() == 0) {
	      return "Null or empty secret";
	    }
	    try {
	      final byte[] keyBytes = Base32String.decode(secret);
	      Mac mac = Mac.getInstance("HMACSHA1");
	      mac.init(new SecretKeySpec(keyBytes, ""));
	      PasscodeGenerator pcg = new PasscodeGenerator(mac);
	      return pcg.generateTimeoutCode();
	    } catch (GeneralSecurityException e) {
	      return "General security exception";
	    } catch (Base32String.DecodingException e) {
	      return "Decoding exception";
	    }
	  }
}
